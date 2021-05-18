package ws.gameServer.features.standalone.extp.arena.ctrl;

import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.relationship.topLevelPojos.pvp.arena.Arena;
import ws.relationship.topLevelPojos.pvp.arena.ArenaRecord;

public interface ArenaCtrl extends PlayerExteControler<Arena> {

    /**
     * 刷新敌方列表
     */
    void refresh();

    /**
     * 清除挑战cd
     */
    void clearCd();

    /**
     * 修改宣言
     *
     * @param newDeclaration
     */
    void modifyDeclaration(String newDeclaration);

    /**
     * 修改pvp形象
     *
     * @param icon
     */
    void modifyPvpIcon(int icon);

    /**
     * 购买攻打次数
     */
    void buyAttackTimes();

    /**
     * 挑战敌人
     *
     * @param rank
     * @param playerId
     */
    void challenge(int rank, String playerId);

    /**
     * 一键膜拜
     */
    void maxWorship();

    /**
     * 获取最高排名奖励，每个档位只可以领取一次
     *
     * @param rankRewards 具体奖励需要的排名数值，如 5000,4000,100..
     */
    void getRankRewards(int rankRewards);

    /**
     * 获取每日积分奖励
     *
     * @param integralRewards
     */
    void getIntegralRewards(int integralRewards);

    /**
     * 获取所有每日积分奖励
     */
    void maxGetIntegralRewards();

    /**
     * 获取当前排名
     *
     * @return
     */
    int getCurRank();


    /**
     * 单次膜拜
     *
     * @param playerId
     */
    void worship(String playerId);


    /**
     * 回放战报
     *
     * @param recordId
     */
    void displayBattleRecords(String recordId);


    /**
     * 获取战报
     */
    void getBattleRecords();

    /**
     * 查询排行榜
     */
    void queryRank();


    /**
     * 玩家被攻击了,增加新的战斗记录
     *
     * @param arenaRecord
     */
    void onAddNewBattleRecords(ArenaRecord arenaRecord);
}
