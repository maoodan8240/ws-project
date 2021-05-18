package ws.mongodbRedisServer.system.date.dayChanged;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.concurrent.ReentrantReadWriteLockSupport;
import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.common.utils.fileHandler.FileReadCharacters;
import ws.common.utils.fileHandler.FileReader;

import java.io.File;
import java.util.Date;

public class _DayChanged extends ReentrantReadWriteLockSupport implements DayChanged {
    private static final Logger logger = LoggerFactory.getLogger(_DayChanged.class);

    public static final String TASKLIST_FILE_PATH = "./tasklist.cron4j";
    private String curDayChangedStr = "";
    private String curDays_yyyy = "";
    private String curDays_MM = "";
    private String curDays_dd = "";
    private int hhbz; // tasklist.cron4j -- 时
    private int mmbz;// tasklist.cron4j -- 分

    public _DayChanged() {
        _init();
    }

    @Override
    public void setDayChangedStr() {
        try {
            lockWrite();
            _setCurDayStr(new Date());
        } finally {
            unlockWrite();
        }
    }

    @Override
    public String getDayChangedStr() {
        try {
            lockRead();
            return curDayChangedStr;
        } finally {
            unlockRead();
        }
    }

    @Override
    public String getDayChangedStr_yyyy() {
        try {
            lockRead();
            return curDays_yyyy;
        } finally {
            unlockRead();
        }
    }

    @Override
    public String getDayChangedStr_MM() {
        try {
            lockRead();
            return curDays_MM;
        } finally {
            unlockRead();
        }
    }

    @Override
    public String getDayChangedStr_dd() {
        try {
            lockRead();
            return curDays_dd;
        } finally {
            unlockRead();
        }
    }

    private void _init() {
        try {
            lockWrite();
            String str = _getTellDayChangedCron4j();
            if (!StringUtils.isEmpty(str)) {
                this.hhbz = Integer.valueOf(str.split(" ")[1]).intValue();
                this.mmbz = Integer.valueOf(str.split(" ")[0]).intValue();
                String day = getDayChangedStrByDate(new Date());
                _setCurDayStr(day);
                logger.info("初始化后系统的切日时间为：" + curDayChangedStr);
            }
        } finally {
            unlockWrite();
        }
    }

    public String getDayChangedStrByDate(Date date) {
        int hhmmBz = this.hhbz * 100 + this.mmbz;
        int hhmm = Integer.valueOf(WsDateUtils.dateToFormatStr(date, WsDateFormatEnum.HHmm)).intValue();
        if (hhmm >= hhmmBz) {
            return WsDateUtils.dateToFormatStr(date, WsDateFormatEnum.yyyyMMdd);
        } else {
            return WsDateUtils.dateToFormatStr(DateUtils.addDays(new Date(), -1), WsDateFormatEnum.yyyyMMdd);
        }
    }

    /**
     * @return
     */
    private String _getTellDayChangedCron4j() {
        File file = new File(TASKLIST_FILE_PATH);
        if (!file.exists()) {
            throw new RuntimeException("Schedule File {" + TASKLIST_FILE_PATH + "} not exist");
        }
        FileReadCharacters fileCharacters = new FileReadCharacters(file);
        TasklistCron4jReader reader = new TasklistCron4jReader(fileCharacters);
        return (String) FileReader.read(reader);
    }

    private void _setCurDayStr(String dayStr) {
        Date date = WsDateUtils.dateToFormatDate(dayStr, WsDateFormatEnum.yyyyMMdd);
        _setCurDayStr(date);
    }

    private void _setCurDayStr(Date date) {
        curDayChangedStr = WsDateUtils.dateToFormatStr(date, WsDateFormatEnum.yyyyMMdd);
        curDays_yyyy = WsDateUtils.dateToFormatStr(date, WsDateFormatEnum.yyyy);
        curDays_MM = WsDateUtils.dateToFormatStr(date, WsDateFormatEnum.MM);
        curDays_dd = WsDateUtils.dateToFormatStr(date, WsDateFormatEnum.dd);
    }
}
