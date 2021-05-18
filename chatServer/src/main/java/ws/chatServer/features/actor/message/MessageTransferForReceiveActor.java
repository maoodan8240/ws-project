package ws.chatServer.features.actor.message;

import akka.actor.ActorContext;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.chatServer.system.actor.WsActorSystem;
import ws.chatServer.system.network.In_ConnectionStatusRequest;
import ws.common.network.server.handler.tcp.MessageSendHolder;
import ws.common.network.server.interfaces.Connection;
import ws.common.utils.general.EnumUtils;
import ws.protos.ChatProtos.Cm_ChatServerLogin;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.EnumsProtos.ErrorCodeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerLoginProtos.Sm_NeedReLogin;
import ws.protos.PlayerLoginProtos.Sm_NeedReLogin.Action;
import ws.relationship.base.PlayerRealmPair;
import ws.relationship.base.ServerRoleEnum;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.In_MessagePassToOtherServer;
import ws.relationship.base.msg.In_MessageReceiveHolder;
import ws.relationship.base.msg.chat.RemoveChatServerPlayer;
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
        if (receiveHolder.getMessage() instanceof Cm_ChatServerLogin) {// Cm_ChatServerLogin 发送到 WS-LoginServer
            In_MessagePassToOtherServer messagePassToOtherServer = null;
            if (ConnectionContainer.containsConnection_FTC(receiveHolder.getConnection())) { // 同一个连接上发送的多次登录请求
                messagePassToOtherServer = new In_MessagePassToOtherServer(messageTransferContext.self(), ConnectionContainer.getFlagByConnection_FTC(receiveHolder.getConnection()), receiveHolder.getMessage());
            } else { // 新的连接上发送的登录请求
                messagePassToOtherServer = new In_MessagePassToOtherServer(messageTransferContext.self(), ObjectId.get().toString(), receiveHolder.getMessage());
                ConnectionContainer.put(messagePassToOtherServer.getConnFlag(), receiveHolder.getConnection());
            }
            LOGGER.debug("ConnFlag={} Connection={}请求登录,正在发送LoginServer进行验证！", messagePassToOtherServer.getConnFlag(), receiveHolder.getConnection());
            messagePassToOtherServer.getTimes().addAll(receiveHolder.joinTime(System.nanoTime()));
            ClusterMessageSender.sendMsgToServer(messageTransferContext, ServerRoleEnum.WS_LoginServer, messagePassToOtherServer, ActorSystemPath.WS_LoginServer_Selection_Login);
        } else {// 其他发送到 WS-ChatServer
            if (ConnectionContainer.containsConnection_FTC(receiveHolder.getConnection())) {
                String flag = ConnectionContainer.getFlagByConnection_FTC(receiveHolder.getConnection());
                PlayerRealmPair realmPair = ConnectionContainer.getRealmPairByFlag_FTC(flag);
                if (realmPair == null) {
                    needReLogin(receiveHolder.getConnection());
                    LOGGER.warn("有请求发送到ChatServer！但是玩家还没有成功登录到ChatServer,通知客户端重新登录！flag={} receiveHolder={} .", flag, receiveHolder.toString());
                    return;
                }
                In_MessagePassToOtherServer messagePassToOtherServer = new In_MessagePassToOtherServer(messageTransferContext.self(), flag, receiveHolder.getMessage(), realmPair.getPlayerId());
                messagePassToOtherServer.getTimes().addAll(receiveHolder.joinTime(System.nanoTime()));
                WsActorSystem.get().actorSelection(ActorSystemPath.WS_ChatServer_Selection_ChatManager).tell(messagePassToOtherServer, messageTransferContext.self());
            } else {
                needReLogin(receiveHolder.getConnection());
                LOGGER.warn("有请求发送到ChatServer！但是玩家还没有成功连接到ChatServer,通知客户端重新登录！ receiveHolder={}", receiveHolder.toString());
            }
        }
    }

    /**
     * 发生连接的状态
     *
     * @param request
     */
    private void on_In_ConnectionStatusRequest(In_ConnectionStatusRequest request) {
        if (ConnectionContainer.containsConnection_FTC(request.getConnection())) {
            if (request.getType() == In_ConnectionStatusRequest.Type.Disconnected) {
                PlayerRealmPair pair = ConnectionContainer.remove(request.getConnection());
                if (pair != null) {
                    RemoveChatServerPlayer.Request removeRequest = new RemoveChatServerPlayer.Request(pair.getInnerRealmId(), pair.getPlayerId());
                    WsActorSystem.get().actorSelection(ActorSystemPath.WS_ChatServer_Selection_ChatManager).tell(removeRequest, messageTransferContext.self());
                }
            } else {
                // HeartBeating OR Offline 直接忽略
                return;
            }
        } else {
            LOGGER.warn("有请求发送到ChatServer！但是玩家还没有连接ChatServer！ requestType={}", request.getType());
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
