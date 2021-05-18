package ws.thirdPartyServer.features.actor.payment.paymentCenter;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.relationship.appServers.thirdPartyServer.In_VerifyingPayment;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.thirdPartyServer.features.actor.payment.payment.PaymentActor;
import ws.thirdPartyServer.features.http.msg.In_HttpRequestContent;

import java.util.HashMap;
import java.util.Map;

public class PaymentCenterActor extends WsActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentCenterActor.class);
    private Map<PlatformTypeEnum, ActorRef> platformToActorRef = new HashMap<>();

    public PaymentCenterActor() {
        init();
    }

    @Override
    public void onRecv(Object msg) throws Exception {
        if (msg instanceof In_HttpRequestContent) {
            In_HttpRequestContent content = (In_HttpRequestContent) msg;
            onHttpRequestContent(content);
        } else if (msg instanceof In_VerifyingPayment.Request) {
            onIn_VerifyingPaymentRequest((In_VerifyingPayment.Request) msg);
        }
    }

    private void onHttpRequestContent(In_HttpRequestContent content) {
        ActorRef actorRef = platformToActorRef.get(content.getPlatformTypeEnum());
        if (actorRef == null) {
            LOGGER.warn("处理渠道的Actor不存在！！！！");
            return;
        }
        actorRef.tell(content, sender());
    }

    private void onIn_VerifyingPaymentRequest(In_VerifyingPayment.Request request) {
        ActorRef actorRef = platformToActorRef.get(request.getPlatformType());
        if (actorRef == null) {
            LOGGER.warn("处理渠道的Actor不存在！！！！");
            return;
        }
        actorRef.tell(request, sender());
    }

    private void init() {
        platformToActorRef.clear();
        for (PlatformTypeEnum platformTypeEnum : PlatformTypeEnum.values()) {
            ActorRef ref = getContext().actorOf(Props.create(PaymentActor.class, platformTypeEnum), ActorSystemPath.WS_ThirdPartyServer_Payment + platformTypeEnum.toString());
            getContext().watch(ref);
            platformToActorRef.put(platformTypeEnum, ref);
        }
    }
}
