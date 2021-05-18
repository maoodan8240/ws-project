package ws.analogClient.features.flow.functions.equipment;

import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.protos.EquipmentProtos.Cm_Equipment;
import ws.protos.EquipmentProtos.Sm_Equipment.Action;
import ws.protos.MessageHandlerProtos.Response;
import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;

public class Func_Equipment_Up_EF_SimpleLv {

    public static void execute() {
        test1();
    }

    /**
     * 测试点说明： 装备正常升级
     */
    public static void test1() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000,4040001:100,4040002:100,4040003:100";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_EF_SIMPLE_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_E);
        b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("4040001:10,4040002:10,4040003:10"));
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_EF_SIMPLE_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：
     * 装备equipPos=POS_E, 需要升品后才能继续升级！curLv=10 curQualityLv=1 curMaxLv=10
     */
    public static void test2() {
        Func_Gm.setLv(20);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000,4040001:100,4040002:100,4040003:100";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 2; i++) {
            Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
            b1.setAction(Cm_Equipment.Action.UP_EF_SIMPLE_LV);
            b1.setHeroId(100000001);
            b1.setEquipPos(EquipmentPositionEnum.POS_E);
            b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("4040001:10,4040002:10,4040003:10"));
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_EF_SIMPLE_LV);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }

}

