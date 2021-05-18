package ws.analogClient.features.flow.functions.shops;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.ShopsProtos.Cm_Shops;
import ws.protos.ShopsProtos.Cm_Shops.Action;
import ws.protos.ShopsProtos.Sm_Shops;

public class Func_Shops_Sync {

    public static void execute() {
        test1();
    }


    /**
     * 测试点说明：
     */
    public static void test1() {
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
     * 测试点说明：
     */
    public static void test2() {
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_Shops.Builder b1 = Cm_Shops.newBuilder();
        b1.setAction(Action.SYNC);
        Response response1 = ClientUtils.send(b1.build(), Sm_Shops.Action.RESP_SYNC);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Func_Gm.triggerMysterious();
    }

}

