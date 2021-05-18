package ws.gameServer.features.standalone.extp.missions.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.table.table.interfaces.cell.ListCell;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.common.utils.di.GlobalInjector;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;
import ws.gameServer.features.standalone.extp.dataCenter.msg.NotifyObj;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_UpdateRegNotifyMsg;
import ws.gameServer.features.standalone.extp.dataCenter.permanent.PermanentDataExtp;
import ws.gameServer.features.standalone.extp.dataCenter.permanent.ctrl.PermanentDataCtrl;
import ws.gameServer.features.standalone.extp.dataCenter.stageDaliy.StageDaliyDataExtp;
import ws.gameServer.features.standalone.extp.dataCenter.stageDaliy.ctrl.StageDaliyDataCtrl;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.missions.utils.MissionsCtrlProtos;
import ws.gameServer.features.standalone.extp.missions.utils.MissionsCtrlUtils;
import ws.gameServer.features.standalone.extp.payment.PaymentExtp;
import ws.gameServer.features.standalone.extp.payment.ctrl.PaymentCtrl;
import ws.gameServer.features.standalone.extp.payment.utils.MonthCardInfo;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.gameServer.system.date.dayChanged.DayChanged;
import ws.protos.EnumsProtos.MissionSmallTypeEnum;
import ws.protos.EnumsProtos.MissionTypeEnum;
import ws.protos.EnumsProtos.MonthCardTypeEnum;
import ws.protos.MissionsProtos.Sm_Missions;
import ws.protos.MissionsProtos.Sm_Missions.Action;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Missions_Row;
import ws.relationship.topLevelPojos.mission.Mission;
import ws.relationship.topLevelPojos.mission.Missions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class _MissionsCtrl extends AbstractPlayerExteControler<Missions> implements MissionsCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_MissionsCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private StageDaliyDataCtrl stageDaliyDataCtrl;
    private PermanentDataCtrl permanentDataCtrl;
    private PaymentCtrl paymentCtrl;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        stageDaliyDataCtrl = getPlayerCtrl().getExtension(StageDaliyDataExtp.class).getControlerForQuery();
        permanentDataCtrl = getPlayerCtrl().getExtension(PermanentDataExtp.class).getControlerForQuery();
        paymentCtrl = getPlayerCtrl().getExtension(PaymentExtp.class).getControlerForQuery();
    }


    @Override
    public void _resetDataAtDayChanged() throws Exception {
        target.getTypeToMissions().remove(MissionTypeEnum.MISS_DALIY);  // 移除所有每日任务
    }

    @Override
    public void _initAfterChanged() throws Exception {
        scanCanHandelMission();
        addEnergyMission(MissionsCtrlUtils.nearestEnergyHour()); // 增加体力任务
    }

    @Override
    public void sync() {

    }


    @Override
    public void syncMission() {
        removeOvertimeEneryMission();
        updateMissionUnDoneMissionValue();
        Map<Integer, Mission> collection = getMissionCollection();
        SenderFunc.sendInner(this, Sm_Missions.class, Sm_Missions.Builder.class, Action.SYNC_MISSION, (b, br) -> {
            b.addAllEntries(MissionsCtrlProtos.create_Sm_Missions_Entry_List(new ArrayList<>(collection.values())));
        });
        save();
    }

    @Override
    public void syncAchieve() {
        removeOvertimeEneryMission();
        updateAchieveUnDoneMissionValue();
        Map<Integer, Mission> collection = getAchieveCollection();
        SenderFunc.sendInner(this, Sm_Missions.class, Sm_Missions.Builder.class, Action.SYNC_ACHIEVE, (b, br) -> {
            b.addAllEntries(MissionsCtrlProtos.create_Sm_Missions_Entry_List(new ArrayList<>(collection.values())));
        });
        save();
    }

    @Override
    public void getRewards(MissionTypeEnum missionType, int mid) {
        removeOvertimeEneryMission();
        logicCheck_MissionId(missionType, mid);
        Table_Missions_Row missionsRow = RootTc.get(Table_Missions_Row.class, mid);
        Mission curMission = getMission(missionType, mid);
        if (curMission.isGet()) {
            String msg = String.format("任务已经完成，已经领取了奖励！ mid=%s ! ", mid);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        NotifyObj notifyObj = MissionsCtrlUtils.parseNotifyObj(missionsRow);
        if (curMission.getValue() < notifyObj.getValue()) {
            String msg = String.format("尚未完成任务，不可领取奖励 mid=%s ! hasDone=%s need=%s", mid, curMission.getValue(), missionsRow.getCompleteCondition());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount reward = missionsRow.getTaskReward();
        LogicCheckUtils.canAdd(itemIoCtrl, reward);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_GET_REWARDS).addItem(reward);
        curMission.setGet(true);
        Mission nextMission = null;
        Table_Missions_Row nextMissionRow = null;
        if (hasNextMissionRow(missionsRow)) {
            nextMissionRow = getNextMissionRow(missionsRow);
            nextMission = tryAddAndUpdateMission(nextMissionRow);
        }
        final Table_Missions_Row nextMissionRow1 = nextMissionRow;
        final Mission nextMission1 = nextMission;
        SenderFunc.sendInner(this, Sm_Missions.class, Sm_Missions.Builder.class, Action.RESP_GET_REWARDS, (b, br) -> {
            if (nextMission1 != null && nextMissionRow1 != null && nextMission1.getMid() > 0) {
                b.setNextEntries(MissionsCtrlProtos.create_Sm_Missions_Entry(nextMissionRow1, nextMission1));
            }
            if (missionsRow.getDisplay()) {
                b.setCurEntry(MissionsCtrlProtos.create_Sm_Missions_Entry(missionsRow, curMission));
            }
            IdMaptoCount toAddRefresh = new IdMaptoCount().addAll(refresh_1);
            itemIoCtrl.refreshItemAddToResponse(toAddRefresh, br);
        });
        save();
    }

    @Override
    public void dealNotifyMsg(Pr_NotifyMsg notifyMsg) {
        if (notifyMsg.getType() == PrivateNotifyTypeEnum.Player_LevelUp) {
            List<Mission> newMissions = scanCanHandelMission();
            if (newMissions.size() <= 0) {
                return;
            }
            SenderFunc.sendInner(this, Sm_Missions.class, Sm_Missions.Builder.class, Action.RESP_SYNC_PART, (b, br) -> {
                b.addAllEntries(MissionsCtrlProtos.create_Sm_Missions_Entry_List(new ArrayList<>(newMissions)));
            });
            save();
        }
    }

    @Override
    public void dealUpdateRegNotifyMsg(Pr_UpdateRegNotifyMsg updateRegNotifyMsg) {
        List<Mission> missionList = getUnDoneMissionListByNotifyType(updateRegNotifyMsg.getType());
        if (missionList.size() > 0) {
            updateSomeUnDoneMissionValue(missionList);
            SenderFunc.sendInner(this, Sm_Missions.class, Sm_Missions.Builder.class, Action.RESP_SYNC_PART, (b, br) -> {
                b.addAllEntries(MissionsCtrlProtos.create_Sm_Missions_Entry_List(new ArrayList<>(missionList)));
            });
            save();
        }
    }

    @Override
    public void onBroadcastEachHour(int hour) {
        Mission newMission = addEnergyMission(hour);
        if (newMission == null) {
            return;
        }
        SenderFunc.sendInner(this, Sm_Missions.class, Sm_Missions.Builder.class, Action.RESP_SYNC_PART, (b, br) -> {
            Table_Missions_Row row = RootTc.get(Table_Missions_Row.class, newMission.getMid());
            b.addEntries(MissionsCtrlProtos.create_Sm_Missions_Entry(row, newMission));
        });
        save();
    }

    /**
     * 选择不同体力时间点对应的配置，添加体力任务
     *
     * @param hour
     * @return
     */
    private Mission addEnergyMission(int hour) {
        TupleCell<Integer> cell = null;
        switch (hour) {
            case MagicNumbers.SPECIAL_HOUR_TIME_9:
                cell = AllServerConfig.Missions_Energy_IdAndEndTime_9.getConfig();
                break;
            case MagicNumbers.SPECIAL_HOUR_TIME_12:
                cell = AllServerConfig.Missions_Energy_IdAndEndTime_12.getConfig();
                break;
            case MagicNumbers.SPECIAL_HOUR_TIME_18:
                cell = AllServerConfig.Missions_Energy_IdAndEndTime_18.getConfig();
                break;
            case MagicNumbers.SPECIAL_HOUR_TIME_21:
                cell = AllServerConfig.Missions_Energy_IdAndEndTime_21.getConfig();
                break;
        }
        if (cell == null) {
            return null;
        }
        String yyyyMMdd = WsDateUtils.dateToFormatStr(new Date(), WsDateFormatEnum.yyyyMMdd);
        String yyyyMMddHH = yyyyMMdd + String.valueOf(cell.get(TupleCell.SECOND));
        Date endDate = WsDateUtils.dateToFormatDate(yyyyMMddHH, WsDateFormatEnum.yyyyMMddHH);
        if (System.currentTimeMillis() > endDate.getTime()) {
            return null;
        }
        return tryAddAndUpdateEnergyMission(cell.get(TupleCell.FIRST), endDate.getTime());
    }

    /**
     * 尝试添加体力任务
     *
     * @param rowId
     * @param finishTime
     */
    private Mission tryAddAndUpdateEnergyMission(int rowId, long finishTime) {
        Table_Missions_Row row = RootTc.get(Table_Missions_Row.class, rowId);
        Mission newMission = tryAddAndUpdateMission(row);
        if (newMission == null) {
            return null;
        }
        newMission.setValue(finishTime); // 表里默认的是0值
        return newMission;
    }

    /**
     * 更新所有任务完成量
     */
    private void updateMissionUnDoneMissionValue() {
        updateOneTypeUnDoneMissionValue(MissionTypeEnum.MISS_DALIY);
        updateOneTypeUnDoneMissionValue(MissionTypeEnum.MISS_COMMON);
    }

    /**
     * 更新所有成就完成量
     */
    private void updateAchieveUnDoneMissionValue() {
        updateOneTypeUnDoneMissionValue(MissionTypeEnum.MISS_ACHIEVE_RISK);
        updateOneTypeUnDoneMissionValue(MissionTypeEnum.MISS_ACHIEVE_EVOLO);
        updateOneTypeUnDoneMissionValue(MissionTypeEnum.MISS_ACHIEVE_ACTIVITY);
    }

    /**
     * 更新一种类型的任务完成量
     *
     * @param missionType
     */
    private void updateOneTypeUnDoneMissionValue(MissionTypeEnum missionType) {
        Map<Integer, Mission> collection = getCollection(missionType);
        for (Mission m : collection.values()) {
            updateOneUnDoneMissionValue(m);
        }
    }

    /**
     * 更新多个任务的完成量
     *
     * @param missionList
     */
    private void updateSomeUnDoneMissionValue(List<Mission> missionList) {
        for (Mission m : missionList) {
            updateOneUnDoneMissionValue(m);
        }
    }

    /**
     * 更新单个任务的完成量
     *
     * @param mission
     */
    private void updateOneUnDoneMissionValue(Mission mission) {
        String today = GlobalInjector.getInstance(DayChanged.class).getDayChangedStr();
        Table_Missions_Row row = RootTc.get(Table_Missions_Row.class, mission.getMid());
        NotifyObj notifyObj = MissionsCtrlUtils.parseNotifyObj(row);
        if (notifyObj.getType().isSpecial()) {// 特殊任务
            updateSpecialUnDoneMissionValue(mission, notifyObj);
            return;
        }
        if (mission.isGet()) {
            return;
        }
        notifyObj.setValue(mission.getValue()); // 更改为当前完成的量
        if (row.getTaskType() == MissionTypeEnum.MISS_DALIY) {
            stageDaliyDataCtrl.updateRegNotifyMsgHasDone(notifyObj, today, today);
        } else {
            permanentDataCtrl.updateRegNotifyMsgHasDone(notifyObj);
        }
        mission.setValue(notifyObj.getValue());
    }

    /**
     * 更新特殊逻辑任务的任务值
     *
     * @param mission
     * @param notifyObj
     * @return
     */
    private void updateSpecialUnDoneMissionValue(Mission mission, NotifyObj notifyObj) {
        if (notifyObj.getType() == PrivateNotifyTypeEnum.MonthCard) {
            MonthCardTypeEnum type = MonthCardTypeEnum.valueOf((int) notifyObj.getValueOri());
            MonthCardInfo cardInfo = paymentCtrl.calcuMonthCardRemainDays(type, mission.isGet());
            mission.setRemainDays(cardInfo.getRemainDays());
            mission.setValue(cardInfo.getHasPay());
        }
    }


    /**
     * 当前任务是否有下一条任务
     *
     * @param curRow
     * @return true 有  false 没有
     */
    private boolean hasNextMissionRow(Table_Missions_Row curRow) {
        return curRow.getAfterTask() > 0 && RootTc.has(Table_Missions_Row.class, curRow.getAfterTask());
    }

    /**
     * 获取下一条任务
     *
     * @param curRow
     * @return
     */
    private Table_Missions_Row getNextMissionRow(Table_Missions_Row curRow) {
        return RootTc.get(Table_Missions_Row.class, curRow.getAfterTask());
    }

    /**
     * 扫描可以处理的任务
     */

    private List<Mission> scanCanHandelMission() {
        ListCell<Integer> listCell = AllServerConfig.Missions_Energy_Ids.getConfig();
        List<Integer> energyIds = listCell.getAll();
        List<Mission> missions = new ArrayList<>();
        List<Table_Missions_Row> rows = RootTc.getAll(Table_Missions_Row.class);
        for (Table_Missions_Row row : rows) {
            if (energyIds.contains(row.getId())) {
                continue;  // 体力任务单独处理，此出跳过
            }
            Mission newMission = tryAddAndUpdateMission(row);
            if (newMission != null) {
                missions.add(newMission);
            }
        }
        return missions;
    }

    /**
     * 尝试添加并且更新任务
     *
     * @param row
     * @return null 未成功添加任务
     */
    private Mission tryAddAndUpdateMission(Table_Missions_Row row) {
        boolean enable = row.getEnable();
        if (enable
                && !isHasGetMissionId(row.getTaskType(), row.getId())
                && isCurLevelMeetsNeed(row)
                && isBeforeMissionHasDone(row)) { // 可以添加任务
            Mission newMission = addMission(row);
            updateOneUnDoneMissionValue(newMission);
            return newMission;
        }
        return null;
    }


    /**
     * 新增任务
     *
     * @param row
     */
    private Mission addMission(Table_Missions_Row row) {
        Mission newMission = new Mission(row.getId(), 0);
        getCollection(row.getTaskType()).put(row.getId(), newMission);
        return newMission;
    }


    /**
     * 前置任务是否已经完成
     *
     * @param row
     * @return true 完成  false 未完成
     */
    private boolean isBeforeMissionHasDone(Table_Missions_Row row) {
        int beforeMid = row.getBeforeTask();
        if (beforeMid <= 0) {
            return true;
        }
        if (hasGetMission(row.getTaskType(), beforeMid)) {
            Mission m = getMission(row.getTaskType(), beforeMid);
            return m.isGet();
        }
        return false;
    }

    /**
     * 当前角色等级是否符合开启需求
     *
     * @param row
     * @return true 符合  false 不符合
     */
    private boolean isCurLevelMeetsNeed(Table_Missions_Row row) {
        if (getPlayerCtrl().getCurLevel() >= row.getLevelNeed()) {
            return true;
        }
        return false;
    }


    /**
     * 是否已经领取过某个任务
     *
     * @param missionType
     * @param mid
     * @return true 已经领取过  false 为领取过
     */
    private boolean isHasGetMissionId(MissionTypeEnum missionType, Integer mid) {
        if (getCollection(missionType).containsKey(mid)) {
            return true;
        }
        return false;
    }


    /**
     * 任务大类的未完成任务集合
     *
     * @return
     */
    private Map<Integer, Mission> getMissionCollection() {
        Map<Integer, Mission> collection = new HashMap<>();
        collection.putAll(getCollection(MissionTypeEnum.MISS_DALIY));
        collection.putAll(getCollection(MissionTypeEnum.MISS_COMMON));
        return collection;
    }

    /**
     * 成就大类的未完成任务集合
     *
     * @return
     */
    private Map<Integer, Mission> getAchieveCollection() {
        Map<Integer, Mission> collection = new HashMap<>();
        collection.putAll(getCollection(MissionTypeEnum.MISS_ACHIEVE_RISK));
        collection.putAll(getCollection(MissionTypeEnum.MISS_ACHIEVE_EVOLO));
        collection.putAll(getCollection(MissionTypeEnum.MISS_ACHIEVE_ACTIVITY));
        return collection;
    }


    /**
     * 未完成任务集合
     *
     * @param missionType
     * @return
     */
    private Map<Integer, Mission> getCollection(MissionTypeEnum missionType) {
        if (!target.getTypeToMissions().containsKey(missionType)) {
            target.getTypeToMissions().put(missionType, new HashMap<>());
        }
        return target.getTypeToMissions().get(missionType);
    }


    /**
     * 是否已经领取了任务
     *
     * @param missionType
     * @param mid
     * @return
     */
    private boolean hasGetMission(MissionTypeEnum missionType, Integer mid) {
        return getCollection(missionType).containsKey(mid);
    }

    /**
     * 获取任务
     *
     * @param missionType
     * @param mid
     * @return
     */
    private Mission getMission(MissionTypeEnum missionType, Integer mid) {
        return getCollection(missionType).get(mid);
    }

    /**
     * 将m2中的值替换到m1中
     *
     * @param m1
     * @param m2
     */
    private void replaceMission(Mission m1, Mission m2) {
        m1.setMid(m2.getMid());
        m1.setValue(m2.getValue());
        m1.setGet(m2.isGet());
    }

    /**
     * 移除超时的体力任务
     */
    private void removeOvertimeEneryMission() {
        Map<Integer, Mission> collection = getCollection(MissionTypeEnum.MISS_DALIY);
        for (Integer mid : new ArrayList<>(getCollection(MissionTypeEnum.MISS_DALIY).keySet())) {
            Mission m = collection.get(mid);
            Table_Missions_Row row = RootTc.get(Table_Missions_Row.class, mid);
            if (row.getTaskSmallType() == MissionSmallTypeEnum.MISS_SMALL_ENERGY) {
                if (System.currentTimeMillis() >= m.getValue()) {
                    collection.remove(mid);
                }
            }
        }
    }


    /**
     * 查询未完成列表中指定PrivateNotifyTypeEnum的集合
     *
     * @param type
     * @return
     */
    private List<Mission> getUnDoneMissionListByNotifyType(PrivateNotifyTypeEnum type) {
        List<Mission> missionList = new ArrayList<>();
        for (Entry<MissionTypeEnum, Map<Integer, Mission>> entry : target.getTypeToMissions().entrySet()) {
            for (Mission m : entry.getValue().values()) {
                Table_Missions_Row row = RootTc.get(Table_Missions_Row.class, m.getMid());
                if (MissionsCtrlUtils.getCompleteType(row) == type && !m.isGet()) {
                    missionList.add(m);
                }
            }
        }
        return missionList;
    }


    private void logicCheck_MissionId(MissionTypeEnum missionType, int mid) {
        if (!RootTc.has(Table_Missions_Row.class, mid)) {
            String msg = String.format("任务表 Table_Missions_Row 中不存在此mid=%s !", mid);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Table_Missions_Row missionsRow = RootTc.get(Table_Missions_Row.class, mid);
        if (missionType != missionsRow.getTaskType()) {
            String msg = String.format("missionType=%s有误，表中配置的为=%s mid=%s !", missionType, missionsRow.getTaskType(), mid);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!isHasGetMissionId(missionsRow.getTaskType(), mid)) {
            String msg = String.format("尚未领取该任务 mid=%s !", mid);
            throw new BusinessLogicMismatchConditionException(msg);
        }
    }
}
