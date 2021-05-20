package ws.gameServer.features.standalone.actor.player.ctrl;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import com.google.protobuf.TextFormat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.mongoDB.interfaces.TopLevelPojo;
import ws.common.mongoDB.utils.WsJsonUtils;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.mc.controler.AbstractControler;
import ws.common.utils.mc.extension.Extension;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.mc.extension.PlayerExtension;
import ws.gameServer.features.standalone.actor.player.msg.Pr_PlayerLvChanged;
import ws.gameServer.features.standalone.actor.player.msg.Pr_PlayerVipLvChanged;
import ws.gameServer.features.standalone.actor.player.utils.PlayerCtrlProtos;
import ws.gameServer.features.standalone.actor.player.utils.PlayerUtils;
import ws.gameServer.features.standalone.actor.player.utils.SimplePlayerUtils;
import ws.gameServer.features.standalone.extp.utils.UpgradeLevel;
import ws.gameServer.features.standalone.utils.LogHandler;
import ws.gameServer.system.date.dayChanged.DayChanged;
import ws.gameServer.system.logHandler.LogExcep;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.EnumsProtos;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerProtos.Sm_Player;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.base.msg.In_MessagePassToGatewayServer;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.centerPlayer.CenterPlayerDao;
import ws.relationship.daos.sdk.realm.OuterToInnerRealmListDao;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.tableRows.Table_Exp_Row;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;
import ws.relationship.topLevelPojos.common.LevelUpObj;
import ws.relationship.topLevelPojos.common.TopLevelHolder;
import ws.relationship.topLevelPojos.player.Player;
import ws.relationship.topLevelPojos.sdk.realm.OuterToInnerRealmList;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.DBUtils;
import ws.relationship.utils.NameUtils;
import ws.relationship.utils.ProtoUtils;

import java.util.*;

