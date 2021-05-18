package ws.analogClient.features.flow.functions.challenge;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.ChallengeProtos.Cm_Challenge;
import ws.protos.ChallengeProtos.Sm_Challenge;
import ws.protos.MessageHandlerProtos.Response;

/**
 * Created by lee on 17-3-27.
 */
public class Func_Challenge_Attack {
    public static void execute() {
        Func_Challenge_Utils.sync();
        testMoney();
        testExp();
        Func_Challenge_Utils.sync();
    }


    /**
     * 测试打金币副本
     * 周1，3，5，7用经验本
     * 周2，4，6用金币本
     * 目前测试状态 配置表不限制开启日期和CD挑战次数
     */
    private static void testMoney() {
        Func_Challenge_Utils.fight(50501, true, 100, 200);
        // Func_Challenge_Utils.fight(50601, true, 100, 200);
        Func_Challenge_Utils.sync();
    }

    /**
     * 测试打经验副本
     * 周1，3，5，7用50601
     * 周2，4，6用50501测试
     */
    private static void testExp() {
        Func_Challenge_Utils.fight(50601, true, 100, 200);
        Func_Challenge_Utils.sync();
    }


    /**
     * 测试挑战不存的关卡
     * 服务器报错 未发布的关卡
     */
    private static void test2() {
        Cm_Challenge.Builder b = Cm_Challenge.newBuilder();
        b.setAction(Cm_Challenge.Action.BEGIN);
        b.setStageId(50);
        Response response = ClientUtils.send(b.build(), Sm_Challenge.Action.RESP_BEGIN);
        ClientUtils.check(response);
    }

    /**
     * 测试当日未开启的关卡
     * 服务器报错，关卡未到开启日期
     * 目前测试状态 配置表不限制开启日期和CD挑战次数
     */
    public static void test3() {
        Cm_Challenge.Builder b = Cm_Challenge.newBuilder();
        b.setAction(Cm_Challenge.Action.BEGIN);
        // b.setStageId(50501);
        b.setStageId(50601);
        Response response = ClientUtils.send(b.build(), Sm_Challenge.Action.RESP_BEGIN);
        ClientUtils.check(response);
    }

    /**
     * 测试挑战自己等级不够的关卡
     * 服务器报错，等级不足无法挑战
     */
    public static void test4() {
        Func_Gm.setLv(15);
        Cm_Challenge.Builder b = Cm_Challenge.newBuilder();
        b.setAction(Cm_Challenge.Action.BEGIN);
        b.setStageId(50501);
        // b.setStageId(50601);
        Response response = ClientUtils.send(b.build(), Sm_Challenge.Action.RESP_BEGIN);
        ClientUtils.check(response);
    }

    /**
     * 测试挑战未解锁的关卡
     * 服务器报错， 关卡未解锁
     */
    public static void test5() {
        Func_Gm.setLv(30);
        Cm_Challenge.Builder b = Cm_Challenge.newBuilder();
        b.setAction(Cm_Challenge.Action.BEGIN);
        b.setStageId(50502);
        // b.setStageId(50602);
        Response response = ClientUtils.send(b.build(), Sm_Challenge.Action.RESP_BEGIN);
        ClientUtils.check(response);
    }

    /**
     * 测试CD冷却未完成
     * 服务器报错 CD冷却未完成
     */
    public static void test6() {
        Func_Challenge_Utils.fight(50501, true, 100, 200);
        // Func_Challenge_Utils.fight(50601, true, 100, 200);
        Cm_Challenge.Builder b = Cm_Challenge.newBuilder();
        b.setAction(Cm_Challenge.Action.BEGIN);
        b.setStageId(50501);
        // b.setStageId(50601);
        Response response = ClientUtils.send(b.build(), Sm_Challenge.Action.RESP_BEGIN);
        ClientUtils.check(response);
    }

    /**
     * 测试结束战斗传入非法战斗标示
     * 服务器报错 LOGIC_EXCEP请求结束的战斗是非法的,stageId=50501
     */
    public static void test7() {
        Cm_Challenge.Builder b = Cm_Challenge.newBuilder();
        b.setAction(Cm_Challenge.Action.BEGIN);
        b.setStageId(50501);
        // b.setStageId(50601);
        Response response = ClientUtils.send(b.build(), Sm_Challenge.Action.RESP_BEGIN);
        ClientUtils.check(response);
        b.setAction(Cm_Challenge.Action.END);
        b.setStageId(50501);
        // b.setStageId(50601);
        b.setFlag(111);
        b.setIsWin(true);
        b.setPercent(2);
        b.setScore(2);
        ClientUtils.send(b.build(), Sm_Challenge.Action.RESP_END);
        b.setAction(Cm_Challenge.Action.SYNC);
        response = ClientUtils.send(b.build(), Sm_Challenge.Action.RESP_SYNC);
        ClientUtils.check(response);
    }


}
