package ws.gameServer.features.standalone.extp.missions.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_UpdateRegNotifyMsg;
import ws.protos.EnumsProtos.MissionTypeEnum;
import ws.relationship.topLevelPojos.mission.Missions;

public interface MissionsCtrl extends PlayerExteControler<Missions> {

    /**
     * 同步任务
     */
    void syncAchieve();

    /**
     * 同步成就
     */
    void syncMission();

    /**
     * 处理通知
     *
     * @param notifyMsg
     */
    void dealNotifyMsg(Pr_NotifyMsg notifyMsg);

    /**
     * 领取奖励
     *
     * @param missionType
     * @param mid
     */
    void getRewards(MissionTypeEnum missionType, int mid);

    /**
     * 处理消息数据更新了通知
     *
     * @param updateRegNotifyMsg
     */
    void dealUpdateRegNotifyMsg(Pr_UpdateRegNotifyMsg updateRegNotifyMsg);


    /**
     * 整点通知
     *
     * @param hour
     */
    void onBroadcastEachHour(int hour);

}
