package ws.gatewayServer.system.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import ws.gatewayServer.features.actor.message.MessageTransferActor;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;

public class WSRootActor extends WsActor {

    public WSRootActor() {
        createChild();
    }

    /**
     * 创建子actor
     */
    private void createChild() {
        ActorRef ref = context().actorOf(Props.create(MessageTransferActor.class), ActorSystemPath.WS_GatewayServer_MessageTransfer);
        context().watch(ref);
    }

    @Override
    public void onRecv(Object msg) throws Exception {

    }
}
