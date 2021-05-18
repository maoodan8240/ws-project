package ws.particularFunctionServer.system.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import ws.particularFunctionServer.features.standalone.mailCenter.MailsCenterActor;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;

public class WsRootActor extends WsActor {

    public WsRootActor() {
        createChild();
    }

    /**
     * 创建子actor
     */
    private void createChild() {
        ActorRef ref1 = getContext().actorOf(Props.create(MailsCenterActor.class), ActorSystemPath.WS_ThirdPartyServer_MailsCenterActor);
        context().watch(ref1);
    }

    @Override
    public void onRecv(Object msg) throws Exception {

    }
}
