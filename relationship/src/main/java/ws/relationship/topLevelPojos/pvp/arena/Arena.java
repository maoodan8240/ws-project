package ws.relationship.topLevelPojos.pvp.arena;

import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

import java.util.ArrayList;
import java.util.List;

public class Arena extends PlayerTopLevelPojo {
    private static final long serialVersionUID = -3935083622106274193L;

    private ArenaBase base = new ArenaBase();
    private List<Integer> rankRewardsList = new ArrayList<>();       // 已经领取排名奖励的列表
    private List<Integer> enemiesRank = new ArrayList<>();           // 敌人排名
    private List<ArenaRecord> records = new ArrayList<>();           // 战报列表
    private List<Integer> integralRewardsList = new ArrayList<>();   // 已经领取的积分列表 -- 每日重置
    private List<String> worshipPlayerIdList = new ArrayList<>();    // 膜拜玩家列表(膜拜的玩家必须在排行榜前十位，并且膜拜过的不能再膜拜)  -- 每日重置


    public Arena() {
    }

    public Arena(String playerId) {
        super(playerId);
    }

    public List<String> getWorshipPlayerIdList() {
        return worshipPlayerIdList;
    }

    public void setWorshipPlayerIdList(List<String> worshipPlayerIdList) {
        this.worshipPlayerIdList = worshipPlayerIdList;
    }

    public List<Integer> getRankRewardsList() {
        return rankRewardsList;
    }

    public void setRankRewardsList(List<Integer> rankRewardsList) {
        this.rankRewardsList = rankRewardsList;
    }

    public List<Integer> getEnemiesRank() {
        return enemiesRank;
    }

    public void setEnemiesRank(List<Integer> enemiesRank) {
        this.enemiesRank = enemiesRank;
    }

    public List<ArenaRecord> getRecords() {
        return records;
    }

    public void setRecords(List<ArenaRecord> records) {
        this.records = records;
    }

    public List<Integer> getIntegralRewardsList() {
        return integralRewardsList;
    }

    public void setIntegralRewardsList(List<Integer> integralRewardsList) {
        this.integralRewardsList = integralRewardsList;
    }

    public ArenaBase getBase() {
        return base;
    }

    public void setBase(ArenaBase base) {
        this.base = base;
    }
}
