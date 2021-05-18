package ws.gameServer.features.standalone.extp.pve2.utils;

import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.table.table.utils.CellUtils;
import ws.protos.EnumsProtos.MapTypeEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Consume_Row;
import ws.relationship.table.tableRows.Table_DropLibrary_Row;
import ws.relationship.table.tableRows.Table_Maps_Row;
import ws.relationship.table.tableRows.Table_Pve_Row;
import ws.relationship.table.tableRows.Table_Vip_Row;
import ws.relationship.topLevelPojos.newPve.Chapter;

import java.util.List;

public class NewPveCtrlUtils {

    public static Table_Pve_Row getPveRow(int stageId) {
        return RootTc.get(Table_Pve_Row.class).get(stageId);
    }

    public static Table_Maps_Row getMapRow(int mapId) {
        return RootTc.get(Table_Maps_Row.class).get(mapId);
    }


    public static IdMaptoCount getSweepDrop(int stageId, int curPlayerLv) {
        Table_Pve_Row pveRow = RootTc.get(Table_Pve_Row.class).get(stageId);
        List<Integer> dropIds = pveRow.getSweepDrop().getAll();
        IdMaptoCount idMaptoCount = Table_DropLibrary_Row.drop(dropIds, curPlayerLv);
        if (_drop(dropIds, idMaptoCount, curPlayerLv)) return idMaptoCount;
        return idMaptoCount;
    }


    private static boolean _drop(List<Integer> stageDropIds, IdMaptoCount idMaptoCount, int curPlayerLv) {
        // 掉落是空的就再此为玩家随机掉落
        if (idMaptoCount.getAll().size() == 0) {
            // 随机5次,每次完成判断是否已经有掉落
            for (int i = 0; i <= 5; i++) {
                idMaptoCount.addAll(Table_DropLibrary_Row.drop(stageDropIds, curPlayerLv));
                // 如果有掉落直接返回掉落
                if (idMaptoCount.getAll().size() != 0) {
                    return true;
                }
            }
        }
        // 随机5次掉落依然为空，不再为其随机，返回空掉落
        return false;
    }

    /**
     * 获取关卡掉落
     *
     * @param stageId
     * @return
     */
    public static IdMaptoCount getStageDrop(int stageId, int curPlayerLv) {
        Table_Pve_Row pveRow = getPveRow(stageId);
        List<Integer> stageDropIds = pveRow.getStageDrop().getAll();
        IdMaptoCount idMaptoCount = Table_DropLibrary_Row.drop(stageDropIds, curPlayerLv);
        // 掉落是空的就再此为玩家随机掉落
        if (idMaptoCount.getAll().size() == 0) {
            // 随机5次,每次完成判断是否已经有掉落
            for (int i = 0; i <= 5; i++) {
                idMaptoCount.addAll(Table_DropLibrary_Row.drop(stageDropIds, curPlayerLv));
                // 如果有掉落直接返回掉落
                if (idMaptoCount.getAll().size() != 0) {
                    return idMaptoCount;
                }
            }
        }
        // 随机5次掉落依然为空，不再为其随机，返回空掉落
        return idMaptoCount;
    }


    /**
     * 获取关卡首落
     *
     * @param stageId
     * @return
     */
    public static IdMaptoCount getStageFirstDrop(int stageId, int curPlayerLv) {
        Table_Pve_Row pveRow = getPveRow(stageId);
        List<Integer> firstDropIds = pveRow.getFirstDrop().getAll();
        return Table_DropLibrary_Row.drop(firstDropIds, curPlayerLv);
    }


    public static IdMaptoCount getActivityDrop(int stageId, int curPlayerLv) {
        Table_Pve_Row pveRow = getPveRow(stageId);
        List<Integer> firstDropIds = pveRow.getActivityDrop().getAll();
        return Table_DropLibrary_Row.drop(firstDropIds, curPlayerLv);
    }

    /**
     * 获取开启宝箱星数
     *
     * @param mapId
     * @param rewardStarLevelNode
     * @return
     */
    public static int getChestStar(int mapId, int rewardStarLevelNode) {
        Table_Maps_Row mapRow = getMapRow(mapId);
        switch (rewardStarLevelNode) {
            case 1:
                return mapRow.getChest1Star();
            case 2:
                return mapRow.getChest2Star();
            case 3:
                return mapRow.getChest3Star();
            default:
                String msg = "boxId=%s,宝箱ID异常";
                throw new BusinessLogicMismatchConditionException(msg);
        }
    }


