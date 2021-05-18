package ws.analogClient.features.flow.functions.Pve;

import ws.analogClient.features.flow.functions.gm.Func_Gm;

/**
 * Created by leetony on 16-10-25.
 */
public class Func_Pve_ResetAttack {
    public static void execute() {
        test4();
    }

    /**
     * 测试挑战精英副本次数用光 继续扫荡的错误
     * 服务器报错 剩余挑战次数不足,stageId=20101
     */
    public static void test0() {
        Func_Pve_Utils.fightPve(true, 3);
        Func_Pve_Utils.fightElitePve(true, 3);
        Func_Gm.setLv(24);
        Func_Gm.setVipLv(4);
        //连续扫荡5次
        Func_Pve_Utils.mopupElite(5);
    }


    /**
     * 测试挑战次数用光 重置挑战次数
     * 正常重置
     */
    public static void test1() {
        Func_Gm.setLv(24);
        Func_Gm.setVipLv(4);
        Func_Gm.addResource("2:5000");
        Func_Pve_Utils.fightPve(true, 3);
        Func_Pve_Utils.fightElitePve(true, 3);
        // 连续扫荡2次
        Func_Pve_Utils.mopupElite(2);
        // 测试重置副本
        Func_Pve_Utils.reset_attack_times();
    }

    /**
     * 测试挑战次数没用光 重置挑战次数的异常
     * 服务器报错 剩余挑战次数不为0,不可以重置！
     */
    public static void test2() {
        Func_Pve_Utils.fightPve(true, 3);
        Func_Pve_Utils.reset_attack_times();
    }

    /**
     * 测试挑战 挑战次数用光继续挑战的错误
     * 服务器报错  剩余挑战次数不足！
     */
    public static void test3() {
        Func_Pve_Utils.fightPve(true, 3);
        for (int i = 0; i <= 5; i++) {
            Func_Pve_Utils.fightElitePve(true, 3);
        }
    }

    /**
     * 测试挑战次数用光 重置挑战次数也用光继续重置
     * 服务器报错 重置次数不足
     */
    public static void test4() {
        Func_Gm.setLv(24);
        Func_Gm.setVipLv(4);
        Func_Gm.addResource("2:5000,3:1200");
        Func_Pve_Utils.fightPve(true, 3);
        Func_Pve_Utils.fightElitePve(true, 3);
        // 连续扫荡2次
        Func_Pve_Utils.mopupElite(2);
        // 测试重置副本
        Func_Pve_Utils.reset_attack_times();

        for (int i = 0; i <= 7; i++) {
            Func_Pve_Utils.mopupElite(3);
            Func_Pve_Utils.reset_attack_times();
        }
        Func_Pve_Utils.sync();
    }
}
