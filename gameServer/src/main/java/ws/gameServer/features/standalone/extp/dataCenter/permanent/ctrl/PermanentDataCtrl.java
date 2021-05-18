package ws.gameServer.features.standalone.extp.dataCenter.permanent.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.gameServer.features.standalone.extp.dataCenter.msg.NotifyObj;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;
import ws.relationship.topLevelPojos.dataCenter.permanentData.PermanentData;

public interface PermanentDataCtrl extends PlayerExteControler<PermanentData> {
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
     */
    void updateRegNotifyMsgHasDone(NotifyObj notifyObj);
}
