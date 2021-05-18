package ws.analogClient.features.flow.functions.Pve;

import ws.protos.MessageHandlerProtos.Response;
import ws.protos.NewPveProtos.Cm_NewPve;
import ws.protos.NewPveProtos.Cm_NewPve.Action;
import ws.protos.NewPveProtos.Cm_NewPve.Builder;
import ws.protos.NewPveProtos.Sm_NewPve;
import ws.analogClient.features.utils.ClientUtils;

/**
 * Created by leetony on 16-10-25.
 */
public class Func_Pve_GetReward {
    public static void execute() {
        test7();
    }


    /**
     * 开宝箱
     */
    public static void test1() {
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

        Builder b2 = Cm_NewPve.newBuilder();
        b2.setAction(Action.GET_REWARDS);
        b2.setChapterId(101);
        b2.setBoxId(1);
        Response response2 = ClientUtils.send(b2.build(), Sm_NewPve.Action.RESP_GET_REWARDS);
        ClientUtils.check(response2);
    }


    /**
     * 测试领取非法的关卡ID的宝箱
     * 服务器报错 异常mapId 不存在
     */
    public static void test2() {
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
        //领取不存在的关卡ID
        Builder b2 = Cm_NewPve.newBuilder();
        b2.setAction(Action.GET_REWARDS);
        b2.setChapterId(1001);
        b2.setBoxId(1);
        Response response2 = ClientUtils.send(b2.build(), Sm_NewPve.Action.RESP_GET_REWARDS);
        ClientUtils.check(response2);
    }


    /**
     * 测试领取星数不够的宝箱
     */
    public static void test3() {
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
        //第一章比较特殊第一个宝箱需要7个星星，而且只有一个宝箱
        Builder b2 = Cm_NewPve.newBuilder();
        b2.setAction(Action.GET_REWARDS);
        b2.setChapterId(101);
        b2.setBoxId(1);
        Response response2 = ClientUtils.send(b2.build(), Sm_NewPve.Action.RESP_GET_REWARDS);
        ClientUtils.check(response2);

    }


    /**
     * 测试领取星星数为0 宝箱是空的 异常
     */
    public static void test4() {
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
        //星星的数量只够开第一个宝箱，现在尝试开第三个宝箱
        Builder b2 = Cm_NewPve.newBuilder();
        b2.setAction(Action.GET_REWARDS);
        b2.setChapterId(101);
        b2.setBoxId(3);
        Response response2 = ClientUtils.send(b2.build(), Sm_NewPve.Action.RESP_GET_REWARDS);
        ClientUtils.check(response2);

    }

    /**
     * 领取已经领取过的宝箱
     */
    public static void test5() {
        Func_Pve_Utils.fightOneChapter();

        Builder b2 = Cm_NewPve.newBuilder();
        b2.setAction(Action.GET_REWARDS);
        b2.setChapterId(101);
        b2.setBoxId(1);
        Response response2 = ClientUtils.send(b2.build(), Sm_NewPve.Action.RESP_GET_REWARDS);
        ClientUtils.check(response2);

        Response response3 = ClientUtils.send(b2.build(), Sm_NewPve.Action.RESP_GET_REWARDS);
        ClientUtils.check(response3);
    }


    /**
     * 测试领取没有宝箱的关卡
     */
    public static void test6() {
        Cm_NewPve.Builder b = Cm_NewPve.newBuilder();
        b.setAction(Action.GET_BOX);
        b.setStageId(10101);
        Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_GET_BOX);
        ClientUtils.check(response);
    }

    /**
     * 正常领取关卡后置宝箱
     */
    public static void test7() {
        Func_Pve_Utils.fightOneChapter();

        Builder b = Cm_NewPve.newBuilder();
        b.setAction(Action.BEGIN_PVE);
        b.setStageId(10201);
        Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_BEGIN_PVE);
        ClientUtils.check(response);
        Builder b1 = Cm_NewPve.newBuilder();
        b1.setAction(Action.END_PVE);
        b1.setStageId(10201);
        b1.setIsWin(true);
        b1.setFlag(response.getSmNewPve().getFlag());
        b1.setStar(3);
        Response response1 = ClientUtils.send(b1.build(), Sm_NewPve.Action.RESP_END_PVE);
        ClientUtils.check(response1);

        b.setStageId(10202);
        response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_BEGIN_PVE);
        ClientUtils.check(response);
        b1.setStageId(10202);
        b1.setFlag(response.getSmNewPve().getFlag());
        response1 = ClientUtils.send(b1.build(), Sm_NewPve.Action.RESP_END_PVE);
        ClientUtils.check(response1);


        Cm_NewPve.Builder b2 = Cm_NewPve.newBuilder();
        b2.setAction(Action.GET_BOX);
        b2.setStageId(10202);
        Response response2 = ClientUtils.send(b2.build(), Sm_NewPve.Action.RESP_GET_BOX);
        ClientUtils.check(response2);
    }

}
