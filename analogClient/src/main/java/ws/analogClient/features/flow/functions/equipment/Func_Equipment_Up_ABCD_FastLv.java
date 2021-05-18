package ws.analogClient.features.flow.functions.equipment;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.protos.EquipmentProtos.Cm_Equipment;
import ws.protos.EquipmentProtos.Sm_Equipment.Action;
import ws.protos.MessageHandlerProtos.Response;
import ws.analogClient.features.utils.ClientUtils;

public class Func_Equipment_Up_ABCD_FastLv {

    public static void execute() {
        test7();
    }

    /**
     * 测试点说明： 装备正常快速升级
     */
    public static void test1() {
        Func_Gm.setLv(30);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10000000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_ABCD_FAST_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        b1.setFastToLv(10);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_FAST_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * 目标等级FastToLv=12, 不正确！正确的为=[10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120]
     */
    public static void test2() {
        Func_Gm.setLv(30);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_ABCD_FAST_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        b1.setFastToLv(12);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_FAST_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * 目标等级FastToLv=35, 不能超过角色等级！
     */
    public static void test3() {
        Func_Gm.setLv(30);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_ABCD_FAST_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        b1.setFastToLv(35);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_FAST_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：正常升级
     * 10级突破的材料不足Item=[1:61400, 4030001:1]，所以在10级停下来了
     */
    public static void test4() {
        Func_Gm.setLv(50);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_ABCD_FAST_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        b1.setFastToLv(15);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_FAST_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：正常升级，自动升品
     */
    public static void test5() {
        Func_Gm.setLv(50);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000000,4030001:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_ABCD_FAST_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        b1.setFastToLv(15);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_FAST_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：4->5品质需要4020007:4,4020009:4,4020008:4 --> 12*6=72 需要72个钻石自动购买
     * 需要的资源为Item=[1:165200, 2:72, 4030004:1, 4030001:1, 4030003:1, 4030002:1]，所以在4品质25级停下来了
     */
    public static void test6() {
        Func_Gm.setLv(50);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000000,2:-10000,4030001:10,4030002:10,4030003:10,4030004:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_ABCD_FAST_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        b1.setFastToLv(30);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_FAST_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：相对于test6，补上钻石可以正常升级，自动升品，自动购买宝石
     */
    public static void test7() {
        Func_Gm.setLv(50);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000000,2:10000,4030001:10,4030002:10,4030003:10,4030004:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_ABCD_FAST_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        b1.setFastToLv(30);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_FAST_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }
}

