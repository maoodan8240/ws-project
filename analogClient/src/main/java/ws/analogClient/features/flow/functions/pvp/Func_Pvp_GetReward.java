package ws.analogClient.features.flow.functions.pvp;

import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PvpProtos.Cm_Pvp;
import ws.protos.PvpProtos.Sm_Pvp;
import ws.protos.PvpProtos.Sm_Pvp_Enemy;

/**
 * Created by lee on 17-3-16.
 */
public class Func_Pvp_GetReward {
    public static void execute() {
        // 清库后测试
        test8();
    }


    /**
     * 挑战机器人第20名，然后领取排名20的奖励
     * 正常测试
     */
    public static void test1() {
        //TODO make a function to challenge font one
        Sm_Pvp_Enemy enemy = Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.GET_RANK_REWARDS);
        b.setRankRewards(enemy.getEnemy().getRank());
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_GET_RANK_REWARDS);
        ClientUtils.check(response);
    }

    /**
     * 挑战机器人，然后领取多个排行奖励
     * 正常测试
     */
    public static void test2() {
        //TODO make a function to challenge font one
        Sm_Pvp_Enemy enemy = Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.GET_RANK_REWARDS);
        b.setRankRewards(enemy.getEnemy().getRank());
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_GET_RANK_REWARDS);
        ClientUtils.check(response);
        b.setRankRewards(enemy.getEnemy().getRank() + 1);
        response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_GET_RANK_REWARDS);
        ClientUtils.check(response);
        b.setRankRewards(enemy.getEnemy().getRank() + 2);
        response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_GET_RANK_REWARDS);
        ClientUtils.check(response);
    }

    /**
     * 测试领取排名未到的排名奖励
     * 服务器报错 排名没达到不能领取这个奖励
     */
    public static void test3() {
        Func_Pvp_Utils.sync();
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.GET_RANK_REWARDS);
        b.setRankRewards(20);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_GET_RANK_REWARDS);
        ClientUtils.check(response);
    }

    /**
     * 挑战机器人第22名，然后领取排名22的奖励,然后再领取排名22的奖励
     * 服务器报错 这个排名已经领取过了
     */
    public static void test4() {
        //TODO make a function to challenge font one
        Sm_Pvp_Enemy enemy = Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.GET_RANK_REWARDS);
        b.setRankRewards(enemy.getEnemy().getRank());
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_GET_RANK_REWARDS);
        ClientUtils.check(response);
        response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_GET_RANK_REWARDS);
        ClientUtils.check(response);
    }

    /**
     * 测试打第一个机器人，然后打第二个4次，积分满了，领取积分奖励，一键领取积分奖励
     */
    public static void test5() {
        Sm_Pvp_Enemy enemy = Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
        Func_Pvp_Utils.clearCd();
        for (int i = 1; i <= 4; i++) {
            Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
            Func_Pvp_Utils.clearCd();
        }
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.GET_INTEGRAL_REWARDS);
        b.setIntegralRewards(2);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_GET_INTEGRAL_REWARDS);
        ClientUtils.check(response);
        b.setAction(Cm_Pvp.Action.MAX_GET_INTEGRAL_REWARDS);
        response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_MAX_GET_INTEGRAL_REWARDS);
        ClientUtils.check(response);
    }

    /**
     * 测试领取没有达到的积分奖励
     * 服务器报错，没有达到这个积分奖励，不能领奖
     */
    public static void test6() {
        Func_Pvp_Utils.sync();
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.GET_INTEGRAL_REWARDS);
        b.setIntegralRewards(2);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_GET_INTEGRAL_REWARDS);
        ClientUtils.check(response);
    }

    /**
     * 测试一键领取，没有可以领取的物品
     * 服务器返回没有可以领取的奖励
     */
    public static void test7() {
        Func_Pvp_Utils.sync();
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.MAX_GET_INTEGRAL_REWARDS);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_MAX_GET_INTEGRAL_REWARDS);
        ClientUtils.check(response);
    }


    /**
     * 先挑战机器人第1名，然后领取积分奖励，然后再领取同一个奖励
     * 服务器报错，这个奖励已经领取过了
     */
    public static void test8() {
        Sm_Pvp_Enemy enemy = Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.GET_INTEGRAL_REWARDS);
        b.setIntegralRewards(2);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_GET_INTEGRAL_REWARDS);
        ClientUtils.check(response);
        response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_GET_INTEGRAL_REWARDS);
        ClientUtils.check(response);
    }


}
