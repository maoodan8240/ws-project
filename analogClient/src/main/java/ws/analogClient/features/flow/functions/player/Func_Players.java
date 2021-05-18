package ws.analogClient.features.flow.functions.player;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerProtos.Cm_Player;
import ws.protos.PlayerProtos.Sm_Player.Action;
import ws.analogClient.features.utils.ClientUtils;

public class Func_Players {

    public static void execute() {
        // test1();
    }

    /**
     * 测试点说明： 正常升级
     */
    public static void test1() {
        Func_Gm.setLv(1);
        // -------------------------  资源需求 ---------------------------
        String resource_1 = "4:15";
        Func_Gm.addResource(resource_1);

        // -------------------------  功能测试 ---------------------------
        Cm_Player.Builder b1 = Cm_Player.newBuilder();
        b1.setAction(Cm_Player.Action.RENAME);
        b1.setNewName("new1111");
        Response response1 = ClientUtils.send(b1.build(), Action.RESP_RENAME);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }
}

