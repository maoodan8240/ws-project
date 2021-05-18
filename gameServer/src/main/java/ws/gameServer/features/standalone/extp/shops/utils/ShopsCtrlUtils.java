package ws.gameServer.features.standalone.extp.shops.utils;

import org.apache.commons.lang3.time.DateUtils;
import ws.common.table.table.interfaces.cell.ListCell;
import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.relationship.table.AllServerConfig;

import java.util.Date;
import java.util.List;

public class ShopsCtrlUtils {

    /**
     * 获取下次刷新的时间点
     *
     * @param curTime
     * @return
     */
    public static long calcuNextFreshTime(long curTime) {
        Date curDay = WsDateUtils.dateToFormatDate(new Date(), WsDateFormatEnum.yyyyMMdd);
        Date nextDay = DateUtils.addDays(curDay, 1);// 第二天
        ListCell<Integer> listCell = AllServerConfig.Shops_AutoRefeshTimeHour.getConfig();
        List<Integer> list = listCell.getAll();
        int first = list.get(0);
        for (int i = 0; i < list.size(); i++) {
            int t1 = list.get(i);
            long time1 = DateUtils.addHours(curDay, t1).getTime();
            if (curTime < time1) {
                return time1;
            }
        }
        return DateUtils.addHours(nextDay, first).getTime();
    }
}
