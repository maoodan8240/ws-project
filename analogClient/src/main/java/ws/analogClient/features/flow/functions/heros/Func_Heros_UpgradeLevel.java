package ws.analogClient.features.flow.functions.heros;

import ws.protos.HerosProtos.Cm_Heros;
import ws.protos.HerosProtos.Cm_Heros.Action;
import ws.protos.HerosProtos.Sm_Heros;
import ws.protos.MessageHandlerProtos.Response;
import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;

public class Func_Heros_UpgradeLevel {

    public static void execute() {
        test7();
    }

    /**
     * 测试点说明： 正常升级
     */
    public static void test1() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1000001:1";
        String resource_2 = "4080001:10000,4080002:10000,4080003:10000,4080004:10000";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_LV);
        b1.setHeroId(100000001);
        b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("4080001:9,4080002:9,4080003:9,4080004:9"));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * heroId=100000001，当前等级为=20 玩家角色等级为=20 不能继续升级！
     */
    public static void test2() {
        // -------------------------  资源需求 ---------------------------
        test1();

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_LV);
        b1.setHeroId(100000001);
        b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("4080001:1,4080002:1,4080003:1,4080004:1"));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * consumeTpIds=[]，升级武将等级消耗材料列表不能为空！
     */
    public static void test3() {
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_LV);
        b1.setHeroId(100000001);
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * 所有消耗品=[100000002:1, 4080004:5, 4080002:5, 4080003:5, 4080001:5] 其中id=100000002,非道具！
     */
    public static void test4() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1000001:1,1000002:1";
        String resource_2 = "4020000:10,4080001:10,4080002:10,4080003:10,4080004:10";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_LV);
        b1.setHeroId(100000001);
        b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("100000002:1,4080001:5,4080002:5,4080003:5,4080004:5"));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：
     * 所有消耗品=[4080004:5, 4080002:5, 4020000:1, 4080003:5, 4080001:5] 其中id=4020000,非卡牌经验道具！
     */
    public static void test5() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1000001:1";
        String resource_2 = "4020000:10,4080001:10,4080002:10,4080003:10,4080004:10";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_LV);
        b1.setHeroId(100000001);
        b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("4020000:1,4080001:5,4080002:5,4080003:5,4080004:5"));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * Item=[100000001:1, 4080004:5, 4080002:5, 4080003:5, 4080001:15]，资源不足！
     */
    public static void test6() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1000001:1";
        String resource_2 = "4020000:10,4080001:10,4080002:10,4080003:10,4080004:10";

        Func_Gm.addResource(resource_1, resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_LV);
        b1.setHeroId(100000001);
        b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("4080001:15,4080002:5,4080003:5,4080004:5"));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：超过最大等级
     * aptitude=13，curLv=106 未找到当前等级的经验行！
     */
    public static void test7() {
        Func_Gm.setLv(106);
        // -------------------------  资源需求 ---------------------------
        String resource_2 = "4080001:100000,4080002:100000,4080003:100000,4080004:100000";

        Func_Gm.addResource(resource_2);

        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_LV);
        b1.setHeroId(100000001);
        b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("4080001:100000,4080002:100000,4080003:100000,4080004:100000"));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }
}

