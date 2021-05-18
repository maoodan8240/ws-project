package ws.thirdPartyServer.features.actor.payment.payment.platform;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.thirdPartyServer.features.utils.AssignedClassHolder;

import java.util.HashMap;
import java.util.Map;


public class _PaymentPlatformHandlerManager implements PaymentPlatformHandlerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(_PaymentPlatformHandlerManager.class);
    private Map<PlatformTypeEnum, PaymentPlatform> platformToPaymentPlatform = new HashMap<>();

    public _PaymentPlatformHandlerManager() {
        init();
    }

    private void init() {
        try {
            platformToPaymentPlatform.clear();
            for (Class<? extends PaymentPlatform> clz : AssignedClassHolder.getPaymentPlatformClasses()) {
                if (clz.getName().equals(AbstractPaymentPlatform.class.getName())) {
                    continue;
                }
                String[] nameArr = clz.getName().split("_");
                String platformStr = nameArr[nameArr.length - 1];
                PlatformTypeEnum PlatformTypeEnum = EnumUtils.getEnum(PlatformTypeEnum.class, platformStr);
                platformToPaymentPlatform.put(PlatformTypeEnum, clz.newInstance());
                LOGGER.info("加入支付渠道PlatformTypeEnum ={} ", PlatformTypeEnum);
            }
        } catch (Exception e) {
            LOGGER.error("加载所有支付渠道处理handler异常！", e);
        }
    }

    @Override
    public PaymentPlatform get(PlatformTypeEnum PlatformTypeEnum) {
        return platformToPaymentPlatform.get(PlatformTypeEnum);
    }
}
