package ws.chatServer.system.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import ws.chatServer.features.actor.chat.ChatManagerActor;
import ws.chatServer.features.actor.message.MessageTransferActor;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.utils.TestActor;

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
        context().watch(context().actorOf(Props.create(TestActor.class), ActorSystemPath.WS_GatewayServer_Test));
        context().watch(context().actorOf(Props.create(ChatManagerActor.class), ActorSystemPath.WS_ChatServer_ChatManager));
    }

    @Override
    public void onRecv(Object msg) throws Exception {

    }
}
