package ws.analogClient.features.flow.functions.equipment;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.protos.EquipmentProtos.Cm_Equipment;
import ws.protos.EquipmentProtos.Sm_Equipment.Action;
import ws.protos.MessageHandlerProtos.Response;

public class Func_Equipment_Up_ABCD_OneLv {

    public static void execute() {
        test2();
    }

    /**
     * 测试点说明： 装备正常升级
     */
    public static void test1() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_ABCD_ONE_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_ONE_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：是武将Id，但是不存在
     * Item=100000031:1，资源不足！
     */
    public static void test2() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_ABCD_ONE_LV);
        b1.setHeroId(100000031);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_ONE_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：Id存在,但是不是武将id
     * 参数heroId=200000031非法！未查询到该武将!
     */
    public static void test3() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_ABCD_ONE_LV);
        b1.setHeroId(200000031);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_ONE_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：金币不够
     * 升级失败，upgradeResult=[times=0, oriLv=1, newLv=1, sumConsume=0, lastNeed=2400],
     * heroLv=1, curEquipLv=1, curEquipQualityLv=1, curMaxLvInCurQuality=10 allMoney=1000
     */
    public static void test4() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:-9000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_ABCD_ONE_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_ONE_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：
     * 装备需要升品后才能继续升级！curLv=10 curQualityLv=1 curMaxLv=10
     */
    public static void test5() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 20; i++) {
            Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
            b1.setAction(Cm_Equipment.Action.UP_ABCD_ONE_LV);
            b1.setHeroId(100000001);
            b1.setEquipPos(EquipmentPositionEnum.POS_A);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_ONE_LV);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }
}

