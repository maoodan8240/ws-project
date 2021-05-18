package ws.mongodbRedisServer.system.date.dayChanged;

import java.util.Date;

public interface DayChanged {

    /**
     * 根据时间点获取当前的切日日期
     *
     * @param date
     * @return 年月日
     */
    String getDayChangedStrByDate(Date date);

    void setDayChangedStr();

    /**
     * @return yyyyMMdd
     */
    String getDayChangedStr();

    /**
     * @return yyyy
     */
    String getDayChangedStr_yyyy();

    /**
     * @return MM
     */
    String getDayChangedStr_MM();

    /**
     * @return dd
     */
    String getDayChangedStr_dd();
}
