package ws.analogClient.features.flow.functions.soulBox;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PyActivitySoulBoxProtos.Cm_PyActivity_SoulBox;
import ws.protos.PyActivitySoulBoxProtos.Sm_PyActivity_SoulBox;

/**
 * Created by lee on 7/4/17.
 */
public class Func_SoulBox {
    public static void execute() {

        test3();
    }


    public static void test1() {
        Cm_PyActivity_SoulBox.Builder b = Cm_PyActivity_SoulBox.newBuilder();
        b.setAction(Cm_PyActivity_SoulBox.Action.SYNC);
        Response response = ClientUtils.send(b.build(), Sm_PyActivity_SoulBox.Action.RESP_SYNC);
        ClientUtils.check(response);
    }

    public static void test2() {
        Func_Gm.addResource("2:60000");
        Cm_PyActivity_SoulBox.Builder b = Cm_PyActivity_SoulBox.newBuilder();
        b.setAction(Cm_PyActivity_SoulBox.Action.PICK_FIVE);
        b.setPickId(1);
        Response response = ClientUtils.send(b.build(), Sm_PyActivity_SoulBox.Action.RESP_PICK_FIVE);
        ClientUtils.check(response);
    }

    public static void test3() {
        Cm_PyActivity_SoulBox.Builder b = Cm_PyActivity_SoulBox.newBuilder();
        b.setAction(Cm_PyActivity_SoulBox.Action.PICK_FIVE);
        b.setPickId(1);
        Response response = ClientUtils.send(b.build(), Sm_PyActivity_SoulBox.Action.RESP_PICK_FIVE);
        ClientUtils.check(response);
    }

    public static void test4() {
        Cm_PyActivity_SoulBox.Builder b = Cm_PyActivity_SoulBox.newBuilder();
        b.setAction(Cm_PyActivity_SoulBox.Action.SELECT);
        b.setHeroId(1000012);
        Response response = ClientUtils.send(b.build(), Sm_PyActivity_SoulBox.Action.RESP_SELECT);
        ClientUtils.check(response);
    }
}
