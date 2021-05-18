package ws.analogClient.features.flow.functions.shops;

import ws.protos.EnumsProtos.ShopTypeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.ShopsProtos.Cm_Shops;
import ws.protos.ShopsProtos.Cm_Shops.Action;
import ws.protos.ShopsProtos.Sm_Shops;
import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;

public class Func_Shops_Buy {

    public static void execute() {
        test6();
    }

    /**
     * 同步
     */
    public static void sync() {
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_Shops.Builder b1 = Cm_Shops.newBuilder();
        b1.setAction(Action.SYNC);
        Response response1 = ClientUtils.send(b1.build(), Sm_Shops.Action.RESP_SYNC);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

    /**
     * 开启神秘商店
     */
    public static void openMysterious() {
        sync();
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Func_Gm.triggerMysterious();
    }


    /**
     * 测试点说明：
     * 正常购买
     */
    public static void test1() {
        sync();
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_Shops.Builder b1 = Cm_Shops.newBuilder();
        b1.setAction(Action.BUY);
        b1.setShopType(ShopTypeEnum.SHOP_GENERAL);
        b1.setIdx(21);
        Response response1 = ClientUtils.send(b1.build(), Sm_Shops.Action.RESP_BUY);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        sync();
    }


    /**
     * 测试点说明：第一次正常购买
     * 商店类型shopType=SHOP_GENERAL,idx=21的商品=4020002:10已经卖光了！
     */
    public static void test2() {
        sync();
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        for (int i = 0; i < 2; i++) {
            Cm_Shops.Builder b1 = Cm_Shops.newBuilder();
            b1.setAction(Action.BUY);
            b1.setShopType(ShopTypeEnum.SHOP_GENERAL);
            b1.setIdx(21);
            Response response1 = ClientUtils.send(b1.build(), Sm_Shops.Action.RESP_BUY);
            if (!response1.getResult()) {
                throw new RuntimeException("服务器返回失败！！！");
            }
        }
        sync();
    }


    /**
     * 测试点说明：
     * 商店类型shopType=SHOP_MYSTERIOUS不存在！
     */
    public static void test3() {
        sync();
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_Shops.Builder b1 = Cm_Shops.newBuilder();
        b1.setAction(Action.BUY);
        b1.setShopType(ShopTypeEnum.SHOP_MYSTERIOUS);
        b1.setIdx(21);
        Response response1 = ClientUtils.send(b1.build(), Sm_Shops.Action.RESP_BUY);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        sync();
    }


    /**
     * 测试点说明：需要调整机器时间到下个刷新时间的前一分钟
     * 给客户端同步了 smMsgAction: "Sm_Shops.RESP_SYNC_PART"
     * 到了刷新时间点了，本次购买失败，重新购买！
     */
    public static void test4() {
        sync();
        try {
            Thread.sleep(120 * 1000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_Shops.Builder b1 = Cm_Shops.newBuilder();
        b1.setAction(Action.BUY);
        b1.setShopType(ShopTypeEnum.SHOP_GENERAL);
        b1.setIdx(21);
        Response response1 = ClientUtils.send(b1.build(), Sm_Shops.Action.RESP_BUY);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        sync();
    }

    /**
     * 测试点说明：
     * 商店类型shopType=SHOP_GENERAL,物品中不存在位置为idx=210的商品！
     */
    public static void test5() {
        sync();
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_Shops.Builder b1 = Cm_Shops.newBuilder();
        b1.setAction(Action.BUY);
        b1.setShopType(ShopTypeEnum.SHOP_GENERAL);
        b1.setIdx(210);
        Response response1 = ClientUtils.send(b1.build(), Sm_Shops.Action.RESP_BUY);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        sync();
    }

    /**
     * 测试点说明：开启神秘商店后，暂停60秒，调整服务器时间使得神秘商店过期
     * 神秘商店已经消失
     * 商店类型shopType=SHOP_MYSTERIOUS不存在！
     */
    public static void test6() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10000000,2:20000000";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        openMysterious();

        Cm_Shops.Builder b1 = Cm_Shops.newBuilder();
        b1.setAction(Action.BUY);
        b1.setShopType(ShopTypeEnum.SHOP_MYSTERIOUS);
        b1.setIdx(31);
        Response response1 = ClientUtils.send(b1.build(), Sm_Shops.Action.RESP_BUY);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        sync();
        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_Shops.Builder b2 = Cm_Shops.newBuilder();
        b2.setAction(Action.BUY);
        b2.setShopType(ShopTypeEnum.SHOP_MYSTERIOUS);
        b2.setIdx(21);
        Response response2 = ClientUtils.send(b2.build(), Sm_Shops.Action.RESP_BUY);
        if (!response2.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        sync();
    }
}

