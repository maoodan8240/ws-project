package ws.gameServer.features.actor.world._msgModule.msgActions;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.actor.world._msgModule.Action;
import ws.gameServer.features.actor.world.ctrl.WorldCtrl;
import ws.relationship.base.msg.CheckPlayerOnlineMsgRequest;

public class Action_CheckPlayerOnlineMsgRequest implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(Action_CheckPlayerOnlineMsgRequest.class);

    @Override
    public void onRecv(Object msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        if (msg instanceof CheckPlayerOnlineMsgRequest) {
            checkPlayerOnlineMsgRequest((CheckPlayerOnlineMsgRequest<?>) msg, worldCtrl, worldActorContext, sender);
        }
    }

    private void checkPlayerOnlineMsgRequest(CheckPlayerOnlineMsgRequest<?> request, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        LOGGER.debug("接收到了发往playerId={} 的CheckPlayerOnlineMsg消息={} ", request.getCheckPlayerId(), request.getAttachMsg().getClass());
        request.setSender(sender);
        worldCtrl.getPlayerIOStatus(request.getCheckPlayerId(), worldActorContext).handleMsg(request);
    }
}
