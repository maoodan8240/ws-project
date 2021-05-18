package ws.analogClient.features.flow.functions.challenge;

import ws.analogClient.features.utils.ClientUtils;
import ws.protos.ChallengeProtos.Cm_Challenge;
import ws.protos.ChallengeProtos.Sm_Challenge;
import ws.protos.MessageHandlerProtos.Response;

/**
 * Created by lee on 17-3-29.
 */
public class Func_Challenge_MopUp {

    public static void execute() {
        test1();
    }

    /**
     * 测试扫荡，由于冷却CD时间的原因，先把CD校验注释后在测试
     */
    public static void test1() {
        // Func_Challenge_Utils.fight(50501, true, 100, 200);
        Func_Challenge_Utils.fight(50601, true, 100, 200);
        Cm_Challenge.Builder b = Cm_Challenge.newBuilder();
        b.setAction(Cm_Challenge.Action.MOPUP);
        // b.setStageId(50501);
        b.setStageId(50601);
        Response response = ClientUtils.send(b.build(), Sm_Challenge.Action.RESP_MOPUP);
        ClientUtils.check(response);
    }


    /**
     * 测试挑战次数已经用完，需要注释CD的校验
     * 服务器报错， 挑战次数已经用完
     */
    public static void test2() {
        Func_Challenge_Utils.fight(50501, true, 100, 200);
        // Func_Challenge_Utils.fightMoney(50601, true, 100, 200);
        Cm_Challenge.Builder b = Cm_Challenge.newBuilder();
        b.setAction(Cm_Challenge.Action.MOPUP);
        b.setStageId(50501);
        // b.setStageId(50601);
        Response response = ClientUtils.send(b.build(), Sm_Challenge.Action.RESP_MOPUP);
        ClientUtils.check(response);
        Func_Challenge_Utils.fight(50501, true, 100, 200);
        // Func_Challenge_Utils.fightMoney(50601, true, 100, 200);
    }
}
