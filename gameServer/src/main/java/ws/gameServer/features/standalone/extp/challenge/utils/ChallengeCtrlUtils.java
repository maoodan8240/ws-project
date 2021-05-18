package ws.gameServer.features.standalone.extp.challenge.utils;

import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.protos.EnumsProtos.ChallengeTypeEnum;
import ws.relationship.base.MagicWords_Redis;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Maps_Row;
import ws.relationship.table.tableRows.Table_Pve_Row;

import java.util.Date;
import java.util.List;

public class ChallengeCtrlUtils {

    /**
     * 否是合法关卡
     *
     * @param stageId
     * @return
     */
    public static boolean isValidStageId(int stageId) {
        return getPveRow(stageId) != null;
    }


    public static Table_Pve_Row getPveRow(int stageId) {
        return RootTc.get(Table_Pve_Row.class).get(stageId);
    }

    /**
     * 玩家等级是否满足挑战
     *
     * @param stageId
     * @param curLevel
     * @return
     */
    public static boolean isPlayerLevelCanAttack(int stageId, int curLevel) {
        return getStageNeedPlayerLevel(stageId) <= curLevel;
    }

    /**
     * 获取关卡需要的玩家的等级
     *
     * @param stageId
     * @return
     */
    public static int getStageNeedPlayerLevel(int stageId) {
        Table_Pve_Row pveRow = getPveRow(stageId);
        return pveRow.getStageLv();
    }

    /**
     * 剩余挑战次数是否满足挑战
     *
     * @param hasAttackTimes
     * @param stageId
     * @return
     */
    public static boolean isAttackTimesEnough(int hasAttackTimes, int stageId) {
        return remainAttackTimes(hasAttackTimes, stageId) >= 1;
    }

    /**
     * 剩余攻击次数
     *
     * @param attackTimes 当前已经挑战的次数
     * @param stageId
     * @return
     */
    public static int remainAttackTimes(int attackTimes, int stageId) {
        Table_Pve_Row pveRow = getPveRow(stageId);
        int dBattleTimes = pveRow.getDailyNumber();
        return dBattleTimes - attackTimes;
    }

    public static boolean isOpenDay(int stageId) {
        Table_Pve_Row pveRow = RootTc.get(Table_Pve_Row.class).get(stageId);
        List<Integer> openDayList = pveRow.getStageStart().getAll();
        String dateWeekStr = WsDateUtils.dateToFormatStr(new Date(), WsDateFormatEnum.EEEE);
        int weekDay = WsDateUtils.formatDayOfWeekStrToNum(dateWeekStr);
        for (Integer openDay : openDayList) {
            if (openDay == 0) {
                return true;
            } else if (openDay == weekDay) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param hasAttackTimes
     * @param stageId
     * @param mopupTimes
     * @return
     */
    public static boolean isAttackTimesEnoughByTimes(int hasAttackTimes, int stageId, int mopupTimes) {
        return remainAttackTimes(hasAttackTimes, stageId) >= mopupTimes;
    }


    /**
     * 根据类型获取关卡的排行榜KEY
     *
     * @param stageType
     * @return
     */
    public static String getRankKeyByStageType(ChallengeTypeEnum stageType) {
        switch (stageType) {
            case CHALLENGE_EXP:
                return MagicWords_Redis.Rank_Challenge_Exp;
            case CHALLENGE_GHOST:
                return MagicWords_Redis.Rank_Challenge_Ghots;
            case CHALLENGE_MONEY:
                return MagicWords_Redis.Rank_Challenge_Money;
            case CHALLENGE_WOMEN:
                return MagicWords_Redis.Rank_Challenge_Women;
            default:
                String msg = "关卡类型异常";
                throw new IllegalArgumentException(msg);
        }
    }


    public static ChallengeTypeEnum getChallengeType(int stageId) {
        int typeId = getChallengeTypeId(stageId);
        return ChallengeTypeEnum.valueOf(typeId);
    }


    public static int getChallengeTypeId(int stageId) {
        Table_Pve_Row pveRow = getPveRow(stageId);
        int mapId = pveRow.getMapId();
        Table_Maps_Row mapsRow = RootTc.get(Table_Maps_Row.class).get(mapId);
        return mapsRow.getMapType();
    }


}
