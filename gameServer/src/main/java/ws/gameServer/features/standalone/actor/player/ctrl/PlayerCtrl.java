package ws.gameServer.features.standalone.actor.player.ctrl;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import ws.common.mongoDB.interfaces.TopLevelPojo;
import ws.common.utils.mc.controler.Controler;
import ws.gameServer.features.standalone.actor.player.mc.extension.PlayerExtension;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;
import ws.relationship.topLevelPojos.player.Player;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public interface PlayerCtrl extends Controler<Player> {

    void setGatewaySender(ActorRef gatewaySender);

    ActorRef getGatewaySender();

    void setConnFlag(String connFlag);

    String getConnFlag();

    String getPlayerId();

    int getOuterRealmId();

    int getInnerRealmId();

    void postInit() throws Exception;

    CenterPlayer getCenterPlayer();

    void setCenterPlayer(CenterPlayer centerPlayer);

    /**
     * 登录
     *
     * @param deviceUid 登录的设备唯一标示
     */
    void onPlayerLogined(String deviceUid);

    /**
     * 同步玩家角色信息
     */

    void sync();

    /**
     * 增加角色经验
     *
     * @param exp
     */
    void addExp(long exp);

    /**
     * 重命名
     *
     * @param newName
     */
    void onRename(String newName);

    /**
     * 设置新的Icon
     *
     * @param newIcon
     */
    void onIcon(int newIcon);

    /**
     * 新的签名
     *
     * @param newSign
     */
    void onSign(String newSign);

    /**
     * 充值增加元宝
     *
     * @param vipMoney
     */
    void addVipMoneyByRecharge(int vipMoney, Enum<?> callerAction);

    /**
     * 获取当前等级
     *
     * @return
     */
    int getCurLevel();


    /**
     * 玩家当前vip等级
     *
     * @return
     */
    int getCurVipLevel();

    /**
     * 设置为机器人
     */
    void onSetRobot();

    /**
     * 获取玩家的基本信息
     *
     * @return
     */
    // Manager_PlayerBaseInfo onOnlinePlayerBaseInfoRequest();

    /**
     * 修改玩家等级
     */
    // void onModiyfPlayerData_Request(In_Manager_ModiyfPlayerData_Request msg);

    /**
     * 添加扩展
     *
     * @param extension
     */
    void addExtension(PlayerExtension<?> extension);


    SimplePlayer getSimplePlayerAfterUpdate();

    /**
     * 返回扩展
     *
     * @param type
     * @return
     */
    <T extends PlayerExtension<?>> T getExtension(Class<T> type);

    /**
     * 返回全部扩展
     *
     * @return
     */
    TreeMap<String, PlayerExtension<?>> getAllExtensions();

    /**
     * 设置PlayerActor的引用
     *
     * @param actorRef
     */
    void setActorRef(ActorRef actorRef);

    /**
     * 获取PlayerActor的引用
     *
     * @param context
     */
    void setContext(ActorContext context);

    /**
     * 设置PlayerActor的上下文
     *
     * @return
     */
    ActorContext getContext();

    /**
     * 获取PlayerActor的上下文
     *
     * @return
     */
    ActorRef getActorRef();

    /**
     * 获取最新的sender
     *
     * @return
     */
    ActorRef getCurSendActorRef();

    /**
     * 设置最新的Sender
     *
     * @param curSendActorRef
     */
    void setCurSendActorRef(ActorRef curSendActorRef);

    /**
     * 每次请求产生的流水后
     *
     * @return
     */
    String getSn();

    /**
     * 设置每次请求的流水号
     *
     * @param sn
     */
    void setSn(String sn);

    void save();

    void resetDataAtDayChanged();

    void setLsoutTime();

    void onPlayerDisconnected();


    <T extends TopLevelPojo> T getTopLevelPojo(Class<T> topLevelPojoClass);

    void setTopLevelPojoClassToTopLevelPojo(Map<Class<? extends TopLevelPojo>, TopLevelPojo> topLevelPojoClassToTopLevelPojo);

    void addToTopLevelPojoClassToTopLevelPojo(TopLevelPojo topLevelPojo);


    /**
     * 设置网络消息的接收时间点和消息的actionName
     *
     * @param times
     * @param actionName
     */
    void setNetworkMsgTimes(List<Long> times, String actionName);

    /**
     * 通过actionName获取网络消息的接收时间点
     *
     * @param actionName
     * @return
     */
    List<Long> getNetworkMsgTimes(String actionName);


    /**
     * 清空待存储列表
     */
    void clearNeedSavePojos();


    /**
     * 待储存的pojo
     *
     * @param pojo
     */
    void addNeedSavePojo(TopLevelPojo pojo);


    /**
     * 存储所有待保存的pojo列表
     */
    void saveAllNeedSavePojos();

    /**
     * ---------------------------- 以下为：统计 及  GM命令的操作 -----------------------
     */

    void statisticsPlayerLevelUp();

    void testBattle();

    void sendResponse(Response response);

    void updateSimplePlayer();

    /**
     * 重新获取CenterPlayer缓存
     */
    void onReGetCenterPlayer();
}
