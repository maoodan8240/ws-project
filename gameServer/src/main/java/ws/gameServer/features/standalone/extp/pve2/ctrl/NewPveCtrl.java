package ws.gameServer.features.standalone.extp.pve2.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.relationship.topLevelPojos.newPve.Chapter;
import ws.relationship.topLevelPojos.newPve.NewPve;
import ws.relationship.topLevelPojos.newPve.NewStage;


public interface NewPveCtrl extends PlayerExteControler<NewPve> {


    /**
     * 领取章节星级奖励
     *
     * @param chapterId            章节
     * @param rewardsStarLevelNode 星级奖励节点,也就是第几个宝箱(0,1,2,3)
     */
    void getStarLevelRewards(int chapterId, int rewardsStarLevelNode);


    /**
     * 进行一次扫荡Pve
     *
     * @param stageId
     */
    void mopUp(int stageId, int mopUpTimes);


    /**
     * 重置关节的攻打次数为0,攻打次数未满的时候不能重置
     *
     * @param stageId
     */
    void resetStageAttackTimes(int stageId);


    /**
     * 开始申请攻打一个关卡
     *
     * @param stageId
     */
    void beginAttackOnePve(int stageId);

    /**
     * 结束战斗
     *
     * @param stageId
     * @param flag
     * @param isWin
     * @param star
     */
    void endAttackOnePve(int stageId, long flag, boolean isWin, int star);

    void getBox(int stageId);


    /**
     * 获取章节
     *
     * @param chapterId
     * @return
     */
    Chapter getChapter(int chapterId);

    /**
     * 获取关卡
     *
     * @param stageId
     * @return
     */
    NewStage getNewStage(int stageId);
}
