package ws.analogClient.features.flow.functions.pickCards;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PickCardsProtos.Cm_PickCards;
import ws.protos.PickCardsProtos.Sm_PickCards.Action;
import ws.analogClient.features.utils.ClientUtils;

public class Func_PickCards_Pick {

    public static void execute() {
        test6();
    }


    /**
     * 测试点说明： id 1：正常抽卡
     */
    public static void test1() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
        b1.setAction(Cm_PickCards.Action.PICK);
        b1.setPickId(1);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_PICK);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明： id 2：正常抽卡
     */
    public static void test2() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
        b1.setAction(Cm_PickCards.Action.PICK);
        b1.setPickId(2);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_PICK);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明： id 3：正常抽卡
     */
    public static void test3() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
        b1.setAction(Cm_PickCards.Action.PICK);
        b1.setPickId(3);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_PICK);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * Item=[2:120]，资源不足！
     */
    public static void test4() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "2:-100000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
        b1.setAction(Cm_PickCards.Action.PICK);
        b1.setPickId(2);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_PICK);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明： 第一次购买折扣测试正常
     */
    public static void test5() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "2:-100000";
        String resource_2 = "2:600";
        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 3; i++) {
            Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
            b1.setAction(Cm_PickCards.Action.PICK);
            b1.setPickId(2);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_PICK);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }


    /**
     * 测试点说明：优先使用道具抽卡，测试正常
     */
    public static void test6() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "2:-100000";
        String resource_2 = "4100007:5";
        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 6; i++) {
            Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
            b1.setAction(Cm_PickCards.Action.PICK);
            b1.setPickId(3);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_PICK);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }
}

