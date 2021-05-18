package ws.gameServer.features.actor.world.playerIOStatus.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.general.EnumUtils;
import ws.gameServer.features.actor.cluster.ClusterListener;
import ws.gameServer.features.actor.world.playerIOStatus.IOStatus;
import ws.gameServer.features.actor.world.playerIOStatus.PlayerIOStatus;
import ws.gameServer.features.actor.world.playerIOStatus.Status;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerLoginProtos.Sm_Login;
import ws.relationship.appServers.loginServer.player.msg.In_ConnectToPlayerRequest;
import ws.relationship.appServers.loginServer.player.msg.In_ConnectToPlayerResponse;
import ws.relationship.base.msg.In_MessagePassToGatewayServer;
import ws.relationship.utils.ProtoUtils;

public class Action_ConnectToPlayer implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(Action_ConnectToPlayer.class);

    @Override
    public void onRecv(Object msg, PlayerIOStatus playerIO) throws Exception {
        if (msg instanceof In_ConnectToPlayerRequest) {
            connectToPlayerRequest((In_ConnectToPlayerRequest) msg, playerIO);
        } else if (msg instanceof In_ConnectToPlayerResponse) {
            connectToPlayerResponse((In_ConnectToPlayerResponse) msg, playerIO);
        }
    }

    private void connectToPlayerRequest(In_ConnectToPlayerRequest msg, PlayerIOStatus playerIOStatus) throws Exception {
        String playerId = msg.getPlayerId();
        IOStatus ioStatus = playerIOStatus.getIoStatus();
        boolean canLogin = false;
        if (ioStatus == IOStatus.NULL || ioStatus == IOStatus.Killend) {
            canLogin = true;
            playerIOStatus.createPlayerIOActor();
            msg.setNewLogin(true);
            LOGGER.debug("playerId={} ioStatus={} 玩家数据不在内存中，直接最新的登陆！", playerId, ioStatus);
        } else if (IOStatus.isIntermediateStatus(ioStatus)) {
            LOGGER.debug("playerId={} ioStatus={} 当前状态不正常，拒绝登录，请稍后再试！", playerId, ioStatus);
        } else if (ioStatus == IOStatus.Inend) {
            canLogin = true;
            msg.setNewLogin(false);
            LOGGER.debug("playerId={} ioStatus={} 玩家数据在内存中，可以重新登录/断线重连！", playerId, ioStatus);
        }
        if (canLogin) {
            playerIOStatus.setIoStatus(IOStatus.Ining);
            playerIOStatus.getPlayerIOActorRef().tell(msg, playerIOStatus.getWorldActorContext().self());
        } else { // 通知客户端登录失败
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_Login, msg.getAction());
            Sm_Login.Builder b = Sm_Login.newBuilder();
            b.setAction(msg.getAction());
            br.setSmLogin(b);
            msg.getGatewaySender().tell(new In_MessagePassToGatewayServer(ClusterListener.getAddress(), msg.getConnFlag(), br.build(), EnumUtils.protoActionToString(msg.getAction())), playerIOStatus.getWorldActorContext().self());
        }
    }

    private void connectToPlayerResponse(In_ConnectToPlayerResponse msg, PlayerIOStatus playerIOStatus) {
        if (msg.isRs()) { // 登录成功
            playerIOStatus.setIoStatus(IOStatus.Inend);
            playerIOStatus.setStatus(Status.Online);  // 登录成功后调整为在线状态
        } else { // 登录失败
            LOGGER.debug("playerId={} ioStatus={} 登录失败！", msg.getRequest().getPlayerId());
            playerIOStatus.killingPlayerIOActor();
        }
    }
}
