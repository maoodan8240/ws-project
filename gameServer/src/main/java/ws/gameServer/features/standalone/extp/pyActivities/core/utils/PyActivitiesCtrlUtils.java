package ws.gameServer.features.standalone.extp.pyActivities.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.gameServer.system.ServerGlobalData;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Activity_Group_Row;
import ws.relationship.table.tableRows.Table_Activity_Realm_Row;
import ws.relationship.topLevelPojos.pyActivities.PyActivities;
import ws.relationship.topLevelPojos.pyActivities.PyGroupActDefault;
import ws.relationship.topLevelPojos.pyActivities.PySubAct;

import java.util.ArrayList;
import java.util.Date;

public class PyActivitiesCtrlUtils {

    /**
     * 添加新的活动
     *
     * @param activities
     * @param realmRow
     */
    public static boolean addNewActivity(PyActivities activities, Table_Activity_Realm_Row realmRow) {
        Table_Activity_Group_Row groupRow = RootTc.get(Table_Activity_Group_Row.class, realmRow.getActivityGroupId());
        boolean rs = false;
        if (groupRow.getGoCommonLogic()) {
            PyGroupActDefault groupActDefault = createGroupActDefault(realmRow, groupRow);
            activities.getRaIdToDefault().put(realmRow.getId(), groupActDefault);
            rs = true;
        } else if (!activities.getSpecialRaIds().contains(realmRow.getId())) {
            // 特殊活动只能有一份实例
            activities.getSpecialRaIds().add(realmRow.getId());
            rs = true;
        }
        return rs;
    }


    /**
     * 创建默认的组活动
     *
     * @param realmRow
     * @param groupRow
     * @return
     */
    private static PyGroupActDefault createGroupActDefault(Table_Activity_Realm_Row realmRow, Table_Activity_Group_Row groupRow) {
        PyGroupActDefault groupActDefault = new PyGroupActDefault(realmRow.getId(), groupRow.getId());
        for (Integer subId : groupRow.getSubId()) {
            PySubAct subAct = new PySubAct(subId);
            groupActDefault.getIdToSubAct().put(subId, subAct);
        }
        return groupActDefault;
    }

    /**
     * 玩家等级，vip等级是否达到了活动的要求
     *
     * @param row
     * @param playerCurLv
     * @param playerCurVipLv
     * @return
     */
    public static boolean isPlayerMeetActivityConfigCondition(Table_Activity_Realm_Row row, int playerCurLv, int playerCurVipLv) {
        if (row.getPlayerLevel() > playerCurLv) {
            return false;
        }
        if (row.getPlayerVipLevel() > playerCurVipLv) {
            return false;
        }
        return true;
    }


    /**
     * 判度所有组活动是否过了展示时间，过了，则移除
     *
     * @param pyActivities
     * @param outerRealmId
     */
    public static void removeOverShowTimeActivity(PyActivities pyActivities, int outerRealmId) {
        for (Integer realmAcId : new ArrayList<>(pyActivities.getRaIdToDefault().keySet())) {
            removeOneGroupActOverShowTimeActivityForRaIdToDefault(pyActivities, realmAcId, outerRealmId);
        }
        for (Integer realmAcId : new ArrayList<>(pyActivities.getSpecialRaIds())) {
            removeOneGroupActOverShowTimeActivityForSpecialRaIds(pyActivities, realmAcId, outerRealmId);
        }
    }

    /**
     * 判度一个组活动是否过了展示时间，过了，则移除(通用活动)
     *
     * @param activities
     * @param realmAcId
     * @param outerRealmId
     */
    private static void removeOneGroupActOverShowTimeActivityForRaIdToDefault(PyActivities activities, int realmAcId, int outerRealmId) {
        if (PyActivitiesCtrlUtils.isRealmActOverShowTime(realmAcId, outerRealmId)) {
            removeOneRealmActivityForRaIdToDefault(activities, realmAcId);
        }
    }

