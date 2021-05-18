package ws.analogClient.features.flow.functions.pvp;

import ws.analogClient.features.flow.functions.gm.Func_Gm;

/**
 * Created by lee on 17-3-14.
 */
public class Func_Pvp_Refresh {
    public static void execute() {
        //以下这几个测试，最好连续测试，保证竞技场数据库是干净的
        test2();
    }

    /**
     * 测试免费次数使用完，继续刷新
     */
    private static void test1() {
        Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.refreshMinOne();
    }

    /**
     * 测试免费次数使用完，继续刷新，钻石不足
     * 服务器报错，钻石不足
     */
    private static void test2() {
        Func_Gm.addResource("2:-100000");
        Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.refreshMinOne();
        Func_Pvp_Utils.refreshMinOne();
    }
}
