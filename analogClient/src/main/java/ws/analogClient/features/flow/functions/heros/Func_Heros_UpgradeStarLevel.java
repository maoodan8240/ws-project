package ws.analogClient.features.flow.functions.heros;

import ws.protos.HerosProtos.Cm_Heros;
import ws.protos.HerosProtos.Cm_Heros.Action;
import ws.protos.HerosProtos.Sm_Heros;
import ws.protos.MessageHandlerProtos.Response;
import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;

public class Func_Heros_UpgradeStarLevel {

    public static void execute() {
        test2();
    }


    /**
     * 测试点说明：
     * 1)  正常升星
     * 2)  heroId=100000001，已经达到最大星级=7 不能继续升星！
     */
    public static void test1() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10000000,1000001:1,4070001:1000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 8; i++) {
            Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
            b1.setAction(Action.UP_STAR_LV);
            b1.setHeroId(100000001);
            Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_STAR_LV);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }


    /**
     * 测试点说明：
     * 1)  正常升星
     * 2)  Item=[1:1200000, 4070001:160]，资源不足！
     */
    public static void test2() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10000000,1000001:1,4070001:200";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 8; i++) {
            Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
            b1.setAction(Action.UP_STAR_LV);
            b1.setHeroId(100000001);
            Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_STAR_LV);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }

}

