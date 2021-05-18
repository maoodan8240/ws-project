package ws.analogClient.features.flow.functions.Pve;

import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.NewPveProtos.Cm_NewPve;
import ws.protos.NewPveProtos.Cm_NewPve.Action;
import ws.protos.NewPveProtos.Cm_NewPve.Builder;
import ws.protos.NewPveProtos.Sm_NewPve;

/**
 * Created by lee on 17-2-16.
 */
public class Func_Pve_Utils {


    /**
     * 测试普通副本(带GM增加等级)
     * 正常副本
     * 正常情况挑战成功
     *
     * @param isWin
     * @param star
     */
    public static void fightPve(boolean isWin, int star) {
        Builder b = Cm_NewPve.newBuilder();
        b.setAction(Action.BEGIN_PVE);
        b.setStageId(10101);
        Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_BEGIN_PVE);
        ClientUtils.check(response);
        Builder b1 = Cm_NewPve.newBuilder();
        b1.setAction(Action.END_PVE);
        b1.setStageId(10101);
        b1.setIsWin(isWin);
        b1.setFlag(response.getSmNewPve().getFlag());
        b1.setStar(star);
        Response response1 = ClientUtils.send(b1.build(), Sm_NewPve.Action.RESP_END_PVE);
        ClientUtils.check(response1);
    }

    public static void fightPveByStageId(int stageId, boolean isWin, int star) {
        Builder b = Cm_NewPve.newBuilder();
        b.setAction(Action.BEGIN_PVE);
        b.setStageId(stageId);
        Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_BEGIN_PVE);
        ClientUtils.check(response);
        Builder b1 = Cm_NewPve.newBuilder();
        b1.setAction(Action.END_PVE);
        b1.setStageId(stageId);
        b1.setIsWin(isWin);
        b1.setFlag(response.getSmNewPve().getFlag());
        b1.setStar(star);
        Response response1 = ClientUtils.send(b1.build(), Sm_NewPve.Action.RESP_END_PVE);
        ClientUtils.check(response1);
    }


    /**
     * 正常通过一章节副本
     */
    public static void fightOneChapter() {
        Builder b = Cm_NewPve.newBuilder();
        b.setAction(Action.BEGIN_PVE);
        b.setStageId(10101);
        Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_BEGIN_PVE);
        ClientUtils.check(response);
        Builder b1 = Cm_NewPve.newBuilder();
        b1.setAction(Action.END_PVE);
        b1.setStageId(10101);
        b1.setIsWin(true);
        b1.setFlag(response.getSmNewPve().getFlag());
        b1.setStar(3);
        Response response1 = ClientUtils.send(b1.build(), Sm_NewPve.Action.RESP_END_PVE);
        ClientUtils.check(response1);

        b.setStageId(10102);
        response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_BEGIN_PVE);
        ClientUtils.check(response);
        b1.setStageId(10102);
        b1.setFlag(response.getSmNewPve().getFlag());
        response1 = ClientUtils.send(b1.build(), Sm_NewPve.Action.RESP_END_PVE);
        ClientUtils.check(response1);

        b.setStageId(10103);
        response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_BEGIN_PVE);
        ClientUtils.check(response);
        b1.setStageId(10103);
        b1.setFlag(response.getSmNewPve().getFlag());
        response1 = ClientUtils.send(b1.build(), Sm_NewPve.Action.RESP_END_PVE);
        ClientUtils.check(response1);
    }

    /**
     * 测试精英副本(带GM增加等级)
     * 正常情况挑战失败需要先通关10101
     *
     * @param isWin
     * @param star
     */
    public static void fightElitePve(boolean isWin, int star) {
        Builder b = Cm_NewPve.newBuilder();
        b.setAction(Action.BEGIN_PVE);
        b.setStageId(20101);
        Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_BEGIN_PVE);
        ClientUtils.check(response);
        Builder b1 = Cm_NewPve.newBuilder();
        b1.setAction(Action.END_PVE);
        b1.setStageId(20101);
        b1.setIsWin(isWin);
        b1.setFlag(response.getSmNewPve().getFlag());
        b1.setStar(star);
        Response response1 = ClientUtils.send(b1.build(), Sm_NewPve.Action.RESP_END_PVE);
        ClientUtils.check(response1);
    }

    /**
     * 扫荡一次
     */
    public static void mopup(int times) {
        for (int i = 0; i <= times; i++) {
            Builder b = Cm_NewPve.newBuilder();
            b.setAction(Action.MOPUP_PVE);
            b.setStageId(10101);
            Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_MOPUP_PVE);
            ClientUtils.check(response);
        }
    }

    /**
     * 扫荡一次
     */
    public static void mopupElite(int times) {
        for (int i = 1; i <= times; i++) {
            Builder b = Cm_NewPve.newBuilder();
            b.setAction(Action.MOPUP_PVE);
            b.setStageId(20101);
            Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_MOPUP_PVE);
            ClientUtils.check(response);
        }
    }


    /**
     * 测试重置副本挑战次数
     */
    public static void reset_attack_times() {
        Builder b = Cm_NewPve.newBuilder();
        b.setAction(Action.RESET_ATTACK_TIMES);
        b.setStageId(20101);
        Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_RESET_ATTACK_TIMES);
        ClientUtils.check(response);
    }


    /**
     * 测试同步
     */
    public static void sync() {
        Cm_NewPve.Builder b = Cm_NewPve.newBuilder();
        b.setAction(Cm_NewPve.Action.SYNC);
        Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_SYNC);
        ClientUtils.check(response);
    }
}
