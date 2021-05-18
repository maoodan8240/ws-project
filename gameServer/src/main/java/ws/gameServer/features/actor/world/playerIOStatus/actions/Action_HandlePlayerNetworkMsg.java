package ws.gameServer.features.actor.world.playerIOStatus.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.actor.world.playerIOStatus.IOStatus;
import ws.gameServer.features.actor.world.playerIOStatus.PlayerIOStatus;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.In_MessagePassToOtherServer;

public class Action_HandlePlayerNetworkMsg implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(Action_HandlePlayerNetworkMsg.class);

    @Override
    public void onRecv(Object msg, PlayerIOStatus playerIO) throws Exception {
        if (msg instanceof In_MessagePassToOtherServer) {
            In_MessagePassToOtherServer messagePassToOtherServer = (In_MessagePassToOtherServer) msg;
            messagePassToOtherServer.getTimes().add(System.nanoTime());
            sendNetworkMsgToPlayer(messagePassToOtherServer, playerIO);
        }
    }

    private void sendNetworkMsgToPlayer(In_MessagePassToOtherServer msg, PlayerIOStatus playerIOStatus) {
        String playerId = msg.getPlayerId();
        IOStatus ioStatus = playerIOStatus.getIoStatus();
        if (ioStatus == IOStatus.Inend) {
            // 玩家数据在内存中，直接给该玩家Actor发送信息
            String playerActorPath = ActorSystemPath.WS_GameServer_Selection_Player.replaceAll("\\*", playerId);
            playerIOStatus.getWorldActorContext().actorSelection(playerActorPath).tell(msg, playerIOStatus.getWorldActorContext().self());
        } else {
            LOGGER.warn("不正常，玩家={}, 连接当前状态为={}, 不应该再接到客户端消息了，但是收到了={}！", playerId, playerIOStatus.getIoStatus(), msg);
        }
    }
}
