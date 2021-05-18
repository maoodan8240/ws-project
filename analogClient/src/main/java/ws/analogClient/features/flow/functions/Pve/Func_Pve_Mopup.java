package ws.analogClient.features.flow.functions.Pve;

import ws.analogClient.features.flow.functions.gm.Func_Gm;

/**
 * Created by leetony on 16-10-25.
 */
public class Func_Pve_Mopup {
    public static void execute() {
        test3();
    }


    /**
     * 测试扫荡
     * 测试点玩家等级没开启扫荡功能
     * 服务器报异常 玩家等级没开启扫荡功能
     */
    public static void test1() {
        Func_Pve_Utils.fightPve(true, 3);
        Func_Gm.setLv(21);
        Func_Pve_Utils.mopup(1);
    }


    /**
     * 测试挑战精英副本次数用光 继续扫荡的错误
     * 服务器报错 剩余挑战次数不足,stageId=20101
     */
    public static void test2() {
        Func_Gm.setLv(24);
        Func_Pve_Utils.fightPve(true, 3);
        Func_Pve_Utils.fightElitePve(true, 3);
        Func_Pve_Utils.mopupElite(5);
    }


    /**
     * 测试扫荡
     * 正常扫荡
     */
    public static void test3() {
        Func_Gm.setLv(24);
        Func_Pve_Utils.fightPve(true, 3);
        Func_Pve_Utils.mopup(1);
    }

    /**
     * 测试扫荡 星数不够异常
     * 服务器报错 星数不够,无法扫荡
     */
    public static void test4() {
        Func_Gm.setLv(24);
        Func_Pve_Utils.fightPve(true, 1);
        Func_Pve_Utils.mopup(1);
    }

    /**
     * 测试扫荡 vip等级不满足扫荡等级
     * 服务器报错 星数不够,无法扫荡
     */
    public static void test5() {
        Func_Gm.setLv(24);
        Func_Pve_Utils.fightPve(true, 1);
        Func_Pve_Utils.mopup(1);
    }


    /**
     * 测试扫荡 玩家等级不满足扫荡等级
     * 服务器报错 星数不够,无法扫荡
     */
    public static void test6() {
        Func_Gm.setLv(1);
        Func_Pve_Utils.fightPve(true, 1);
        Func_Pve_Utils.mopup(1);
    }
}
