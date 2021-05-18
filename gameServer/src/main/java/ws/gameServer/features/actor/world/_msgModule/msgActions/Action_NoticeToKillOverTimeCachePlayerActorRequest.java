package ws.gameServer.features.actor.world._msgModule.msgActions;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.actor.world._msgModule.Action;
import ws.gameServer.features.actor.world.ctrl.WorldCtrl;
import ws.gameServer.features.actor.world.msg.In_NoticeToKillOverTimeCachePlayerActorRequest;
import ws.gameServer.features.actor.world.playerIOStatus.IOStatus;
import ws.gameServer.features.actor.world.playerIOStatus.PlayerIOStatus;
import ws.gameServer.features.actor.world.playerIOStatus.Status;

public class Action_NoticeToKillOverTimeCachePlayerActorRequest implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(Action_NoticeToKillOverTimeCachePlayerActorRequest.class);

    @Override
    public void onRecv(Object msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        if (msg instanceof In_NoticeToKillOverTimeCachePlayerActorRequest) {
            noticeToKillOverTimeCachePlayerActorRequest((In_NoticeToKillOverTimeCachePlayerActorRequest) msg, worldCtrl, worldActorContext, sender);
        }
    }

    private void noticeToKillOverTimeCachePlayerActorRequest(In_NoticeToKillOverTimeCachePlayerActorRequest msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        for (PlayerIOStatus playerIOStatus : worldCtrl.getTarget().getPlayerIdToIOStatus().values()) {
            if (playerIOStatus.getIoStatus() == IOStatus.Inend && playerIOStatus.getStatus() == Status.Offline) {
                if ((System.currentTimeMillis() - playerIOStatus.getLastHandleCheckOnlineMsgTime()) > msg.getOverTime() * 60 * 1000) {
                    playerIOStatus.prepareToKillingPlayerIOActor();
                    LOGGER.debug("玩家PlayerId={} 即将被移除,现在通知其保存玩家数据到数据库中！", playerIOStatus.getPlayerId());
                }
            }
        }
    }
}
