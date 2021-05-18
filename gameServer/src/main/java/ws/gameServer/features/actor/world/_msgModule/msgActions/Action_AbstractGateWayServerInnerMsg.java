package ws.gameServer.features.actor.world._msgModule.msgActions;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import ws.gameServer.features.actor.world._msgModule.Action;
import ws.gameServer.features.actor.world.ctrl.WorldCtrl;
import ws.relationship.base.msg.AbstractGateWayServerInnerMsg;

public class Action_AbstractGateWayServerInnerMsg implements Action {

    @Override
    public void onRecv(Object msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        if (msg instanceof AbstractGateWayServerInnerMsg) {
            abstractGateWayServerInnerMsg((AbstractGateWayServerInnerMsg) msg, worldCtrl, worldActorContext, sender);
        }
    }

    private void abstractGateWayServerInnerMsg(AbstractGateWayServerInnerMsg msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        worldCtrl.getPlayerIOStatus(msg.getPlayerId(), worldActorContext).handleMsg(msg);
    }
}
