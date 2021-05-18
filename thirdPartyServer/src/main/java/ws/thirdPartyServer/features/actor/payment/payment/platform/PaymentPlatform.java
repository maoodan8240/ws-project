package ws.thirdPartyServer.features.actor.payment.payment.platform;

import akka.actor.ActorRef;
import ws.relationship.appServers.thirdPartyServer.In_VerifyingPayment;
import ws.thirdPartyServer.features.http.msg.In_HttpRequestContent;

public interface PaymentPlatform {

    void processPaymentRequestContent(In_HttpRequestContent content, ActorRef sender, ActorRef self) throws Exception;

    void verifyingPaymentRequest(In_VerifyingPayment.Request request, ActorRef sender, ActorRef self) throws Exception;

}
