package ws.analogClient.features.flow.functions.pvp;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PvpProtos.Cm_Pvp;
import ws.protos.PvpProtos.Sm_Pvp;
import ws.protos.PvpProtos.Sm_Pvp_Enemy;

/**
 * Created by lee on 17-3-9.
 */
public class Func_Pvp_Challenge {


    public static void execute() {
        //以下这几个测试，最好连续测试，保证竞技场数据库是干净的
        test8();
        // Func_Pvp_Utils.clearCd();
        // Func_Pvp_Utils.fightSuccess(59, "dd");
        // refresh();
    }


    public static void refresh() {
        Func_Pvp_Utils.sync();
        Sm_Pvp_Enemy enemy;
        for (; ; ) {
            enemy = Func_Pvp_Utils.refreshMinOne();
            if (enemy != null) {
                Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
                break;
            }
        }
    }

    /**
     * 挑战一个机器人
     */
    public static void test1() {
        Func_Pvp_Utils.sync();
        Sm_Pvp_Enemy enemy = Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
    }


    /**
     * 挑战2次
     * 测试结果为CD冷却未完成报错
     */
    public static void test2() {
        Sm_Pvp_Enemy enemy = Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
        Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
    }


    /**
     * 挑战5次
     * 正常测试
     */
    public static void test3() {
        Sm_Pvp_Enemy enemy = Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
        Func_Pvp_Utils.clearCd();
        for (int i = 1; i <= 4; i++) {
            Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
            Func_Pvp_Utils.clearCd();
        }
    }

    /**
     * 挑战5次后，购买挑战次数,再挑战
     * 正常测试
     */
    public static void test4() {
        Sm_Pvp_Enemy enemy = Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
        Func_Pvp_Utils.clearCd();
        for (int i = 1; i <= 4; i++) {
            Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
            Func_Pvp_Utils.clearCd();
        }
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.BUY_TIMES);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_BUY_TIMES);
        ClientUtils.check(response);
        Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
    }

    /**
     * 挑战用完了，继续挑战
     * 服务器报错，挑战次数已经用光了
     */
    public static void test5() {
        Sm_Pvp_Enemy enemy = Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
        Func_Pvp_Utils.clearCd();
        for (int i = 1; i <= 5; i++) {
            Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
            Func_Pvp_Utils.clearCd();
        }

    }


    /**
     * CD没有冷却时，清除CD
     * 服务器报错，无需清除CD
     */
    public static void test6() {
        Func_Pvp_Utils.sync();
        Func_Pvp_Utils.clearCd();
    }

    /**
     * 清除CD时，钻石不足
     * 服务器报错，钻石不足
     */
    public static void test7() {
        Func_Gm.addResource("2:-100000");
        Sm_Pvp_Enemy enemy = Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
        Func_Pvp_Utils.clearCd();
    }


    /**
     * 挑战比自己排行高的玩家
     */
    public static void test8() {
        Sm_Pvp_Enemy enemy = Func_Pvp_Utils.refreshMaxOne();
        Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
        Func_Pvp_Utils.clearCd();
        for (int i = 1; i <= 4; i++) {
            enemy = Func_Pvp_Utils.refreshMaxOne();
            Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
            Func_Pvp_Utils.clearCd();
        }
    }


    //同步，当前排行和上一次随机排行时的排位不一样，需要重新随机排行

    //挑战，挑战时当前排行和上次随机排行时的排位不同了，需要重新随机排行

    //挑战失败，所有的逻辑

}
