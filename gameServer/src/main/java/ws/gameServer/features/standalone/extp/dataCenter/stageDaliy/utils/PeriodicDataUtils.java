package ws.gameServer.features.standalone.extp.dataCenter.stageDaliy.utils;

import ws.gameServer.features.standalone.extp.dataCenter.enums.NotifyAddValueTypeEnum;
import ws.gameServer.features.standalone.extp.dataCenter.msg.NotifyObj;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;

import java.util.HashMap;
import java.util.Map;

public class PeriodicDataUtils {

    /**
     * 处理通知信息
     *
     * @param notifyMsg
     * @param tiv       typeCode --- missionId --- value
     */
    public static void dealNotifyMsgToTiv(Pr_NotifyMsg notifyMsg, Map<Integer, Map<Integer, Long>> tiv) {
        int typeCode = notifyMsg.getTypeCode();
        Map<Integer, Long> iTov = tiv.get(typeCode);
        if (iTov == null) {
            iTov = new HashMap<>();
            tiv.put(typeCode, iTov);
        }
        dealIav(iTov, notifyMsg);
    }

    /**
     * 计算已经完成的量
     *
     * @param notifyObj
     * @param tiv
     * @return
     */
    public static long calcuHasCompleteValue(NotifyObj notifyObj, Map<Integer, Map<Integer, Long>> tiv) {
        int typeCode = notifyObj.getTypeCode();
        if (!tiv.containsKey(typeCode)) {
            return 0;
        }
        Map<Integer, Long> iTov = tiv.get(typeCode);
        int id = notifyObj.getId();
        return getValue(iTov, id);
    }


    /**
     * 更新值
     *
     * @param iTov
     * @param notifyMsg
     */
    private static void dealIav(Map<Integer, Long> iTov, Pr_NotifyMsg notifyMsg) {
        int id = notifyMsg.getId();
        long oldValue = getValue(iTov, id);
        long notifyMsgValue = notifyMsg.getValue();
        // 只有相同的id才能修改数据， 即使是-1也要和-1匹配
        long newValue = 0;
        NotifyAddValueTypeEnum addValueType = notifyMsg.getType().getAddValueType();
        if (addValueType == NotifyAddValueTypeEnum.ADD) {
            newValue = oldValue + notifyMsgValue;
        } else if (addValueType == NotifyAddValueTypeEnum.COVER) {
            newValue = notifyMsgValue;
        }
        if (newValue > oldValue) {
            iTov.put(id, newValue);
        }
    }

    /**
     * 获取值
     *
     * @param iTov
     * @param id
     * @return
     */
    private static long getValue(Map<Integer, Long> iTov, int id) {
        Long v = iTov.get(id);
        return v == null ? 0 : v;
    }
}
