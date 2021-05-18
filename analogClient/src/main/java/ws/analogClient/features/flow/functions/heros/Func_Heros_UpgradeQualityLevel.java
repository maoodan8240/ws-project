package ws.analogClient.features.flow.functions.heros;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.HerosProtos.Cm_Heros;
import ws.protos.HerosProtos.Cm_Heros.Action;
import ws.protos.HerosProtos.Sm_Heros;
import ws.protos.MessageHandlerProtos.Response;

public class Func_Heros_UpgradeQualityLevel {

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
     * 测试点说明： 正常升品
     */
    public static void test1() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:100000,4020000:10,4020001:10,4020002:10,4020003:10";
        String resource_2 = "1000001:1";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_QUALITY_LV);
        b1.setHeroId(100000001);
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_QUALITY_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：
     * 1) 正常升品，并且造成解锁战魂
     * 2) heroId=100000001，当前等级为=75 升品需求等级=80 不能继续升品！
     */
    public static void test2() {
        test11(75);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10000000,4020000:100000,4020001:100000,4020004:100000,4020007:100000,4020010:100000,4020013:100000,4020016:100000,4020019:100000,4020022:100000,4020025:100000,4020028:100000,4020031:100000,4020034:100000,4020002:100000,4020005:100000,4020008:100000,4020011:100000,4020014:100000,4020017:100000,4020020:100000,4020023:100000,4020026:100000,4020029:100000,4020032:100000,4020035:100000,4020003:100000,4020006:100000,4020009:100000,4020012:100000,4020015:100000,4020018:100000,4020021:100000,4020024:100000,4020027:100000,4020030:100000,4020033:100000,4020036:100000";
        String resource_2 = "";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 30; i++) {
            Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
            b1.setAction(Action.UP_QUALITY_LV);
            b1.setHeroId(100000001);
            Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_QUALITY_LV);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }


    /**
     * 测试点说明：
     * 1) 正常升品，并且造成解锁战魂
     * 2) heroId=100000001，已经达到最大品级=COLOR_RED_6 不能继续升品！
     */
    public static void test3() {
        test11(102);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10000000,4020000:100000,4020001:100000,4020004:100000,4020007:100000,4020010:100000,4020013:100000,4020016:100000,4020019:100000,4020022:100000,4020025:100000,4020028:100000,4020031:100000,4020034:100000,4020002:100000,4020005:100000,4020008:100000,4020011:100000,4020014:100000,4020017:100000,4020020:100000,4020023:100000,4020026:100000,4020029:100000,4020032:100000,4020035:100000,4020003:100000,4020006:100000,4020009:100000,4020012:100000,4020015:100000,4020018:100000,4020021:100000,4020024:100000,4020027:100000,4020030:100000,4020033:100000,4020036:100000";
        String resource_2 = "";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 30; i++) {
            Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
            b1.setAction(Action.UP_QUALITY_LV);
            b1.setHeroId(100000001);
            Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_QUALITY_LV);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }


    /**
     * 测试点说明：
     * 升品的武将不存在
     */
    public static void test4() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:100000,4020000:10,4020001:10,4020002:10,4020003:10";
        String resource_2 = "";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_QUALITY_LV);
        b1.setHeroId(100000001);
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_QUALITY_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

}

