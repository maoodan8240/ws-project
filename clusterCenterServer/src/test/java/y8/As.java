package y8;

import org.apache.commons.lang3.time.DateUtils;
import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lee on 17-3-8.
 */
public class As {
    private static int getContinuousDataNumber(Map<String, Object> dailyToData) {
        Integer[] dailyArr = new Integer[dailyToData.size()];
        int i = 0;
        for (String day : dailyToData.keySet()) {
            dailyArr[i] = Integer.valueOf(day);
            i++;
        }
        Arrays.sort(dailyArr);
        int maxContinuous = 1;
        int maxContinuousHistory = 1;
        for (int j = 0; j < dailyArr.length - 1; j++) {
            Integer dayStart = dailyArr[j];
            Integer dayNext = dailyArr[j + 1];
            Date start = WsDateUtils.dateToFormatDate("" + dayStart, WsDateFormatEnum.yyyyMMdd);
            Date startTomorrow = DateUtils.addDays(start, 1);
            String startTomorrowStr = WsDateUtils.dateToFormatStr(startTomorrow, WsDateFormatEnum.yyyyMMdd);
            if (dayNext.intValue() == Integer.valueOf(startTomorrowStr).intValue()) {
                maxContinuous++;
            } else {
                if (maxContinuous >= maxContinuousHistory) {
                    maxContinuousHistory = maxContinuous;
                }
                maxContinuous = 1;
            }
        }
        if (maxContinuous >= maxContinuousHistory) {
            maxContinuousHistory = maxContinuous;
        }
        return maxContinuousHistory;
    }

    public static void main(String[] args) {
        Map<String, Object> dailyToData = new HashMap<>();
        dailyToData.put("20170227", 1);
        dailyToData.put("20170228", 1);
        dailyToData.put("20170301", 1);
        dailyToData.put("20170305", 1);
        dailyToData.put("20170306", 1);
        dailyToData.put("20170307", 1);
        dailyToData.put("20170302", 1);
        dailyToData.put("20170303", 1);
        int x = getContinuousDataNumber(dailyToData);
        System.out.println(x);
    }
}
