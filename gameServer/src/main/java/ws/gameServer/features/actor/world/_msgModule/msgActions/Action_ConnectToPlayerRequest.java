package ws.gameServer.features.actor.world._msgModule.msgActions;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import ws.gameServer.features.actor.world._msgModule.Action;
import ws.gameServer.features.actor.world.ctrl.WorldCtrl;
import ws.relationship.appServers.loginServer.player.msg.In_ConnectToPlayerRequest;

public class Action_ConnectToPlayerRequest implements Action {

    @Override
    public void onRecv(Object msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        if (msg instanceof In_ConnectToPlayerRequest) {
            connectToPlayerRequest((In_ConnectToPlayerRequest) msg, worldCtrl, worldActorContext, sender);
        }
    }

    private void connectToPlayerRequest(In_ConnectToPlayerRequest msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        worldCtrl.getPlayerIOStatus(msg.getPlayerId(), worldActorContext).handleMsg(msg);
    }
}
