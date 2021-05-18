package ws.gameServer.features.standalone.extp.missions.utils;

import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;
import ws.gameServer.features.standalone.extp.dataCenter.msg.NotifyObj;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.tableRows.Table_Missions_Row;

import java.util.Date;

public class MissionsCtrlUtils {
    private static final String CONDITION_DECOLLATOR_KV = ":";


    /**
     * 解析CompleteType和CompleteCondition两个字段
     *
     * @param row
     * @return
     * @throws Exception
     */
    public static NotifyObj parseNotifyObj(Table_Missions_Row row) {
        return parseNotifyObj(row.getCompleteType(), row.getCompleteCondition(), row.getId());
    }

    /**
     * 解析CompleteType和CompleteCondition两个字段
     *
     * @param completeType
     * @param completeCondition
     * @param id
     * @return
     */
    public static NotifyObj parseNotifyObj(String completeType, String completeCondition, Integer id) {
        PrivateNotifyTypeEnum notifyType = getCompleteType(completeType, id);
        NotifyObj notifyObj = new NotifyObj(notifyType);
        if (completeCondition.contains(CONDITION_DECOLLATOR_KV)) {
            String[] argsArr = completeCondition.split(CONDITION_DECOLLATOR_KV);
            if (argsArr.length != 2) {
                String msg = String.format("id=%s, completeCondition=%s 解析异常！", id, completeCondition);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            notifyObj.setId(Integer.valueOf(argsArr[0]));
            notifyObj.setValue(Long.valueOf(argsArr[1]));
        } else {
            notifyObj.setId(-1);
            notifyObj.setValue(Long.valueOf(completeCondition));
        }
        notifyObj.setValueOri(notifyObj.getValue());
        return notifyObj;
    }

    /**
     * 获取任务完成类型
     *
     * @param row
     * @return
     */
    public static PrivateNotifyTypeEnum getCompleteType(Table_Missions_Row row) {
        return getCompleteType(row.getCompleteType(), row.getId());
    }

    /**
     * 获取任务完成类型
     *
     * @param completeType
     * @return
     */
    public static PrivateNotifyTypeEnum getCompleteType(String completeType, Integer id) {
        PrivateNotifyTypeEnum type = null;
        try {
            type = PrivateNotifyTypeEnum.valueOf(completeType);
        } catch (Exception e) {
            // 忽略异常
        }
        if (type == null) {
            String msg = String.format("id=%s, 解析name=%s失败! PrivateNotifyTypeEnum没有该项.", id, completeType);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        return type;
    }


    /**
     * 获取当前最近的体力时间点
     *
     * @return
     */
    public static int nearestEnergyHour() {
        int hour = Integer.valueOf(WsDateUtils.dateToFormatStr(new Date(), WsDateFormatEnum.HH)).intValue();
        if (hour >= MagicNumbers.SPECIAL_HOUR_TIME_9 && hour < MagicNumbers.SPECIAL_HOUR_TIME_12) { // [9,12)
            return MagicNumbers.SPECIAL_HOUR_TIME_9;
        } else if (hour >= MagicNumbers.SPECIAL_HOUR_TIME_12 && hour < MagicNumbers.SPECIAL_HOUR_TIME_18) { // [12,18)
            return MagicNumbers.SPECIAL_HOUR_TIME_12;
        } else if (hour >= MagicNumbers.SPECIAL_HOUR_TIME_18 && hour < MagicNumbers.SPECIAL_HOUR_TIME_21) { // [18,21)
            return MagicNumbers.SPECIAL_HOUR_TIME_18;
        } else if (hour >= MagicNumbers.SPECIAL_HOUR_TIME_21 && hour < MagicNumbers.SPECIAL_HOUR_TIME_23) { // [21,23)
            return MagicNumbers.SPECIAL_HOUR_TIME_21;
        }
        return 0;
    }

}
