package ws.gameServer.features.standalone.actor.arenaCenter.ctrl;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.mc.controler.AbstractControler;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.arenaCenter.msg.In_AddPlayerToPvpCenter;
import ws.gameServer.features.standalone.actor.arenaCenter.msg.In_ExchangeRank;
import ws.gameServer.features.standalone.actor.arenaCenter.msg.In_GetRankToPvpCenter;
import ws.gameServer.features.standalone.actor.arenaCenter.msg.In_GotoSettleDaliyRankReward;
import ws.gameServer.features.standalone.actor.arenaCenter.msg.In_QueryEnemies;
import ws.gameServer.features.standalone.extp.mails.utils.MailsCtrlUtils;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.base.MagicWords_Redis;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.CheckPlayerOnlineMsgRequest;
import ws.relationship.base.msg.mail.In_AddGmMail;
import ws.relationship.base.msg.mail.In_AddGmMail.Request;
import ws.relationship.daos.arenaCenterRanker.ArenaCenterRankerDao;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_RankDailyReward_Row;
import ws.relationship.table.tableRows.Table_Robot_Row;
import ws.relationship.topLevelPojos.common.TopLevelHolder;
import ws.relationship.topLevelPojos.mails.SysMail;
import ws.relationship.topLevelPojos.pvp.arenaCenter.ArenaCenter;
import ws.relationship.topLevelPojos.pvp.arenaCenter.ArenaCenterRanker;
import ws.relationship.utils.ClusterMessageSender;
import ws.relationship.utils.DBUtils;
import ws.relationship.utils.InitRealmCreatedTargetsDB;
import ws.relationship.utils.RelationshipCommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class _ArenaCenterCtrl extends AbstractControler<TopLevelHolder> implements ArenaCenterCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_ArenaCenterCtrl.class);
    private static final int CACHE_COUNT = 6000; // 缓存5000名以内的玩家 常用玩家+用于发奖励
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static Map<Integer, ArenaCenterRankerDao> centerOuterRealmIdToDao = new HashMap<>();
    private Map<Integer, ArenaCenter> centerOuterRealmIdToArenaCenter = new HashMap<>();                           // ArenaCenter
    private Map<Integer, Map<String, ArenaCenterRanker>> centerOuterRealmIdToPlayerIdToRanker = new HashMap<>();   // 玩家Id<-->对象缓存
    private Map<Integer, Map<Integer, ArenaCenterRanker>> centerOuterRealmIdToRankToRanker = new HashMap<>();      // 玩家名次<-->对象缓存 (21点发奖励用)
    private static final long delay = 1000;  // 延迟1秒
    private static final long period = 1000; // 每1秒钟一个周期
    private static final long SEND_MAIL_NUM_EACH_SECOND_EACH_REALMID = 30; // 每1秒每个服钟发送邮件的数量
    private Timer timer = new Timer();
    private ActorRef curSender;
    private ActorContext context;


    @Override
    public void newPlayerToCenter(int playerOuterRealmId, int centerOuterRealmId, String playerId) {
        LogicCheckUtils.validateParam(Integer.class, playerOuterRealmId);
        LogicCheckUtils.validateParam(Integer.class, centerOuterRealmId);
        LogicCheckUtils.validateParam(String.class, playerId);

        ArenaCenter arenaCenter = getArenaCenter(centerOuterRealmId);
        int curRank = getRankByPlayerId(arenaCenter, playerId);
        if (curRank <= 0) {
            int preMaxRank = updateMaxRanker(arenaCenter);
            ArenaCenterRanker ranker = new ArenaCenterRanker(playerId, preMaxRank, playerOuterRealmId);
            putRankerAndUpdateCacheAndSaveToDB(arenaCenter, ranker);
            curRank = preMaxRank;
            LOGGER.debug("竞技场:添加新玩家排名为:{}", ranker.getRank());
        }
        In_AddPlayerToPvpCenter.Response response = new In_AddPlayerToPvpCenter.Response(curRank);
        this.curSender.tell(response, ActorRef.noSender());
        DBUtils.saveStringPojo(arenaCenter.getOuterRealmId(), arenaCenter);
    }


    @Override
    public void exchangeRank(int centerOuterRealmId, String attackerId, String beAttackerId) {
        LogicCheckUtils.validateParam(Integer.class, centerOuterRealmId);
        LogicCheckUtils.validateParam(String.class, attackerId);
        LogicCheckUtils.validateParam(String.class, beAttackerId);

        ArenaCenter arenaCenter = getArenaCenter(centerOuterRealmId);
        int rankAttackOld = getRankByPlayerId(arenaCenter, attackerId);
        int rankBeAttackerOld = getRankByPlayerId(arenaCenter, beAttackerId);
        ArenaCenterRanker attacker = getRankerByRank(arenaCenter, rankAttackOld);
        ArenaCenterRanker beAttacker = getRankerByRank(arenaCenter, rankBeAttackerOld);
        if (rankAttackOld > rankBeAttackerOld) { // 如果攻击者的排名比被攻击者大，10:3 被攻击者排名靠前，需要交换
            // 先从集合中移除待交换排名的2个玩家，交换后再添加
            removeRankerAndUpdateCache(arenaCenter, attacker);
            removeRankerAndUpdateCache(arenaCenter, beAttacker);
            attacker.setRank(rankBeAttackerOld);
            beAttacker.setRank(rankAttackOld);
            // 更新排名
            putRankerAndUpdateCacheAndSaveToDB(arenaCenter, attacker);
            putRankerAndUpdateCacheAndSaveToDB(arenaCenter, beAttacker);
        }
        LOGGER.info("竞技场排名交换！ attackerId={} beAttackerId={} rankAttackOld={} rankBeAttackerOld={} rankAttackNow={} rankBeAttackerNow={}.",
                attackerId, beAttackerId, rankAttackOld, rankBeAttackerOld, attacker.getRank(), beAttacker.getRank());
        In_ExchangeRank.Response response = new In_ExchangeRank.Response(attacker.getRank(), beAttacker.getRank());
        this.curSender.tell(response, ActorRef.noSender());
    }

    @Override
    public void setCurSendActorRef(ActorRef sender) {
        this.curSender = sender;
    }


    @Override
    public void setCurContext(ActorContext context) {
        this.context = context;
    }

    @Override
    public void queryRankerLis(int centerOuterRealmId, String playerId, List<Integer> rankList) {
        LogicCheckUtils.validateParam(Integer.class, centerOuterRealmId);
        LogicCheckUtils.validateParam(String.class, playerId);
        LogicCheckUtils.validateParam(List.class, rankList);

        Map<Integer, ArenaCenterRanker> rankToRankerTmp = new HashMap<>();
        ArenaCenter arenaCenter = getArenaCenter(centerOuterRealmId);
        List<Integer> rankListFinal = new ArrayList<>();  // 最终返回的名次列表
        for (Integer rank : rankList) {
            if (rank > arenaCenter.getMaxRank()) {
                rank = arenaCenter.getMaxRank();
            }
            ArenaCenterRanker ranker = getRankerByRank(arenaCenter, rank);
            if (ranker != null) {
                rankListFinal.add(ranker.getRank());
                rankToRankerTmp.put(ranker.getRank(), ranker.clone()); // clone
            }
        }
        In_QueryEnemies.Response response = new In_QueryEnemies.Response(rankToRankerTmp, rankListFinal);
        fillTopTenRankers(response, arenaCenter);
        this.curSender.tell(response, ActorRef.noSender());
    }

    @Override
    public void queryRank(int centerOuterRealmId, String playerId) {
        ArenaCenter arenaCenter = getArenaCenter(centerOuterRealmId);
        int rank = getRankByPlayerId(arenaCenter, playerId);
        In_GetRankToPvpCenter.Response response = new In_GetRankToPvpCenter.Response(rank);
        this.curSender.tell(response, ActorRef.noSender());
    }


    @Override
    public void shouldSettleDaliyRankReward() {
        LOGGER.info("开始结算竞技场排名奖励！");
        cloneAllRankerForSendRewards();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                context.self().tell(new In_GotoSettleDaliyRankReward.Request(), ActorRef.noSender());
            }
        }, delay, period);
    }

    @Override
    public void gotoSettleDaliyRankReward() {
        List<Integer> realmIdLis = new ArrayList<>(centerOuterRealmIdToRankToRanker.keySet());
        Iterator<Integer> it1 = centerOuterRealmIdToRankToRanker.keySet().iterator();
        if (it1.hasNext()) {
            int num = 0;
            int outerRealmId = it1.next();
            LOGGER.info("开始结算竞技场排名奖励！ outerRealmId={} .", outerRealmId);
            Map<Integer, ArenaCenterRanker> rankToRanker = centerOuterRealmIdToRankToRanker.get(outerRealmId);
            Iterator<Integer> it2 = rankToRanker.keySet().iterator();
            while (it2.hasNext()) {
                ArenaCenterRanker ranker = rankToRanker.get(it2.next());
                sendRankRewardsMail(outerRealmId, ranker);
                it2.remove();
                num++;
                if (num >= SEND_MAIL_NUM_EACH_SECOND_EACH_REALMID) {
                    break;
                }
            }
            if (rankToRanker.size() <= 0) {
                it1.remove();
                LOGGER.info("outerRealmId={} 奖励结算完毕.", outerRealmId);
            }
        }
        if (centerOuterRealmIdToRankToRanker.size() <= 0) {
            timer.cancel();
            LOGGER.info("realmIdLis={} 奖励结算完毕.", realmIdLis);
            realmIdLis.clear();
        }
    }


    // 更新排名信息 & 缓存
    private void putRankerAndUpdateCacheDoNotSaveToDB(ArenaCenter arenaCenter, ArenaCenterRanker ranker) {
        if (ranker == null) {
            return;
        }
        if (!ranker.isRobot() && ranker.getRank() > CACHE_COUNT) {
            return;
        }
        arenaCenter.getRankToRanker().put(ranker.getRank(), ranker);
        cacheRankerObj(arenaCenter.getOuterRealmId(), ranker);
    }

    // 更新排名信息 & 缓存
    private void putRankerAndUpdateCacheAndSaveToDB(ArenaCenter arenaCenter, ArenaCenterRanker ranker) {
        if (ranker == null) {
            return;
        }
        saveRanker_ToDB(arenaCenter.getOuterRealmId(), ranker);
        putRankerAndUpdateCacheDoNotSaveToDB(arenaCenter, ranker);
    }


    // 移除排名信息 & 缓存
    private ArenaCenterRanker removeRankerAndUpdateCache(ArenaCenter arenaCenter, ArenaCenterRanker ranker) {
        arenaCenter.getRankToRanker().remove(ranker.getRank());
        removeRankerObjInCache(arenaCenter.getOuterRealmId(), ranker.getPlayerId());
        return ranker;
    }


    /**
     * 缓存[玩家排名对象]
     *
     * @param centerOuterRealmId
     * @param ranker
     */
    private void cacheRankerObj(int centerOuterRealmId, ArenaCenterRanker ranker) {
        Map<String, ArenaCenterRanker> playerIdToRanker = RelationshipCommonUtils.getMapByKey(centerOuterRealmIdToPlayerIdToRanker, centerOuterRealmId);
        playerIdToRanker.put(ranker.getPlayerId(), ranker);
    }

    /**
     * 从缓存中删除[玩家排名对象]
     *
     * @param centerOuterRealmId
     * @param playerId
     */
    private void removeRankerObjInCache(int centerOuterRealmId, String playerId) {
        Map<String, ArenaCenterRanker> playerIdToRanker = RelationshipCommonUtils.getMapByKey(centerOuterRealmIdToPlayerIdToRanker, centerOuterRealmId);
        playerIdToRanker.remove(playerId);
    }


    /**
     * 缓存里面是否有[玩家排名对象]
     *
     * @param centerOuterRealmId
     * @param playerId
     * @return
     */
    private boolean containsRankerObjInCache(int centerOuterRealmId, String playerId) {
        Map<String, ArenaCenterRanker> playerIdToRanker = RelationshipCommonUtils.getMapByKey(centerOuterRealmIdToPlayerIdToRanker, centerOuterRealmId);
        return playerIdToRanker.containsKey(playerId);
    }

    /**
     * 缓存里面获取[玩家排名对象]
     *
     * @param centerOuterRealmId
     * @param playerId
     * @return
     */
    private ArenaCenterRanker getRankerObjInCache(int centerOuterRealmId, String playerId) {
        Map<String, ArenaCenterRanker> playerIdToRanker = RelationshipCommonUtils.getMapByKey(centerOuterRealmIdToPlayerIdToRanker, centerOuterRealmId);
        return playerIdToRanker.get(playerId);
    }

    /**
     * 获取玩家排名
     *
     * @param arenaCenter
     * @param playerId
     * @return
     */
    private int getRankByPlayerId(ArenaCenter arenaCenter, String playerId) {
        int centerOuterRealmId = arenaCenter.getOuterRealmId();
        if (containsRankerObjInCache(centerOuterRealmId, playerId)) {
            ArenaCenterRanker ranker = getRankerObjInCache(centerOuterRealmId, playerId);
            LOGGER.debug("查询玩家排名:在缓存中 playerId={} rank={} .", playerId, ranker.getRank());
            return ranker.getRank();
        } else { // 不在缓存去库里查询
            ArenaCenterRanker ranker = queryRankerByPlayerId_FormDB(centerOuterRealmId, playerId);
            if (ranker != null) {
                LOGGER.debug("查询玩家排名:不在缓存中，去数据库中查询 playerId={} rank={} .", playerId, ranker.getRank());
                putRankerAndUpdateCacheDoNotSaveToDB(arenaCenter, ranker);  // 只缓存不存库
                return ranker.getRank();
            }
        }
        LOGGER.debug("查询玩家排名:排名系统(缓存&数据库)中没有该玩家排名信息 playerId={} ... ", playerId);
        return -1;
    }

    /**
     * 通过排名获取[玩家排名对象]
     *
     * @param arenaCenter
     * @param rank
     * @return
     */
    private ArenaCenterRanker getRankerByRank(ArenaCenter arenaCenter, int rank) {
        int centerOuterRealmId = arenaCenter.getOuterRealmId();
        if (arenaCenter.getRankToRanker().containsKey(rank)) {
            LOGGER.debug("通过rank={} 查询[玩家排名对象]:在缓存中 ... ", rank);
            return arenaCenter.getRankToRanker().get(rank);
        }
        LOGGER.debug("通过rank={} 查询[玩家排名对象]:不在缓存中,去数据库中查询 ... ", rank);
        ArenaCenterRanker ranker = queryRankerByRank_FormDB(centerOuterRealmId, rank);
        putRankerAndUpdateCacheAndSaveToDB(arenaCenter, ranker);
        return ranker;
    }


    /**
     * 最后一名为固定的机器人，新来的插入他前面。
     *
     * @param arenaCenter
     * @return 更新之前的排名
     */
    private int updateMaxRanker(ArenaCenter arenaCenter) {
        int preMaxRank = arenaCenter.getMaxRank();
        LOGGER.debug("竞技场:当前最大排名为:{}", preMaxRank);
        int nextMaxRank = preMaxRank + 1;
        ArenaCenterRanker ranker = getRankerByRank(arenaCenter, preMaxRank);
        removeRankerAndUpdateCache(arenaCenter, ranker);                                                 // 先移除,空出的位置留给新来的
        ranker.setRank(nextMaxRank);
        putRankerAndUpdateCacheAndSaveToDB(arenaCenter, ranker);                                         // 再添加 & 更新缓存
        arenaCenter.setMaxRank(nextMaxRank);
        DBUtils.saveStringPojo(arenaCenter.getOuterRealmId(), arenaCenter);
        LOGGER.debug("竞技场:更新最大排名为:{}", arenaCenter.getMaxRank());
        return preMaxRank;
    }


    /**
     * 获取竞技场Center
     *
     * @param centerOuterRealmId
     * @return
     */
    private ArenaCenter getArenaCenter(int centerOuterRealmId) {
        if (centerOuterRealmIdToArenaCenter.containsKey(centerOuterRealmId)) {
            return centerOuterRealmIdToArenaCenter.get(centerOuterRealmId);
        }
        // todo 完全从redis中移除
        ArenaCenter arenaCenter = DBUtils.getStringPojo(centerOuterRealmId, ArenaCenter.class);
        if (arenaCenter == null) {
            if (InitRealmCreatedTargetsDB.containsTopLevelPojo(centerOuterRealmId, ArenaCenter.class.getSimpleName())) {
                throw new BusinessLogicMismatchConditionException("ArenaCenter已经创建过，但是获取失败，请检查！centerOuterRealmId=" + centerOuterRealmId);
            }
            arenaCenter = createArenaCenter(centerOuterRealmId);
            initArenaCenter(arenaCenter);
            DBUtils.saveStringPojo(centerOuterRealmId, arenaCenter);
            InitRealmCreatedTargetsDB.update(centerOuterRealmId, arenaCenter);
        }
        loadTopNRankerIntoCache(arenaCenter);
        centerOuterRealmIdToArenaCenter.put(centerOuterRealmId, arenaCenter);
        return arenaCenter;
    }


    /**
     * 创建新的Center
     *
     * @param centerOuterRealmId
     * @return
     */
    private ArenaCenter createArenaCenter(int centerOuterRealmId) {
        ArenaCenter arenaCenter = new ArenaCenter();
        arenaCenter.setAutoId(ObjectId.get().toString());
        arenaCenter.setOuterRealmId(centerOuterRealmId);
        return arenaCenter;
    }

    /**
     * 初始化机器人
     *
     * @param arenaCenter
     */
    private void initArenaCenter(ArenaCenter arenaCenter) {
        long time1 = System.currentTimeMillis();
        int centerOuterRealmId = arenaCenter.getOuterRealmId();
        List<Table_Robot_Row> rowList = RootTc.get(Table_Robot_Row.class).values();
        int maxRank = 0; // 最后一名机器人排名
        List<ArenaCenterRanker> rankers = new ArrayList<>();
        for (Table_Robot_Row row : rowList) {
            ArenaCenterRanker ranker = new ArenaCenterRanker(ObjectId.get().toString(), row.getId(), centerOuterRealmId, row.getId());
            putRankerAndUpdateCacheDoNotSaveToDB(arenaCenter, ranker);
            maxRank = row.getId();
            rankers.add(ranker);
        }
        arenaCenter.setMaxRank(maxRank);
        saveRankerList_ToDB(centerOuterRealmId, rankers); // 机器人存库
        long time2 = System.currentTimeMillis();
        LOGGER.info("centerOuterRealmId={} 初始化机器人{}个耗时:{}毫秒. ", centerOuterRealmId, rowList.size(), (time2 - time1));
    }

    /**
     * 发送前10名玩家
     *
     * @param response
     * @param arenaCenter
     * @return
     */
    private In_QueryEnemies.Response fillTopTenRankers(In_QueryEnemies.Response response, ArenaCenter arenaCenter) {
        for (int i = MagicNumbers.DEFAULT_ONE; i <= MagicNumbers.DEFAULT_TEN; i++) {
            ArenaCenterRanker arenaCenterRanker = getRankerByRank(arenaCenter, i).clone(); // clone
            if (arenaCenterRanker != null) {
                response.getTopTenRankers().put(i, arenaCenterRanker);
            }
        }
        return response;
    }

    private ArenaCenterRanker queryRankerByPlayerId_FormDB(int centerOuterRealmId, String playerId) {
        return getDao(centerOuterRealmId).findOne(playerId);
    }

    private ArenaCenterRanker queryRankerByRank_FormDB(int centerOuterRealmId, int rank) {
        return getDao(centerOuterRealmId).queryByRank(rank);
    }


    private void saveRanker_ToDB(int centerOuterRealmId, ArenaCenterRanker ranker) {
        getDao(centerOuterRealmId).insertIfExistThenReplace(ranker);
    }

    private void saveRankerList_ToDB(int centerOuterRealmId, List<ArenaCenterRanker> rankers) {
        getDao(centerOuterRealmId).mutliReplace(rankers);
    }

    private void loadTopNRankerIntoCache(ArenaCenter arenaCenter) {
        long time1 = System.currentTimeMillis();
        int centerOuterRealmId = arenaCenter.getOuterRealmId();
        List<ArenaCenterRanker> rankerList = getDao(centerOuterRealmId).queryRankTopN_NotRobot(CACHE_COUNT);
        rankerList.forEach(ranker -> {
            putRankerAndUpdateCacheDoNotSaveToDB(arenaCenter, ranker);
        });
        long time2 = System.currentTimeMillis();
        LOGGER.info("centerOuterRealmId={} 加载竞技场(非机器人)Top{}个耗时:{}毫秒. ", centerOuterRealmId, rankerList.size(), (time2 - time1));
    }


    /**
     * 获取RealmCreatedTargetsDao
     *
     * @param centerOuterRealmId
     * @return
     */
    private static ArenaCenterRankerDao getDao(int centerOuterRealmId) {
        if (centerOuterRealmIdToDao.containsKey(centerOuterRealmId)) {
            return centerOuterRealmIdToDao.get(centerOuterRealmId);
        }
        ArenaCenterRankerDao dao = GlobalInjector.getInstance(ArenaCenterRankerDao.class);
        dao.init(MONGO_DB_CLIENT, MagicWords_Redis.TopLevelPojo_Game_Prefix + centerOuterRealmId);
        centerOuterRealmIdToDao.put(centerOuterRealmId, dao);
        return dao;
    }


    /**
     * clone所有的[玩家排名对象],作为发奖励用
     */
    private void cloneAllRankerForSendRewards() {
        int maxRank = AllServerConfig.Pvp_DaliyCalcuMaxRank.getConfig();
        centerOuterRealmIdToArenaCenter.forEach((centerOuterRealmId, arenaCenter) -> {
            arenaCenter.getRankToRanker().forEach((rank, rankerObj) -> {
                if (!rankerObj.isRobot() && rankerObj.getRank() <= maxRank) {
                    rankerObj = rankerObj.clone();
                    Map<Integer, ArenaCenterRanker> rankToRanker = RelationshipCommonUtils.getMapByKey(centerOuterRealmIdToRankToRanker, centerOuterRealmId);
                    rankToRanker.put(rankerObj.getRank(), rankerObj);
                }
            });
        });
    }


    /**
     * 给玩家发送排名奖励
     *
     * @param centerOuterRealmId
     * @param ranker
     */
    private void sendRankRewardsMail(int centerOuterRealmId, ArenaCenterRanker ranker) {
        IdMaptoCount idMaptoCount = Table_RankDailyReward_Row.getArenaRankRewards(ranker.getRank());
        SysMail sysMail = MailsCtrlUtils.createSysMail(MagicNumbers.SYSTEM_MAIL_ID_ARENA_DALIY_RANK_REWARDS, idMaptoCount, ranker.getRank());
        In_AddGmMail.Request addGmMailRequest = new In_AddGmMail.Request(ranker.getPlayerId(), centerOuterRealmId, sysMail);
        CheckPlayerOnlineMsgRequest<Request> request = new CheckPlayerOnlineMsgRequest<>(ranker.getPlayerId(), addGmMailRequest);
        ClusterMessageSender.sendMsgToPath(context, request, ActorSystemPath.WS_GameServer_Selection_World);
        LOGGER.info("开始结算竞技场排名奖励！发送排名奖励邮件. centerOuterRealmId={} playerId={} rank={} .", centerOuterRealmId, ranker.getPlayerId(), ranker.getRank());
    }


    @Override
    public void sendPrivateMsg(PrivateMsg privateMsg) {

    }
}
