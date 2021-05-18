package ws.analogClient.features.flow.functions.itembag;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.protos.ItemBagProtos.Cm_ItemBag;
import ws.protos.ItemBagProtos.Cm_ItemBag.Action;
import ws.protos.ItemBagProtos.Sm_ItemBag;
import ws.protos.MessageHandlerProtos.Response;
import ws.analogClient.features.utils.ClientUtils;

public class Func_ItemBag_UseItem {

    public static void execute() {
        test11();
    }


    /**
     * 测试点说明： 正常使用物品 （直接掉落）
     */
    public static void test1() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "4110105:10,4110107:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_ItemBag.Builder b1 = Cm_ItemBag.newBuilder();
        b1.setAction(Action.USE_ITEM);
        b1.setId(4110105);
        b1.setCount(1);
        Response response1 = ClientUtils.send(b1.build(), Sm_ItemBag.Action.RESP_USE_ITEM);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明： 正常使用物品 （通过掉落库掉落）
     */
    public static void test11() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "4110105:10,4110201:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_ItemBag.Builder b1 = Cm_ItemBag.newBuilder();
        b1.setAction(Action.USE_ITEM);
        b1.setId(4110201);
        b1.setCount(2);
        Response response1 = ClientUtils.send(b1.build(), Sm_ItemBag.Action.RESP_USE_ITEM);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * itemTemplateId=41101051 Table_Item_Row 表中不存在该itemTemplateId.
     */
    public static void test2() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "4110105:10,4110107:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_ItemBag.Builder b1 = Cm_ItemBag.newBuilder();
        b1.setAction(Action.USE_ITEM);
        b1.setId(41101051);
        b1.setCount(1);
        Response response1 = ClientUtils.send(b1.build(), Sm_ItemBag.Action.RESP_USE_ITEM);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * itemTemplateId=4040003 Table_Item_Row 中配置为不能使用.
     */
    public static void test3() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "4110105:10,4110107:10,4040003:01";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_ItemBag.Builder b1 = Cm_ItemBag.newBuilder();
        b1.setAction(Action.USE_ITEM);
        b1.setId(4040003);
        b1.setCount(2);
        Response response1 = ClientUtils.send(b1.build(), Sm_ItemBag.Action.RESP_USE_ITEM);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }
}