public class _PlayerCtrl extends AbstractControler<Player> implements PlayerCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_PlayerCtrl.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final OuterToInnerRealmListDao OUTER_TO_INNER_REALMLIST_DAO = DaoContainer.getDao(OuterToInnerRealmList.class);
    private static final CenterPlayerDao CENTER_PLAYER_DAO = DaoContainer.getDao(CenterPlayer.class);

    static {
        CENTER_PLAYER_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
        OUTER_TO_INNER_REALMLIST_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
    }


    private ActorContext context;
    private ActorRef actorRef;
    private ActorRef curSendActorRef;
    private ActorRef gatewaySender;
    private String connFlag;
    private Map<Class<? extends TopLevelPojo>, TopLevelPojo> topLevelPojoClassToTopLevelPojo = new HashMap<>();
    private Map<Class<? extends TopLevelPojo>, TopLevelPojo> classToNeedSavePojo = new HashMap<>();
    private String sn;
    private TreeMap<String, PlayerExtension<?>> nameMaptoExtension = new TreeMap<>();
    private List<Long> times;
    private String actionName;
    private long oldestLoginTime;
    private CenterPlayer centerPlayer;

    @Override
    public String getPlayerId() {
        return target.getPlayerId();
    }

    @Override
    public int getOuterRealmId() {
        return target.getAccount().getOuterRealmId();
    }

    @Override
    public int getInnerRealmId() {
        return target.getAccount().getInnerRealmId();
    }

    @Override
    public void sync() {
        Response.Builder br = ProtoUtils.create_Response(Code.Sm_Player, Sm_Player.Action.RESP_SYNC);
        br.setSmPlayer(PlayerCtrlProtos.create_Sm_Player(target));
        br.setResult(true);
        sendResponse(br.build());
    }

    @Override
    public void onRename(String newName) {
        Response.Builder br = ProtoUtils.create_Response(Code.Sm_Player, Sm_Player.Action.RESP_RENAME);
        if (StringUtils.isBlank(newName)) {
            LOGGER.warn("新的名字不能为空！");
            sendResponse(br.build());
            return;
        }
        boolean rs = NameUtils.inertNewName(newName);
        if (!rs) {
            LOGGER.warn("名字已经存在了！newName={} ", newName);
            br.setErrorCode(EnumsProtos.ErrorCodeEnum.PLAYER_NAME_ALREADY_USED);
            sendResponse(br.build());
            return;
        }
        int freeTs = AllServerConfig.Player_Rename_FreeTimes.getConfig();
        IdMaptoCount refresh = null;
        int oldTimes = target.getOther().getReNameTs();
        int newTimes = oldTimes + 1;
        if (oldTimes >= freeTs) {
            int consume = AllServerConfig.Player_Rename_Consume.getConfig();
            IdAndCount reduce = new IdAndCount(ResourceTypeEnum.RES_VIPMONEY_VALUE, consume);
        }
        target.getOther().setReNameTs(newTimes);
        target.getBase().setName(newName);
        Sm_Player.Builder b = Sm_Player.newBuilder();
        b.setAction(Sm_Player.Action.RESP_RENAME);
        b.setName(newName);
        br.setSmPlayer(b);
        br.setResult(true);
        sendResponse(br.build());
        save();
    }

    @Override
    public void onIcon(int newIcon) {
        Response.Builder br = ProtoUtils.create_Response(Code.Sm_Player, Sm_Player.Action.RESP_REICON);
        Sm_Player.Builder b = Sm_Player.newBuilder();
        b.setAction(Sm_Player.Action.RESP_REICON);
        b.setIconId(newIcon);
        target.getBase().setIconId(newIcon);
        br.setSmPlayer(b);
        br.setResult(true);
        sendResponse(br.build());
        save();
    }

    @Override
    public void onSign(String newSign) {
//        Response.Builder br = ProtoUtils.create_Response(Code.Sm_Player, Sm_Player.Action.RESP_SIGN);
//        Sm_Player.Builder b = Sm_Player.newBuilder();
//        b.setAction(Sm_Player.Action.RESP_SIGN);
//        b.setSign(newSign);
//        target.getBase().setSign(newSign);
//        br.setSmPlayer(b);
//        br.setResult(true);
//        sendResponse(br.build());
        save();
    }

    @Override
    public void addExp(long exp) {
        int oldLv = getCurLevel();
        LevelUpObj levelUpObj = new LevelUpObj(oldLv, target.getBase().getOverflowExp());
        int curMaxLevel = AllServerConfig.Player_CurMaxLv.getConfig();
        UpgradeLevel.levelUpKeepOvf(levelUpObj, exp, curMaxLevel, (oldLevel) -> {
            return Table_Exp_Row.getPlayerUpNeedExp(oldLevel);
        });
        target.getBase().setLevel(levelUpObj.getLevel());
        target.getBase().setOverflowExp(levelUpObj.getOvfExp());
        int newLv = getCurLevel();
        sync();
        save();
        if (newLv > oldLv) {
            statisticsPlayerLevelUp();
            sendPrivateMsg(new Pr_PlayerLvChanged());
            LogHandler.playerLvUpLog(target);
        }
    }

    private void addExpInner(long exp) {
    }

    @Override
    public void addVipMoneyByRecharge(int vipMoney, Enum<?> callerAction) {
        if (vipMoney <= 0) {
            LOGGER.warn(LogExcep.LOGIC_RETURN + "注意！充值增加的元宝为0！");
            return;
        }
        int oldVipLv = getCurVipLevel();
        target.getPayment().setVipTotalExp(target.getPayment().getVipTotalExp() + vipMoney);
        int maxVipLv = AllServerConfig.Player_Max_VIPLv.getConfig();
        LevelUpObj levelUpObj = PlayerUtils.upPlayerVipLv(target.getPayment().getVipTotalExp(), maxVipLv);
        target.getPayment().setVipLevel(levelUpObj.getLevel());
        // 添加资源
        IdAndCount addVipMoney = new IdAndCount(ResourceTypeEnum.RES_VIPMONEY_VALUE, vipMoney);
        int newVipLv = getCurVipLevel();
        sync();
        save();
        if (newVipLv > oldVipLv) {
            sendPrivateMsg(new Pr_PlayerVipLvChanged());
            LogHandler.playerVipLvUpLog(target);
        }
    }

    private void handlePayment(int vipMoney, String innerOrderId) {
    }

    private void handlePaymentNotify(int todayPay) {
    }

    @Override
    public void onSetRobot() {
    }


    @Override
    public void save() {
        addNeedSavePojo(this.target);
    }

    @Override
    public void sendPrivateMsg(PrivateMsg privateMsg) {
        getActorRef().tell(privateMsg, ActorRef.noSender());
    }

    @Override
    public void setActorRef(ActorRef actorRef) {
        this.actorRef = actorRef;
    }

    @Override
    public void setContext(ActorContext context) {
        this.context = context;
    }

    public ActorRef getCurSendActorRef() {
        return curSendActorRef;
    }

    public void setCurSendActorRef(ActorRef curSendActorRef) {
        this.curSendActorRef = curSendActorRef;
    }

    public ActorContext getContext() {
        return context;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }

    @Override
    public void setSn(String sn) {
        this.sn = sn;
    }

    @Override
    public String getSn() {
        return sn;
    }

    @Override
    public void addExtension(PlayerExtension<?> extension) {
        String extensionName = parseExtensionNameFrom(extension.getClass());
        nameMaptoExtension.put(extensionName, extension);
    }


    @Override
    public void updateSimplePlayer() {
        SimplePlayer simplePlayerNew = SimplePlayerUtils.createSimplePlayer(this, topLevelPojoClassToTopLevelPojo);
        if (!_isSameSimplePlayer(simplePlayerNew)) {
            this.topLevelPojoClassToTopLevelPojo.put(SimplePlayer.class, simplePlayerNew);
            putToClassToNeedSavePojo(simplePlayerNew);
        }
    }

    @Override
    public SimplePlayer getSimplePlayerAfterUpdate() {
        updateSimplePlayer();
        return _getSimplePlayer();
    }

    @Override
    public void onReGetCenterPlayer() {
        centerPlayer = CENTER_PLAYER_DAO.findCenterPlayer(getPlayerId());
        LOGGER.info("playerId={} SimpleId={} centerPlayer 缓存更新了！", getPlayerId(), target.getBase().getSimpleId());
    }

    private SimplePlayer _getSimplePlayer() {
        return (SimplePlayer) topLevelPojoClassToTopLevelPojo.get(SimplePlayer.class);
    }

    /**
     * 是否是同一个simplePlayer
     *
     * @param simplePlayernNew
     * @return
     */
    private boolean _isSameSimplePlayer(SimplePlayer simplePlayernNew) {
        String curSimplePlayer = WsJsonUtils.javaObjectToJSONStr(_getSimplePlayer());
        String simplePlayerStrNew = WsJsonUtils.javaObjectToJSONStr(simplePlayernNew);
        return simplePlayerStrNew.equals(curSimplePlayer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends PlayerExtension<?>> T getExtension(Class<T> type) {
        String extensionName = parseExtensionNameFrom(type);
        return (T) nameMaptoExtension.get(extensionName);
    }

    @Override
    public TreeMap<String, PlayerExtension<?>> getAllExtensions() {
        return nameMaptoExtension;
    }

    @SuppressWarnings("rawtypes")
    private static String parseExtensionNameFrom(Class<? extends Extension> extensionType) {
        return extensionType.getName().replaceAll("\\.", "_");
    }

    @Override
    public void resetDataAtDayChanged() {
        if (!GlobalInjector.getInstance(DayChanged.class).getDayChangedStr().equals(target.getOther().getLastResetDay())) {
            target.getOther().setLoginDays(target.getOther().getLoginDays() + 1);
            target.getOther().setLastResetDay(GlobalInjector.getInstance(DayChanged.class).getDayChangedStr());
            statisticsPlayerLoginDays(); // 每天执行一次
            statisticsPlayerLevelUp();   // 每天执行一次
        }
    }

    @Override
    public void postInit() throws Exception {
        updatePlayerInnerRealmId();
        resetDataAtDayChanged();
    }

    @Override
    public void onPlayerLogined(String deviceUid) {
        if (oldestLoginTime <= 0) {
            oldestLoginTime = System.currentTimeMillis();
        }
        this.target.getOther().setLsinTime(oldestLoginTime); // 只要玩家没有从内存中移除，登录时间保持变
        this.target.getOther().setLsoutTime(MagicNumbers.DEFAULT_NEGATIVE_ONE); // 表示在线
        updatePlayerLastLoginRank();

        updateSimplePlayer();
        saveAllNeedSavePojos();
        sync();
        LogHandler.playerLoginLog(target, deviceUid);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends TopLevelPojo> T getTopLevelPojo(Class<T> topLevelPojoClass) {
        return (T) topLevelPojoClassToTopLevelPojo.get(topLevelPojoClass);
    }

    public void setTopLevelPojoClassToTopLevelPojo(Map<Class<? extends TopLevelPojo>, TopLevelPojo> topLevelPojoClassToTopLevelPojo) {
        this.topLevelPojoClassToTopLevelPojo.clear();
        topLevelPojoClassToTopLevelPojo.forEach((clazz, pojo) -> {
            if (pojo != null) {
                this.topLevelPojoClassToTopLevelPojo.put(clazz, pojo);
            }
        });
        topLevelPojoClassToTopLevelPojo.clear();
    }

    @Override
    public void addToTopLevelPojoClassToTopLevelPojo(TopLevelPojo topLevelPojo) {
        if (topLevelPojo == null) {
            return;
        }
        this.topLevelPojoClassToTopLevelPojo.put(topLevelPojo.getClass(), topLevelPojo);
    }

    public void setNetworkMsgTimes(List<Long> times, String actionName) {
        this.times = new ArrayList<>(times);
        this.actionName = actionName;
    }

    public List<Long> getNetworkMsgTimes(String actionName) {
        if (!StringUtils.isEmpty(this.actionName) && this.actionName.equals(actionName)) {
            return this.times;
        }
        return new ArrayList<>();
    }


    @Override
    public int getCurLevel() {
        return target.getBase().getLevel();
    }

    @Override
    public int getCurVipLevel() {
        return target.getPayment().getVipLevel();
    }

    public CenterPlayer getCenterPlayer() {
        return centerPlayer;
    }

    @Override
    public void setCenterPlayer(CenterPlayer centerPlayer) {
        this.centerPlayer = centerPlayer;
    }

    /**
     * ---------------------------- 以下为：GM命令的操作 -----------------------
     */

    public ActorRef getGatewaySender() {
        return gatewaySender;
    }

    public void setGatewaySender(ActorRef gatewaySender) {
        this.gatewaySender = gatewaySender;
    }

    public void setConnFlag(String connFlag) {
        this.connFlag = connFlag;
    }

    public String getConnFlag() {
        return connFlag;
    }


    @Override
    public void testBattle() {
       
    }

    @Override
    public void sendResponse(Response response) {
        LOGGER.debug("\n服务器相应客户端内容为={}\n《\n{}》 ", response.getSmMsgAction(), TextFormat.printToUnicodeString(response));
        In_MessagePassToGatewayServer messagePassToGatewayServer = new In_MessagePassToGatewayServer(getConnFlag(), response, response.getSmMsgAction());
        getGatewaySender().tell(messagePassToGatewayServer, getActorRef());
    }

    @Override
    public void clearNeedSavePojos() {
        LOGGER.trace("\n---------------清理待存Pojo列表.............");
        this.classToNeedSavePojo.clear();
    }

    @Override
    public void addNeedSavePojo(TopLevelPojo pojo) {
        putToClassToNeedSavePojo(pojo);
        if (SimplePlayerUtils.sensitive(pojo)) {
            updateSimplePlayer();
        }
    }

    private void putToClassToNeedSavePojo(TopLevelPojo pojo) {
        if (pojo == null || pojo instanceof TopLevelHolder) {
            return;
        }
        if (topLevelPojoClassToTopLevelPojo.containsKey(pojo.getClass())) {
            this.classToNeedSavePojo.put(pojo.getClass(), pojo);
        } else {
            String msg = String.format("玩家TopLevelPojo集合中不存在此pojoClass=%s !", pojo.getClass());
            throw new BusinessLogicMismatchConditionException(msg);
        }
    }


    @Override
    public void saveAllNeedSavePojos() {
        if (this.classToNeedSavePojo.size() > 0) {
            LOGGER.trace("---------------保存Pojo列表............." + this.classToNeedSavePojo);
            int outerRealmId = getOuterRealmId();
            List<TopLevelPojo> pojoList = new ArrayList<>(this.classToNeedSavePojo.values());
            DBUtils.saveHashPojo(outerRealmId, pojoList);
        }
    }

    @Override
    public void setLsoutTime() {
        target.getOther().setLsoutTime(System.currentTimeMillis());
        updateSimplePlayer();
        saveAllNeedSavePojos();
    }

    @Override
    public void onPlayerDisconnected() {
        setLsoutTime();
        LogHandler.playerLogoutLog(target, oldestLoginTime);
    }

    // 设置innerRealmId
    private void updatePlayerInnerRealmId() {
        OuterToInnerRealmList realmList = OUTER_TO_INNER_REALMLIST_DAO.findByOuterRealmId(target.getAccount().getOuterRealmId());
        target.getAccount().setInnerRealmId(realmList.getInnerRealmId());
    }

    // 更新玩家最近一次登录时间点
    private void updatePlayerLastLoginRank() {
//        RedisRankUtils.insertRank(System.currentTimeMillis(), getPlayerId(), getOuterRealmId(), CommonRankTypeEnum.RK_PLAYERLOGINTIME);
//        RedisRankUtils.removeFromRankByRange(getOuterRealmId(), CommonRankTypeEnum.RK_PLAYERLOGINTIME, MagicNumbers.PLAYER_LAST_LOGIN_STORE_COUNT, Integer.MAX_VALUE);
    }

    // ======================== 针对玩家模块的数据采集 start start start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    /**
     * 统计玩家升级
     */
    public void statisticsPlayerLevelUp() {
//        Pr_NotifyMsg notifyMsg1 = new Pr_NotifyMsg(PrivateNotifyTypeEnum.Player_LevelUp, target.getBase().getLevel());
//        sendPrivateMsg(notifyMsg1);
    }


    /**
     * 统计玩家登录
     */
    private void statisticsPlayerLoginDays() {
//        Pr_NotifyMsg notifyMsg1 = new Pr_NotifyMsg(PrivateNotifyTypeEnum.Player_LoginAllDays, target.getOther().getLoginDays());
//        sendPrivateMsg(notifyMsg1);
//
//        Pr_NotifyMsg notifyMsg2 = new Pr_NotifyMsg(PrivateNotifyTypeEnum.Player_LoginOneDay, MagicNumbers.DEFAULT_ONE);
//        sendPrivateMsg(notifyMsg2);
    }

    // ======================== 针对玩家模块的数据采集 end end end <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
}