    /**
     * 判度一个组活动是否过了展示时间，过了，则移除(特殊活动)
     *
     * @param activities
     * @param realmAcId
     * @param outerRealmId
     */
    private static void removeOneGroupActOverShowTimeActivityForSpecialRaIds(PyActivities activities, int realmAcId, int outerRealmId) {
        if (PyActivitiesCtrlUtils.isRealmActOverShowTime(realmAcId, outerRealmId)) {
            removeOneRealmActivityForSpecialRaIds(activities, realmAcId);
        }
    }

    /**
     * 移除一个服活动(通用活动)
     *
     * @param activities
     * @param realmAcId
     */
    private static void removeOneRealmActivityForRaIdToDefault(PyActivities activities, int realmAcId) {
        activities.getRaIdToDefault().remove(realmAcId);
    }

    /**
     * 移除一个服活动(特殊活动)
     *
     * @param activities
     * @param realmAcId
     */
    private static void removeOneRealmActivityForSpecialRaIds(PyActivities activities, int realmAcId) {
        Number number = realmAcId;
        activities.getSpecialRaIds().remove(number);
    }

    /**
     * 服活动是否到结束时间了
     *
     * @param realmAcId
     * @param outerRealmId
     * @return true 结束了
     */
    public static boolean isRealmActOverEndTime(int realmAcId, int outerRealmId) {
        Table_Activity_Realm_Row realmRow = RootTc.get(Table_Activity_Realm_Row.class, realmAcId);
        return isRealmActivityOverEndTime(realmRow, outerRealmId);
    }

    /**
     * 服活动是否过了展示结束时间了
     *
     * @param realmAcId
     * @param outerRealmId
     * @return true 过了
     */
    public static boolean isRealmActOverShowTime(int realmAcId, int outerRealmId) {
        Table_Activity_Realm_Row realmRow = RootTc.get(Table_Activity_Realm_Row.class, realmAcId);
        return isRealmActivityOverShowTime(realmRow, outerRealmId);
    }

    /**
     * 活动是否结束了
     *
     * @param row
     * @param outerRealmId
     * @return true 结束了
     */
    public static boolean isRealmActivityOverEndTime(Table_Activity_Realm_Row row, int outerRealmId) {
        if (row == null) {
            return true;
        }
        if (!row.getIsEffective()) {
            return true;
        }
        if (!StringUtils.isBlank(row.getStartTime1()) &&    // 开始日期不为空
                !StringUtils.isBlank(row.getEndTime1()) &&  // 结束日期不为空
                isTimeExpired(row.getEndTime1())) {
            return true;
        } else if (row.getStartTime2() > 0 &&               // 开始日期不为空
                row.getEndTime2() > 0 &&                    // 结束日期不为空
                isServerTimeExpired(row.getEndTime2(), outerRealmId)) {
            return true;
        }
        return false;
    }

    /**
     * 活动是否展示时间都过了
     *
     * @param row
     * @param outerRealmId
     * @return true 过了
     */
    public static boolean isRealmActivityOverShowTime(Table_Activity_Realm_Row row, int outerRealmId) {
        if (row == null) {
            return true;
        }
        if (!row.getIsEffective()) {
            return true;
        }
        if (!StringUtils.isBlank(row.getStartTime1()) &&        // 开始日期不为空
                !StringUtils.isBlank(row.getShowEndTime1()) &&  // 展示结束日期不为空
                isTimeExpired(row.getShowEndTime1())) {
            return true;
        } else if (row.getStartTime2() > 0 &&                   // 开始日期不为空
                row.getShowEndTime2() > 0 &&                    // 展示结束日期不为空
                isServerTimeExpired(row.getShowEndTime2(), outerRealmId)) {
            return true;
        }
        return false;
    }

    /**
     * 获取开始时间
     *
     * @param row
     * @param outerRealmId
     * @return
     */
    public static long getStartTime(Table_Activity_Realm_Row row, int outerRealmId) {
        if (!StringUtils.isBlank(row.getStartTime1()) &&     // 开始日期不为空
                !StringUtils.isBlank(row.getEndTime1())) {   // 结束日期不为空
            return getSpecifiedTime(row.getStartTime1());
        } else if (row.getStartTime2() > 0 &&               // 开始日期不为空
                row.getEndTime2() > 0) {                    // 结束日期不为空
            return getTimeStartWithServerOpenTime(row.getStartTime2(), outerRealmId);
        }
        return -1;
    }


