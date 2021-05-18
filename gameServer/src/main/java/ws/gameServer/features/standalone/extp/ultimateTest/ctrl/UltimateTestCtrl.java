package ws.gameServer.features.standalone.extp.ultimateTest.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.protos.EnumsProtos.HardTypeEnum;
import ws.protos.EnumsProtos.UltimateTestBuffIndexTypeEnum;
import ws.protos.UltimateTestProtos.Sm_UltimateTest_Hero_Info;
import ws.protos.UltimateTestProtos.Sm_UltimateTest_Use_Buff;
import ws.relationship.topLevelPojos.ultimateTest.UltimateTest;

import java.util.List;

public interface UltimateTestCtrl extends PlayerExteControler<UltimateTest> {

    /**
     * 开始战斗
     *
     * @param hardLevel
     */
    void beginAttack(HardTypeEnum hardLevel);

    /**
     * 结束战斗
     *
     * @param flag
     * @param stageLevel
     * @param heroIdsList
     * @param star
     * @param isWin
     * @param hardLevel
     */
    void endAttack(long flag, int stageLevel, List<Sm_UltimateTest_Hero_Info> heroIdsList, int star, boolean isWin, HardTypeEnum hardLevel);

    /**
     * 获取敌人
     */
    void getEnemy();

    /**
     * 开启钻石宝箱
     *
     * @param stageLevel
     */
    void openBox(int stageLevel);

    /**
     * 购买buff关卡
     *
     * @param buffIndex
     */
    void buyBuff(UltimateTestBuffIndexTypeEnum buffIndex);

    /**
     * 获奖关卡
     */
    void getReward();


    /**
     * 获取特殊关卡奖励
     *
     * @param rewardScore
     */
    void getSpeReward(int rewardScore);

    /**
     * 同步特殊关卡信息
     */
    void speReward();

    /**
     * 开启所有宝箱
     *
     * @param times
     */
    void openAllBox(int times);

    /**
     * 跳到楼层
     */
    void goToLevel();

    /**
     * 获取buff关卡信息
     */
    void getBuffInfo();

    /**
     * 使用buff
     *
     * @param useBuffs
     * @param buffIndex
     */
    void useBuff(List<Sm_UltimateTest_Use_Buff> useBuffs, UltimateTestBuffIndexTypeEnum buffIndex);

    /**
     * 修改试炼塔形象
     *
     * @param icon
     */
    void changeIcon(int icon);
}
