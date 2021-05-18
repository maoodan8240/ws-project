package ws.analogClient.features.flow.functions.missions;

import ws.protos.MessageHandlerProtos.Response;
import ws.protos.MissionsProtos.Cm_Missions;
import ws.protos.MissionsProtos.Cm_Missions.Action;
import ws.protos.MissionsProtos.Sm_Missions;
import ws.analogClient.features.utils.ClientUtils;

public class Func_Missions_Sync {

    public static void execute() {
        test1();
    }

    /**
     * 测试点说明： 正常升级
     */
    public static void test1() {
        // -------------------------  功能测试 ---------------------------
        Cm_Missions.Builder b1 = Cm_Missions.newBuilder();
        b1.setAction(Action.SYNC_MISSION);
        Response response1 = ClientUtils.send(b1.build(), Sm_Missions.Action.SYNC_MISSION);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }

}

