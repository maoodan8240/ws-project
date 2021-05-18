package ws.gameServer.features.standalone.extp.dataCenter.stageDaliy.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.common.utils.di.GlobalInjector;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.dataCenter.enums.NotifyScopeEnum;
import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;
import ws.gameServer.features.standalone.extp.dataCenter.msg.NotifyObj;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_UpdateRegNotifyMsg;
import ws.gameServer.features.standalone.extp.dataCenter.stageDaliy.utils.PeriodicDataUtils;
import ws.gameServer.features.standalone.extp.heros.HerosExtp;
import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.gameServer.features.standalone.extp.itemBag.ItemBagExtp;
import ws.gameServer.features.standalone.extp.itemBag.ctrl.ItemBagCtrl;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.system.date.dayChanged.DayChanged;
import ws.gameServer.system.logHandler.LogExcep;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.topLevelPojos.dataCenter.stageDaliyData.Data;
import ws.relationship.topLevelPojos.dataCenter.stageDaliyData.StageDaliyData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class _StageDaliyDataCtrl extends AbstractPlayerExteControler<StageDaliyData> implements StageDaliyDataCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_StageDaliyDataCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private HerosCtrl herosCtrl;
    private ItemBagCtrl itemBagCtrl;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        herosCtrl = getPlayerCtrl().getExtension(HerosExtp.class).getControlerForQuery();
        itemBagCtrl = getPlayerCtrl().getExtension(ItemBagExtp.class).getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
    }

    @Override
    public void sync() {
    }

    @Override
    public void dealNotifyMsg(Pr_NotifyMsg notifyMsg) {
        LogicCheckUtils.requireNonNull(notifyMsg, Pr_NotifyMsg.class);
        LogicCheckUtils.requireNonNull(notifyMsg.getType(), PrivateNotifyTypeEnum.class);
        NotifyScopeEnum scope = notifyMsg.getType().getScope();
        if (scope == NotifyScopeEnum.COMMON) {
            return;
        }
        removeOvfData();
        Data data = getTodayData();
        PeriodicDataUtils.dealNotifyMsgToTiv(notifyMsg, data.getTiv());
        sendPrivateMsg(new Pr_UpdateRegNotifyMsg(notifyMsg.getType()));
        save();
    }


    @Override
    public void updateRegNotifyMsgHasDone(NotifyObj notifyObj, String startDay, String endDay) {
        LogicCheckUtils.requireNonNull(notifyObj, NotifyObj.class);
        LogicCheckUtils.requireNonNull(notifyObj.getType(), PrivateNotifyTypeEnum.class);
        logicCheck_StartDayAndEndDay(startDay, endDay);
        Map<Integer, Data> dateToData = getCanDealData(startDay, endDay);
        if (dateToData.size() == 0) {
            LOGGER.warn(LogExcep.LOGIC_WARN + "计算活动进度时，请求的计算起始日期={} 结束日期={} 在数据中心中不存在数据！", startDay, endDay);
        }
        long valueFinal = 0;
        for (Data data : dateToData.values()) {
            // 【重点】日期区间>=2的活动都是累计的，否则就应该以 NotifyScopeEnum.COMMON 方式处理
            // 比如: 累计充值、累计消耗、开启宝箱次数
            valueFinal += PeriodicDataUtils.calcuHasCompleteValue(notifyObj, data.getTiv());
        }
        notifyObj.setValue(valueFinal);
    }

    /**
     * 可以处理的日期--数据
     *
     * @param startDay
     * @param endDay
     * @return
     */
    private Map<Integer, Data> getCanDealData(String startDay, String endDay) {
        Map<Integer, Data> dateToData = new LinkedHashMap<>();
        Integer startInt = Integer.valueOf(startDay);
        Integer endInt = Integer.valueOf(endDay);
        for (Integer date : sortedKeys()) {
            if (date >= startInt && date <= endInt) {
                dateToData.put(date, target.getDateToData().get(date));
            }
        }
        return dateToData;
    }


    /**
     * 检查startDay和endDay
     *
     * @param startDay
     * @param endDay
     */
    public static void logicCheck_StartDayAndEndDay(String startDay, String endDay) {
        LogicCheckUtils.requireNonNull(startDay, String.class);
        LogicCheckUtils.requireNonNull(endDay, String.class);
        Date start = WsDateUtils.dateToFormatDate(startDay, WsDateFormatEnum.yyyyMMdd);
        String startStr = WsDateUtils.dateToFormatStr(start, WsDateFormatEnum.yyyyMMdd);
        if (!startDay.equals(startStr)) {
            String msg = String.format("startDay日期有误！ startDay=%s", startDay);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Date end = WsDateUtils.dateToFormatDate(startDay, WsDateFormatEnum.yyyyMMdd);
        String endStr = WsDateUtils.dateToFormatStr(end, WsDateFormatEnum.yyyyMMdd);
        if (!endDay.equals(endStr)) {
            String msg = String.format("endDay日期有误！ endDay=%s", endDay);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (Integer.valueOf(startDay) > Integer.valueOf(endDay)) {
            String msg = String.format("开始日期不能大于截止日期！ startDay=%s endDay=%s", startDay, endDay);
            throw new BusinessLogicMismatchConditionException(msg);
        }
    }

    private Data getTodayData() {
        String today = GlobalInjector.getInstance(DayChanged.class).getDayChangedStr();
        Integer todayInt = Integer.valueOf(today);
        return getData(todayInt);
    }

    private Data getData(Integer date) {
        Data data = target.getDateToData().get(date);
        if (data == null) {
            data = new Data();
            target.getDateToData().put(date, data);
        }
        return data;
    }

    /**
     * 移除超过存储上限的部分
     */
    private void removeOvfData() {
        for (Integer date : sortedKeys()) {
            for (int i = 0; i <= MagicNumbers.MAX_STORE_DALIY_DATA_DAYS * 2; i++) {
                if (target.getDateToData().size() > MagicNumbers.MAX_STORE_DALIY_DATA_DAYS) {
                    target.getDateToData().remove(date);
                } else {
                    break;
                }
            }
        }
    }

    /**
     * key值排序
     *
     * @return
     */
    private List<Integer> sortedKeys() {
        Integer[] keys = target.getDateToData().keySet().toArray(new Integer[]{});
        Arrays.sort(keys);
        return new ArrayList<>(Arrays.asList(keys));
    }
}
