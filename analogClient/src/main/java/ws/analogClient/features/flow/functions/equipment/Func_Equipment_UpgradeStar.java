package ws.analogClient.features.flow.functions.equipment;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.protos.EquipmentProtos.Cm_Equipment;
import ws.protos.EquipmentProtos.Sm_Equipment.Action;
import ws.protos.MessageHandlerProtos.Response;

public class Func_Equipment_UpgradeStar {

    public static void execute() {
        test12();
    }


    /**
     * 测试点说明： ABCD 正常升品
     */
    public static void test1() {
        // ABCD
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "4100004:2,4010001:1";
        Func_Gm.addResource(resource_1);
        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_STAR);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_STAR);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明： ABCD
     * 装备已经达到最大星级=5, 无法继续升星！
     */
    public static void test2() {
        // ABCD
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "4100004:1,4010001:1,4100004:2,4010001:1,4100004:4, 4010001:1,4100004:8, 4010001:1,4100004:16, 4010001:1";
        Func_Gm.addResource(resource_1);
        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 10; i++) {
            Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
            b1.setAction(Cm_Equipment.Action.UP_STAR);
            b1.setHeroId(100000001);
            b1.setEquipPos(EquipmentPositionEnum.POS_A);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_STAR);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }

    /**
     * 测试点说明： ABCD
     * Item=[4100004:2, 4010001:1]，资源不足！
     */
    public static void test3() {
        // ABCD
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "";
        Func_Gm.addResource(resource_1);
        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_STAR);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_STAR);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    //=====================================EF============================================

    /**
     * 测试点说明： EF 正常升品
     */
    public static void test11() {
        // ABCD
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "4011001:1, 4100004:2";
        Func_Gm.addResource(resource_1);
        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_STAR);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_E);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_STAR);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明： EF
     * 装备已经达到最大星级=5, 无法继续升星！
     */
    public static void test12() {
        // ABCD
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "4011007:1, 4100004:1,4011007:1, 4100004:2,4011007:1, 4100004:4,4011007:1, 4100004:8,4011007:1, 4100004:16";
        Func_Gm.addResource(resource_1);
        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 10; i++) {
            Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
            b1.setAction(Cm_Equipment.Action.UP_STAR);
            b1.setHeroId(100000001);
            b1.setEquipPos(EquipmentPositionEnum.POS_F);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_STAR);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }

    /**
     * 测试点说明： EF
     * Item=[4011001:1, 4100004:2]，资源不足！
     */
    public static void test13() {
        // ABCD
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "";
        Func_Gm.addResource(resource_1);
        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_STAR);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_E);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_STAR);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


}

