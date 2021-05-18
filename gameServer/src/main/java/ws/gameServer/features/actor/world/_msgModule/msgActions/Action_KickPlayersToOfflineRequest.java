package ws.gameServer.features.actor.world._msgModule.msgActions;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.actor.world._msgModule.Action;
import ws.gameServer.features.actor.world.ctrl.WorldCtrl;
import ws.gameServer.features.actor.world.msg.In_KickPlayersToOfflineRequest;

public class Action_KickPlayersToOfflineRequest implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(Action_KickPlayersToOfflineRequest.class);

    @Override
    public void onRecv(Object msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        if (msg instanceof In_KickPlayersToOfflineRequest) {
            kickPlayersToOfflineRequest((In_KickPlayersToOfflineRequest) msg, worldCtrl, worldActorContext, sender);
        }
    }

    private void kickPlayersToOfflineRequest(In_KickPlayersToOfflineRequest msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        for (String playerId : msg.getPlayerIds()) {
            LOGGER.info("从后台发来的命令，玩家PlayerId={} 即将强制下线,现在通知其保存玩家数据到数据库中！", playerId);
        }
    }
}
