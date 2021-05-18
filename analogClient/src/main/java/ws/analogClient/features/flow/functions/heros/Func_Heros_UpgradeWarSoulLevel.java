package ws.analogClient.features.flow.functions.heros;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.EnumsProtos.WarSoulPositionEnum;
import ws.protos.HerosProtos.Cm_Heros;
import ws.protos.HerosProtos.Cm_Heros.Action;
import ws.protos.HerosProtos.Sm_Heros;
import ws.protos.MessageHandlerProtos.Response;

public class Func_Heros_UpgradeWarSoulLevel {

    public static void execute() {
        test6();
    }

    /**
     * 测试点说明： 武将等级正常升级
     */
    public static void up_HeroLv(int level) {
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
     * 测试点说明：
     * 1) 正常升品，并且造成解锁战魂
     */
    public static void up_heroQualityLv(int times) {
        up_HeroLv(102);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10000000,4020000:100000,4020001:100000,4020004:100000,4020007:100000,4020010:100000,4020013:100000,4020016:100000,4020019:100000,4020022:100000,4020025:100000,4020028:100000,4020031:100000,4020034:100000,4020002:100000,4020005:100000,4020008:100000,4020011:100000,4020014:100000,4020017:100000,4020020:100000,4020023:100000,4020026:100000,4020029:100000,4020032:100000,4020035:100000,4020003:100000,4020006:100000,4020009:100000,4020012:100000,4020015:100000,4020018:100000,4020021:100000,4020024:100000,4020027:100000,4020030:100000,4020033:100000,4020036:100000";

        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < times; i++) {
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
     * 测试点说明： 战魂正常升级
     */
    public static void test1() {
        up_heroQualityLv(25); // 所有战魂都解锁

        // -------------------------  资源需求 ---------------------------
        String resource_2 = "4020016:100,4020017:100,4020018:100,4020019:100";
        Func_Gm.addResource(resource_2);
        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_SOUL_LV);
        b1.setHeroId(100000001);
        b1.setSoulPos(WarSoulPositionEnum.POS_Soul_A);
        b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("4020016:10,4020017:10,4020018:10,4020019:10"));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_SOUL_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * 武将当前的品质=9 可以解锁的最大战魂位置=1, 已经解锁的战魂={POS_Soul_A=ws.relationship.topLevelPojos.common.LevelUpObj@2896db2}, 当前请求的战魂=POS_Soul_B过大！
     */
    public static void test2() {
        up_heroQualityLv(8); // 解锁A

        // -------------------------  资源需求 ---------------------------
        String resource_2 = "4020016:100,4020017:100,4020018:100,4020019:100";
        Func_Gm.addResource(resource_2);
        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_SOUL_LV);
        b1.setHeroId(100000001);
        b1.setSoulPos(WarSoulPositionEnum.POS_Soul_B);
        b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("4020016:10,4020017:10,4020018:10,4020019:10"));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_SOUL_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：
     * 所有消耗品=[100000001:10, 4020017:10, 4020019:10, 4020018:10] 其中id=100000001,非道具！
     */
    public static void test3() {
        up_heroQualityLv(8); // 解锁A

        // -------------------------  资源需求 ---------------------------
        String resource_2 = "100000001:100,4020017:100,4020018:100,4020019:100";
        Func_Gm.addResource(resource_2);
        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_SOUL_LV);
        b1.setHeroId(100000001);
        b1.setSoulPos(WarSoulPositionEnum.POS_Soul_A);
        b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("100000001:10,4020017:10,4020018:10,4020019:10"));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_SOUL_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：
     * 所有消耗品=[4020017:10, 4020019:10, 4010002:10, 4020018:10] 其中id=4010002,非战魂经验道具！
     */
    public static void test4() {
        up_heroQualityLv(8); // 解锁A

        // -------------------------  资源需求 ---------------------------
        String resource_2 = "4010002:100,4020017:100,4020018:100,4020019:100";
        Func_Gm.addResource(resource_2);
        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_SOUL_LV);
        b1.setHeroId(100000001);
        b1.setSoulPos(WarSoulPositionEnum.POS_Soul_A);
        b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("4010002:10,4020017:10,4020018:10,4020019:10"));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_SOUL_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * Item=1:20000000，资源不足！
     */
    public static void test5() {
        up_heroQualityLv(25); // 所有战魂都解锁

        // -------------------------  资源需求 ---------------------------
        String resource_2 = "4020016:10000";
        Func_Gm.addResource(resource_2);
        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_SOUL_LV);
        b1.setHeroId(100000001);
        b1.setSoulPos(WarSoulPositionEnum.POS_Soul_A);
        b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("4020016:10000"));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_SOUL_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：
     * 正常升级达到最大等级
     */
    public static void test6() {
        up_heroQualityLv(25); // 所有战魂都解锁

        // -------------------------  资源需求 ---------------------------
        String resource_2 = "1:4000000,4020016:2000";
        Func_Gm.addResource(resource_2);
        // -------------------------  功能测试 ---------------------------
        Cm_Heros.Builder b1 = Cm_Heros.newBuilder();
        b1.setAction(Action.UP_SOUL_LV);
        b1.setHeroId(100000001);
        b1.setSoulPos(WarSoulPositionEnum.POS_Soul_A);
        b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("4020016:2000"));
        Response response1 = ClientUtils.send(b1.build(), Sm_Heros.Action.RESP_UP_SOUL_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

}

