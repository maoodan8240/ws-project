package ws.analogClient.features.flow.functions.equipment;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.protos.EquipmentProtos.Cm_Equipment;
import ws.protos.EquipmentProtos.Sm_Equipment.Action;
import ws.protos.MessageHandlerProtos.Response;

public class Func_Equipment_ReduceStar {

    public static void execute() {
        test1();
    }


    /**
     * 测试点说明： ABCD 升星
     */
    public static void upStar(int times) {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "4100004:1,4010001:1,4100004:2,4010001:1,4100004:4, 4010001:1,4100004:8, 4010001:1,4100004:16, 4010001:1";
        Func_Gm.addResource(resource_1);
        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < times; i++) {
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
     * 测试点说明： ABCD 正常将星
     */
    public static void test1() {
        upStar(5);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000";
        Func_Gm.addResource(resource_1);
        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 5; i++) {
            Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
            b1.setAction(Cm_Equipment.Action.REDUCE_STAR);
            b1.setHeroId(100000001);
            b1.setEquipPos(EquipmentPositionEnum.POS_A);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_REDUCE_STAR);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }

    /**
     * 测试点说明： ABCD
     * 装备星级=0, 已经是最低星级！
     */
    public static void test2() {
        // -------------------------  资源需求 ---------------------------
        // String resource_1 = "1:-1000000";
        String resource_1 = "1:1000";
        Func_Gm.addResource(resource_1);
        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 1; i++) {
            Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
            b1.setAction(Cm_Equipment.Action.REDUCE_STAR);
            b1.setHeroId(100000001);
            b1.setEquipPos(EquipmentPositionEnum.POS_A);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_REDUCE_STAR);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }

    /**
     * 测试点说明： ABCD
     * Item=2:30，资源不足！
     */
    public static void test3() {
        upStar(3);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "2:-100000";
        Func_Gm.addResource(resource_1);
        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 1; i++) {
            Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
            b1.setAction(Cm_Equipment.Action.REDUCE_STAR);
            b1.setHeroId(100000001);
            b1.setEquipPos(EquipmentPositionEnum.POS_A);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_REDUCE_STAR);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }


}

