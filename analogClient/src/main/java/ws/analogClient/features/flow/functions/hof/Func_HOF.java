package ws.analogClient.features.flow.functions.hof;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.protos.HOFProtos.Cm_HOF;
import ws.protos.HOFProtos.Sm_HOF;
import ws.protos.MessageHandlerProtos.Response;
import ws.analogClient.features.utils.ClientUtils;

/**
 * Created by lee on 17-2-7.
 */
public class Func_HOF {
    public static void execute() {
        test_1();
    }

    /**
     * 测试同步
     */
    public static void test_1() {
        Func_Gm.setLv(36);
        Cm_HOF.Builder b = Cm_HOF.newBuilder();
        b.setAction(Cm_HOF.Action.SYNC);
        Response response = ClientUtils.send(b.build(), Sm_HOF.Action.RESP_SYNC);
        ClientUtils.check(response);
    }

    /**
     * 测试等级未到功能开启等级
     */
    public static void test_2() {
        Cm_HOF.Builder b = Cm_HOF.newBuilder();
        b.setAction(Cm_HOF.Action.SYNC);
        Response response = ClientUtils.send(b.build(), Sm_HOF.Action.RESP_SYNC);
        ClientUtils.check(response);
    }


}
