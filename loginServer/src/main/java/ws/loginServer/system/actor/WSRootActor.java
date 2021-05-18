package ws.loginServer.system.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import ws.loginServer.features.actor.login.LoginActor;
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
        ActorRef ref1 = context().actorOf(Props.create(LoginActor.class), ActorSystemPath.WS_LoginServer_Login);
        context().watch(ref1);
    }

    @Override
    public void onRecv(Object msg) throws Exception {

    }
}
