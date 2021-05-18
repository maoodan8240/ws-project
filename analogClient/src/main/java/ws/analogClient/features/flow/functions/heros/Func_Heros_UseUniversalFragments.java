package ws.analogClient.features.flow.functions.heros;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.HerosProtos.Cm_Heros;
import ws.protos.HerosProtos.Cm_Heros.Action;
import ws.protos.HerosProtos.Sm_Heros;
import ws.protos.MessageHandlerProtos.Response;

public class Func_Heros_UseUniversalFragments {

    public static void execute() {
        test3();
    }


    /**
     * 测试点说明： 武将等级正常升级
     */
    public static void test11(int level) {
        Func_Gm.setLv(level);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1000001:1";
        String resource_2 = "4080001:30000,4080002:30000,4080003:30000,4080004:30000";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_LV);
        b1.setHeroId(100000001);
        b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("4080001:30000,4080002:30000,4080003:30000,4080004:30000"));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明： 正常吃万能碎片
     */
    public static void test1() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:100000,4100005:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.USE_UNIVERSAL_FRAG);
        b1.setHeroId(100000001);
        b1.setNum(1);
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_USE_UNIVERSAL_FRAG);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * 控指针异常，不做业务判断
     */
    public static void test2() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:100000,4100005:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.USE_UNIVERSAL_FRAG);
        b1.setHeroId(4100005);
        b1.setNum(1);
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_USE_UNIVERSAL_FRAG);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：假冒的武将Id
     * Item=[100001111:1, 4100005:1]，资源不足！
     */
    public static void test3() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:100000,4100005:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.USE_UNIVERSAL_FRAG);
        b1.setHeroId(100001111);
        b1.setNum(1);
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_USE_UNIVERSAL_FRAG);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：万能碎片不够
     * Item=[100000001:1, 4100005:100]，资源不足！
     */
    public static void test5() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:100000,4100005:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.USE_UNIVERSAL_FRAG);
        b1.setHeroId(100000001);
        b1.setNum(100);
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_USE_UNIVERSAL_FRAG);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

}

