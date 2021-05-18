package ws.thirdPartyServer.features.test;

import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.thirdPartyServer.features.http.HttpMethodEnums;
import ws.thirdPartyServer.features.http.msg.In_HttpRequestContent;


public class Test_PaymentPlatform_Handler_UC {

    public static void main(String[] args) {
        PlatformTypeEnum platformTypeEnum = PlatformTypeEnum.AWO;
        HttpMethodEnums httpMethodEnums = HttpMethodEnums.POST;
        String postBody = "{\"sign\":\"af3b122c35d7d5b03dab96041b5a3fa6\",\"data\":{\"failedDesc\":\"\",\"amount\":\"1.00\",\"callbackInfo\":\"55e2e7f9e4b0dd6549894ba3|20019|1441092030466\",\"cpOrderId\":\"e82a10d35f30477fb74677988fe711c2\",\"accountId\":\"366d3dc2f9512fc5ff41b768bc50165a\",\"gameId\":\"578258\",\"payWay\":\"999\",\"orderStatus\":\"S\",\"orderId\":\"201509011519407754067\",\"creator\":\"JY\"},\"ver\":\"2.0\"}";
        In_HttpRequestContent content = new In_HttpRequestContent(platformTypeEnum, httpMethodEnums, null, null, postBody);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
