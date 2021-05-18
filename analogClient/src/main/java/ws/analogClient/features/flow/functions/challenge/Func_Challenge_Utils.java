package ws.analogClient.features.flow.functions.challenge;

import ws.analogClient.features.utils.ClientUtils;
import ws.protos.ChallengeProtos.Cm_Challenge;
import ws.protos.ChallengeProtos.Sm_Challenge;
import ws.protos.MessageHandlerProtos.Response;

/**
 * Created by lee on 17-3-23.
 */
public class Func_Challenge_Utils {

    public static void fight(int stageId, boolean isWin, int percent, int score) {
        Cm_Challenge.Builder b = Cm_Challenge.newBuilder();
        b.setAction(Cm_Challenge.Action.BEGIN);
        b.setStageId(stageId);
        Response response = ClientUtils.send(b.build(), Sm_Challenge.Action.RESP_BEGIN);
        ClientUtils.check(response);
        long flag = response.getSmChallenge().getFlag();
        b.setAction(Cm_Challenge.Action.END);
        b.setStageId(stageId);
        b.setFlag(flag);
        b.setIsWin(isWin);
        b.setPercent(percent);
        b.setScore(score);
        response = ClientUtils.send(b.build(), Sm_Challenge.Action.RESP_END);
        ClientUtils.check(response);
    }


    public static void sync() {
        Cm_Challenge.Builder b = Cm_Challenge.newBuilder();
        b.setAction(Cm_Challenge.Action.SYNC);
        Response response = ClientUtils.send(b.build(), Sm_Challenge.Action.RESP_SYNC);
        ClientUtils.check(response);
    }
}
