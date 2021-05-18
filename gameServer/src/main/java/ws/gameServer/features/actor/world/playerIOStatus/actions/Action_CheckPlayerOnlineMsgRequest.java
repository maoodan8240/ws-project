package ws.gameServer.features.actor.world.playerIOStatus.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.actor.world.playerIOStatus.IOStatus;
import ws.gameServer.features.actor.world.playerIOStatus.PlayerIOStatus;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.CheckPlayerOnlineMsgRequest;

public class Action_CheckPlayerOnlineMsgRequest implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(Action_CheckPlayerOnlineMsgRequest.class);

    @Override
    public void onRecv(Object msg, PlayerIOStatus playerIO) throws Exception {
        if (msg instanceof CheckPlayerOnlineMsgRequest) {
            checkPlayerOnlineMsgRequest((CheckPlayerOnlineMsgRequest<?>) msg, playerIO);
        }
    }

    private void checkPlayerOnlineMsgRequest(CheckPlayerOnlineMsgRequest<?> msg, PlayerIOStatus playerIOStatus) throws Exception {
        LOGGER.debug("接收到了发往CheckMsgPlayerId={} IOPlayerId={} 的消息={} ", msg.getCheckPlayerId(), playerIOStatus.getPlayerId(), msg.getAttachMsg().getClass());
        String playerId = msg.getCheckPlayerId();
        IOStatus ioStatus = playerIOStatus.getIoStatus();
        if (IOStatus.isIntermediateStatus(ioStatus)) {
            playerIOStatus.getWaitingSendCheckOnlineMsgs().add(msg);
            LOGGER.debug("playerId={} ioStatus={} 玩家正在登录中 | 正在退出内存 | 正在处理离线消息，消息进入缓存，稍后发送！", playerId, ioStatus);
        } else if (ioStatus == IOStatus.Inend) { // 玩家数据在内存中，直接给该玩家Actor发送信息
            String playerActorPath = ActorSystemPath.WS_GameServer_Selection_Player.replaceAll("\\*", playerId);
            while (!playerIOStatus.getWaitingSendCheckOnlineMsgs().isEmpty()) {
                CheckPlayerOnlineMsgRequest<?> msgTmp = playerIOStatus.getWaitingSendCheckOnlineMsgs().remove(0);
                playerIOStatus.getWorldActorContext().actorSelection(playerActorPath).tell(msgTmp.getAttachMsg(), msgTmp.getSender());
            }
            playerIOStatus.getWorldActorContext().actorSelection(playerActorPath).tell(msg.getAttachMsg(), msg.getSender());
            LOGGER.debug("playerId={} ioStatus={} 玩家玩家数据【在】内存中，直接发送消息给玩家！ playerActorPath={} ", playerId, ioStatus, playerActorPath);
        } else { // ioStatus == IOStatus.Killend || ioStatus == IOStatus.NULL
            playerIOStatus.getWaitingSendCheckOnlineMsgs().add(msg);
            playerIOStatus.createPlayerIOActor();
            playerIOStatus.sendCheckPlayerOnlineMsg();
            LOGGER.debug("playerId={} ioStatus={} 玩家玩家数据【不在】内存中，给玩家发送离线消息！", playerId, ioStatus);
        }
    }
}
