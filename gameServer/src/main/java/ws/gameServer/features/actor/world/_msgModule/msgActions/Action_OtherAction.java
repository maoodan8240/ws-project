package ws.gameServer.features.actor.world._msgModule.msgActions;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.actor.world._msgModule.Action;
import ws.gameServer.features.actor.world.ctrl.WorldCtrl;
import ws.relationship.base.msg.In_BroadcastEachMinute;

public class Action_OtherAction implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(Action_TerminatedAction.class);

    @Override
    public void onRecv(Object msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        if (msg instanceof In_BroadcastEachMinute.Request) {
            onBroadcastEachMinute((In_BroadcastEachMinute.Request) msg, worldCtrl);
        }
    }

    private void onBroadcastEachMinute(In_BroadcastEachMinute.Request request, WorldCtrl worldCtrl) {
        worldCtrl.statisticsCurGameSeverStatus();
    }
}
