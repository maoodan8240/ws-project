package ws.gameServer.features.standalone.extp.challenge.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.relationship.topLevelPojos.challenge.Challenge;

public interface ChallengeCtrl extends PlayerExteControler<Challenge> {


    /**
     * 开始挑战
     *
     * @param stageId
     */
    void beginAttack(int stageId);


    /**
     * 结束战斗
     *
     * @param stageId
     * @param flag
     * @param isWin
     * @param percent
     * @param score
     */
    void endAttack(int stageId, long flag, boolean isWin, int percent, int score);

    /**
     * 扫荡
     *
     * @param stageId
     * @param mopupTimes
     */
    void mopup(int stageId, int mopupTimes);


}
