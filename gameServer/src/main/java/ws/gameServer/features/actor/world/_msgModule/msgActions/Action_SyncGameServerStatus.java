package ws.gameServer.features.actor.world._msgModule.msgActions;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import ws.gameServer.features.actor.world._msgModule.Action;
import ws.gameServer.features.actor.world.ctrl.WorldCtrl;
import ws.gameServer.features.actor.world.msg.In_SyncGameServerStatus;

public class Action_SyncGameServerStatus implements Action {

    @Override
    public void onRecv(Object msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        if (msg instanceof In_SyncGameServerStatus) {
            onSyncGameServerStatus(worldCtrl, (In_SyncGameServerStatus) msg);
        }
    }

    private void onSyncGameServerStatus(WorldCtrl worldCtrl, In_SyncGameServerStatus reuest) throws Exception {
    }
}
