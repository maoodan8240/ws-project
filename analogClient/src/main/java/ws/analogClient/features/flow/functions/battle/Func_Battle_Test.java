package ws.analogClient.features.flow.functions.battle;

import ws.analogClient.features.utils.ClientUtils;
import ws.protos.BattleProtos.Cm_TestBattle;
import ws.protos.BattleProtos.Sm_TestBattle;
import ws.protos.MessageHandlerProtos.Response;

public class Func_Battle_Test {

    public static void execute() {
        test1();
    }

    /**
     * 同步
     */
    public static void test1() {
        // -------------------------  资源需求 ---------------------------
        // -------------------------  功能测试 ---------------------------
        Cm_TestBattle.Builder b1 = Cm_TestBattle.newBuilder();
        b1.setAction(Cm_TestBattle.Action.TEST);
        Response response1 = ClientUtils.send(b1.build(), Sm_TestBattle.Action.RESP_TEST);
        if (!response1.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }
}

