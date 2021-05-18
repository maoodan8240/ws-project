package ws.analogClient.features.flow.functions.energyRole;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.EnergyRoleProtos.Cm_EnergyRole;
import ws.protos.EnergyRoleProtos.Cm_EnergyRole.Action;
import ws.protos.EnergyRoleProtos.Sm_EnergyRole;
import ws.protos.MessageHandlerProtos.Response;

public class Func_EnergyRole_SettleEnergy {
    public static void execute() {
        test1();
    }

    /**
     */
    private static void test1() {
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "3:-100000";
        Func_Gm.addResource(resource_1);
        // -------------------------  功能测试 ---------------------------
        Cm_EnergyRole.Builder b = Cm_EnergyRole.newBuilder();
        b.setAction(Action.SETTLE_ENERGY);
        Response response = ClientUtils.send(b.build(), Sm_EnergyRole.Action.RESP_SETTLE_ENERGY);
        ClientUtils.check(response);
    }


}
