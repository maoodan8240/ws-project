package ws.relationship.topLevelPojos.ultimateTest;

import ws.protos.EnumsProtos.HardTypeEnum;
import ws.protos.EnumsProtos.UltimateTestBuffIndexTypeEnum;
import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by leetony on 16-11-11.
 */
public class UltimateTest extends PlayerTopLevelPojo {
    private static final long serialVersionUID = -7249789313410397557L;

    private int icon; // 试炼塔形象
    /**
     * 当前攻打的关卡层数 每日清零
     */
    private int stageLevel;
    /**
     * 当前关卡挑战的难度 战后设只成null
     */
    private HardTypeEnum hardLevel;

    /**
     * <当前层数<难度:最高星数>>
     */
    private Map<Integer, Map<HardTypeEnum, Integer>> stageLvToBestStar = new HashMap<>();
    /**
     * 历史最高层数
     * 可以跳到的最高楼层
     */
    private int historyHighLevel;
    /**
     * 试炼英雄获得的buff 次日清零
     */
    private List<Integer> heroBuffIds = new ArrayList<>();
    /**
     * 今日试炼积分 次日清零
     */
    private int testScore;
    /**
     * 历史积分
     * 总积分           昨日的总积分的百分之8          今日积分
     * 试炼奖励界面显示的积分  =   historyTestScore     +   testScore
     */
    private int historyTestScore;
    /**
     * 最后一次挑战的时间
     */
    private long flag;
    /**
     * 星数 次日清零
     */
    private int buffStar;
    /**
     * 格斗家id对应试炼塔格斗家 每日清空
     */
    private Map<Integer, UltimateTestHero> idToHeros = new HashMap<>();
    /**
     * 当前关卡 困难度对应的玩家 战后胜利清空 每日清空
     */
    private Map<HardTypeEnum, UltimatetestEnemy> hardLevelToEnemies = new HashMap<>();
    /**
     * buff类型对应buff
     */
    private Map<UltimateTestBuffIndexTypeEnum, UltimatetestBuff> buffIndexAndBuff = new HashMap<>();
    /**
     * 已经领取过的特殊奖励Id
     */
    private List<Integer> rewardScores = new ArrayList<>();
    /**
     * stageLevel对应打开的次数 每日清空
     */
    private Map<Integer, Integer> stageLevelToTimes = new HashMap<>();

    public UltimateTest() {
    }

    public Map<Integer, Map<HardTypeEnum, Integer>> getStageLvToBestStar() {
        return stageLvToBestStar;
    }

    public void setStageLvToBestStar(Map<Integer, Map<HardTypeEnum, Integer>> stageLvToBestStar) {
        this.stageLvToBestStar = stageLvToBestStar;
    }

    public UltimateTest(String playerId) {
        super(playerId);
    }

    public Map<Integer, Integer> getStageLevelToTimes() {
        return stageLevelToTimes;
    }

    public void setStageLevelToTimes(Map<Integer, Integer> stageLevelToTimes) {
        this.stageLevelToTimes = stageLevelToTimes;
    }

    public List<Integer> getRewardScores() {
        return rewardScores;
    }

    public void setRewardScores(List<Integer> rewardScores) {
        this.rewardScores = rewardScores;
    }

    public Map<HardTypeEnum, UltimatetestEnemy> getHardLevelToEnemies() {
        return hardLevelToEnemies;
    }

    public void setHardLevelToEnemies(Map<HardTypeEnum, UltimatetestEnemy> hardLevelToEnemies) {
        this.hardLevelToEnemies = hardLevelToEnemies;
    }

    public List<Integer> getHeroBuffIds() {
        return heroBuffIds;
    }

    public void setHeroBuffIds(List<Integer> heroBuffIds) {
        this.heroBuffIds = heroBuffIds;
    }


    public HardTypeEnum getHardLevel() {
        return hardLevel;
    }

    public void setHardLevel(HardTypeEnum hardLevel) {
        this.hardLevel = hardLevel;
    }

    public int getHistoryHighLevel() {
        return historyHighLevel;
    }

    public void setHistoryHighLevel(int historyHighLevel) {
        this.historyHighLevel = historyHighLevel;
    }


    public int getStageLevel() {
        return stageLevel;
    }

    public void setStageLevel(int stageLevel) {
        this.stageLevel = stageLevel;
    }

    public int getTestScore() {
        return testScore;
    }

    public void setTestScore(int testScore) {
        this.testScore = testScore;
    }

    public int getHistoryTestScore() {
        return historyTestScore;
    }

    public void setHistoryTestScore(int historyTestScore) {
        this.historyTestScore = historyTestScore;
    }

    public long getFlag() {
        return flag;
    }

    public void setFlag(long flag) {
        this.flag = flag;
    }

    public int getBuffStar() {
        return buffStar;
    }

    public void setBuffStar(int buffStar) {
        this.buffStar = buffStar;
    }

    public Map<Integer, UltimateTestHero> getIdToHeros() {
        return idToHeros;
    }

    public void setIdToHeros(Map<Integer, UltimateTestHero> idToHeros) {
        this.idToHeros = idToHeros;
    }

    public Map<UltimateTestBuffIndexTypeEnum, UltimatetestBuff> getBuffIndexAndBuff() {
        return buffIndexAndBuff;
    }

    public void setBuffIndexAndBuff(Map<UltimateTestBuffIndexTypeEnum, UltimatetestBuff> buffIndexAndBuff) {
        this.buffIndexAndBuff = buffIndexAndBuff;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
