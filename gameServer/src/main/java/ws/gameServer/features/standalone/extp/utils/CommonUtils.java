package ws.gameServer.features.standalone.extp.utils;

import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.common.utils.date.WsWeekEnum;
import ws.common.utils.di.GlobalInjector;
import ws.gameServer.system.date.dayChanged.DayChanged;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;

import java.util.Date;
import java.util.List;

/**
 * Created by lee on 16-10-8.
 */
public class CommonUtils {


    /**
     * 增加id-value
     *
     * @param idMaptoCount
     * @param id
     * @param number
     */
    private void addIdAndCount(IdMaptoCount idMaptoCount, int id, int number) {
        if (id > 0 && number > 0) {
            idMaptoCount.add(new IdAndCount(id, number));
        }
    }

    /**
     * null 转空字符
     *
     * @param str
     * @return
     */
    public static String converNullToEmpty(String str) {
        return str == null ? "" : str;
    }


    /**
     * 转换id->idandcount
     *
     * @param consumeIdOrTpIds
     * @return
     */
    public static IdMaptoCount idsToIdMaptoCount(List<Integer> consumeIdOrTpIds) {
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        for (Integer id : consumeIdOrTpIds) {
            idMaptoCount.add(new IdAndCount(id));
        }
        return idMaptoCount;
    }

    /**
     * 从IdMaptoCount获取第一个IdAndCount
     *
     * @param idMaptoCount
     * @return
     */
    public static IdAndCount getFirstIdAndCount(IdMaptoCount idMaptoCount) {
        return idMaptoCount.getAll().get(0);
    }


    /**
     * 获取当前切日相关的星期
     *
     * @return
     */
    public static WsWeekEnum getCurDayChangedWeek() {
        Date curDate = WsDateUtils.dateToFormatDate(GlobalInjector.getInstance(DayChanged.class).getDayChangedStr(), WsDateFormatEnum.yyyyMMdd);
        String weekStr = WsDateUtils.dateToFormatStr(curDate, WsDateFormatEnum.EEEE);
        return WsWeekEnum.parse(weekStr);
    }

}
