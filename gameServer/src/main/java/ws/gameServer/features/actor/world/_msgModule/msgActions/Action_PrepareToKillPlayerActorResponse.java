package ws.gameServer.features.actor.world._msgModule.msgActions;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.actor.world._msgModule.Action;
import ws.gameServer.features.actor.world.ctrl.WorldCtrl;
import ws.gameServer.features.actor.world.msg.In_PrepareToKillPlayerActorResponse;
import ws.gameServer.features.actor.world.playerIOStatus.IOStatus;
import ws.gameServer.features.actor.world.playerIOStatus.PlayerIOStatus;
import ws.gameServer.features.actor.world.playerIOStatus.Status;

public class Action_PrepareToKillPlayerActorResponse implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(Action_PrepareToKillPlayerActorResponse.class);

    @Override
    public void onRecv(Object msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        if (msg instanceof In_PrepareToKillPlayerActorResponse) {
            prepareToKillPlayerActorResponse((In_PrepareToKillPlayerActorResponse) msg, worldCtrl, worldActorContext, sender);
        }
    }

    private void prepareToKillPlayerActorResponse(In_PrepareToKillPlayerActorResponse msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        PlayerIOStatus playerIOStatus = worldCtrl.getPlayerIOStatus(msg.getTargetPlayerId(), worldActorContext);
        if (playerIOStatus.getIoStatus() == IOStatus.Inend || playerIOStatus.getStatus() == Status.Offline) {
            playerIOStatus.killingPlayerIOActor();
            LOGGER.info("玩家PlayerId={} 掉线了, 准备从内存中彻底移除...", msg.getTargetPlayerId());
        }
    }
}