    /**
     * 获取宝箱奖励
     *
     * @param mapId
     * @param rewardStarLevelNode
     * @return
     */
    public static IdMaptoCount getStarRewardChest(int mapId, int rewardStarLevelNode) {
        Table_Maps_Row mapRow = getMapRow(mapId);
        switch (rewardStarLevelNode) {
            case 1:
                return IdMaptoCount.parse(mapRow.getChest1());
            case 2:
                return IdMaptoCount.parse(mapRow.getChest2());
            case 3:
                return IdMaptoCount.parse(mapRow.getChest3());
        }
        String msg = String.format("不支持的星级:%s", rewardStarLevelNode);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 校验关卡是否合法和发布
     *
     * @param stageId
     */
    public static boolean isValidStageId(int stageId) {
        return getPveRow(stageId) != null;
    }

    /**
     * 是否只能挑战一次
     *
     * @param stageId
     * @return
     */
    public static boolean isFirst(int stageId) {
        Table_Pve_Row pveRow = getPveRow(stageId);
        return pveRow.getIsFirst() == 0;
    }


    /**
     * 获取关卡需要的等级
     *
     * @param stageId
     * @return
     */
    public static int getStageNeedPlayerLevel(int stageId) {
        Table_Pve_Row pveRow = getPveRow(stageId);
        return pveRow.getStageLv();
    }

    /**
     * 玩家等级是否可以挑战这个关卡
     *
     * @param stageId
     * @param curPlayerLv
     * @return
     */
    public static boolean isPlayerLevelCanAttack(int stageId, int curPlayerLv) {
        return getStageNeedPlayerLevel(stageId) <= curPlayerLv;
    }

    public static IdAndCount getFinishStageRoleExp(int stageId) {
        Table_Pve_Row pveRow = getPveRow(stageId);
        long expCount = pveRow.getStageRoleExp();
        return new IdAndCount(ResourceTypeEnum.RES_ROLE_EXP.getNumber(), expCount);
    }


    /**
     * 获取需要消耗的体力
     *
     * @param stageId
     * @return needEnergy
     */
    public static int getNeedEnergy(int stageId) {
        Table_Pve_Row pveRow = getPveRow(stageId);
        return pveRow.getSpiritUse();
    }

    public static IdAndCount getFinishStageMoney(int stageId) {
        Table_Pve_Row pveRow = getPveRow(stageId);
        long moneyCount = pveRow.getStageGold();
        return new IdAndCount(ResourceTypeEnum.RES_MONEY.getNumber(), moneyCount);
    }


    /**
     * 是否至少还有1次挑战次数
     *
     * @param attackTimes 当前已经挑战的次数
     * @param stageId
     * @return true 还有挑战次数 false没有挑战次数
     */
    public static boolean isAttackTimesEnough(int attackTimes, int stageId) {
        return remainAttackTimes(attackTimes, stageId) >= 1;
    }

    /**
     * 剩余攻击次数
     *
     * @param attackTimes 当前已经挑战的次数
     * @param stageId
     * @return
     */
    public static int remainAttackTimes(int attackTimes, int stageId) {
        int dBattleTimes = getPveRow(stageId).getDailyNumber();
        if (dBattleTimes == 0) {
            return MagicNumbers.MAX_TIMES;
        }
        return dBattleTimes - attackTimes;
    }


    /**
     * 是否可以重置关卡挑战次数
     *
     * @param hasRestTimes
     * @param curPlayerVipLevel
     * @return true可以重置
     */
    public static boolean isCanRestAttackTimesByVipLv(int hasRestTimes, int curPlayerVipLevel) {
        Table_Vip_Row vipRow = Table_Vip_Row.getVipRow(curPlayerVipLevel);
        int maxStageRestTimes = vipRow.getStageReset();
        return hasRestTimes < maxStageRestTimes;
    }

    /**
     * 获取购买重置所用消耗
     *
     * @param resetTimes
     * @return
     */
    public static IdAndCount getBuyResetConsume(int resetTimes) {
        int nextTimes = resetTimes + MagicNumbers.DEFAULT_ONE;
        return Table_Consume_Row.pveBuyResetConsume(nextTimes);
    }


    /**
     * 关卡星数是否满足扫荡条件
     *
     * @param chapter
     * @param stageId
     * @return
     */
    public static boolean starCanMop(Chapter chapter, int stageId) {
        int stageMaxStar = chapter.getIdToStage().get(stageId).getMaxStar();
        return stageMaxStar == MagicNumbers.PVE_MOPUP_STAR;
    }


    /**
     * 挑战多次检查次数是否满足挑战
     *
     * @param dailyAttackTimes
     * @param stageId
     * @param times
     * @return
     */
    public static boolean isAttackTimesEnoughByTimes(int dailyAttackTimes, int stageId, int times) {
        return remainAttackTimes(dailyAttackTimes, stageId) >= times;
    }


    /**
     * 获取章节类型
     *
     * @param stageId
     * @return
     */
    public static MapTypeEnum getStageType(int stageId) {
        Table_Pve_Row pveRow = getPveRow(stageId);
        Table_Maps_Row mapRow = getMapRow(pveRow.getMapId());
        return MapTypeEnum.valueOf(mapRow.getMapType());
    }


    /**
     * 此关卡是否含有宝箱
     *
     * @param stageId
     * @return
     */
    public static boolean hasChest(int stageId) {
        Table_Pve_Row pveRow = getPveRow(stageId);
        return !CellUtils.isEmptyCell(pveRow.getStageChest());
    }

    /**
     * 获取关卡宝箱
     *
     * @param stageId
     * @return
     */
    public static IdMaptoCount getChest(int stageId) {
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        Table_Pve_Row pveRow = getPveRow(stageId);
        List<TupleCell<Integer>> tupleCellList = pveRow.getStageChest().getAll();
        for (TupleCell<Integer> tupleCell : tupleCellList) {
            idMaptoCount.add(new IdAndCount(tupleCell.get(TupleCell.FIRST), tupleCell.get(TupleCell.SECOND)));
        }
        return idMaptoCount;
    }


    /**
     * 获取战前扣除体力值
     *
     * @param type
     * @return
     */
    public static int getBeginDeductionEnergyByStage(MapTypeEnum type) {
        if (type == MapTypeEnum.MAP_ELITE) {
            return AllServerConfig.Pve_Begin_Elite_DeductionEnergy.getConfig();
        } else if (type == MapTypeEnum.MAP_SIMPLE) {
            return AllServerConfig.Pve_Begin_Normal_DeductionEnergy.getConfig();
        }
        return 0;
    }

    public static boolean isValidChaterId(int chapterId) {
        return getMapRow(chapterId) != null;
    }
}
