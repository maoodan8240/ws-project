package ws.thirdPartyServer.features.actor.payment.payment.platform;

import ws.protos.EnumsProtos.PlatformTypeEnum;


public interface PaymentPlatformHandlerManager {
    PaymentPlatform get(PlatformTypeEnum PlatformTypeEnum);
}
