package ws.gameServer.features.standalone.extp.dataCenter.stageDaliy.ctrl;

import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.gameServer.features.standalone.extp.dataCenter.msg.NotifyObj;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;
import ws.relationship.topLevelPojos.dataCenter.stageDaliyData.StageDaliyData;

public interface StageDaliyDataCtrl extends PlayerExteControler<StageDaliyData> {


    /**
     * 处理接到的通知消息
     *
     * @param notifyMsg
     */
    void dealNotifyMsg(Pr_NotifyMsg notifyMsg);


    /**
     * 更新通知消息中已经完成的量
     *
     * @param notifyObj
     * @param startDay
     * @param endDay
     */
    void updateRegNotifyMsgHasDone(NotifyObj notifyObj, String startDay, String endDay);
}
