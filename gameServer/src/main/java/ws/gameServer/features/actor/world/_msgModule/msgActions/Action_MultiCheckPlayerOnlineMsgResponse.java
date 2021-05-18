package ws.gameServer.features.actor.world._msgModule.msgActions;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import ws.gameServer.features.actor.world._msgModule.Action;
import ws.gameServer.features.actor.world.ctrl.WorldCtrl;
import ws.relationship.base.msg.MultiCheckPlayerOnlineMsg;

public class Action_MultiCheckPlayerOnlineMsgResponse implements Action {

    @Override
    public void onRecv(Object msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        if (msg instanceof MultiCheckPlayerOnlineMsg.Response) {
            onMultiCheckPlayerOnlineMsgResponse((MultiCheckPlayerOnlineMsg.Response) msg, worldCtrl, worldActorContext, sender);
        }
    }

    private void onMultiCheckPlayerOnlineMsgResponse(MultiCheckPlayerOnlineMsg.Response response, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        worldCtrl.getPlayerIOStatus(response.getRequest().getCheckPlayerId(), worldActorContext).handleMsg(response);
    }
}
