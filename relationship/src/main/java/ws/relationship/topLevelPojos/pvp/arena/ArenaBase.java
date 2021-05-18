package ws.relationship.topLevelPojos.pvp.arena;

import java.io.Serializable;

public class ArenaBase implements Serializable {
    private static final long serialVersionUID = -8322830780465019422L;

    private int challengeTimes;            // 已经挑战的次数 -- 每日重置
    private int refreshTimes;              // 已经刷新的次数 -- 每日重置

    private int buyChallengeTimes;         // 购买攻击的次数 -- 每日重置
    private int buyRefreshTimes;           // 购买刷新的次数 -- 每日重置

    private long cdEndTime;                // cd结束时间点 -- 每日重置
    private String declaration;            // 宣言
    private int integral;                  // 积分 -- 每日重置
    private int lastRefreshRank;           // 最后一次刷新排名列表时的排位
    private int maxRank = -1;              // 历史最高排名
    private int pvpIcon;                   // pvp形象
    private int victoryTimes;              // 胜利的次数


    public ArenaBase() {
    }


    public int getChallengeTimes() {
        return challengeTimes;
    }

    public void setChallengeTimes(int challengeTimes) {
        this.challengeTimes = challengeTimes;
    }

    public long getCdEndTime() {
        return cdEndTime;
    }

    public void setCdEndTime(long cdEndTime) {
        this.cdEndTime = cdEndTime;
    }

    public int getRefreshTimes() {
        return refreshTimes;
    }

    public void setRefreshTimes(int refreshTimes) {
        this.refreshTimes = refreshTimes;
    }

    public String getDeclaration() {
        return declaration;
    }

    public void setDeclaration(String declaration) {
        this.declaration = declaration;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public int getLastRefreshRank() {
        return lastRefreshRank;
    }

    public void setLastRefreshRank(int lastRefreshRank) {
        this.lastRefreshRank = lastRefreshRank;
    }

    public int getMaxRank() {
        return maxRank;
    }

    public void setMaxRank(int maxRank) {
        this.maxRank = maxRank;
    }

    public int getPvpIcon() {
        return pvpIcon;
    }

    public void setPvpIcon(int pvpIcon) {
        this.pvpIcon = pvpIcon;
    }

    public int getVictoryTimes() {
        return victoryTimes;
    }

    public void setVictoryTimes(int victoryTimes) {
        this.victoryTimes = victoryTimes;
    }

    public int getBuyChallengeTimes() {
        return buyChallengeTimes;
    }

    public void setBuyChallengeTimes(int buyChallengeTimes) {
        this.buyChallengeTimes = buyChallengeTimes;
    }

    public int getBuyRefreshTimes() {
        return buyRefreshTimes;
    }

    public void setBuyRefreshTimes(int buyRefreshTimes) {
        this.buyRefreshTimes = buyRefreshTimes;
    }
}
