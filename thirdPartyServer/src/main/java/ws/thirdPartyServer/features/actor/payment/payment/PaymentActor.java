package ws.thirdPartyServer.features.actor.payment.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.di.GlobalInjector;
import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.relationship.appServers.thirdPartyServer.In_VerifyingPayment;
import ws.relationship.base.actor.WsActor;
import ws.thirdPartyServer.features.actor.payment.payment.platform.PaymentPlatform;
import ws.thirdPartyServer.features.actor.payment.payment.platform.PaymentPlatformHandlerManager;
import ws.thirdPartyServer.features.http.msg.In_HttpRequestContent;


public class PaymentActor extends WsActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentActor.class);
    private PlatformTypeEnum PlatformTypeEnum;

    public PaymentActor(PlatformTypeEnum PlatformTypeEnum) {
        this.PlatformTypeEnum = PlatformTypeEnum;
    }

    @Override
    public void onRecv(Object msg) throws Exception {
        if (msg instanceof In_HttpRequestContent) {
            onHttpRequestContent((In_HttpRequestContent) msg);
        } else if (msg instanceof In_VerifyingPayment.Request) {
            onVerifyingPaymentRequest((In_VerifyingPayment.Request) msg);
        }
    }

    private void onHttpRequestContent(In_HttpRequestContent content) {
        LOGGER.info("\nBBB > [支付] > 收到第三方返回的充值数据content={}", content.toString());

        PlatformTypeEnum PlatformTypeEnum = content.getPlatformTypeEnum();
        PaymentPlatform paymentPlatform = getPaymentPlatform(PlatformTypeEnum);
        if (paymentPlatform == null) {
            return;
        }
        try {
            paymentPlatform.processPaymentRequestContent(content, sender(), self());
        } catch (Exception e) {
            LOGGER.error("处理第三方支付信息处理失败！", e);
        }
    }

    private void onVerifyingPaymentRequest(In_VerifyingPayment.Request request) {
        PlatformTypeEnum PlatformTypeEnum = request.getPlatformType();
        PaymentPlatform paymentPlatform = getPaymentPlatform(PlatformTypeEnum);
        if (paymentPlatform == null) {
            return;
        }
        try {
            paymentPlatform.verifyingPaymentRequest(request, sender(), self());
        } catch (Exception e) {
            LOGGER.error("处理第三方支付信息验证失败！", e);
        }
    }

    private PaymentPlatform getPaymentPlatform(PlatformTypeEnum PlatformTypeEnum) {
        if (PlatformTypeEnum != this.PlatformTypeEnum) {
            LOGGER.error("奇怪！PlatformTypeEnum ={} 的支付请求怎么会发到PlatformTypeEnum ={}的Actor上呢！", PlatformTypeEnum, this.PlatformTypeEnum);
            return null;
        }
        PaymentPlatformHandlerManager ctrl = GlobalInjector.getInstance(PaymentPlatformHandlerManager.class);
        PaymentPlatform paymentPlatform = ctrl.get(PlatformTypeEnum);
        if (paymentPlatform == null) {
            LOGGER.error("糟糕！在PaymentPlatformHandlerManager的PaymentPlatform缓存中没有找到PlatformTypeEnum ={}的处理handler", PlatformTypeEnum);
            return null;
        }
        return paymentPlatform;
    }
}
