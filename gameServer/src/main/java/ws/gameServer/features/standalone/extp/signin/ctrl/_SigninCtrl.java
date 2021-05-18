package ws.gameServer.features.standalone.extp.signin.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.SigninProtos.Sm_Signin;
import ws.protos.SigninProtos.Sm_Signin.Action;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Comulative_Row;
import ws.relationship.table.tableRows.Table_Sign_Row;
import ws.relationship.topLevelPojos.signin.Signin;
import ws.relationship.utils.RelationshipCommonUtils;

import java.util.Date;
import java.util.List;

public class _SigninCtrl extends AbstractPlayerExteControler<Signin> implements SigninCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_SigninCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
    }

    @Override
    public void sync() {
        tryMonthChange();
        tryDayChange();
        SenderFunc.sendInner(this, Sm_Signin.class, Sm_Signin.Builder.class, Sm_Signin.Action.RESP_SYNC, (b, br) -> {
            b.setAllSignCount(target.getAllSigninTimes());
            b.setIsSignin(target.isSignin());
            b.setMonthSignCount(target.getMonthSigninTimes());
            b.setComulativeCounts(target.getCumulatvieGift());
        });
        save();
    }


    @Override
    public void signin() {
        if (target.isSignin()) {
            String msg = "本日已签到";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Table_Sign_Row signRow = getTodaySignRow();
        IdMaptoCount idMaptoCount = _getSigninGift(signRow);
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Sm_Signin.Action.RESP_SIGNIN).addItem(idMaptoCount);
        _signin();
        SenderFunc.sendInner(this, Sm_Signin.class, Sm_Signin.Builder.class, Sm_Signin.Action.RESP_SIGNIN, (b, br) -> {
            b.setComulativeCounts(target.getCumulatvieGift());
            b.setIsSignin(target.isSignin());
            b.setAllSignCount(target.getAllSigninTimes());
            b.setMonthSignCount(target.getMonthSigninTimes());
            b.setVipLv(target.getVipLv());
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
        });
        save();
    }

    @Override
    public void addVipSignin() {
        if (!target.isSignin()) {
            String msg = String.format("month=%s signDate=%s 本日尚未签到，不能补签. ", target.getMonth(), target.getLastSignDate());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Table_Sign_Row todaySignRow = getTodaySignRow();
        if (!todaySignRow.getIsDouble()) {
            String msg = String.format("month=%s signDate=%s 本日不是VIP双倍，不能补签. ", target.getMonth(), target.getLastSignDate());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (getPlayerCtrl().getCurVipLevel() < todaySignRow.getvIPLv()) {
            String msg = String.format("month=%s signDate=%s VIP=%s等级不足，不能补签. ", target.getMonth(), target.getLastSignDate(), getPlayerCtrl().getCurVipLevel());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (target.getVipLv() >= getPlayerCtrl().getCurVipLevel()) {
            String msg = String.format("month=%s signDate=%s 今日VIP=%s签到奖励已经领取，不能补签. ", target.getMonth(), target.getLastSignDate(), target.getVipLv());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        target.setVipLv(getPlayerCtrl().getCurVipLevel());
        IdMaptoCount idMaptoCount = _getSigninOneTimesGift(todaySignRow);
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Action.RESP_ADD_VIP_SIGNIN).addItem(idMaptoCount);
        SenderFunc.sendInner(this, Sm_Signin.class, Sm_Signin.Builder.class, Sm_Signin.Action.RESP_ADD_VIP_SIGNIN, (b, br) -> {
            b.setComulativeCounts(target.getCumulatvieGift());
            b.setIsSignin(target.isSignin());
            b.setAllSignCount(target.getAllSigninTimes());
            b.setMonthSignCount(target.getMonthSigninTimes());
            b.setVipLv(target.getVipLv());
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
        });
        save();
    }


    @Override
    public void comulativeReward() {
        int signCount = target.getAllSigninTimes();
        int needDays = nextCumulatvieGiftNeedDays();
        if (signCount < needDays) {
            String msg = String.format("不符合领取的累计奖励的条件! signCount=%s needDays=%s", signCount, needDays);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int curCumulatvieGift = target.getCumulatvieGift();
        int nextCumulatvieGift = curCumulatvieGift + MagicNumbers.DEFAULT_ONE;
        Table_Comulative_Row row = getNextCumulatvieGiftRow(curCumulatvieGift);
        IdMaptoCount idMaptoCount = IdMaptoCount.parse(row.getRewardIdAndCount());
        LogicCheckUtils.canAdd(itemIoCtrl, idMaptoCount);
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Sm_Signin.Action.RESP_COMULATIVE_REWARD).addItem(idMaptoCount);
        target.setCumulatvieGift(nextCumulatvieGift);
        SenderFunc.sendInner(this, Sm_Signin.class, Sm_Signin.Builder.class, Sm_Signin.Action.RESP_COMULATIVE_REWARD, (b, br) -> {
            b.setComulativeCounts(target.getCumulatvieGift());
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
        });
        save();
    }

    /**
     * 签到
     */
    private void _signin() {
        int monthSignDate = target.getMonthSigninTimes();
        target.setMonthSigninTimes(monthSignDate + MagicNumbers.DEFAULT_ONE);
        target.setAllSigninTimes(target.getAllSigninTimes() + MagicNumbers.DEFAULT_ONE);
        String yyyyMMdd = WsDateUtils.dateToFormatStr(new Date(), WsDateFormatEnum.yyyyMMdd);
        target.setLastSignDate(yyyyMMdd);
        target.setSignin(true);
        target.setVipLv(getPlayerCtrl().getCurVipLevel());
    }


    /**
     * 领取下一个累计签到奖励需要的累计签到次数
     *
     * @return
     */
    private int nextCumulatvieGiftNeedDays() {
        List<Table_Comulative_Row> rowList = RootTc.get(Table_Comulative_Row.class).values();
        int sumNext = 0;
        for (int i = 0; i <= target.getCumulatvieGift(); i++) {
            sumNext += rowList.get(i).getTotalSignDay();
        }
        return sumNext;
    }


    /**
     * 签到的月份是否改变
     */
    private void tryMonthChange() {
        int curMonth = Integer.valueOf(WsDateUtils.dateToFormatStr(new Date(), WsDateFormatEnum.MM));
        if (target.getMonth() != curMonth) {
            target.setMonthSigninTimes(0);
            target.setMonth(curMonth);
            save();
        }
    }

    /**
     * 签到日是否改变，改变则设置为未签到
     */
    private void tryDayChange() {
        String yyyyMMdd = WsDateUtils.dateToFormatStr(new Date(), WsDateFormatEnum.yyyyMMdd);
        if (yyyyMMdd.equals(target.getLastSignDate())) {
            return;
        } else {
            target.setSignin(false);
        }
    }


    /**
     * 获取签到N倍奖励
     *
     * @return
     */
    private IdMaptoCount _getSigninGift(Table_Sign_Row todaySignRow) {
        return RelationshipCommonUtils.severalTimesIdMaptoCount(_getSigninOneTimesGift(todaySignRow), getTodayGiftTimes(todaySignRow));
    }


    /**
     * 获取签到单倍奖励
     *
     * @return
     */
    private IdMaptoCount _getSigninOneTimesGift(Table_Sign_Row todaySignRow) {
        return todaySignRow.getRewardIdAndCount();
    }


    /**
     * 今日的签到行
     *
     * @return
     */
    private Table_Sign_Row getTodaySignRow() {
        int month = target.getMonth();
        int signinTimes = target.getMonthSigninTimes() + 1; // 未签到
        if (target.isSignin()) {
            signinTimes = target.getMonthSigninTimes();     // 已签到
        }
        return Table_Sign_Row.getSignRow(month, signinTimes);
    }


    /**
     * 今日奖励的倍数
     *
     * @param todaySignRow
     * @return
     */
    private int getTodayGiftTimes(Table_Sign_Row todaySignRow) {
        if (todaySignRow.getIsDouble() && getPlayerCtrl().getCurVipLevel() >= todaySignRow.getvIPLv()) { // vip 签到双倍
            return MagicNumbers.DEFAULT_TWO;
        }
        return MagicNumbers.DEFAULT_ONE;
    }

    /**
     * 获取下一个累计签到的奖励
     *
     * @param curCumulatvieGift
     * @return
     */
    public Table_Comulative_Row getNextCumulatvieGiftRow(int curCumulatvieGift) {
        List<Table_Comulative_Row> rowList = RootTc.get(Table_Comulative_Row.class).values();
        if (rowList.size() < (curCumulatvieGift + 1)) {
            return rowList.get(rowList.size() - 1);
        }
        return rowList.get(curCumulatvieGift);
    }
}
