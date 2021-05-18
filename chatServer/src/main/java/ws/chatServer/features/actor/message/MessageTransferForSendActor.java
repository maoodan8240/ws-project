package ws.chatServer.features.actor.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.chatServer.system.actor.WsActorSystem;
import ws.common.network.server.handler.tcp.MessageSendHolder;
import ws.common.network.server.interfaces.Connection;
import ws.protos.ChatProtos.Sm_ChatServerLogin;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.In_MessagePassToGatewayServer;
import ws.relationship.base.msg.chat.AddChatServerPlayer;

public class MessageTransferForSendActor extends WsActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageTransferForSendActor.class);

    @Override
    public void onRecv(Object msg) throws Exception {
        // 向客户端发送消息
        if (msg instanceof In_MessagePassToGatewayServer) {
            In_MessagePassToGatewayServer sendHolder = (In_MessagePassToGatewayServer) msg;
            sendHolder.getTimes().add(System.nanoTime());
            on_In_MessagePassToGatewayServer(sendHolder);
        }
    }

    /**
     * 向客户端发送消息
     *
     * @param sendHolder
     */
    private void on_In_MessagePassToGatewayServer(In_MessagePassToGatewayServer sendHolder) {
        if (ConnectionContainer.containsFlag_FTC(sendHolder.getConnFlag())) {
            Connection connection = ConnectionContainer.getConnectionByFlag_FTC(sendHolder.getConnFlag());
            if (connection == null) {
                LOGGER.warn("服务器向客户端发送消息时ConnFlag存在, 但是Connection不存在！sendHolder={}", sendHolder.toString());
                return;
            }
            if (sendHolder.getMessage() instanceof Response) {
                Response Response = (Response) sendHolder.getMessage();
                // 登录且登录成功时记录Flag对应的GameServer的Address
                if (Response.getMsgCode() == Code.Sm_ChatServerLogin) {
                    Sm_ChatServerLogin sm_Login = Response.getSmChatServerLogin();
                    if (sm_Login.getAction() == Sm_ChatServerLogin.Action.RESP_AUTH) {
                        if (Response.getResult()) {
                            ConnectionContainer.put(sendHolder.getConnFlag(), sm_Login.getRpid(), sm_Login.getInnerRealmId(), sm_Login.getOuterRealmId());
                            AddChatServerPlayer.Request addRequest = new AddChatServerPlayer.Request(sm_Login.getRpid(), sm_Login.getInnerRealmId(), sm_Login.getOuterRealmId());
                            WsActorSystem.get().actorSelection(ActorSystemPath.WS_ChatServer_Selection_ChatManager).tell(addRequest, self());
                            LOGGER.debug("ConnFlag={} Connection={}登录验证-通过！GameId={} OuterRealmId={} ", sendHolder.getConnFlag(), connection, sm_Login.getRpid(), sm_Login.getOuterRealmId());
                        } else {
                            LOGGER.debug("ConnFlag={} Connection={}登录验证-失败！", sendHolder.getConnFlag(), connection);
                        }
                    }
                }
                connection.send(new MessageSendHolder(sendHolder.getMessage(), sendHolder.getMsgActionName(), sendHolder.getTimes()));
            } else {
                LOGGER.warn("向客户端发送信息时，发送的对象不为Response！ sendHolder={}", sendHolder.toString());
            }
        } else {
            LOGGER.warn("服务器向客户端发送消息时ConnFlag不存在！sendHolder={}", sendHolder.toString());
        }
    }
}