package ws.gameServer.features.standalone.extp.redPoint.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.gameServer.features.standalone.extp.redPoint.msg.Pr_CheckRedPointMsg.Response;
import ws.gameServer.features.standalone.extp.redPoint.msg.Pr_NotifyRedPointMsg;
import ws.relationship.topLevelPojos.common.TopLevelHolder;

public interface RedPointCtrl extends PlayerExteControler<TopLevelHolder> {

    void notifyRedPoint(Pr_NotifyRedPointMsg.Request privateMsg);


    void checkRedPointMsg(Response privateMsg);
}
