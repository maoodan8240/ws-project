package ws.analogClient.features.flow.functions.pvp;

import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PvpProtos.Cm_Pvp;
import ws.protos.PvpProtos.Sm_Pvp;
import ws.analogClient.features.utils.ClientUtils;

/**
 * Created by lee on 17-3-9.
 */
public class Func_Pvp_Worship {

    public static void execute() {
        test2();
    }

    /**
     * 测试一键膜拜
     */
    public static void test1() {
        Func_Pvp_Utils.sync();
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.MAX_WORSHIP);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_MAX_WORSHIP);
        ClientUtils.check(response);
    }

    /**
     * 测试一键膜拜
     * 异常（没有可以膜拜的玩家）
     */
    public static void test2() {
        Func_Pvp_Utils.sync();
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.MAX_WORSHIP);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_MAX_WORSHIP);
        ClientUtils.check(response);
        Response response1 = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_MAX_WORSHIP);
        ClientUtils.check(response1);
    }

}
