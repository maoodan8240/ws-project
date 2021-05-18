package ws.gameServer.features.actor.world.playerIOStatus.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.actor.world.playerIOStatus.IOStatus;
import ws.gameServer.features.actor.world.playerIOStatus.PlayerIOStatus;
import ws.gameServer.features.actor.world.playerIOStatus.Status;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.AbstractGateWayServerInnerMsg;
import ws.relationship.base.msg.In_PlayerDisconnectedRequest;
import ws.relationship.base.msg.In_PlayerHeartBeatingRequest;
import ws.relationship.base.msg.In_PlayerOfflineRequest;

public class Action_ConnectionStatus implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(Action_ConnectionStatus.class);

    @Override
    public void onRecv(Object msg, PlayerIOStatus playerIO) throws Exception {
        if (msg instanceof In_PlayerOfflineRequest /* 离线通知 */
                || msg instanceof In_PlayerHeartBeatingRequest /* 心跳信息通知 */
                || msg instanceof In_PlayerDisconnectedRequest /* 断开连接通知 */) {
            connectionStatus((AbstractGateWayServerInnerMsg) msg, playerIO);
        }
    }

    private void connectionStatus(AbstractGateWayServerInnerMsg msg, PlayerIOStatus playerIOStatus) throws Exception {
        String playerId = msg.getPlayerId();
        if (msg instanceof In_PlayerHeartBeatingRequest) {
            playerIOStatus.setStatus(Status.Online);
            LOGGER.debug("playerId={} ioStatus={} Status={} 接收到心跳信息！", playerId, playerIOStatus.getIoStatus(), playerIOStatus.getStatus());
        } else {// In_PlayerHeartBeatingRequest | In_PlayerDisconnectedRequest
            playerIOStatus.setStatus(Status.Offline);
            LOGGER.debug("playerId={} ioStatus={} Status={} 接收到离线信息信息！ msg={} ", playerId, playerIOStatus.getIoStatus(), playerIOStatus.getStatus(), msg.getClass().getSimpleName());
        }
        if (playerIOStatus.getIoStatus() == IOStatus.Inend) {
            // 玩家数据在内存中，直接给该玩家Actor发送信息
            String playerActorPath = ActorSystemPath.WS_GameServer_Selection_Player.replaceAll("\\*", playerId);
            playerIOStatus.getWorldActorContext().actorSelection(playerActorPath).tell(msg, playerIOStatus.getWorldActorContext().self());
        }
    }
}
