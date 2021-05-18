package ws.gatewayServer.features.actor.message;

import akka.actor.ActorContext;
import akka.actor.Address;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.network.server.handler.tcp.MessageSendHolder;
import ws.common.network.server.interfaces.Connection;
import ws.common.utils.general.EnumUtils;
import ws.gatewayServer.system.actor.WsActorSystem;
import ws.gatewayServer.system.network.In_ConnectionStatusRequest;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.EnumsProtos.ErrorCodeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerLoginProtos.Cm_Login;
import ws.protos.PlayerLoginProtos.Sm_NeedReLogin;
import ws.protos.PlayerLoginProtos.Sm_NeedReLogin.Action;
import ws.relationship.base.ServerRoleEnum;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.In_MessagePassToOtherServer;
import ws.relationship.base.msg.In_MessageReceiveHolder;
import ws.relationship.base.msg.In_PlayerDisconnectedRequest;
import ws.relationship.base.msg.In_PlayerHeartBeatingRequest;
import ws.relationship.base.msg.In_PlayerOfflineRequest;
import ws.relationship.utils.ClusterMessageSender;

import java.util.ArrayList;

public class MessageTransferForReceiveActor extends WsActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageTransferForReceiveActor.class);
    private final ActorContext messageTransferContext;

    public MessageTransferForReceiveActor(ActorContext messageTransferContext) {
        this.messageTransferContext = messageTransferContext;
    }

    @Override
    public void onRecv(Object msg) throws Exception {
        // 接受到客户端发送来的消息
        if (msg instanceof In_MessageReceiveHolder) {
            In_MessageReceiveHolder receiveHolder = (In_MessageReceiveHolder) msg;
            on_In_MessageReceiveHolder(receiveHolder);
            receiveHolder.clear();
            receiveHolder = null;
        } else if (msg instanceof In_ConnectionStatusRequest) {
            on_In_ConnectionStatusRequest((In_ConnectionStatusRequest) msg);
        }
    }

    /**
     * 接受到客户端发送来的消息
     *
     * @param receiveHolder
     */
    private void on_In_MessageReceiveHolder(In_MessageReceiveHolder receiveHolder) {
        if (receiveHolder.getMessage() instanceof Cm_Login) {// Cm_Login 发送到 WS-LoginServer
            In_MessagePassToOtherServer messagePassToOtherServer = null;
            if (ConnectionContainer.containsConnInFlagToConn(receiveHolder.getConnection())) { // 同一个连接上发送的多次登录请求
                String connFlag = ConnectionContainer.getFlagByConnInFlagToConn(receiveHolder.getConnection());
                messagePassToOtherServer = new In_MessagePassToOtherServer(messageTransferContext.self(), connFlag, receiveHolder.getMessage());
            } else { // 新的连接上发送的登录请求
                messagePassToOtherServer = new In_MessagePassToOtherServer(messageTransferContext.self(), ObjectId.get().toString(), receiveHolder.getMessage());
                ConnectionContainer.put(messagePassToOtherServer.getConnFlag(), receiveHolder.getConnection());
            }
            messagePassToOtherServer.getTimes().addAll(receiveHolder.joinTime(System.nanoTime()));
            ClusterMessageSender.sendMsgToServer(messageTransferContext, ServerRoleEnum.WS_LoginServer, messagePassToOtherServer, ActorSystemPath.WS_LoginServer_Selection_Login);
        } else {// 其他发送到 WS-GameServer
            if (!ConnectionContainer.containsConnInFlagToConn(receiveHolder.getConnection())) {
                LOGGER.warn("链接管理中心不存在该连接，直接关闭！receiveHolder={}", receiveHolder.toString());
                receiveHolder.getConnection().close();
                return;
            }
            if (ConnectionContainer.containsAttachmentByConnInFlagToConn(receiveHolder.getConnection())) {
                String connFlag = ConnectionContainer.getFlagByConnInFlagToConn(receiveHolder.getConnection());
                ConnectionAttachment attachment = ConnectionContainer.getAttachmentByConnInFlagToConn(receiveHolder.getConnection());
                Address address = attachment.getAddress();
                In_MessagePassToOtherServer messagePassToOtherServer = new In_MessagePassToOtherServer(messageTransferContext.self(), connFlag, receiveHolder.getMessage(), attachment.getGameId());
                messagePassToOtherServer.getTimes().addAll(receiveHolder.joinTime(System.nanoTime()));
                WsActorSystem.get().actorSelection(address.toString() + ActorSystemPath.WS_GameServer_Selection_World).tell(messagePassToOtherServer, messageTransferContext.self());
            } else {
                needReLogin(receiveHolder.getConnection());
                LOGGER.warn("有请求发送到Gameserver！但是玩家还没有成功登录Gameserver,通知客户端重新登录！ receiveHolder={}", receiveHolder.toString());
            }
        }
    }

    /**
     * 发生连接的状态
     *
     * @param request
     */
    private void on_In_ConnectionStatusRequest(In_ConnectionStatusRequest request) {
        if (!ConnectionContainer.containsConnInFlagToConn(request.getConnection())) {
            LOGGER.warn("链接管理中心不存在该连接，直接关闭！receiveHolder={}", request.toString());
            request.getConnection().close();
            return;
        }
        Object msg = null;
        if (ConnectionContainer.containsAttachmentByConnInFlagToConn(request.getConnection())) {
            String connFlag = ConnectionContainer.getFlagByConnInFlagToConn(request.getConnection());
            ConnectionAttachment attachment = ConnectionContainer.getAttachmentByConnInFlagToConn(request.getConnection());
            Address address = attachment.getAddress();
            if (request.getType() == In_ConnectionStatusRequest.Type.HeartBeating) {
                msg = new In_PlayerHeartBeatingRequest(connFlag, attachment.getGameId());
            } else if (request.getType() == In_ConnectionStatusRequest.Type.Offline) {
                msg = new In_PlayerOfflineRequest(connFlag, attachment.getGameId());
            } else if (request.getType() == In_ConnectionStatusRequest.Type.Disconnected) {
                msg = new In_PlayerDisconnectedRequest(connFlag, attachment.getGameId());
                ConnectionContainer.remove(request.getConnection());
            } else {
                return;
            }
            WsActorSystem.get().actorSelection(address.toString() + ActorSystemPath.WS_GameServer_Selection_World).tell(msg, messageTransferContext.self());
        } else {
            needReLogin(request.getConnection());
            LOGGER.warn("有请求发送到Gameserver！但是玩家还没有成功登录Gameserver,通知客户端重新登录！ requestType={}", request.getType());
        }
    }

    /**
     * 通知客户端重新登录
     *
     * @param connection
     */
    private void needReLogin(Connection connection) {
        Sm_NeedReLogin.Builder builder = Sm_NeedReLogin.newBuilder();
        builder.setAction(Action.RESP_RELOGIN);
        Response.Builder resp = Response.newBuilder();
        resp.setMsgCode(Code.Sm_NeedReLogin);
        resp.setResult(true);
        resp.setErrorCode(ErrorCodeEnum.UNKNOWN);
        resp.setSmMsgAction(EnumUtils.protoActionToString(Action.RESP_RELOGIN));
        resp.setSmNeedReLogin(builder);
        connection.send(new MessageSendHolder(resp.build(), resp.getSmMsgAction(), new ArrayList<>()));
    }
}
