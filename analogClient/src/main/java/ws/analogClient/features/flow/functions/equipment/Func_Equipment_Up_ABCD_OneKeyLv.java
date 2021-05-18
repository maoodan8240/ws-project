package ws.analogClient.features.flow.functions.equipment;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.protos.EquipmentProtos.Cm_Equipment;
import ws.protos.EquipmentProtos.Sm_Equipment.Action;
import ws.protos.MessageHandlerProtos.Response;

public class Func_Equipment_Up_ABCD_OneKeyLv {

    public static void execute() {
        test3();
    }

    /**
     * 测试点说明： 装备 一键升级-->达到当前品级的最大等级
     */
    public static void test1() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:100000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_ABCD_ONE_KEY_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_ONE_KEY_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明： 装备 一键升级 --> 耗尽金币，达到所能达到的最大等级
     */
    public static void test2() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_ABCD_ONE_KEY_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_ONE_KEY_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * 装备需要升品后才能继续升级！curLv=10 curQualityLv=1 curMaxLv=10
     */
    public static void test3() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:100000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 2; i++) {
            Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
            b1.setAction(Cm_Equipment.Action.UP_ABCD_ONE_KEY_LV);
            b1.setHeroId(100000001);
            b1.setEquipPos(EquipmentPositionEnum.POS_A);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_ONE_KEY_LV);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }
}

