package ws.chatServer.system.network;

import akka.actor.ActorRef;
import ws.common.network.server.handler.tcp.MessageReceiveHolder;
import ws.common.network.server.interfaces.Connection;
import ws.common.network.server.interfaces.NetworkListener;
import ws.chatServer.system.actor.WsActorSystem;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.In_MessageReceiveHolder;

public class BroadcastNetworkMessageListener implements NetworkListener {

    @Override
    public void onReceive(MessageReceiveHolder receiveHolder) {
        WsActorSystem.get().actorSelection(ActorSystemPath.WS_GatewayServer_Selection_MessageTransfer).tell(new In_MessageReceiveHolder(receiveHolder), ActorRef.noSender());
    }

    @Override
    public void onHeartBeating(Connection connection) {
        WsActorSystem.get().actorSelection(ActorSystemPath.WS_GatewayServer_Selection_MessageTransfer).tell(new In_ConnectionStatusRequest(connection, In_ConnectionStatusRequest.Type.HeartBeating), ActorRef.noSender());
    }

    @Override
    public void onOffline(Connection connection) {
        WsActorSystem.get().actorSelection(ActorSystemPath.WS_GatewayServer_Selection_MessageTransfer).tell(new In_ConnectionStatusRequest(connection, In_ConnectionStatusRequest.Type.Offline), ActorRef.noSender());
    }

    @Override
    public void onDisconnected(Connection connection) {
        WsActorSystem.get().actorSelection(ActorSystemPath.WS_GatewayServer_Selection_MessageTransfer).tell(new In_ConnectionStatusRequest(connection, In_ConnectionStatusRequest.Type.Disconnected), ActorRef.noSender());
    }
}
