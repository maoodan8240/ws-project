package ws.gameServer.features.standalone.extp.pyActivities.core.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;
import ws.relationship.topLevelPojos.pyActivities.PyActivities;

public interface PyActivitiesCtrl extends PlayerExteControler<PyActivities> {

    /**
     * 玩家登录了
     */
    void onPlayerLogined();

    /**
     * 购买基金
     */
    void buyFund();


    /**
     * 领取奖励
     *
     * @param realmAcId
     * @param subActivityId
     */
    void getRewards(int realmAcId, int subActivityId);


    /**
     * 通知该类型的数据发生了变动
     *
     * @param type
     */
    void onUpdateRegNotifyMsg(PrivateNotifyTypeEnum type);


    /**
     * 是否已经领取了特殊的组活动（该组活动如果开启了，只允许一份实例）
     *
     * @param groupActId
     * @return <=0 表示未开启该活动，否则返回活动的realmActivityId
     */
    int hasGetSpecialGroupActivity(int groupActId);
}
