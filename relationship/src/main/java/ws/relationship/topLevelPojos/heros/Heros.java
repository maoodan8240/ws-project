package ws.relationship.topLevelPojos.heros;

import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

import java.util.HashMap;
import java.util.Map;

public class Heros extends PlayerTopLevelPojo {
    private static final long serialVersionUID = 4352682670635843976L;

    private int maxHeroIdSeq;         // 最大HeroId顺序号
    private int skillPoint;           // 技能点
    private long lastSettleTime;      // 技能点最后结算时间

    private int dyUpSkillTs;          // 每日升级技能次数
    private int dyUpQualityTs;        // 每日升级武将品质次数
    private int dyUpHeroLvTs;         // 每日升级武将次数
    private int hasBuySkillPointTs;   // 已经购买技能点的次数 - 每日重置

    private Map<Integer, Hero> idToHero = new HashMap<>(); // 所有武将

    public Heros() {
    }

    public Heros(String playerId) {
        super(playerId);
    }

    public int getMaxHeroIdSeq() {
        return maxHeroIdSeq;
    }

    public void setMaxHeroIdSeq(int maxHeroIdSeq) {
        this.maxHeroIdSeq = maxHeroIdSeq;
    }

    public Map<Integer, Hero> getIdToHero() {
        return idToHero;
    }

    public void setIdToHero(Map<Integer, Hero> idToHero) {
        this.idToHero = idToHero;
    }

    public int getSkillPoint() {
        return skillPoint;
    }

    public void setSkillPoint(int skillPoint) {
        this.skillPoint = skillPoint;
    }

    public long getLastSettleTime() {
        return lastSettleTime;
    }

    public void setLastSettleTime(long lastSettleTime) {
        this.lastSettleTime = lastSettleTime;
    }

    public int getDyUpSkillTs() {
        return dyUpSkillTs;
    }

    public void setDyUpSkillTs(int dyUpSkillTs) {
        this.dyUpSkillTs = dyUpSkillTs;
    }

    public int getDyUpQualityTs() {
        return dyUpQualityTs;
    }

    public void setDyUpQualityTs(int dyUpQualityTs) {
        this.dyUpQualityTs = dyUpQualityTs;
    }

    public int getDyUpHeroLvTs() {
        return dyUpHeroLvTs;
    }

    public void setDyUpHeroLvTs(int dyUpHeroLvTs) {
        this.dyUpHeroLvTs = dyUpHeroLvTs;
    }

    public int getHasBuySkillPointTs() {
        return hasBuySkillPointTs;
    }

    public void setHasBuySkillPointTs(int hasBuySkillPointTs) {
        this.hasBuySkillPointTs = hasBuySkillPointTs;
    }
}
