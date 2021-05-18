package ws.thirdPartyServer.features.actor.payment.payment.platform;

import akka.actor.ActorRef;
import ws.relationship.appServers.thirdPartyServer.In_VerifyingPayment;
import ws.thirdPartyServer.features.http.HttpMethodEnums;
import ws.thirdPartyServer.features.http.msg.In_HttpRequestContent;

public abstract class AbstractPaymentPlatform implements PaymentPlatform {

    @Override
    public final void processPaymentRequestContent(In_HttpRequestContent content, ActorRef sender, ActorRef self) throws Exception {
        if (content == null || content.getHttpMethodEnums() == null) {
            return;
        }
        if (content.getHttpMethodEnums() == HttpMethodEnums.GET) {
            processPaymentRequestContent_Get(content, sender, self);
        } else if (content.getHttpMethodEnums() == HttpMethodEnums.POST) {
            processPaymentRequestContent_Post(content, sender, self);
        }
    }

    public void processPaymentRequestContent_Get(In_HttpRequestContent content, ActorRef sender, ActorRef self) throws Exception {

    }

    public void processPaymentRequestContent_Post(In_HttpRequestContent content, ActorRef sender, ActorRef self) throws Exception {

    }

    @Override
    public void verifyingPaymentRequest(In_VerifyingPayment.Request request, ActorRef sender, ActorRef self) throws Exception {

    }
}
