package ws.analogClient.features.flow.functions.UltimateTest;

import ws.analogClient.features.utils.ClientUtils;
import ws.protos.EnumsProtos.HardTypeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.UltimateTestProtos.Cm_UltimateTest;
import ws.protos.UltimateTestProtos.Sm_UltimateTest;

import java.util.ArrayList;

/**
 * Created by lee on 17-3-23.
 */
public class Func_Ultimatetest_Utils {


    public static void getEnemy() {
        Cm_UltimateTest.Builder b = Cm_UltimateTest.newBuilder();
        b.setAction(Cm_UltimateTest.Action.GET_ENEMY);
        Response response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_GET_ENEMY);
        ClientUtils.check(response);
    }

    public static void sync() {
        Cm_UltimateTest.Builder b = Cm_UltimateTest.newBuilder();
        b.setAction(Cm_UltimateTest.Action.SYNC);
        Response response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_SYNC);
        ClientUtils.check(response);
    }

    public static void fightSuccess() {
        Cm_UltimateTest.Builder b = Cm_UltimateTest.newBuilder();
        b.setAction(Cm_UltimateTest.Action.BEGIN_ATTACK);
        b.setHardLevel(HardTypeEnum.HARD);
        Response response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_BEGIN_ATTACK);
        ClientUtils.check(response);
        b.setAction(Cm_UltimateTest.Action.END_ATTACK);
        b.setFlag(response.getSmUltimateTest().getFlag());
        b.setStageLevel(response.getSmUltimateTest().getStageLevel());
        b.addAllHeroIds(new ArrayList<>());
        b.setStar(3);
        b.setIsWin(true);
        b.setHardLevel(HardTypeEnum.HARD);
        response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_END_ATTACK);
        ClientUtils.check(response);
    }

    public static void fightFailed() {
        Cm_UltimateTest.Builder b = Cm_UltimateTest.newBuilder();
        b.setAction(Cm_UltimateTest.Action.BEGIN_ATTACK);
        b.setHardLevel(HardTypeEnum.HARD);
        Response response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_BEGIN_ATTACK);
        ClientUtils.check(response);
        b.setAction(Cm_UltimateTest.Action.END_ATTACK);
        b.setFlag(response.getSmUltimateTest().getFlag());
        b.setStageLevel(response.getSmUltimateTest().getStageLevel());
        b.addAllHeroIds(new ArrayList<>());
        b.setStar(3);
        b.setIsWin(false);
        b.setHardLevel(HardTypeEnum.HARD);
        response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_END_ATTACK);
        ClientUtils.check(response);
    }


    public static void getBuff() {
        Cm_UltimateTest.Builder b = Cm_UltimateTest.newBuilder();
        b.setAction(Cm_UltimateTest.Action.GET_BUFF);
        Response response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_GET_BUFF);
        ClientUtils.check(response);
    }


    public static void getRewards() {
        Cm_UltimateTest.Builder b = Cm_UltimateTest.newBuilder();
        b.setAction(Cm_UltimateTest.Action.GET_REWARD);
        Response response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_GET_REWARD);
        ClientUtils.check(response);
    }

    public static void openBox(int stageLevel) {
        Cm_UltimateTest.Builder b = Cm_UltimateTest.newBuilder();
        b.setAction(Cm_UltimateTest.Action.OPEN_BOX);
        b.setStageLevel(stageLevel);
        Response response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_OPEN_BOX);
        ClientUtils.check(response);
    }

    public static void buyBuff() {
        Cm_UltimateTest.Builder b = Cm_UltimateTest.newBuilder();
        b.setAction(Cm_UltimateTest.Action.BUFF);
        Response response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_BUFF);
        ClientUtils.check(response);
    }


    public static void speReward() {
        Cm_UltimateTest.Builder b = Cm_UltimateTest.newBuilder();
        b.setAction(Cm_UltimateTest.Action.SPE_REWARD);
        Response response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_SPE_REWARD);
        ClientUtils.check(response);

    }

}
