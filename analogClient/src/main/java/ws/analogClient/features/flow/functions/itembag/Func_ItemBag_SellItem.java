package ws.analogClient.features.flow.functions.itembag;

import ws.protos.ItemBagProtos.Cm_ItemBag;
import ws.protos.ItemBagProtos.Cm_ItemBag.Action;
import ws.protos.ItemBagProtos.Sm_ItemBag;
import ws.protos.MessageHandlerProtos.Response;
import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;

public class Func_ItemBag_SellItem {

    public static void execute() {
        // test5();
    }


    /**
     * 测试点说明： 正常出售物品
     */
    public static void test1() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "4030011:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_ItemBag.Builder b1 = Cm_ItemBag.newBuilder();
        b1.setAction(Action.SELL_ITEM);
        b1.setId(4030011);
        b1.setCount(10);
        Response response1 = ClientUtils.send(b1.build(), Sm_ItemBag.Action.RESP_SELL_ITEM);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * itemTemplateId=40300111 Table_Item_Row 表中不存在该itemTemplateId.
     */
    public static void test2() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "4030011:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_ItemBag.Builder b1 = Cm_ItemBag.newBuilder();
        b1.setAction(Action.SELL_ITEM);
        b1.setId(40300111);
        b1.setCount(1);
        Response response1 = ClientUtils.send(b1.build(), Sm_ItemBag.Action.RESP_SELL_ITEM);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * itemTemplateId=4010010 Table_Item_Row 中配置为不能出售.
     */
    public static void test3() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "4030011:10,4010010:11";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_ItemBag.Builder b1 = Cm_ItemBag.newBuilder();
        b1.setAction(Action.SELL_ITEM);
        b1.setId(4010010);
        b1.setCount(2);
        Response response1 = ClientUtils.send(b1.build(), Sm_ItemBag.Action.RESP_SELL_ITEM);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


    /**
     * 测试点说明：
     * Item=[4030011:11]，资源不足！
     */
    public static void test4() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "4030011:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_ItemBag.Builder b1 = Cm_ItemBag.newBuilder();
        b1.setAction(Action.SELL_ITEM);
        b1.setId(4030011);
        b1.setCount(11);
        Response response1 = ClientUtils.send(b1.build(), Sm_ItemBag.Action.RESP_SELL_ITEM);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 测试点说明：
     * count=-1 count必须大于0.
     */
    public static void test5() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "4030011:10";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_ItemBag.Builder b1 = Cm_ItemBag.newBuilder();
        b1.setAction(Action.SELL_ITEM);
        b1.setId(4030011);
        b1.setCount(-1);
        Response response1 = ClientUtils.send(b1.build(), Sm_ItemBag.Action.RESP_SELL_ITEM);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }


}

