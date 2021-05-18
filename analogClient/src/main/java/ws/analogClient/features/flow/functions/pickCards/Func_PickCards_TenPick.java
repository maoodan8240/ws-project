package ws.analogClient.features.flow.functions.pickCards;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PickCardsProtos.Cm_PickCards;
import ws.protos.PickCardsProtos.Sm_PickCards.Action;

public class Func_PickCards_TenPick {

    public static void execute() {
        test4();
    }


    /**
     * 测试点说明： id 2：第一次折扣，第二次全价，正常通过
     */
    public static void test1() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "2:-100000,2:1140,2:2280";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 2; i++) {
            Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
            b1.setAction(Cm_PickCards.Action.TEN_PICK);
            b1.setPickId(2);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_TEN_PICK);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }

    /**
     * 测试点说明：
     * Item=[2:2280]，资源不足！
     */
    public static void test2() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "2:-100000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
        b1.setAction(Cm_PickCards.Action.TEN_PICK);
        b1.setPickId(2);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_TEN_PICK);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明： id 2：第一次折扣，第二次全价，正常通过, 获得20个4080002
     */
    public static void test3() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:-1000000,1:90000,1:180000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 2; i++) {
            Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
            b1.setAction(Cm_PickCards.Action.TEN_PICK);
            b1.setPickId(1);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_TEN_PICK);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }


    /**
     * 测试点说明： id 2：第一次折扣，第二次全价，正常通过, 获得20个 资源编号为18的资源
     */
    public static void test4() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "2:-100000,2:340,2:680";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 2; i++) {
            Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
            b1.setAction(Cm_PickCards.Action.TEN_PICK);
            b1.setPickId(3);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_TEN_PICK);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }


}