    /**
     * 获取结束时间
     *
     * @param row
     * @param outerRealmId
     * @return
     */
    public static long getEndTime(Table_Activity_Realm_Row row, int outerRealmId) {
        if (!StringUtils.isBlank(row.getStartTime1()) &&     // 开始日期不为空
                !StringUtils.isBlank(row.getEndTime1())) {   // 结束日期不为空
            return getSpecifiedTime(row.getEndTime1());
        } else if (row.getStartTime2() > 0 &&               // 开始日期不为空
                row.getEndTime2() > 0) {                    // 结束日期不为空
            return getTimeStartWithServerOpenTime(row.getEndTime2(), outerRealmId);
        }
        return -1;
    }


    /**
     * 时间是否过期了
     *
     * @param time
     * @return true 过期  false 未过期
     */
    private static boolean isTimeExpired(String time) {
        if (System.currentTimeMillis() >= getSpecifiedTime(time)) {
            return true;
        }
        return false;
    }


    /**
     * 指定的时间
     *
     * @param time
     * @return
     */
    private static long getSpecifiedTime(String time) {
        Date date = WsDateUtils.dateToFormatDate(time, WsDateFormatEnum.yyyyMMddHHmm);
        return date.getTime();
    }

    /**
     * Sever时间是否过期了
     *
     * @param time 单为为分钟
     * @return
     */
    private static boolean isServerTimeExpired(int time, int outerRealmId) {
        if (System.currentTimeMillis() >= getTimeStartWithServerOpenTime(time, outerRealmId)) {
            return true;
        }
        return false;
    }


    /**
     * 以开服时间为起始点的时间
     *
     * @param time 单为为分钟
     * @return
     */
    private static long getTimeStartWithServerOpenTime(int time, int outerRealmId) {
        String openDateStr = ServerGlobalData.getOuterToInnerRealmList(outerRealmId).getDate();
        Date openDate = WsDateUtils.dateToFormatDate(openDateStr, WsDateFormatEnum.yyyyMMddHHmm);
        long endTime = openDate.getTime() + DateUtils.MILLIS_PER_MINUTE * time;
        return endTime;
    }


    /**
     * 是否已经领取了某个活动
     *
     * @param activities
     * @param realmAcId
     * @return
     */
    public static boolean containsRealmActivity(PyActivities activities, int realmAcId) {
        if (activities.getRaIdToDefault().containsKey(realmAcId)) {
            return true;
        }
        return false;
    }

    /**
     * 获取组活动
     *
     * @param activities
     * @param realmAcId
     * @return
     */
    public static PyGroupActDefault getGroupActDefault(PyActivities activities, int realmAcId) {
        return activities.getRaIdToDefault().get(realmAcId);
    }

    /**
     * 是否包含子活动
     *
     * @param groupActDefault
     * @param subActId
     * @return
     */
    public static boolean containsSubActivity(PyGroupActDefault groupActDefault, int subActId) {
        return groupActDefault.getIdToSubAct().containsKey(subActId);
    }


    /**
     * 获取子活动
     *
     * @param groupActDefault
     * @param subActId
     * @return
     */
    public static PySubAct getSubActivity(PyGroupActDefault groupActDefault, int subActId) {
        return groupActDefault.getIdToSubAct().get(subActId);
    }


    /**
     * 组活动是否开启了时限
     *
     * @param row
     * @return true 是开启了时限 ， false 未开启时限，即永久活动
     */
    public static boolean isRealmActOpenTimeLimit(Table_Activity_Realm_Row row) {
        if (StringUtils.isBlank(row.getStartTime1()) && row.getStartTime2() <= 0) {  // 开始日期都为空
            return true;
        }
        return false;
    }


    /**
     * 组活动的所有子活动都领取了奖励
     *
     * @param groupActDefault
     * @return
     */
    public static boolean isGroupActAllSubActGetRewards(PyGroupActDefault groupActDefault) {
        for (PySubAct subAct : groupActDefault.getIdToSubAct().values()) {
            if (!subAct.isGet()) {
                return false;
            }
        }
        return true;
    }

}
