package ws.thirdPartyServer.system.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.thirdPartyServer.features.actor.loginCheck.loginCheckCenter.LoginCheckCenterActor;
import ws.thirdPartyServer.features.actor.payment.paymentCenter.PaymentCenterActor;

public class WSRootActor extends WsActor {

    public WSRootActor() {
        createChild();
    }

    /**
     * 创建子actor
     */
    private void createChild() {
        ActorRef ref1 = getContext().actorOf(Props.create(PaymentCenterActor.class), ActorSystemPath.WS_ThirdPartyServer_PaymentCenter);
        ActorRef ref2 = getContext().actorOf(Props.create(LoginCheckCenterActor.class), ActorSystemPath.WS_ThirdPartyServer_LoginCheckCenter);

        context().watch(ref1);
        context().watch(ref2);
    }

    @Override
    public void onRecv(Object msg) throws Exception {

    }
}
