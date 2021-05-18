package ws.analogClient.features.flow.functions.pvp;

import ws.analogClient.features.flow.functions.gm.Func_Gm;
import ws.analogClient.features.utils.ClientUtils;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PvpProtos.Cm_Pvp;
import ws.protos.PvpProtos.Sm_Pvp;
import ws.protos.PvpProtos.Sm_Pvp_Enemy;

/**
 * Created by lee on 17-3-16.
 */
public class Func_Pvp_Buy {
    public static void execute() {
        // 清库后测试
        test2();
    }

    /**
     * Gm命令去掉玩家身上的金币
     * 测试购买挑战次数 钻石不足
     * 服务器报错，钻石不足
     */
    public static void test1() {
        Func_Gm.addResource("2:-99950");
        Sm_Pvp_Enemy enemy = Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
        Func_Pvp_Utils.clearCd();
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        for (int i = 1; i <= 4; i++) {
            Func_Pvp_Utils.fight(enemy.getEnemy().getRank(), enemy.getEnemy().getBase().getSimplePlayer().getPlayerId());
            Func_Pvp_Utils.clearCd();
        }
        b.setAction(Cm_Pvp.Action.BUY_TIMES);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_BUY_TIMES);
        ClientUtils.check(response);
    }


    /**
     * 挑战次数没有用尽时，购买挑战次数
     * 服务器报错，挑战次数没有用光，不能购买
     */
    public static void test2() {
        Cm_Pvp.Builder b = Cm_Pvp.newBuilder();
        b.setAction(Cm_Pvp.Action.BUY_TIMES);
        Response response = ClientUtils.send(b.build(), Sm_Pvp.Action.RESP_BUY_TIMES);
        ClientUtils.check(response);
    }


}
