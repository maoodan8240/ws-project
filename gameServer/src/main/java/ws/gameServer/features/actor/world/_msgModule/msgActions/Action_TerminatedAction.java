package ws.gameServer.features.actor.world._msgModule.msgActions;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.actor.world._msgModule.Action;
import ws.gameServer.features.actor.world.ctrl.WorldCtrl;
import ws.gameServer.features.actor.world.playerIOStatus.PlayerIOStatus;
import ws.relationship.base.cluster.ActorSystemPath;

public class Action_TerminatedAction implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(Action_TerminatedAction.class);

    @Override
    public void onRecv(Object msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        if (msg instanceof Terminated) {
            terminated((Terminated) msg, worldCtrl, worldActorContext, sender);
        }
    }

    private void terminated(Terminated msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        String actorName = msg.actor().path().elements().toList().last();
        String playerId = actorName.replaceFirst(ActorSystemPath.WS_GameServer_PlayerIO, "");
        LOGGER.info("玩家PlayerId={} 掉线了, 已经从内存中彻底移除...", playerId);
        PlayerIOStatus playerIOStatus = worldCtrl.getPlayerIOStatus(playerId, worldActorContext);
        playerIOStatus.killendPlayerIOActor();
    }
}
