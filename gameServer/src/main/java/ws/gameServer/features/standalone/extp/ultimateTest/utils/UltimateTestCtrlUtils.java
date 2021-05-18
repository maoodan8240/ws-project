package ws.gameServer.features.standalone.extp.ultimateTest.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.protos.EnumsProtos.HardTypeEnum;
import ws.protos.EnumsProtos.UltimateTestBuffIndexTypeEnum;
import ws.protos.EnumsProtos.UltimateTestTypeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_DropLibrary_Row;
import ws.relationship.table.tableRows.Table_Ultimate_Monster_Row;
import ws.relationship.table.tableRows.Table_Ultimate_Stage_Row;
import ws.relationship.topLevelPojos.common.Iac;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.topLevelPojos.ultimateTest.UltimatetestBuff;
import ws.relationship.topLevelPojos.ultimateTest.UltimatetestEnemy;
import ws.relationship.topLevelPojos.ultimateTest.UltimatetestMonster;
import ws.relationship.utils.NameUtils;
import ws.relationship.utils.RandomUtils;
import ws.relationship.utils.SimplePojoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class UltimateTestCtrlUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(UltimateTestCtrlUtils.class);

    public static Table_Ultimate_Stage_Row getUltimateStageRow(int stageLevel) {
        return RootTc.get(Table_Ultimate_Stage_Row.class).get(stageLevel);
    }

    public static Table_Ultimate_Monster_Row getUltimateMosterRow(int stageLevel) {
        return RootTc.get(Table_Ultimate_Monster_Row.class).get(stageLevel);
    }


    public static UltimatetestEnemy createEnemy(int stageLevel, HardTypeEnum hardLevel, int monsterMaxGrade) {
        String playerName = NameUtils.random();
        List<UltimatetestMonster> monsterList = randomMonsterList(stageLevel, hardLevel, monsterMaxGrade);
        UltimatetestEnemy enemy = new UltimatetestEnemy(playerName, monsterList, hardLevel);
        return enemy;
    }

    /**
     * 随机一组怪物
     *
     * @param stageLevel
     * @param hardLevel
     * @return
     */
    public static List<UltimatetestMonster> randomMonsterList(int stageLevel, HardTypeEnum hardLevel, int monsterMaxGrade) {
        List<UltimatetestMonster> monsterList = new ArrayList<>();

        for (int i = 0; i < MagicNumbers.MAX_TEAM_NUMBER; i++) {
            LOGGER.debug("准备随机敌人怪物" + i);
            UltimatetestMonster monster = randomMonster(stageLevel, hardLevel, monsterMaxGrade);
            LOGGER.debug("敌人怪物" + i + "={}", ToStringBuilder.reflectionToString(monster, ToStringStyle.MULTI_LINE_STYLE));
            monsterList.add(monster);
        }
        return monsterList;
    }

    /**
     * 随机一个怪物
     *
     * @param stageLevel
     * @param hardLevel
     * @return
     */
    public static UltimatetestMonster randomMonster(int stageLevel, HardTypeEnum hardLevel, int maxGarde) {
        List<Table_Ultimate_Monster_Row> monsterRowList = RootTc.get(Table_Ultimate_Monster_Row.class).values();
        int monsterCount = monsterRowList.size();
        //随机一个monsterId
        int monsterIdIndex = RandomUtils.dropBetweenTowNum(MagicNumbers.DEFAULT_ONE, monsterCount);
        TupleCell<Integer> levelArea = Table_Ultimate_Stage_Row.getStageLevelArea(stageLevel, hardLevel);
        TupleCell<Integer> monsterStarArea = Table_Ultimate_Stage_Row.getMonsterStarByHardLevelandStageLevel(stageLevel, hardLevel);
        int monsterLv = RandomUtils.dropBetweenTowNum(levelArea.get(TupleCell.FIRST), levelArea.get(TupleCell.SECOND));
        int monsterStar = RandomUtils.dropBetweenTowNum(monsterStarArea.get(TupleCell.FIRST), monsterStarArea.get(TupleCell.SECOND));
        int monsterId = monsterRowList.get(monsterIdIndex - MagicNumbers.DEFAULT_ONE).getId();
        // 怪物品级 暂定为玩家品级最高的英雄，正负+1
        int monsterGrade = RandomUtils.dropBetweenTowNum(maxGarde - MagicNumbers.DEFAULT_ONE, maxGarde + MagicNumbers.DEFAULT_ONE);
        return new UltimatetestMonster(monsterId, monsterLv, monsterStar, monsterGrade);
    }


    /**
     * 获取试炼第一关
     *
     * @return
     */
    public static int getFirstStageId() {
        Table_Ultimate_Stage_Row ultimateStageRow = RootTc.get(Table_Ultimate_Stage_Row.class).get(MagicNumbers.DEFAULT_ONE);
        return ultimateStageRow.getId();
    }

    /**
     * 是否是合法的难度
     *
     * @param hardLevel
     * @return
     */
    public static boolean isIegalHardLevel(HardTypeEnum hardLevel) {
        for (HardTypeEnum type : HardTypeEnum.values()) {
            if (hardLevel == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取下一个关卡
     *
     * @param stageLevel
     * @return
     */
    public static int getNextStageLevel(int stageLevel) {
        Table_Ultimate_Stage_Row ultimateStageRow = UltimateTestCtrlUtils.getUltimateStageRow(stageLevel + 1);
        if (ultimateStageRow == null) {
            String msg = String.format("获取下一个关卡失败 nextStageLevel=%s", stageLevel + 1);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        return ultimateStageRow.getId();
    }

    /**
     * 检查这个关卡是否有这个box
     *
     * @param stageLevel
     * @return
     */
    public static boolean hasBoxStage(int stageLevel) {
        Table_Ultimate_Stage_Row stageRow = getUltimateStageRow(stageLevel);
        return stageRow.getBoxPrize() != 0;
    }

    /**
     * 是否是领奖关卡
     *
     * @param stageLevel
     * @return
     */
    public static boolean isGetRewardStage(int stageLevel) {
        Table_Ultimate_Stage_Row stageRow = getUltimateStageRow(stageLevel);
        return stageRow.getLevelType() == UltimateTestTypeEnum.REWARD_STAGE_VALUE;
    }

    /**
     * 获取宝箱物品
     *
     * @param stageLevel
     */
    public static IdMaptoCount getBoxReward(int stageLevel, int curPlayerLv) {
        Table_Ultimate_Stage_Row stageRow = getUltimateStageRow(stageLevel);
        return Table_DropLibrary_Row.drop(stageRow.getBoxPrize(), curPlayerLv);
    }

    /**
     * 是否是购买buff关卡
     *
     * @param stageLevel
     * @return
     */
    public static boolean isBuffStage(int stageLevel) {
        Table_Ultimate_Stage_Row stageRow = getUltimateStageRow(stageLevel);
        return stageRow.getLevelType() == UltimateTestTypeEnum.BUFF_STAGE_VALUE;
    }


    /**
     * 获取关卡奖励
     *
     * @param stageLevel
     * @return
     */
    public static IdMaptoCount getReward(int stageLevel, int curPlayerLv) {
        Table_Ultimate_Stage_Row stageRow = getUltimateStageRow(stageLevel);
        return Table_DropLibrary_Row.drop(stageRow.getPrize(), curPlayerLv);
    }

    /**
     * 获取关卡赠送的试炼币
     *
     * @param stageLevel
     * @return
     */
    public static IdAndCount getTestMoney(int stageLevel) {
        Table_Ultimate_Stage_Row stageRow = getUltimateStageRow(stageLevel);
        return null;
    }

    /**
     * 获取玩家SimplePlayer对应排行值
     *
     * @param playerIdToRankValue
     * @param outRealmId
     */
    public static Map<SimplePlayer, Long> getSimplePlayerToRankValue(Map<String, Long> playerIdToRankValue, int outRealmId) {
        Map<SimplePlayer, Long> simplePlayerToRankValue = new LinkedHashMap<>();
        Map<String, SimplePlayer> idToSimplePlayer = SimplePojoUtils.querySimplePlayerLisByIds(new ArrayList<>(playerIdToRankValue.keySet()), outRealmId);
        for (Entry<String, Long> entry : playerIdToRankValue.entrySet()) {
            SimplePlayer simplePlayer = idToSimplePlayer.get(entry.getKey());
            simplePlayerToRankValue.put(simplePlayer, entry.getValue());
        }

        return simplePlayerToRankValue;
    }


    /**
     * 是否已经领取过这个Id
     *
     * @param rewardIds
     * @param rewardId
     * @return
     */
    public static boolean hasGetReward(List<Integer> rewardIds, int rewardId) {
        return rewardIds.contains(rewardId);
    }

    /**
     * 是否是战斗关卡
     *
     * @param stageLevel
     * @return
     */
    public static boolean isAttackStage(int stageLevel) {
        Table_Ultimate_Stage_Row configRow = getUltimateStageRow(stageLevel);
        return configRow.getLevelType() == UltimateTestTypeEnum.ATTACK_STAGE_VALUE;
    }


    /**
     * 获取星级系数
     *
     * @param stageLevel
     * @param hardLevel
     * @return
     */
    public static int getStarByHardLevelandStageLevel(int stageLevel, HardTypeEnum hardLevel) {
        switch (hardLevel) {
            case EASY:
                return MagicNumbers.ULTIMATE_TEST_EASY_STAR;
            case NORMAL:
                return MagicNumbers.ULTIMATE_TEST_NORMAL_STAR;
            case HARD:
                return MagicNumbers.ULTIMATE_TEST_HARD_STAR;
        }
        String msg = String.format("爬塔难度传入错误 stageLevel=%s,hardLevel=%s", stageLevel, hardLevel);
        throw new BusinessLogicMismatchConditionException(msg);
    }


    /**
     * 是否有敌方阵容
     *
     * @param hardLevelToEnemies
     * @return
     */
    public static boolean hasEnemy(Map<HardTypeEnum, UltimatetestEnemy> hardLevelToEnemies) {
        // 试炼同步阵容，对应的三个难度 简单,中等,困难3个难度
        return hardLevelToEnemies.size() == MagicNumbers.DEFAULT_THREE;
    }

    /**
     * 获取低于本层的所有BUFF关卡
     *
     * @param stageLevel
     * @return
     */
    public static List<Integer> getAllBuffStageIds(int stageLevel) {
        List<Integer> allBuffStageIds = new ArrayList<>();
        List<Table_Ultimate_Stage_Row> ultimateStageRowList = RootTc.get(Table_Ultimate_Stage_Row.class).values();
        for (Table_Ultimate_Stage_Row ultimateStageRow : ultimateStageRowList) {
            if (ultimateStageRow.getId() < stageLevel && ultimateStageRow.getLevelType() == UltimateTestTypeEnum.BUFF_STAGE_VALUE) {
                allBuffStageIds.get(ultimateStageRow.getId());
            }
        }
        return allBuffStageIds;
    }

    /**
     * 获取低于本层的所有奖励关卡
     *
     * @param stageLevel
     * @return
     */
    public static List<Integer> getAllRewardStageIds(int stageLevel) {
        List<Integer> allRewardStageIds = new ArrayList<>();
        List<Table_Ultimate_Stage_Row> ultimateStageRowList = RootTc.get(Table_Ultimate_Stage_Row.class).values();
        for (Table_Ultimate_Stage_Row ultimateStageRow : ultimateStageRowList) {
            if (ultimateStageRow.getId() < stageLevel && ultimateStageRow.getLevelType() == UltimateTestTypeEnum.REWARD_STAGE_VALUE) {
                allRewardStageIds.add(ultimateStageRow.getId());
            }
        }
        return allRewardStageIds;
    }

    /**
     * 获取低于本层的所有战斗关卡
     *
     * @param stageLevel
     * @return
     */
    public static List<Integer> getAllAttackStageLevels(int stageLevel) {
        List<Integer> allAttackStageIds = new ArrayList<>();
        List<Table_Ultimate_Stage_Row> ultimateStageRowList = RootTc.get(Table_Ultimate_Stage_Row.class).values();
        for (Table_Ultimate_Stage_Row ultimateStageRow : ultimateStageRowList) {
            if (ultimateStageRow.getId() < stageLevel && ultimateStageRow.getLevelType() == UltimateTestTypeEnum.ATTACK_STAGE_VALUE) {
                allAttackStageIds.add(ultimateStageRow.getId());
            }
        }
        return allAttackStageIds;
    }


    public static List<IdMaptoCount> getAllReward(List<Integer> allRewardStageIds, IdMaptoCount idMaptoCount, int curPlayerLv) {
        List<IdMaptoCount> idMaptoCountList = new ArrayList<>();
        for (Integer stageId : allRewardStageIds) {
            IdMaptoCount addIdMapToCount = UltimateTestCtrlUtils.getReward(stageId, curPlayerLv);
            idMaptoCount.addAll(addIdMapToCount);
            idMaptoCountList.add(addIdMapToCount);
        }
        return idMaptoCountList;
    }

    public static Map<UltimateTestBuffIndexTypeEnum, UltimatetestBuff> randomBuff(int stageLevel) {
        Map<UltimateTestBuffIndexTypeEnum, UltimatetestBuff> buffConsumTypeEnumUltimatetestBuffMap = new HashMap<>();
        Table_Ultimate_Stage_Row ultimateStageRow = getUltimateStageRow(stageLevel);
        Iac iac1 = ultimateStageRow.getBuff1Score();
        UltimatetestBuff ultimatetestBuff1 = new UltimatetestBuff(iac1.getId(), (int) iac1.getCount());
        buffConsumTypeEnumUltimatetestBuffMap.put(UltimateTestBuffIndexTypeEnum.UltimateTestBuffIndex_ONE, ultimatetestBuff1);
        Iac iac2 = ultimateStageRow.getBuff2Score();
        UltimatetestBuff ultimatetestBuff2 = new UltimatetestBuff(iac2.getId(), (int) iac2.getCount());
        buffConsumTypeEnumUltimatetestBuffMap.put(UltimateTestBuffIndexTypeEnum.UltimateTestBuffIndex_TWO, ultimatetestBuff2);
        Iac iac3 = ultimateStageRow.getBuff3Score();
        UltimatetestBuff ultimatetestBuff3 = new UltimatetestBuff(iac3.getId(), (int) iac3.getCount());
        buffConsumTypeEnumUltimatetestBuffMap.put(UltimateTestBuffIndexTypeEnum.UltimateTestBuffIndex_THREE, ultimatetestBuff3);
        return buffConsumTypeEnumUltimatetestBuffMap;
    }

}
