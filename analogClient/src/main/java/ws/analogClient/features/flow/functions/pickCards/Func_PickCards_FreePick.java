package ws.analogClient.features.flow.functions.pickCards;

import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PickCardsProtos.Cm_PickCards;
import ws.protos.PickCardsProtos.Sm_PickCards.Action;
import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;

public class Func_PickCards_FreePick {

    public static void execute() {
        test5();
    }


    /**
     * 测试点说明： 正常免费抽卡
     */
    public static void test1() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
        b1.setAction(Cm_PickCards.Action.FREE_PICK);
        b1.setPickId(1);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_FREE_PICK);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：
     * pickId=1 当前不可以使用免费抽卡！ 当前时间=1488443355900， 免费时间=1488443955884， 已经使用的免费次数=1 .
     */
    public static void test2() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 2; i++) {
            Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
            b1.setAction(Cm_PickCards.Action.FREE_PICK);
            b1.setPickId(1);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_FREE_PICK);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }

    /**
     * 测试点说明：
     * Table_PickCards_Row 无此pickId=10 ！
     */
    public static void test3() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
        b1.setAction(Cm_PickCards.Action.FREE_PICK);
        b1.setPickId(10);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_FREE_PICK);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：等待时间测试 ，需要把配制表免费抽卡时间间隔改为60秒
     */
    public static void test4() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 6; i++) {
            Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
            b1.setAction(Cm_PickCards.Action.FREE_PICK);
            b1.setPickId(1);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_FREE_PICK);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
            try {
                Thread.sleep(61 * 1000l);
                System.out.println("暂停中");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试点说明：
     * 只有一次免费的情况，第一次正常
     * 第二次： pickId=2 当前不可以使用免费抽卡！ 当前时间=1488445177801， 免费时间=1488445176788， 已经使用的免费次数=1 总的可以使用的次数=1 .
     */
    public static void test5() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 6; i++) {
            Cm_PickCards.Builder b1 = Cm_PickCards.newBuilder();
            b1.setAction(Cm_PickCards.Action.FREE_PICK);
            b1.setPickId(2);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_FREE_PICK);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
            try {
                Thread.sleep(61 * 1000l);
                System.out.println("暂停中");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

