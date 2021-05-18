package ws.analogClient.features.flow.functions.Pve;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.NewPveProtos.Cm_NewPve;
import ws.protos.NewPveProtos.Cm_NewPve.Action;
import ws.protos.NewPveProtos.Sm_NewPve;

/**
 * Created by leetony on 16-10-25.
 */
public class Func_Pve_Attack {
    public static void execute() {
        test1();
        // Cm_NewPve.Builder b = Cm_NewPve.newBuilder();
        // b.setAction(Cm_NewPve.Action.GET_BOX);
        // b.setStageId(10201);
        // Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_GET_BOX);
        // ClientUtils.check(response);
    }


    /**
     * 测试挑战不存在的副本异常
     * 服务器报错找不到副本
     */
    public static void error_stage() {
        Func_Gm.setLv(22);
        Cm_NewPve.Builder b = Cm_NewPve.newBuilder();
        b.setAction(Cm_NewPve.Action.BEGIN_PVE);
        b.setStageId(100111);
        Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_BEGIN_PVE);
        ClientUtils.check(response);
    }

    /**
     * 测试只能挑战成功一次的副本,第二次会报错
     * 这个测试需要修改配置表里对应该100102关卡的isFirst字段，要改成1，现在配置表中没有只能挑战一次的关卡
     */
    public static void oneTimesStage() {
        Cm_NewPve.Builder b = Cm_NewPve.newBuilder();
        b.setAction(Cm_NewPve.Action.BEGIN_PVE);
        b.setStageId(100102);
        Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_BEGIN_PVE);
        ClientUtils.check(response);
        Cm_NewPve.Builder b1 = Cm_NewPve.newBuilder();
        b1.setAction(Cm_NewPve.Action.END_PVE);
        b1.setStageId(100102);
        b1.setIsWin(true);
        b1.setFlag(response.getSmNewPve().getFlag());
        b1.setStar(3);
        Response response1 = ClientUtils.send(b1.build(), Sm_NewPve.Action.RESP_END_PVE);
        ClientUtils.check(response1);
    }


    /**
     * 正常挑战普通副本
     */
    public static void test1() {
        Func_Pve_Utils.fightPve(true, 3);
    }

    /**
     * 正常挑战精英副本
     */
    public static void test2() {
        Func_Pve_Utils.fightPve(true, 3);
        Func_Pve_Utils.fightElitePve(true, 3);


    }


    /**
     * 测试挑战 挑战次数用光继续挑战的错误
     * 服务器报错 剩余挑战次数不足
     */
    public static void test3() {
        Func_Pve_Utils.fightPve(true, 3);
        for (int i = 0; i <= 5; i++) {
            Func_Pve_Utils.fightElitePve(true, 3);
        }
    }

    /**
     * 测试挑战前置关卡未解锁的关卡
     * 服务器报未满足解锁条件
     */
    public static void test4() {
        Cm_NewPve.Builder b = Cm_NewPve.newBuilder();
        b.setAction(Action.BEGIN_PVE);
        b.setStageId(20101);
        Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_BEGIN_PVE);
        ClientUtils.check(response);
        Cm_NewPve.Builder b1 = Cm_NewPve.newBuilder();
        b1.setAction(Action.END_PVE);
        b1.setStageId(20101);
        b1.setIsWin(true);
        b1.setFlag(response.getSmNewPve().getFlag());
        b1.setStar(3);
        Response response1 = ClientUtils.send(b1.build(), Sm_NewPve.Action.RESP_END_PVE);
        ClientUtils.check(response1);
    }


    /**
     * 非法参数
     */
    public static void test5() {
        Cm_NewPve.Builder b = Cm_NewPve.newBuilder();
        b.setAction(Action.BEGIN_PVE);
        b.setStageId(0);
        Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_BEGIN_PVE);
        ClientUtils.check(response);
        Cm_NewPve.Builder b1 = Cm_NewPve.newBuilder();
        b1.setAction(Action.END_PVE);
        b1.setStageId(0);
        b1.setIsWin(true);
        b1.setFlag(response.getSmNewPve().getFlag());
        b1.setStar(3);
        Response response1 = ClientUtils.send(b1.build(), Sm_NewPve.Action.RESP_END_PVE);
        ClientUtils.check(response1);
    }

    /**
     * 挑战失败，打输了
     */
    public static void test6() {
        Cm_NewPve.Builder b = Cm_NewPve.newBuilder();
        b.setAction(Action.BEGIN_PVE);
        b.setStageId(10101);
        Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_BEGIN_PVE);
        ClientUtils.check(response);
        Cm_NewPve.Builder b1 = Cm_NewPve.newBuilder();
        b1.setAction(Action.END_PVE);
        b1.setStageId(10101);
        b1.setIsWin(false);
        b1.setFlag(response.getSmNewPve().getFlag());
        b1.setStar(3);
        Response response1 = ClientUtils.send(b1.build(), Sm_NewPve.Action.RESP_END_PVE);
        ClientUtils.check(response1);
    }


    /**
     * 非法星数
     */
    public static void test7() {
        Func_Pve_Utils.fightPve(true, 20);
    }


    /**
     * 测试结束战斗传非法的战斗标示
     */
    public static void test8() {
        Cm_NewPve.Builder b = Cm_NewPve.newBuilder();
        b.setAction(Action.BEGIN_PVE);
        b.setStageId(10101);
        Response response = ClientUtils.send(b.build(), Sm_NewPve.Action.RESP_BEGIN_PVE);
        ClientUtils.check(response);
        Cm_NewPve.Builder b1 = Cm_NewPve.newBuilder();
        b1.setAction(Action.END_PVE);
        b1.setStageId(10101);
        b1.setIsWin(false);
        b1.setFlag(-1);
        b1.setStar(3);
        Response response1 = ClientUtils.send(b1.build(), Sm_NewPve.Action.RESP_END_PVE);
        ClientUtils.check(response1);
    }


    /**
     * 测试结束战斗传非法的战斗标示
     */
    public static void test9() {
        Func_Gm.setLv(0);
        Func_Pve_Utils.fightPve(true, 3);
    }

    public static void test10() {
        Func_Pve_Utils.sync();
        Func_Pve_Utils.fightPveByStageId(10101, true, 3);
        Func_Pve_Utils.fightPveByStageId(10102, true, 3);
        Func_Pve_Utils.fightPveByStageId(10103, true, 3);
        Func_Pve_Utils.fightPveByStageId(10201, true, 3);
        Func_Pve_Utils.fightPveByStageId(10202, true, 3);
        Func_Pve_Utils.fightPveByStageId(10203, true, 3);
        Func_Pve_Utils.fightPveByStageId(10204, true, 3);
        Func_Pve_Utils.fightPveByStageId(10205, true, 3);
        Func_Pve_Utils.fightPveByStageId(10206, true, 3);
        Func_Pve_Utils.fightPveByStageId(10207, true, 3);
        Func_Pve_Utils.fightPveByStageId(10208, true, 3);
        Func_Pve_Utils.fightPveByStageId(10301, true, 3);
        Func_Pve_Utils.fightPveByStageId(10302, true, 3);
        Func_Pve_Utils.fightPveByStageId(10303, true, 3);
        Func_Pve_Utils.fightPveByStageId(10304, true, 3);
        Func_Pve_Utils.fightPveByStageId(10305, true, 3);
        Func_Pve_Utils.fightPveByStageId(10306, true, 3);
        Func_Pve_Utils.fightPveByStageId(10307, true, 3);
        Func_Pve_Utils.fightPveByStageId(10308, true, 3);
        Func_Pve_Utils.fightPveByStageId(20101, true, 3);
        Func_Pve_Utils.fightPveByStageId(20102, true, 3);
        Func_Pve_Utils.fightPveByStageId(20103, true, 3);
        Func_Pve_Utils.sync();
    }


}
