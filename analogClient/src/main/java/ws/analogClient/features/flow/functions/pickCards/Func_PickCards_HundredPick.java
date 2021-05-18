package ws.analogClient.features.flow.functions.pickCards;

import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PickCardsProtos.Cm_PickCards;
import ws.protos.PickCardsProtos.Sm_PickCards.Action;
import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;

public class Func_PickCards_HundredPick {

    public static void execute() {
        test2();
    }


    /**
     * 测试点说明：id 1：第一次折扣，第二次全价，正常通过
     */
    public static void test1() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:-1000000,1:900000,1:1800000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 2; i++) {
            Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
            b1.setAction(Cm_PickCards.Action.HUNDRED_PICK);
            b1.setPickId(1);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_HUNDRED_PICK);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }

    /**
     * 测试点说明：
     * pickId=2 不支持100连抽.
     */
    public static void test2() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:-1000000,1:900000,1:1800000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 2; i++) {
            Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
            b1.setAction(Cm_PickCards.Action.HUNDRED_PICK);
            b1.setPickId(2);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_HUNDRED_PICK);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }
}

