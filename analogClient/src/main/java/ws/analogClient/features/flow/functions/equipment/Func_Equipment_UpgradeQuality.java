package ws.analogClient.features.flow.functions.equipment;

import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.protos.EquipmentProtos.Cm_Equipment;
import ws.protos.EquipmentProtos.Sm_Equipment.Action;
import ws.protos.MessageHandlerProtos.Response;
import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;

public class Func_Equipment_UpgradeQuality {

    public static void execute() {
        test12();
    }

    /**
     * 测试点说明： 装备正常升级
     */
    public static void upEF_EquipLv(int times) {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000,4050001:100,4050002:100,4050003:100";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < times; i++) {
            Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
            b1.setAction(Cm_Equipment.Action.UP_EF_SIMPLE_LV);
            b1.setHeroId(100000001);
            b1.setEquipPos(EquipmentPositionEnum.POS_F);
            b1.setConsumeTpIds(Func_Gm.create_Sm_Common_IdMaptoCount("4050001:1,4050002:1,4050003:1"));
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_EF_SIMPLE_LV);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }

    /**
     * 测试点说明： 装备正常升级
     */

    public static void upABCD_EquipLv(int money) {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:-1000000,2:-100000,4040001:100,4040002:100,4040003:100";
        Func_Gm.addResource(resource_1);
        String resource_2 = "1:" + money;
        Func_Gm.addResource(resource_2);

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


    //==========================ABCD=====================================================

    /**
     * 测试点说明： ABCD 正常升品
     */

    public static void test1() {
        // ABCD
        Func_Gm.setLv(20);
        upABCD_EquipLv(21600);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:5000,4030001:1";
        Func_Gm.addResource(resource_1);
        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_QUALITY);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_QUALITY);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：
     * 升品等级不符合！当前等级=9, 需要等级=10, 当前品质=1
     */

    public static void test2() {
        // ABCD
        Func_Gm.setLv(20);
        upABCD_EquipLv(21500);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:-1000000,2:-100000,4040001:100,4040002:100,4040003:100";
        Func_Gm.addResource(resource_1);
        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_QUALITY);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_QUALITY);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：
     * Item=[1:5000, 4030001:1]，资源不足！
     */

    public static void test3() {
        // ABCD
        Func_Gm.setLv(20);
        upABCD_EquipLv(21600);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:-1000000";
        Func_Gm.addResource(resource_1);
        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_QUALITY);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_QUALITY);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：升品自动购买宝石失败，钻石不足 Item=[1:20000, 2:66, 4030004:1, 4020007:1]，资源不足！
     */

    public static void test4() {
        // ABCD
        Func_Gm.setLv(50);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000000,2:-100000,4030001:10,4030002:10,4030003:10,4030004:10,4020007:1";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        // ----- 升级到25级，需要升品
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_ABCD_FAST_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        b1.setFastToLv(25);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_FAST_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }


        // -------------------------  功能测试 ---------------------------
        // 升品自动购买失败
        Cm_Equipment.Builder b2 = Cm_Equipment.newBuilder();
        b2.setAction(Cm_Equipment.Action.UP_QUALITY);
        b2.setHeroId(100000001);
        b2.setEquipPos(EquipmentPositionEnum.POS_A);
        Response response2 = ClientUtils.send(b2.build(), Action.RESP_UP_QUALITY);
        if (!response2.getResult())

        {
            throw new RuntimeException("服务器返回失败！！！");
        }
        upABCD_EquipLv(1);
    }

    /**
     * 测试点说明：ABCD 正常升品，自动购买
     */

    public static void test5() {
        // ABCD
        Func_Gm.setLv(50);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:1000000,4030001:10,4030002:10,4030003:10,4030004:10,4020007:1";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        // ----- 升级到25级，需要升品
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_ABCD_FAST_LV);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_A);
        b1.setFastToLv(25);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_ABCD_FAST_LV);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }

        // -------------------------  功能测试 ---------------------------
        // 升品自动购买失败
        Cm_Equipment.Builder b2 = Cm_Equipment.newBuilder();
        b2.setAction(Cm_Equipment.Action.UP_QUALITY);
        b2.setHeroId(100000001);
        b2.setEquipPos(EquipmentPositionEnum.POS_A);
        Response response2 = ClientUtils.send(b2.build(), Action.RESP_UP_QUALITY);
        if (!response2.getResult())

        {
            throw new RuntimeException("服务器返回失败！！！");
        }
        upABCD_EquipLv(1);
    }


// 4020007:1000000,4020008:1000000,4020009:1000000,4020010:1000000,4020011:1000000,4020012:1000000,4020013:1000000,4020014:1000000,4020015:1000000,4020016:1000000,4020017:1000000,4020018:1000000,4020019:1000000,4020020:1000000,4020021:1000000,4020022:1000000,4020023:1000000,4020024:1000000,4020025:1000000,4020026:1000000,4020027:1000000,4020028:1000000,4020029:1000000,4020030:1000000,4020031:1000000,4020032:1000000,4020033:1000000,4020034:1000000,4020035:1000000,4020036:1000000,4030001:1000000,4030002:1000000,4030003:1000000,4030004:1000000,4030005:1000000,4030006:1000000,4030007:1000000,4030008:1000000,4030009:1000000,4030010:1000000,4030011:1000000,4030012:1000000,4030013:1000000,4030014:1000000,4030015:1000000,4030016:1000000,4030017:1000000,4030018:1000000,4030019:1000000,

    //==========================EF=====================================================

    /**
     * 测试点说明： EF 正常升品，自动购买缺少的是试炼币，升品后自动升级
     */

    public static void test10() {
        // ABCD
        Func_Gm.setLv(20);
        upEF_EquipLv(1);
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_QUALITY);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_F);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_QUALITY);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：第一次成功，第二次失败
     * 升品等级不符合！当前等级=15, 需要等级=20, 当前品质=2
     */

    public static void test11() {
        // ABCD
        Func_Gm.setLv(20);
        upEF_EquipLv(1);
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 2; i++) {
            Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
            b1.setAction(Cm_Equipment.Action.UP_QUALITY);
            b1.setHeroId(100000001);
            b1.setEquipPos(EquipmentPositionEnum.POS_F);
            Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_QUALITY);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
    }

    /**
     * 测试点说明： EF 升品失败，钻石不足，自动购买失败Item=[1:5000, 2:100]，资源不足！
     */

    public static void test12() {
        // ABCD
        Func_Gm.setLv(20);
        upEF_EquipLv(1);
        String resource_1 = "2:-100000"; // 扣除钻石
        Func_Gm.addResource(resource_1);
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_Equipment.Builder b1 = Cm_Equipment.newBuilder();
        b1.setAction(Cm_Equipment.Action.UP_QUALITY);
        b1.setHeroId(100000001);
        b1.setEquipPos(EquipmentPositionEnum.POS_F);
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_UP_QUALITY);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

}

