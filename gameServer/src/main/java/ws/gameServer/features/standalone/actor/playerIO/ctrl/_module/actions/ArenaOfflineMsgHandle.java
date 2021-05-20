package ws.gameServer.features.standalone.actor.playerIO.ctrl._module.actions;

import akka.actor.ActorRef;
import ws.gameServer.features.standalone.actor.playerIO.ctrl._module.Action;

/**
 * Created by lee on 5/23/17.
 */
public class ArenaOfflineMsgHandle implements Action {
    @Override
    public void handleOfflineMsg(String selfPlayerId, Object msg, ActorRef oriSender) {
    }

    
}
