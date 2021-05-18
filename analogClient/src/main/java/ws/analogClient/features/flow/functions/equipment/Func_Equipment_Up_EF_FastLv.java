package ws.analogClient.features.flow.functions.equipment;

import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.protos.EquipmentProtos.Cm_Equipment;
import ws.protos.EquipmentProtos.Sm_Equipment.Action;
import ws.protos.MessageHandlerProtos.Response;
import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;

public class Func_Equipment_Up_EF_FastLv {

    public static void execute() {
        test5();
    }

    /**
     * 测试点说明： 装备正常升级，自动升品，自动购买（经济场币不足，自动转为钻石）
     */
    public static void test1() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000,4040001:100,4040002:100,4040003:100";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_EF_FAST_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_E);
        b1.setFastToLv(20); // 10 级自动升品了
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_EF_FAST_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * 目标等级FastToLv=32, 不正确！正确的为=[10, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120]
     */
    public static void test2() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000,4040001:100,4040002:100,4040003:100";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_EF_FAST_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_E);
        b1.setFastToLv(32);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_EF_FAST_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * 目标等级FastToLv=30, 不能超过角色等级！
     */
    public static void test3() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000,4040001:100,4040002:100,4040003:100";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_EF_FAST_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_E);
        b1.setFastToLv(30);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_EF_FAST_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明： 装备正常升级，但是（经济场币不足，自动转为钻石，钻石却不足，自动升品失败！）
     */
    public static void test4() {
        Func_Gm.setLv(30);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000,2:-100000,4040001:100,4040002:100,4040003:100";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_EF_FAST_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_E);
        b1.setFastToLv(30);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_EF_FAST_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明： 升级经验需要的金币不足
     */
    public static void test5() {
        Func_Gm.setLv(30);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:-1000000,2:-100000,4040001:100,4040002:100,4040003:100";
        Func_Gm.addResource(resource_1);
        String resource_2 = "1:1000";
        Func_Gm.addResource(resource_2);


        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_EF_FAST_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_E);
        b1.setFastToLv(30);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_EF_FAST_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


}

