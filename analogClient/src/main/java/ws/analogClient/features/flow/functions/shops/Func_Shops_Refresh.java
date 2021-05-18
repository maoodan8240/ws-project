package ws.analogClient.features.flow.functions.shops;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.protos.EnumsProtos.ShopTypeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.ShopsProtos.Cm_Shops;
import ws.protos.ShopsProtos.Cm_Shops.Action;
import ws.protos.ShopsProtos.Sm_Shops;
import ws.analogClient.features.utils.ClientUtils;

public class Func_Shops_Refresh {

    public static void execute() {
        test1();
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
    public static void buy(ShopTypeEnum shopType) {
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_Shops.Builder b1 = Cm_Shops.newBuilder();
        b1.setAction(Action.BUY);
        b1.setShopType(shopType);
        b1.setIdx(21);
        Response response1 = ClientUtils.send(b1.build(), Sm_Shops.Action.RESP_BUY);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        sync();
    }


    /**
     * 测试点说明：
     * 正常购买
     */
    public static void test1() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10000000,2:20000000";
        Func_Gm.addResource(resource_1);
        // -------------------------  功能测试 ---------------------------
        openMysterious();
        buy(ShopTypeEnum.SHOP_GENERAL);
        buy(ShopTypeEnum.SHOP_MYSTERIOUS);


        Cm_Shops.Builder b1 = Cm_Shops.newBuilder();
        b1.setAction(Action.REFRESH);
        b1.setShopType(ShopTypeEnum.SHOP_GENERAL);
        Response response1 = ClientUtils.send(b1.build(), Sm_Shops.Action.RESP_REFRESH);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        sync();
    }

    /**
     * 测试点说明：
     * 测试强制刷新，运行test2()后，停止gameserver，修改Table_Shop.tab神秘商店的ForceRefreshNo字段
     * 重启后运行test1(),测试sync中SHOP_MYSTERIOUS的商品有没有被强制刷新
     */
    public static void test2() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "1:10000000,2:20000000";
        Func_Gm.addResource(resource_1);
        // -------------------------  功能测试 ---------------------------
        openMysterious();
        buy(ShopTypeEnum.SHOP_MYSTERIOUS);
        sync();
    }

}

