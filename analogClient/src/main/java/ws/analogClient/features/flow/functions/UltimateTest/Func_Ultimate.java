package ws.analogClient.features.flow.functions.UltimateTest;

import ws.analogClient.features.utils.ClientUtils;
import ws.protos.EnumsProtos.HardTypeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.UltimateTestProtos.Cm_UltimateTest;
import ws.protos.UltimateTestProtos.Sm_UltimateTest;

import java.util.ArrayList;

/**
 * Created by lee on 17-3-29.
 */
public class Func_Ultimate {

    public static void execute() {
        Func_Ultimatetest_Utils.sync();
        Func_Ultimatetest_Utils.fightSuccess();
    }

    /**
     * 正常的逻辑 同步，挑战，领奖，开箱子，随机敌人，挑战，随机buff，购买buff
     */
    private static void test1() {
        Func_Ultimatetest_Utils.sync();
        Func_Ultimatetest_Utils.fightSuccess();
        Func_Ultimatetest_Utils.getBuff();
        Func_Ultimatetest_Utils.buyBuff();
        Func_Ultimatetest_Utils.getEnemy();
        Func_Ultimatetest_Utils.fightSuccess();
        Func_Ultimatetest_Utils.getRewards();
        Func_Ultimatetest_Utils.openBox(4);
    }

    /**
     * 测试不是战斗关卡
     * 服务器报错，不是战斗关卡
     */
    private static void test2() {
        Func_Ultimatetest_Utils.sync();
        Func_Ultimatetest_Utils.fightSuccess();
        Func_Ultimatetest_Utils.fightSuccess();
    }

    /**
     * 测试没有敌人
     * 服务器报错，没有敌人
     */
    private static void test3() {
        Func_Ultimatetest_Utils.sync();
        Func_Ultimatetest_Utils.fightSuccess();
        Func_Ultimatetest_Utils.getBuff();
        Func_Ultimatetest_Utils.fightSuccess();
    }

    /**
     * 测试结束战斗参数不合格
     * 服务器报错，结束战斗参数不合格
     */
    private static void test4() {
        Func_Ultimatetest_Utils.sync();
        Cm_UltimateTest.Builder b = Cm_UltimateTest.newBuilder();
        b.setAction(Cm_UltimateTest.Action.BEGIN_ATTACK);
        b.setHardLevel(HardTypeEnum.HARD);
        Response response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_BEGIN_ATTACK);
        ClientUtils.check(response);
        b.setAction(Cm_UltimateTest.Action.END_ATTACK);
        b.setFlag(22);
        b.setStageLevel(response.getSmUltimateTest().getStageLevel());
        b.addAllHeroIds(new ArrayList<>());
        b.setStar(3);
        b.setIsWin(true);
        b.setHardLevel(HardTypeEnum.HARD);
        response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_END_ATTACK);
        ClientUtils.check(response);
    }


    /**
     * 测试不是领奖关卡
     * 服务器报错，不是领奖关卡
     */
    private static void test5() {
        Func_Ultimatetest_Utils.getRewards();
    }


    /**
     * 测试不是购买BUFF关卡购买buff
     * 服务器报错，没有这个buffType
     */
    private static void test6() {
        Func_Ultimatetest_Utils.buyBuff();
    }

    /**
     * 购买所有buff,星数不够
     * 服务器报错，buffStar不足以购买,buffType=FORTY,needBuffStar=40,当前拥有buffStar=18
     */
    private static void test7() {
        Func_Ultimatetest_Utils.sync();
        Func_Ultimatetest_Utils.fightSuccess();
        Func_Ultimatetest_Utils.getBuff();
        Cm_UltimateTest.Builder b = Cm_UltimateTest.newBuilder();
        b.setAction(Cm_UltimateTest.Action.BUFF);
        Response response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_BUFF);
        ClientUtils.check(response);
        b.setAction(Cm_UltimateTest.Action.BUFF);
        response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_BUFF);
        ClientUtils.check(response);
        b.setAction(Cm_UltimateTest.Action.BUFF);
        response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_BUFF);
        ClientUtils.check(response);

    }


    public static void test8() {
        Func_Ultimatetest_Utils.sync();
        Func_Ultimatetest_Utils.fightFailed();
    }

    public static void test9() {
        Func_Ultimatetest_Utils.getBuff();
    }


    public static void test10() {
        Func_Ultimatetest_Utils.sync();
        Cm_UltimateTest.Builder b = Cm_UltimateTest.newBuilder();
        b.setAction(Cm_UltimateTest.Action.GO_TO_LEVEL);
        Response response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_GO_TO_LEVEL);
        ClientUtils.check(response);
    }

    public static void test11() {
        Cm_UltimateTest.Builder b = Cm_UltimateTest.newBuilder();
        b.setAction(Cm_UltimateTest.Action.OPEN_ALL_BOX);
        Response response = ClientUtils.send(b.build(), Sm_UltimateTest.Action.RESP_OPEN_ALL_BOX);
        ClientUtils.check(response);
    }

    public static void test12() {
        Func_Ultimatetest_Utils.speReward();
    }

}
