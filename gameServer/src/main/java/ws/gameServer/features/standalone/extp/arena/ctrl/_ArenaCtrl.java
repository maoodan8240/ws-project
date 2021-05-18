package ws.gameServer.features.standalone.extp.arena.ctrl;

import com.google.protobuf.ByteString;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.gameServer.features.standalone.actor.arenaCenter.msg.In_AddPlayerToPvpCenter;
import ws.gameServer.features.standalone.actor.arenaCenter.msg.In_ExchangeRank;
import ws.gameServer.features.standalone.actor.arenaCenter.msg.In_GetRankToPvpCenter;
import ws.gameServer.features.standalone.actor.arenaCenter.msg.In_QueryEnemies;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.arena.msg.In_AddNewBattleRecords;
import ws.gameServer.features.standalone.extp.arena.utils.ArenaCtrlUtils;
import ws.gameServer.features.standalone.extp.arena.utils.ArenaOnlineOfflineUtils;
import ws.gameServer.features.standalone.extp.arena.utils.ArenaProtoUtils;
import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;
import ws.gameServer.features.standalone.extp.itemBag.ItemBagExtp;
import ws.gameServer.features.standalone.extp.itemBag.ctrl.ItemBagCtrl;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.resourcePoint.ResourcePointExtp;
import ws.gameServer.features.standalone.extp.resourcePoint.ctrl.ResourcePointCtrl;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.BattleEnumsProtos.BattleResultTypeEnum;
import ws.protos.BattleProtos.Sm_Battle_BackData;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.PvpProtos.Sm_Pvp;
import ws.protos.PvpProtos.Sm_Pvp.Action;
import ws.protos.PvpProtos.Sm_Pvp_Enemy;
import ws.protos.errorCode.ErrorCodeProtos.ErrorCodeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.base.PojoUtils;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.CheckPlayerOnlineMsgRequest;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.exception.CmMessageIllegalArgumentException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.tableRows.Table_Consume_Row;
import ws.relationship.table.tableRows.Table_PointReward_Row;
import ws.relationship.table.tableRows.Table_RankDailyReward_Row;
import ws.relationship.topLevelPojos.battleRecord.BattleRecord;
import ws.relationship.topLevelPojos.pvp.arena.Arena;
import ws.relationship.topLevelPojos.pvp.arena.ArenaRecord;
import ws.relationship.topLevelPojos.pvp.arenaCenter.ArenaCenterRanker;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.ActorMsgSynchronizedExecutor;
import ws.relationship.utils.ClusterMessageSender;
import ws.relationship.utils.ProtoUtils;
import ws.relationship.utils.RandomUtils;
import ws.relationship.utils.SimplePojoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class _ArenaCtrl extends AbstractPlayerExteControler<Arena> implements ArenaCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_ArenaCtrl.class);

    private ResourcePointExtp resourcePointExtp;
    private ResourcePointCtrl resourcePointCtrl;
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private ItemBagExtp itemBagExtp;
    private ItemBagCtrl itemBagCtrl;
    private Map<Integer, ArenaCenterRanker> topTenPlayer = new HashMap<>(); //前10排位信息
    private Map<Integer, ArenaCenterRanker> rankToPlayer = new HashMap<>(); //本地缓存排位信息


    @Override
    public void _initReference() throws Exception {
        resourcePointExtp = getPlayerCtrl().getExtension(ResourcePointExtp.class);
        resourcePointCtrl = resourcePointExtp.getControlerForQuery();
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        itemBagExtp = getPlayerCtrl().getExtension(ItemBagExtp.class);
        itemBagCtrl = itemBagExtp.getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
        target.getIntegralRewardsList().clear();
        target.getWorshipPlayerIdList().clear();
        target.getRecords().clear(); // 清空战报

        target.getBase().setChallengeTimes(0);
        target.getBase().setRefreshTimes(0);
        target.getBase().setBuyChallengeTimes(0);
        target.getBase().setBuyRefreshTimes(0);
        target.getBase().setCdEndTime(0);
        target.getBase().setIntegral(0);
    }

    @Override
    public void sync() {
        _logicCheck_ArenaIsOpen();
        int curRank = getCurRank(); // 新用户返回-1
        curRank = tryAddPlayerToPvpCenter(curRank);
        if (curRank <= 0) {
            String msg = "加入竞技场中心失败！";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        _setMaxRank(curRank);
        gotoRefreshCache(curRank);
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();

        SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, Sm_Pvp.Action.RESP_SYNC, (b, br) -> {
            b.setPvpBase(ArenaProtoUtils.create_Sm_Pvp_Base(target.getBase()));
            b.addAllEnemies(createAllEnemyInfo());
            b.addAllEnemiesTopTen(createAllTopTenEnemyInfo());
            b.setSelf(ProtoUtils.create_Sm_Common_SimplePlayer_Pvp(simplePlayer));
            b.addAllRecords(ArenaProtoUtils.create_Sm_Pvp_Record_List(target.getRecords()));
            b.addAllRankRewardsList(target.getRankRewardsList());
            b.addAllIntegralRewardsList(target.getIntegralRewardsList());
            b.addAllWorshipPlayerIdList(target.getWorshipPlayerIdList());
        });
        save();
    }


    @Override
    public void refresh() {
        Sm_Pvp.Action action = Sm_Pvp.Action.RESP_REFRESH;
        _logicCheck_ArenaIsOpen();
        IdMaptoCount needFresh = new IdMaptoCount();
        if (!_hasFreeTimes()) {
            // 自动购买刷新次数
            IdMaptoCount needVipMoney = _mathRefreshTimesVipMoney();
            LogicCheckUtils.canRemove(itemIoCtrl, needVipMoney);
            needFresh.addAll(itemIoExtp.getControlerForUpdate(action).removeItem(needVipMoney));
            _saveBuyRefreshTimes();
        }
        int curRank = getCurRank();
        _refreshAndQuery(curRank);
        _saveRefreshTimes();
        SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, action, (b, br) -> {
            b.setPvpBase(ArenaProtoUtils.create_Sm_Pvp_Base(target.getBase()));
            b.addAllEnemies(createAllEnemyInfo());
            b.addAllEnemiesTopTen(createAllTopTenEnemyInfo());
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(needFresh), br);
        });
        save();
    }


    @Override
    public void clearCd() {
        _logicCheck_ArenaIsOpen();
        Sm_Pvp.Action action = Sm_Pvp.Action.RESP_CLEAR_CD;
        if (_isCdReady()) {
            String msg = "not need clear";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int clearCdConsume = AllServerConfig.Pvp_Clean_CD_Money.getConfig();
        IdAndCount needVipMoney = new IdAndCount(ResourceTypeEnum.RES_VIPMONEY.getNumber(), clearCdConsume);
        LogicCheckUtils.canRemove(itemIoCtrl, needVipMoney);
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(action).removeItem(needVipMoney);
        _clearCd();
        SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, action, (b, br) -> {
            b.setPvpBase(ArenaProtoUtils.create_Sm_Pvp_Base(target.getBase()));
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
        });
        save();
    }


    @Override
    public void modifyDeclaration(String newDeclaration) {
        _logicCheck_ArenaIsOpen();
        if (StringUtils.isBlank(newDeclaration)) {
            String msg = "declaration can not be empty";
            throw new CmMessageIllegalArgumentException(msg);
        }
        //TODO 判断字符长度
        if (_isSameDeclaration(newDeclaration)) {
            String msg = "不能使用相同的宣言！";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        target.getBase().setDeclaration(newDeclaration);
        SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, Sm_Pvp.Action.RESP_MODIFY_DECLARATION, (b, br) -> {
            b.setSelf(ProtoUtils.create_Sm_Common_SimplePlayer_Pvp(getPlayerCtrl().getSimplePlayerAfterUpdate()));
        });
        save();
    }

    @Override
    public void modifyPvpIcon(int icon) {
        _logicCheck_ArenaIsOpen();
        LogicCheckUtils.validateParam(Integer.class, icon);
        if (_isSameIcon(icon)) {
            String msg = "不能替换相同的头像，是不是撒";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        target.getBase().setPvpIcon(icon);
        SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, Sm_Pvp.Action.RESP_MODIFY_PVP_ICON, (b, br) -> {
            b.setSelf(ProtoUtils.create_Sm_Common_SimplePlayer_Pvp(getPlayerCtrl().getSimplePlayerAfterUpdate()));
        });
        save();
    }


    @Override
    public void buyAttackTimes() {
        _logicCheck_ArenaIsOpen();
        Sm_Pvp.Action action = Sm_Pvp.Action.RESP_BUY_TIMES;
        if (_hasChallengeTimes()) {
            String msg = "挑战次数没有用尽，不能购买";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdAndCount needVipMoney = _mathBuyChallengeTimesVipMoney();
        LogicCheckUtils.canRemove(itemIoCtrl, needVipMoney);
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(action).removeItem(needVipMoney);
        _saveBuyChallengeTimes();
        SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, action, (b, br) -> {
            b.setPvpBase(ArenaProtoUtils.create_Sm_Pvp_Base(target.getBase()));
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
        });
        save();
    }


    @Override
    public void challenge(int rank, String playerId) {
        _logicCheck_ArenaIsOpen();
        LogicCheckUtils.validateParam(Integer.class, rank);
        LogicCheckUtils.validateParam(String.class, playerId);
        Sm_Pvp.Action action = Sm_Pvp.Action.RESP_CHALLENGE;
        if (!_isCanChallenge()) {
            String msg = "挑战次数用完了";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!_isCdReady()) {
            String msg = "CD冷却中";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (getPlayerCtrl().getPlayerId().equals(playerId)) {
            String msg = "不能挑战自己";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int curRank = getCurRank();
        gotoRefreshCache(curRank);
        if (!_isAttackPlayerInCacheList(rank, playerId)) {
            // 传入的挑战信息在本地找不到(玩家的挑战排行榜不新了)，将本地排行信息同步给玩家
            SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, Sm_Pvp.Action.RESP_CHALLENGE, (b, br) -> {
                b.setSelf(ProtoUtils.create_Sm_Common_SimplePlayer_Pvp(getPlayerCtrl().getSimplePlayerAfterUpdate()));
                b.addAllEnemies(createAllEnemyInfo());
                b.addAllEnemiesTopTen(createAllTopTenEnemyInfo());
                br.setErrorCode(ErrorCodeEnum.PVP_RANK_MODIFY);
                br.setResult(false);
            });
            String msg = "传入的挑战信息在本地找不到，将本地排行信息同步给玩家";
            LOGGER.warn(msg);
            save();
            return;
        }
        ArenaCenterRanker beAttackRanker = rankToPlayer.get(rank);
        Sm_Battle_BackData backData = _fight(beAttackRanker); // 如果胜利,beAttackRanker的名次会被修改
        ArenaRecord arenaRecord = create_BattleRecord(beAttackRanker, backData);
        target.getRecords().add(arenaRecord);
        gotoRefreshCache(curRank);
        _saveChallengeTimesAndCd();
        SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, action, (b, br) -> {
            b.setPvpBase(ArenaProtoUtils.create_Sm_Pvp_Base(target.getBase()));
            b.setBackData(backData);
            b.setSelf(ProtoUtils.create_Sm_Common_SimplePlayer_Pvp(getPlayerCtrl().getSimplePlayerAfterUpdate()));
            b.addAllEnemies(createAllEnemyInfo());
            b.addAllEnemiesTopTen(createAllTopTenEnemyInfo());
            b.setBeAttacker(_create_Sm_Pvp_Enemy(beAttackRanker));
        });
        save();
        sendToAddNewBattleRecords(arenaRecord, beAttackRanker);
        saveBattleRecord(arenaRecord.getRecordId(), backData);
        statisticsDaliyAttackTimes();
    }


    @Override
    public void maxWorship() {
        Sm_Pvp.Action action = Sm_Pvp.Action.RESP_MAX_WORSHIP;
        _logicCheck_ArenaIsOpen();
        List<String> canWorshipPlayerIdList = _getCanWorshipPlayer();
        if (canWorshipPlayerIdList.size() <= 0) {
            String msg = "没有可以膜拜的玩家";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdAndCount reward = ArenaCtrlUtils.getWorshipReward(canWorshipPlayerIdList.size());
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(action).addItem(reward);
        target.getWorshipPlayerIdList().addAll(canWorshipPlayerIdList);
        SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, action, (b, br) -> {
            b.addAllWorshipPlayerIdList(target.getWorshipPlayerIdList());
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
        });
        save();
    }


    @Override
    public void getRankRewards(int rankRewards) {
        LogicCheckUtils.validateParam(Integer.class, rankRewards);
        _logicCheck_ArenaIsOpen();
        int curMaxRank = target.getBase().getMaxRank();
        if (curMaxRank <= 0) {
            String msg = String.format("当前curMaxRank=%s <=0 不能领取任何奖励 (数值越小排名越大) ", curMaxRank);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (curMaxRank > rankRewards) {
            String msg = String.format("排名没达到不能领取这个奖励 curMaxRank=%s(数值越小排名越大) rankRewards=%s", curMaxRank, rankRewards);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Table_PointReward_Row rewardRow = Table_PointReward_Row.getArenaRankRewardRow(rankRewards);
        if (_hasGetRankReward(rankRewards)) {
            String msg = String.format("这个排名已经领取过了 rankRewards=%s", rankRewards);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount toAdd = rewardRow.getRewards(getPlayerCtrl().getCurLevel());
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Sm_Pvp.Action.RESP_GET_RANK_REWARDS).addItem(toAdd);
        target.getRankRewardsList().add(rankRewards);
        SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, Sm_Pvp.Action.RESP_GET_RANK_REWARDS, (b, br) -> {
            b.addAllRankRewardsList(target.getRankRewardsList());
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
        });
        save();
    }

    @Override
    public void getIntegralRewards(int integralRewards) {
        LogicCheckUtils.validateParam(Integer.class, integralRewards);
        _logicCheck_ArenaIsOpen();
        int curIntegral = target.getBase().getIntegral();
        if (curIntegral < integralRewards) {
            String msg = String.format("积分没达到不能领取这个奖励 integralRewards=%s curIntegral=%s !", integralRewards, curIntegral);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Table_RankDailyReward_Row row = Table_RankDailyReward_Row.getArenaPointRow(integralRewards);
        int curVipLv = getPlayerCtrl().getTarget().getPayment().getVipLevel();
        if (row.getLimitVip() > curVipLv) {
            String msg = String.format("该积分行vip限制不能领取，不能领取这个奖励 integralRewards=%s curVipLv=%s limitVip=%s !", integralRewards, curVipLv, row.getLimitVip());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (_hasGetIntegralReward(integralRewards)) {
            String msg = String.format("这个积分奖励已经领取过了 integralRewards=%s", integralRewards);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Sm_Pvp.Action.RESP_GET_INTEGRAL_REWARDS).addItem(row.getReward());
        target.getIntegralRewardsList().add(integralRewards);
        SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, Sm_Pvp.Action.RESP_GET_INTEGRAL_REWARDS, (b, br) -> {
            b.addAllIntegralRewardsList(target.getIntegralRewardsList());
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
        });
        save();
    }


    @Override
    public void maxGetIntegralRewards() {
        Sm_Pvp.Action action = Sm_Pvp.Action.RESP_MAX_GET_INTEGRAL_REWARDS;
        _logicCheck_ArenaIsOpen();
        List<Table_RankDailyReward_Row> rowList = _getCanGetIntegralRewards();
        if (rowList.size() == 0) {
            String msg = "没有可以领取的积分奖励行了";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        _addIntegralPoint(rowList);
        IdMaptoCount rewards = ArenaCtrlUtils.getAreanPointReward(rowList);
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(action).addItem(rewards);
        SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, action, (b, br) -> {
            b.addAllIntegralRewardsList(target.getIntegralRewardsList());
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
        });
        save();
    }

    @Override
    public void worship(String playerId) {
        Sm_Pvp.Action action = Action.RESP_WORSHIP;
        if (target.getBase().getLastRefreshRank() <= MagicNumbers.DEFAULT_TEN) {
            String msg = String.format("已经是前10的玩家了，不能膜拜！ LastRefreshRank=%s ", target.getBase().getLastRefreshRank());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        List<String> canWorshipPlayerIdList = _getCanWorshipPlayer();
        if (canWorshipPlayerIdList.size() <= 0) {
            String msg = "没有可以膜拜的玩家";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!canWorshipPlayerIdList.contains(playerId)) {
            String msg = String.format("该玩家=%s不在Top10中，不能膜拜", playerId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdAndCount reward = ArenaCtrlUtils.getWorshipReward(1);
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(action).addItem(reward);
        target.getWorshipPlayerIdList().add(playerId);
        SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, action, (b, br) -> {
            b.addAllWorshipPlayerIdList(target.getWorshipPlayerIdList());
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
        });
        save();
    }

    @Override
    public void displayBattleRecords(String recordId) {
        Sm_Pvp.Action action = Action.RESP_DISPLAY_BATTLE_RECORDS;
        LogicCheckUtils.validateParam(String.class, recordId);
        if (!ObjectId.isValid(recordId)) {
            String msg = String.format("不是一个合法的战斗脚本! Id=%s", recordId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Sm_Battle_BackData backData = getBattleRecord(recordId);
        if (backData == null) {
            String msg = String.format("获取战斗脚本失败! Id=%s", recordId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, action, (b, br) -> {
            b.setBackData(backData);
        });
    }

    @Override
    public void getBattleRecords() {
        Sm_Pvp.Action action = Action.RESP_GET_BATTLE_RECORDS;
        SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, action, (b, br) -> {
            b.addAllRecords(ArenaProtoUtils.create_Sm_Pvp_Record_List(target.getRecords()));
        });
    }

    @Override
    public void queryRank() {
        Sm_Pvp.Action action = Action.RESP_QUERY_RANK;
        List<Integer> rankLis = new ArrayList<>();
        // 11-50名
        for (int i = MagicNumbers.DEFAULT_TEN + 1; i <= MagicNumbers.ARENA_RANK_QUERY_TOP_50; i++) {
            rankLis.add(i);
        }
        List<Sm_Pvp_Enemy> enemyList = new ArrayList<>();
        In_QueryEnemies.Response response = _query(rankLis);
        _create_Sm_Pvp_EnemyList(enemyList, response.getRankToRanker());
        _create_Sm_Pvp_EnemyList(enemyList, response.getTopTenRankers());
        SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, action, (b, br) -> {
            b.addAllRankList(enemyList);
        });
    }

    @Override
    public void onAddNewBattleRecords(ArenaRecord arenaRecord) {
        Sm_Pvp.Action action = Action.RESP_BEATTACK;
        ArenaOnlineOfflineUtils.onAddNewBattleRecords_Online(arenaRecord, target);
        int rank = getCurRank();
        SenderFunc.sendInner(this, Sm_Pvp.class, Sm_Pvp.Builder.class, action, (b, br) -> {
            b.setNewRankAfterBeAttack(rank);
        });
        save();
    }


    /*
============================================================以下为内部方法==============================================================
 */


    /**
     * 尝试添加到竞技场
     */
    private int tryAddPlayerToPvpCenter(int curRank) {
        if (curRank <= 0 || target.getEnemiesRank().size() == 0) { // 在竞技场中还没有排名信息
            In_AddPlayerToPvpCenter.Request request = new In_AddPlayerToPvpCenter.Request(getPlayerCtrl().getOuterRealmId(), getPlayerCtrl().getInnerRealmId(), getPlayerCtrl().getPlayerId(), false);
            In_AddPlayerToPvpCenter.Response response1 = _sendMsgToPvpCenter(request);
            curRank = response1.getNewRank();
        }
        return curRank;
    }

    private void _logicCheck_ArenaIsOpen() {
        if (!_isArenaOpen()) {
            String msg = "等级未到，功能未开启";
            throw new BusinessLogicMismatchConditionException(msg);
        }
    }

    private void _saveBuyChallengeTimes() {
        target.getBase().setBuyChallengeTimes(target.getBase().getBuyChallengeTimes() + 1);
        _clearCd();
    }


    /**
     * 刷新排名缓存列表
     *
     * @param curRank
     */
    private void gotoRefreshCache(int curRank) {
        if (_isRankNeedRefresh(curRank)) {
            _refreshAndQuery(curRank);
        } else {
            //查询本地排行榜在竞技场中心的最新排位的玩家
            In_QueryEnemies.Response response = _query(target.getEnemiesRank());
            _refreshCache(response.getRankToRanker(), response.getTopTenRankers());
        }
    }


    private Sm_Battle_BackData _fight(ArenaCenterRanker beAttackRanker) {
        String beAttackPlayerId = beAttackRanker.getPlayerId();
        boolean robot = beAttackRanker.isRobot();
        Sm_Battle_BackData backData;
        if (robot) {
            backData = ArenaCtrlUtils.runArenaBattle_Robot(getPlayerCtrl(), beAttackRanker.getRobotId());
        } else {
            // todo ranker.getOuterRealmId() 还是 inner
            backData = ArenaCtrlUtils.runArenaBattle_Player(getPlayerCtrl(), beAttackRanker.getOuterRealmId(), beAttackPlayerId);
        }
        if (backData.getResult().getRsType() == BattleResultTypeEnum.BATTLE_RS_VICTORY) { // 胜利
            In_ExchangeRank.Request request = new In_ExchangeRank.Request(getPlayerCtrl().getInnerRealmId(), getPlayerCtrl().getPlayerId(), beAttackPlayerId);
            In_ExchangeRank.Response response = _sendMsgToPvpCenter(request);
            int nowRank = response.getAttackNewRank();
            beAttackRanker.setRank(response.getBeAttackNewRank());
            _battleWinSave(nowRank);
        } else {
            _battleFailSave();
        }
        return backData;
    }


    private void _refreshAndQuery(int curRank) {
        List<Integer> newRankList = _randomRankList(curRank);
        In_QueryEnemies.Response response = _query(newRankList);
        _refreshEnemiesList(response.getRankListFinal(), curRank);
        _refreshCache(response.getRankToRanker(), response.getTopTenRankers());
    }

    private List<Sm_Pvp_Enemy> createAllEnemyInfo() {
        List<Sm_Pvp_Enemy> enemyList = new ArrayList<>();
        // 随机排名
        _create_Sm_Pvp_EnemyList(enemyList, this.rankToPlayer);
        return enemyList;
    }


    private List<Sm_Pvp_Enemy> createAllTopTenEnemyInfo() {
        List<Sm_Pvp_Enemy> enemyList = new ArrayList<>();
        // 前10的排名
        _create_Sm_Pvp_EnemyList(enemyList, this.topTenPlayer);
        return enemyList;
    }

    private void _create_Sm_Pvp_EnemyList(List<Sm_Pvp_Enemy> enemyList, Map<Integer, ArenaCenterRanker> rankToPlayer) {
        fillRobotEnemyInfo(enemyList, rankToPlayer);
        List<String> playerIdList = _getPlayerIdByRankToPlayer(rankToPlayer);
        Map<String, SimplePlayer> idToSimplePlayer = SimplePojoUtils.querySimplePlayerLisByIds(playerIdList, getPlayerCtrl().getSimplePlayerAfterUpdate().getOutRealmId());
        _fillRealPlayerEnemyInfo(enemyList, idToSimplePlayer, rankToPlayer);
        playerIdList.clear();
        idToSimplePlayer.clear();
    }


    private Sm_Pvp_Enemy _create_Sm_Pvp_Enemy(ArenaCenterRanker rankerObj) {
        if (rankerObj.isRobot()) {
            return ArenaProtoUtils.create_Sm_Pvp_Enemy(rankerObj.getPlayerId(), rankerObj.getRobotId(), rankerObj.getRank());
        } else {
            SimplePlayer simplePlayer = SimplePojoUtils.querySimplePlayerById(rankerObj.getPlayerId(), rankerObj.getOuterRealmId());
            if (simplePlayer != null) {
                return ArenaProtoUtils.create_Sm_Pvp_Enemy(simplePlayer);
            }
        }
        return null;
    }

    private void fillRobotEnemyInfo(List<Sm_Pvp_Enemy> enemyList, Map<Integer, ArenaCenterRanker> rankToPlayer) {
        rankToPlayer.forEach((rank, rankerObj) -> {
            if (rankerObj.isRobot()) {
                enemyList.add(ArenaProtoUtils.create_Sm_Pvp_Enemy(rankerObj.getPlayerId(), rankerObj.getRobotId(), rankerObj.getRank()));
            }
        });
    }


    private List<String> _getPlayerIdByRankToPlayer(Map<Integer, ArenaCenterRanker> rankToPlayer) {
        List<String> playerIdList = new ArrayList<>();
        for (Entry<Integer, ArenaCenterRanker> entry : rankToPlayer.entrySet()) {
            if (entry.getValue().isRobot()) { // 过滤掉机器人的id
                continue;
            }
            playerIdList.add(entry.getValue().getPlayerId());
        }
        return playerIdList;
    }

    private void _fillRealPlayerEnemyInfo(List<Sm_Pvp_Enemy> enemyList, Map<String, SimplePlayer> idToSimplePlayer, Map<Integer, ArenaCenterRanker> rankToPlayer) {
        for (Entry<Integer, ArenaCenterRanker> entry : rankToPlayer.entrySet()) {
            ArenaCenterRanker ranker = entry.getValue();
            if (ranker.isRobot()) {
                continue;
            }
            if (idToSimplePlayer.containsKey(ranker.getPlayerId())) {
                SimplePlayer simplePlayer = idToSimplePlayer.get(ranker.getPlayerId());
                simplePlayer.setPvpRank(ranker.getRank());
                enemyList.add(ArenaProtoUtils.create_Sm_Pvp_Enemy(simplePlayer));
            }
        }
    }

    /**
     * 是否同最后一次刷新排名相同
     *
     * @param curRank
     * @return
     */

    private boolean _isRankNeedRefresh(int curRank) {
        return curRank != target.getBase().getLastRefreshRank() || target.getEnemiesRank().size() == 0;
    }


    /**
     * 添加领取的积分点
     *
     * @param canGetRowList
     */
    private void _addIntegralPoint(List<Table_RankDailyReward_Row> canGetRowList) {
        for (Table_RankDailyReward_Row row : canGetRowList) {
            target.getIntegralRewardsList().add(row.getPoint());
        }
    }

    /**
     * 是否还有战次数
     *
     * @return
     */
    private boolean _hasChallengeTimes() {
        int maxAttackTimes = AllServerConfig.Pvp_Challenge_Times.getConfig();
        return target.getBase().getChallengeTimes() < maxAttackTimes;
    }


    /**
     * 是否在本地缓存中
     *
     * @param rank
     * @param playerId
     * @return
     */
    private boolean _isAttackPlayerInCacheList(int rank, String playerId) {
        if (this.rankToPlayer.containsKey(rank)) {
            if (this.rankToPlayer.get(rank).getPlayerId().equals(playerId)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 是否和现在一样的头像
     *
     * @param newIcon
     * @return
     */
    private boolean _isSameIcon(int newIcon) {
        return target.getBase().getPvpIcon() == newIcon;
    }


    /**
     * 是否是相同的宣言
     *
     * @param newDeclaration
     * @return
     */
    private boolean _isSameDeclaration(String newDeclaration) {
        return newDeclaration.equals(target.getBase().getDeclaration());
    }

    /**
     * 计算购买挑战次数的花费
     *
     * @return
     */
    private IdAndCount _mathBuyChallengeTimesVipMoney() {
        int nextTimes = target.getBase().getChallengeTimes() + 1;
        int maxAttackTimes = AllServerConfig.Pvp_Challenge_Times.getConfig();
        //溢出次数
        int extraTimes = nextTimes - maxAttackTimes;
        return Table_Consume_Row.arneaBuyChallengeConsume(extraTimes);
    }

    /**
     * Cd冷却是否完成
     *
     * @return
     */
    private boolean _isCdReady() {
        return System.currentTimeMillis() >= target.getBase().getCdEndTime();
    }

    /**
     * 清空CD
     */
    private void _clearCd() {
        target.getBase().setCdEndTime(MagicNumbers.DEFAULT_ZERO);
    }


    /**
     * 挑战次数是否满足挑战条件
     *
     * @return
     */
    private boolean _isCanChallenge() {
        int maxChallengeTimes = AllServerConfig.Pvp_Challenge_Times.getConfig();
        return target.getBase().getBuyChallengeTimes() + maxChallengeTimes > target.getBase().getChallengeTimes();
    }

    /**
     * 挑战胜利保存
     *
     * @param nowRank
     */
    private void _battleWinSave(int nowRank) {
        _setMaxRank(nowRank);
        target.getBase().setVictoryTimes(target.getBase().getVictoryTimes() + 1);
        int integral = AllServerConfig.Pvp_Integral_Max.getConfig();
        target.getBase().setIntegral(target.getBase().getIntegral() + integral);
        getPlayerCtrl().updateSimplePlayer();
    }

    /**
     * 挑战失败保存
     */
    private void _battleFailSave() {
        int integral = _getBattleFailIntegral();
        target.getBase().setIntegral(target.getBase().getIntegral() + integral);
    }

    /**
     * 获取挑战失败积分
     *
     * @return
     */
    private int _getBattleFailIntegral() {
        int playerLv = getPlayerCtrl().getCurLevel();
        int vipLv = getPlayerCtrl().getSimplePlayerAfterUpdate().getVipLv();
        // todo 检查下逻辑
        TupleListCell<Integer> tuple = AllServerConfig.Pvp_Integral_Role.getConfig();
        TupleCell<Integer> tupleCell = tuple.getAll().get(MagicNumbers.DEFAULT_ZERO);
        if (playerLv >= tupleCell.get(TupleCell.FIRST) || vipLv >= tupleCell.get(TupleCell.SECOND)) {
            return AllServerConfig.Pvp_Integral_Max.getConfig();
        }
        return AllServerConfig.Pvp_Integral_Min.getConfig();
    }

    /**
     * 挑战结束保存次数和CD
     */
    private void _saveChallengeTimesAndCd() {
        target.getBase().setChallengeTimes(target.getBase().getChallengeTimes() + 1);
        int cd = AllServerConfig.Pvp_CD.getConfig();
        target.getBase().setCdEndTime(System.currentTimeMillis() + (long) cd);
    }


    /**
     * 是否为新的最高排名
     *
     * @param nowRank
     * @return true nowRank为最高排名 (数值越大排名越小)
     */
    private boolean _isNewMaxRank(int nowRank) {
        int curMax = target.getBase().getMaxRank();
        if (curMax <= 0) {
            return true;
        }
        return curMax > nowRank;
    }


    /**
     * 获取可以膜拜的玩家
     *
     * @return
     */
    private List<String> _getCanWorshipPlayer() {
        List<String> canWorshipPlayerIdList = new ArrayList<>();
        List<String> worshipPlayerList = target.getWorshipPlayerIdList();
        for (Entry<Integer, ArenaCenterRanker> entry : this.topTenPlayer.entrySet()) {
            if (!worshipPlayerList.contains(entry.getValue().getPlayerId())) {
                canWorshipPlayerIdList.add(entry.getValue().getPlayerId());
            }
        }
        return canWorshipPlayerIdList;
    }


    /**
     * 是否已经领取过这个奖励
     *
     * @param rewardsIdx
     * @return
     */
    private boolean _hasGetRankReward(int rewardsIdx) {
        return target.getRankRewardsList().contains(rewardsIdx);
    }


    @Override
    public int getCurRank() {
        In_GetRankToPvpCenter.Request request = new In_GetRankToPvpCenter.Request(getPlayerCtrl().getInnerRealmId(), getPlayerCtrl().getPlayerId());
        In_GetRankToPvpCenter.Response response = _sendMsgToPvpCenter(request);
        return response.getRank();
    }

    /**
     * 获取新的随机排行榜
     *
     * @param curRank
     * @return
     */
    private List<Integer> _randomRankList(int curRank) {
        TupleListCell<Integer> tupleListCell = AllServerConfig.Pvp_Refresh_Role.getConfig();
        List<TupleCell<Integer>> tupleList = tupleListCell.getAll();
        List<Integer> rankList = new ArrayList<>();
        if (curRank >= MagicNumbers.ARENA_BEGIN_RANDOM_RANK) { // >=51 开始随机
            for (TupleCell<Integer> tuple : tupleList) {
                _randomRank(rankList, curRank, tuple);
            }
        } else {  // <= 50 顺序展示
            // 尝试向前取3名，可能取不到3名
            for (int i = curRank - 1; i >= 1; i--) {
                if (rankList.size() >= (tupleList.size() - 1)) {
                    break;
                }
                rankList.add(i);
            }
            // 向后取剩余的几位
            for (int i = curRank + 1; i <= (curRank + tupleList.size()); i++) {
                if (rankList.size() >= tupleList.size()) { // 达到总的需要的人数
                    break;
                }
                rankList.add(i);
            }
        }
        return rankList;
    }

    /**
     * 随机整个新的排行榜列表
     *
     * @param rankList
     * @param curRank
     * @param tuple
     */

    private void _randomRank(List<Integer> rankList, int curRank, TupleCell<Integer> tuple) {
        int rank = _random(curRank, tuple);
        if (rankList.contains(rank)) {
            _randomRank(rankList, curRank, tuple);
        } else {
            rankList.add(rank);
        }
    }


    /**
     * 随机一个新的排位
     *
     * @param curRank
     * @param tuple
     * @return
     */
    private int _random(int curRank, TupleCell<Integer> tuple) {
        int rank = (curRank * RandomUtils.dropBetweenTowNum(tuple.get(TupleCell.FIRST), tuple.get(TupleCell.SECOND))) / MagicNumbers.PERCENT_NUMBER;
        for (int i = 0; i < MagicNumbers.DEFAULT_100; i++) {
            if (rank == curRank) {
                rank = (curRank * RandomUtils.dropBetweenTowNum(tuple.get(TupleCell.FIRST), tuple.get(TupleCell.SECOND))) / MagicNumbers.PERCENT_NUMBER;
            } else {
                break;
            }
        }
        if (rank == curRank) {
            String msg = "rank still is youself !!!";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        return rank;
    }

    /**
     * 刷新ArenaCtrl缓存
     *
     * @param rankToRankerTmp
     * @param topTenRankers
     */
    private void _refreshCache(Map<Integer, ArenaCenterRanker> rankToRankerTmp, Map<Integer, ArenaCenterRanker> topTenRankers) {
        // 缓存一份玩家排名对应id，玩家挑战时来校验是否在这个缓存中，如果没找到说明玩家排行已经发生了变动
        _refreshRankToPlayer(rankToRankerTmp);
        _refreshTopTenPlayer(topTenRankers);
    }

    /**
     * 刷新随机排行位置上的player缓存
     *
     * @param rankToRankerTmp
     */
    private void _refreshRankToPlayer(Map<Integer, ArenaCenterRanker> rankToRankerTmp) {
        this.rankToPlayer.clear();
        rankToPlayer.putAll(rankToRankerTmp);
    }

    private void _setMaxRank(int rank) {
        if (_isNewMaxRank(rank)) {
            target.getBase().setMaxRank(rank);
        }
    }

    /**
     * 刷新前10玩家缓存
     *
     * @param topTenRankers
     */
    private void _refreshTopTenPlayer(Map<Integer, ArenaCenterRanker> topTenRankers) {
        this.topTenPlayer.clear();
        for (Entry<Integer, ArenaCenterRanker> entry : topTenRankers.entrySet()) {
            this.topTenPlayer.put(entry.getKey(), entry.getValue());
        }
    }


    /**
     * 刷新本地排名缓存，下次用这些排名去center取实时玩家
     *
     * @param rankerList
     * @param lastRefreshRank
     */
    private void _refreshEnemiesList(List<Integer> rankerList, int lastRefreshRank) {
        target.getEnemiesRank().clear();
        target.getEnemiesRank().addAll(rankerList);
        target.getBase().setLastRefreshRank(lastRefreshRank);
    }

    private <I, O> O _sendMsgToPvpCenter(I request) {
        InnerMsg response = ActorMsgSynchronizedExecutor.sendMsgToServer(getPlayerCtrl().getContext().actorSelection(ActorSystemPath.WS_GameServer_Selection_ArenaCenter), request);
        LogicCheckUtils.checkResponse(response);
        return (O) response;
    }


    /**
     * 带排行列表刷新查询
     *
     * @param rankList
     * @return
     */
    private In_QueryEnemies.Response _query(List<Integer> rankList) {
        In_QueryEnemies.Request request = new In_QueryEnemies.Request(getPlayerCtrl().getPlayerId(), getPlayerCtrl().getInnerRealmId(), rankList);
        return _sendMsgToPvpCenter(request);
    }


    /**
     * 计算刷新所需要消耗
     *
     * @return
     */
    private IdMaptoCount _mathRefreshTimesVipMoney() {
        int nextTimes = target.getBase().getRefreshTimes() + 1;
        int freeTimes = AllServerConfig.Pvp_Free_Fresh_Times.getConfig();
        int extraTimes = nextTimes - freeTimes;
        return new IdMaptoCount().add(Table_Consume_Row.arneaRefreshConsume(extraTimes));
    }


    private void _saveRefreshTimes() {
        target.getBase().setRefreshTimes(target.getBase().getRefreshTimes() + 1);
    }

    private void _saveBuyRefreshTimes() {
        target.getBase().setBuyRefreshTimes(target.getBase().getBuyRefreshTimes() + 1);
    }

    /**
     * 是否还有免费的刷新次数
     *
     * @return
     */
    private boolean _hasFreeTimes() {
        int freeTimes = AllServerConfig.Pvp_Free_Fresh_Times.getConfig();
        return target.getBase().getRefreshTimes() < freeTimes;
    }

    /**
     * 是否已经领取过的这个积分奖励
     *
     * @param integralRewards
     * @return
     */
    private boolean _hasGetIntegralReward(int integralRewards) {
        return target.getIntegralRewardsList().contains(integralRewards);
    }


    /**
     * 获取当前所有可以领取的积分奖励Row
     *
     * @return
     */
    private List<Table_RankDailyReward_Row> _getCanGetIntegralRewards() {
        List<Table_RankDailyReward_Row> canGetRow = new ArrayList<>();
        List<Integer> hasGetIntegralReawrdsList = target.getIntegralRewardsList();
        List<Table_RankDailyReward_Row> rows = Table_RankDailyReward_Row.getArenaPointRows();
        for (Table_RankDailyReward_Row row : rows) {
            if (hasGetIntegralReawrdsList.contains(row.getPoint())) {
                continue; // 已经领取过了
            }
            if (row.getLimitVip() > getPlayerCtrl().getTarget().getPayment().getVipLevel()) {
                continue; // vip等级不足
            }
            if (target.getBase().getIntegral() >= row.getPoint()) {
                canGetRow.add(row);
            }
        }
        return canGetRow;
    }


    /**
     * 创建战斗记录
     *
     * @param beAttackRanker
     * @param backData
     * @return
     */
    private ArenaRecord create_BattleRecord(ArenaCenterRanker beAttackRanker, Sm_Battle_BackData backData) {
        boolean rs = backData.getResult().getRsType() == BattleResultTypeEnum.BATTLE_RS_VICTORY; // 胜利
        ArenaRecord record;
        if (beAttackRanker.isRobot()) {
            record = new ArenaRecord(ObjectId.get().toString(), rs, beAttackRanker.getRobotId());
        } else {
            SimplePlayer simplePlayer = SimplePojoUtils.querySimplePlayerById(beAttackRanker.getPlayerId(), beAttackRanker.getOuterRealmId());
            record = new ArenaRecord(ObjectId.get().toString(), rs, simplePlayer.getPlayerName(), simplePlayer.getLv(), simplePlayer.getIcon(), simplePlayer.getBattleValue(), simplePlayer.getFirstHandValue());
        }
        return record;
    }


    /**
     * 给被攻击者添加战斗记录
     *
     * @param arenaRecord
     * @param beAttackRanker
     */
    private void sendToAddNewBattleRecords(ArenaRecord arenaRecord, ArenaCenterRanker beAttackRanker) {
        if (beAttackRanker.isRobot()) {
            return;
        }
        String beAttackId = beAttackRanker.getPlayerId();
        int outerRealmId = beAttackRanker.getOuterRealmId();
        In_AddNewBattleRecords.Request req = new In_AddNewBattleRecords.Request(arenaRecord, beAttackId, outerRealmId);
        CheckPlayerOnlineMsgRequest<In_AddNewBattleRecords.Request> request = new CheckPlayerOnlineMsgRequest<>(beAttackId, req);
        ClusterMessageSender.sendMsgToPath(getPlayerCtrl().getContext(), request, ActorSystemPath.WS_GameServer_Selection_World);
    }


    /**
     * 保存战斗脚本
     *
     * @param recordId
     * @param backData
     */
    private void saveBattleRecord(String recordId, Sm_Battle_BackData backData) {
        String script = backData.toByteString().toStringUtf8();
        BattleRecord battleRecord = new BattleRecord(recordId, script);
        try {
            PojoUtils.saveAllCommonPojoToHashesRedis(battleRecord);
        } catch (Exception e) {
            LOGGER.error("保存战斗脚本出错！", e);
        }
    }


    /**
     * 获取战斗脚本
     *
     * @param recordId
     * @return
     */
    private Sm_Battle_BackData getBattleRecord(String recordId) {
        try {
            BattleRecord battleRecord = PojoUtils.getAllCommonPojoFromHashesRedis(BattleRecord.class, recordId);
            Sm_Battle_BackData.Builder b = Sm_Battle_BackData.newBuilder();
            b.mergeFrom(ByteString.copyFromUtf8(battleRecord.getRecordId()));
            return b.build();
        } catch (Exception e) {
            LOGGER.error("获取战斗脚本出错！", e);
        }
        return null;
    }

    public boolean _isArenaOpen() {
        int openLv = AllServerConfig.Pvp_Open_Level.getConfig();
        return getPlayerCtrl().getCurLevel() >= openLv;
    }


    // ======================== 数据采集 start start start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private void statisticsDaliyAttackTimes() {
        Pr_NotifyMsg notifyMsg2 = new Pr_NotifyMsg(PrivateNotifyTypeEnum.Arean_DaliyAttackTimes, target.getBase().getChallengeTimes());
        sendPrivateMsg(notifyMsg2);
    }

    // ======================== 数据采集 end end end <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
}
