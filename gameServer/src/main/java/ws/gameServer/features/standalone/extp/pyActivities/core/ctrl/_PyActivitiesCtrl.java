package ws.gameServer.features.standalone.extp.pyActivities.core.ctrl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.di.GlobalInjector;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.dataCenter.enums.NotifyScopeEnum;
import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;
import ws.gameServer.features.standalone.extp.dataCenter.msg.NotifyObj;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;
import ws.gameServer.features.standalone.extp.dataCenter.permanent.PermanentDataExtp;
import ws.gameServer.features.standalone.extp.dataCenter.permanent.ctrl.PermanentDataCtrl;
import ws.gameServer.features.standalone.extp.dataCenter.stageDaliy.StageDaliyDataExtp;
import ws.gameServer.features.standalone.extp.dataCenter.stageDaliy.ctrl.StageDaliyDataCtrl;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.missions.utils.MissionsCtrlUtils;
import ws.gameServer.features.standalone.extp.payment.PaymentExtp;
import ws.gameServer.features.standalone.extp.payment.ctrl.PaymentCtrl;
import ws.gameServer.features.standalone.extp.pyActivities.core.utils.PyActivitiesCtrlProtos;
import ws.gameServer.features.standalone.extp.pyActivities.core.utils.PyActivitiesCtrlUtils;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.gameServer.system.date.dayChanged.DayChanged;
import ws.protos.EnumsProtos.ActivityBigTypeEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.PyActivitiesProtos.Sm_PyActivities;
import ws.protos.PyActivitiesProtos.Sm_PyActivities.Action;
import ws.protos.PyActivitiesProtos.Sm_PyActivities_RealmAct;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Activity_Group_Row;
import ws.relationship.table.tableRows.Table_Activity_Realm_Row;
import ws.relationship.table.tableRows.Table_Activity_Sub_Row;
import ws.relationship.topLevelPojos.pyActivities.PyActivities;
import ws.relationship.topLevelPojos.pyActivities.PyGroupActDefault;
import ws.relationship.topLevelPojos.pyActivities.PySubAct;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class _PyActivitiesCtrl extends AbstractPlayerExteControler<PyActivities> implements PyActivitiesCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_PyActivitiesCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private StageDaliyDataCtrl stageDaliyDataCtrl;
    private PermanentDataCtrl permanentDataCtrl;
    private PaymentCtrl paymentCtrl;
    private Map<Integer, Integer> realmActIdsChanged = new ConcurrentHashMap<>();
    private Timer timer = new Timer();
    private static final long delay = 1000;                           // 延迟1秒

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        stageDaliyDataCtrl = getPlayerCtrl().getExtension(StageDaliyDataExtp.class).getControlerForQuery();
        permanentDataCtrl = getPlayerCtrl().getExtension(PermanentDataExtp.class).getControlerForQuery();
        paymentCtrl = getPlayerCtrl().getExtension(PaymentExtp.class).getControlerForQuery();
    }

    @Override
    public void _initBeforeChanged() throws Exception {
        PyActivitiesCtrlUtils.removeOverShowTimeActivity(target, getPlayerCtrl().getOuterRealmId());
        loadNewGroupAct();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
        resetDaliyAct();
        handleActLogin(); // 处理登录活动
    }

    @Override
    public void _initAfterChanged() throws Exception {
        updateAllSubActValue();
    }

    @Override
    public void onPlayerLogined() {
        sync();
        save();
    }

    @Override
    public void sync() {
        SenderFunc.sendInner(this, Sm_PyActivities.class, Sm_PyActivities.Builder.class, Sm_PyActivities.Action.RESP_SYNC, (b, br) -> {
            b.setHasBufFund(target.isHasBuyFund());
            b.addAllRealmActs(PyActivitiesCtrlProtos.create_Sm_PyActivities_RealmAct_Lis(target, getPlayerCtrl().getOuterRealmId()));
        });
        save();
    }


    private void syncPart(int deleteRealmActId, PyGroupActDefault... groupActDefaultArr) {
        SenderFunc.sendInner(this, Sm_PyActivities.class, Sm_PyActivities.Builder.class, Action.RESP_SYNC_PART, (b, br) -> {
            if (deleteRealmActId > 0) {
                b.addDelRealmAcId(deleteRealmActId);
            }
            if (groupActDefaultArr != null && groupActDefaultArr.length > 0) {
                for (PyGroupActDefault actDefault : groupActDefaultArr) {
                    Sm_PyActivities_RealmAct.Builder builder = PyActivitiesCtrlProtos.create_Sm_PyActivities_RealmAct(actDefault.getRealmAcId(), getPlayerCtrl().getOuterRealmId());
                    builder.setGroupAct(PyActivitiesCtrlProtos.create_Sm_PyActivities_GroupAct(actDefault));
                    b.addRealmActs(builder);
                }
            }
        });
    }


    @Override
    public void onUpdateRegNotifyMsg(PrivateNotifyTypeEnum type) {
        target.getRaIdToDefault().values().forEach((groupActDefault) -> {
            groupActDefault.getIdToSubAct().values().forEach((subAct) -> {
                if (!subAct.isGet()) {
                    long oldValue = subAct.getValue();
                    Table_Activity_Sub_Row subRow = RootTc.get(Table_Activity_Sub_Row.class, subAct.getSubAcId());
                    NotifyObj notifyObj = MissionsCtrlUtils.parseNotifyObj(subRow.getCompleteType(), subRow.getCompleteCondition(), subAct.getSubAcId());
                    if (notifyObj.getType() == type) {
                        _updateOneSubActValue(subAct, notifyObj);
                    }
                    long newValue = subAct.getValue();
                    if (newValue > oldValue) {
                        realmActIdsChanged.put(groupActDefault.getRealmAcId(), 1);
                    }
                }
            });
        });
        if (realmActIdsChanged.size() > 0) {
            timer.cancel();
            timer = new Timer();
            timer.schedule(new SyncPart(this), delay);
            save();
        }
    }

    @Override
    public void buyFund() {
        if (target.isHasBuyFund()) {
            String msg = String.format("已经购买了基金，不需要重复购买！");
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int needPlayerLv = AllServerConfig.Activity_BuyFund_NeedPlayerLv.getConfig();
        int needPlayerVipLv = AllServerConfig.Activity_BuyFund_NeedPlayerVipLv.getConfig();
        if (getPlayerCtrl().getCurLevel() < needPlayerLv) {
            String msg = String.format("购买基金需要玩家等级=%s！", needPlayerLv);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (getPlayerCtrl().getCurVipLevel() < needPlayerVipLv) {
            String msg = String.format("购买基金需要玩家VIP等级=%s！", needPlayerVipLv);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int consume = AllServerConfig.Activity_BuyFund.getConfig();
        IdAndCount reduce = new IdAndCount(ResourceTypeEnum.RES_VIPMONEY_VALUE, consume);
        LogicCheckUtils.canRemove(itemIoCtrl, reduce);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_BUY_FUND).removeItem(reduce);
        target.setHasBuyFund(true);
        sendPrivateMsg(new Pr_NotifyMsg(PrivateNotifyTypeEnum.Buy_Fund_Complete, MagicNumbers.DEFAULT_ONE));
        SenderFunc.sendInner(this, Sm_PyActivities.class, Sm_PyActivities.Builder.class, Action.RESP_BUY_FUND, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1), br);
            b.setHasBufFund(target.isHasBuyFund());
        });
        save();
    }

    @Override
    public void getRewards(int realmAcId, int subActId) {
        int outerRealmId = getPlayerCtrl().getOuterRealmId();
        Table_Activity_Realm_Row realmRow = RootTc.get(Table_Activity_Realm_Row.class, realmAcId);
        Table_Activity_Group_Row groupRow = RootTc.get(Table_Activity_Group_Row.class, realmRow.getActivityGroupId());
        Table_Activity_Sub_Row subRow = RootTc.get(Table_Activity_Sub_Row.class, subActId);
        if (!PyActivitiesCtrlUtils.containsRealmActivity(target, realmAcId)) {
            String msg = String.format("尚未领取该[服]活动！ realmAcId=%s outerRealmId=%s .", realmAcId, outerRealmId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        PyGroupActDefault groupActDefault = PyActivitiesCtrlUtils.getGroupActDefault(target, realmAcId);
        if (!PyActivitiesCtrlUtils.containsSubActivity(groupActDefault, subActId)) {
            String msg = String.format("尚未领取该[子]活动！ realmAcId=%s subActId=%s outerRealmId=%s .", realmAcId, subActId, outerRealmId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        PySubAct subAct = PyActivitiesCtrlUtils.getSubActivity(groupActDefault, subActId);
        if (subAct.isGet()) {
            String msg = String.format("子活动已经领取了奖励！ realmAcId=%s subActId=%s outerRealmId=%s .", realmAcId, subActId, outerRealmId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        NotifyObj notifyObj = MissionsCtrlUtils.parseNotifyObj(subRow.getCompleteType(), subRow.getCompleteCondition(), subActId);
        if (subAct.getValue() < notifyObj.getValue()) {
            String msg = String.format("尚未完成子活动，不可领取奖励！ realmAcId=%s subActId=%s outerRealmId=%s curValue=%s needValue=%s .", realmAcId, subActId, outerRealmId, subAct.getValue(), notifyObj.getValue());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (getPlayerCtrl().getCurLevel() < subRow.getCompletePlayerLevel()) {
            String msg = String.format("尚未完成子活动，不可领取奖励！ realmAcId=%s subActId=%s outerRealmId=%s needPlayerLv=%s.", realmAcId, subActId, outerRealmId, subRow.getCompletePlayerLevel());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (getPlayerCtrl().getCurVipLevel() < subRow.getCompletePlayerVipLevel()) {
            String msg = String.format("尚未完成子活动，不可领取奖励！ realmAcId=%s subActId=%s outerRealmId=%s needPlayerVIPLv=%s.", realmAcId, subActId, outerRealmId, subRow.getCompletePlayerVipLevel());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        getRewardsSpecialConditionCheck(groupRow);
        IdMaptoCount reward = subRow.getRewards();
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Sm_PyActivities.Action.RESP_GET_REWARDS).addItem(reward);
        subAct.setGet(true);
        SenderFunc.sendInner(this, Sm_PyActivities.class, Sm_PyActivities.Builder.class, Sm_PyActivities.Action.RESP_GET_REWARDS, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(refresh_1, br);
            b.setSubActObj(PyActivitiesCtrlProtos.create_Sm_PyActivities_SubActObj(realmAcId, realmRow.getActivityGroupId(), subAct));
        });

        // 永久活动都领取了奖励，则移除服活动
        if (PyActivitiesCtrlUtils.isRealmActOpenTimeLimit(realmRow) && PyActivitiesCtrlUtils.isGroupActAllSubActGetRewards(groupActDefault)) {
            target.getRaIdToDefault().remove(groupActDefault.getRealmAcId());
            realmActIdsChanged.remove(groupActDefault.getRealmAcId());
            syncPart(groupActDefault.getRealmAcId());
        }
        save();
    }

    @Override
    public int hasGetSpecialGroupActivity(int groupActId) {
        for (Integer realmActId : target.getSpecialRaIds()) {
            Table_Activity_Realm_Row realmRow = RootTc.get(Table_Activity_Realm_Row.class, realmActId);
            if (realmRow.getActivityGroupId() == groupActId) {
                return realmActId;
            }
        }
        return -1;
    }

    /**
     * 更新所有活动的完成进度
     *
     * @param
     */
    private void updateAllSubActValue() {
        for (PyGroupActDefault groupActDefault : target.getRaIdToDefault().values()) {
            updateSomeSubActValue(groupActDefault);
        }
    }


    /**
     * 更新几个子活动的完成度
     *
     * @param groupActDefault
     */
    private void updateSomeSubActValue(PyGroupActDefault groupActDefault) {
        Table_Activity_Group_Row groupRow = RootTc.get(Table_Activity_Group_Row.class, groupActDefault.getGroupAcId());
        if (PyActivitiesCtrlUtils.isRealmActOverEndTime(groupActDefault.getRealmAcId(), getPlayerCtrl().getOuterRealmId())) {
            return; // 活动已经结束了，不用更新完成度了
        }
        if (groupRow.getType() == ActivityBigTypeEnum.ACTB_LOGIN) {
            return; // 登录子活动完成度单独更新
        }
        LinkedHashMap<Integer, PySubAct> idToSubAct = groupActDefault.getIdToSubAct(); //子活动Id -- 子活动
        for (PySubAct subAct : idToSubAct.values()) {
            updateOneSubActValue(subAct);
        }
    }


    /**
     * 更新一个子活动的完成度
     */
    private void updateOneSubActValue(PySubAct subAct) {
        if (subAct.isGet()) {
            return;  // 已经完成任务
        }
        Table_Activity_Sub_Row subRow = RootTc.get(Table_Activity_Sub_Row.class, subAct.getSubAcId());
        if (StringUtils.isBlank(subRow.getCompleteType()) || StringUtils.isBlank(subRow.getCompleteCondition())) {
            return;
        }
        NotifyObj notifyObj = MissionsCtrlUtils.parseNotifyObj(subRow.getCompleteType(), subRow.getCompleteCondition(), subAct.getSubAcId());
        _updateOneSubActValue(subAct, notifyObj);
    }

    private void _updateOneSubActValue(PySubAct subAct, NotifyObj notifyObj) {
        if (subAct.isGet()) {
            return;  // 已经完成任务
        }
        String today = GlobalInjector.getInstance(DayChanged.class).getDayChangedStr();
        notifyObj.setValue(subAct.getValue());
        if (notifyObj.getType().getScope() == NotifyScopeEnum.DAILY) {
            stageDaliyDataCtrl.updateRegNotifyMsgHasDone(notifyObj, today, today);
        } else {
            permanentDataCtrl.updateRegNotifyMsgHasDone(notifyObj);
        }
        subAct.setValue(notifyObj.getValue());
    }


    /**
     * 处理登录活动
     * 该逻辑必须处于_resetDataAtDayChanged，来保证每天只执行一次
     */
    private void handleActLogin() {
        for (PyGroupActDefault groupActDefault : target.getRaIdToDefault().values()) {
            if (PyActivitiesCtrlUtils.isRealmActOverEndTime(groupActDefault.getRealmAcId(), getPlayerCtrl().getOuterRealmId())) {
                continue; // 活动已经结束了，不用更新完成度了
            }
            Table_Activity_Group_Row groupRow = RootTc.get(Table_Activity_Group_Row.class, groupActDefault.getGroupAcId());
            if (groupRow.getType() == ActivityBigTypeEnum.ACTB_LOGIN) {
                for (PySubAct subActivity : groupActDefault.getIdToSubAct().values()) {
                    if (!subActivity.isGet()) { // 查找第一个未完成（包括未领取奖励）的子活动
                        subActivity.setValue(MagicNumbers.DEFAULT_ONE);
                        break;
                    }
                }
            }
        }
    }


    /**
     * 加载新的活动
     */
    private void loadNewGroupAct() {
        int outerRealmId = getPlayerCtrl().getOuterRealmId();
        List<Table_Activity_Realm_Row> rows = Table_Activity_Realm_Row.outerRealmActivities(outerRealmId);
        for (Table_Activity_Realm_Row row : rows) {
            if (target.getHasGetRaIds().contains(row.getId())) {
                continue;
            }
            if (PyActivitiesCtrlUtils.containsRealmActivity(target, row.getId())) {
                continue;
            }
            if (PyActivitiesCtrlUtils.isRealmActivityOverEndTime(row, outerRealmId)) {
                continue;
            }
            if (!PyActivitiesCtrlUtils.isPlayerMeetActivityConfigCondition(row, getPlayerCtrl().getCurLevel(), getPlayerCtrl().getCurVipLevel())) {
                continue;
            }
            boolean rs = PyActivitiesCtrlUtils.addNewActivity(target, row);
            if (rs) {
                target.getHasGetRaIds().add(row.getId());
            }
        }
    }


    /**
     * 重置每日需要重置的活动
     */
    private void resetDaliyAct() {
        for (PyGroupActDefault groupActDefault : target.getRaIdToDefault().values()) {
            Table_Activity_Realm_Row realmRow = RootTc.get(Table_Activity_Realm_Row.class, groupActDefault.getRealmAcId());
            if (!realmRow.getDailyReset()) {
                continue;
            }
            for (PySubAct subAct : groupActDefault.getIdToSubAct().values()) {
                subAct.setGet(false);
                subAct.setValue(0);
                subAct.setExchangeTs(0);
            }
        }
    }


    /**
     * 领取奖励特殊逻辑判断
     */
    private void getRewardsSpecialConditionCheck(Table_Activity_Group_Row groupRow) {
        if (groupRow.getType() == ActivityBigTypeEnum.ACTB_FUND) { // 等级基金
            if (!target.isHasBuyFund()) {
                String msg = String.format("尚未购买等级基金 不能领取奖励.");
                throw new BusinessLogicMismatchConditionException(msg);
            }
        }
    }


    /**
     * 间隔2秒，增量同步
     */
    private class SyncPart extends TimerTask {
        private _PyActivitiesCtrl pyActivitiesCtrl;

        public SyncPart(_PyActivitiesCtrl pyActivitiesCtrl) {
            this.pyActivitiesCtrl = pyActivitiesCtrl;
        }

        @Override
        public void run() {
            //todo 以消息的形式处理
            List<Integer> changed = new ArrayList<>(realmActIdsChanged.keySet());
            realmActIdsChanged.clear();
            SenderFunc.sendInner(pyActivitiesCtrl, Sm_PyActivities.class, Sm_PyActivities.Builder.class, Action.RESP_SYNC_PART, (b, br) -> {
                for (Integer realmActId : changed) {
                    PyGroupActDefault actDefault = PyActivitiesCtrlUtils.getGroupActDefault(target, realmActId);
                    Sm_PyActivities_RealmAct.Builder builder = PyActivitiesCtrlProtos.create_Sm_PyActivities_RealmAct(actDefault.getRealmAcId(), getPlayerCtrl().getOuterRealmId());
                    builder.setGroupAct(PyActivitiesCtrlProtos.create_Sm_PyActivities_GroupAct(actDefault));
                    b.addRealmActs(builder);
                }
            });
        }
    }
}
