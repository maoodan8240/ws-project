package ws.gameServer.features.standalone.extp.payment.ctrl;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.common.utils.di.GlobalInjector;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.payment.utils.MonthCardInfo;
import ws.gameServer.features.standalone.extp.payment.utils.PaymentCtrlProtos;
import ws.gameServer.features.standalone.extp.payment.utils.PaymentCtrlUtils;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.gameServer.system.date.dayChanged.DayChanged;
import ws.protos.EnumsProtos.MonthCardTypeEnum;
import ws.protos.EnumsProtos.OrderStatusEnum;
import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.PaymentProtos.Cm_Payment_SyncOrder;
import ws.protos.PaymentProtos.Sm_Payment;
import ws.protos.PaymentProtos.Sm_Payment.Action;
import ws.relationship.appServers.thirdPartyServer.In_ProcessPayment;
import ws.relationship.appServers.thirdPartyServer.In_VerifyingPayment;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.base.ServerRoleEnum;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.paymentOrder.PaymentOrderDao;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Payment_Row;
import ws.relationship.table.tableRows.Table_Vip_Row;
import ws.relationship.topLevelPojos.payment.Payment;
import ws.relationship.topLevelPojos.paymentOrder.PaymentOrder;
import ws.relationship.utils.ActorMsgSynchronizedExecutor;
import ws.relationship.utils.RelationshipCommonUtils;

import java.util.Date;

public class _PaymentCtrl extends AbstractPlayerExteControler<Payment> implements PaymentCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_PaymentCtrl.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final PaymentOrderDao PAYMENT_ORDER_DAO = DaoContainer.getDao(PaymentOrder.class);

    static {
        PAYMENT_ORDER_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
    }

    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
        resetAllMonthCardExpired();
    }

    @Override
    public void sync() {
        SenderFunc.sendInner(this, Sm_Payment.class, Sm_Payment.Builder.class, Action.RESP_SYNC, (b, br) -> {
            b.addAllPaymentInfo(PaymentCtrlProtos.create_Sm_Payment_PaymentInfo_Lis(target.getPaymentIdToTimes()));
            b.addAllHasBuyVipLvGift(target.getHasBuyVipLvGift());
        });
    }

    @Override
    public void syncOrder(Cm_Payment_SyncOrder syncOrder) {
        String innerOrderId = syncOrder.getInnerOrderId();
        int goodId = syncOrder.getGoodId();        // 购买物品的Id
        String goodName = syncOrder.getGoodName(); // 购买物品的支付名称
        String receipt = syncOrder.getReceipt();   // 收据
        PaymentOrder order = PAYMENT_ORDER_DAO.findOneByInnerOrderId(innerOrderId);
        Table_Payment_Row paymentRow = RootTc.get(Table_Payment_Row.class).get(goodId);
        if (paymentRow == null) { // 非法数据
            LOGGER.warn("同步订单 > 当前订单的商品Id不存在，innerOrderId={} goodId={} !", innerOrderId, goodId);
            return;
        }
        if (order == null) {
            syncOrderToClient(innerOrderId, OrderStatusEnum.OD_NON_EXIST);
            LOGGER.warn("同步订单 > 当前订单不存在，innerOrderId={} ", innerOrderId);
            return;
        }
        if (!order.getPlayerId().equals(getPlayerCtrl().getPlayerId())) { // 非法数据
            syncOrderToClient(innerOrderId, OrderStatusEnum.OD_ILLEGAL);
            LOGGER.warn("同步订单 > 当前订单由玩家playerId={}，selfPlayerId={}, innerOrderId={} ", order.getPlayerId(), getPlayerCtrl().getPlayerId(), innerOrderId);
            return;
        }
        if (goodId != order.getGoodId()) { // 非法数据
            syncOrderToClient(innerOrderId, OrderStatusEnum.OD_ILLEGAL);
            LOGGER.warn("同步订单 > 请求同步的[商品Id]和玩家在服务器购买时的[商品Id]不一致！ order.GoodId={} request.GoodId={}", order.getGoodId(), goodId);
            return;
        }
        if (!order.getGoodName().equals(goodName)) { // 非法数据
            syncOrderToClient(innerOrderId, OrderStatusEnum.OD_ILLEGAL);
            LOGGER.warn("同步订单 > 请求同步的[商品Name]和玩家在服务器购买时的[商品Name]不一致！ order.GoodName={} request.goodName={}", order.getGoodName(), goodName);
            return;
        }
        if (order.getOrderStatus() == OrderStatusEnum.OD_FAILED || order.getOrderStatus() == OrderStatusEnum.OD_SUCCESS) {
            syncOrderToClient(innerOrderId, order.getOrderStatus());
            LOGGER.warn("同步订单 > 请求同步的订单已经处理完毕！ innerOrderId={} ", innerOrderId);
            return;
        }
        PlatformTypeEnum platformType = getPlayerCtrl().getTarget().getAccount().getPlatformType();
        if (platformType == PlatformTypeEnum.AWO) { // todo 内部测试用
            handleThirdPartyReturnMsg(paymentRow, order, ObjectId.get().toString(), innerOrderId, true);
            return;
        }
        In_VerifyingPayment.Request request = new In_VerifyingPayment.Request(platformType, receipt, null, order.getArgs());
        In_VerifyingPayment.Response response = ActorMsgSynchronizedExecutor.sendMsgToSingleServerIgnoreEnv(ServerRoleEnum.WS_ThirdPartyServer, getPlayerCtrl().getContext(), ActorSystemPath.WS_ThirdPartyServer_Selection_PaymentCenter, request);
        handleThirdPartyReturnMsg(paymentRow, order, response.getOuterOrderId(), innerOrderId, response.isRs());
    }

    @Override
    public void buyVipGift(int vipLv) {
        LogicCheckUtils.validateParam(Integer.class, vipLv);
        int curVipLv = getPlayerCtrl().getTarget().getPayment().getVipLevel();
        if (curVipLv < vipLv) {
            String msg = String.format("curVipLv=%s，购买vip礼包的等级=%s，不能购买！", curVipLv, vipLv);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Table_Vip_Row vipRow = Table_Vip_Row.getVipRow(vipLv);
        if (vipRow == null) {
            String msg = String.format("数据表Table_Vip_Row ！不含该VIP等级=%s数据！", vipLv);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount toAdd = vipRow.getvIPPackage();
        if (toAdd.getAll().size() <= 0) {
            String msg = String.format("rowId=%s vipLv=%s vip礼包内容为空，不能购买！", vipRow.getId(), vipLv);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (target.getHasBuyVipLvGift().contains(vipLv)) {
            String msg = String.format("已经购买过vipLv=%s的礼包了！ hasBuy=%s", vipLv, target.getHasBuyVipLvGift());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdAndCount toReduce = new IdAndCount(ResourceTypeEnum.RES_VIPMONEY_VALUE, vipRow.getvIPPackagePrice());
        LogicCheckUtils.canAdd(itemIoCtrl, toAdd);
        LogicCheckUtils.canRemove(itemIoCtrl, toReduce);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_BUY_VIP_GIFT).removeItem(toReduce);
        IdMaptoCount refresh_2 = itemIoExtp.getControlerForUpdate(Action.RESP_BUY_VIP_GIFT).addItem(toAdd);
        target.getHasBuyVipLvGift().add(vipLv);
        SenderFunc.sendInner(this, Sm_Payment.class, Sm_Payment.Builder.class, Action.RESP_BUY_VIP_GIFT, (b, br) -> {
            b.addAllHasBuyVipLvGift(target.getHasBuyVipLvGift());
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).addAll(refresh_2), br);
        });
        save();
    }

    @Override
    public void create(int goodId, String args) {
        LogicCheckUtils.validateParam(Integer.class, goodId);
        Table_Payment_Row paymentRow = RootTc.get(Table_Payment_Row.class, goodId);
        PlatformTypeEnum platformType = getPlayerCtrl().getTarget().getAccount().getPlatformType();
        if (paymentRow.getPlatformType() != platformType) {
            String msg = String.format("goodId=%s，Row_platformType=%s Player_PlatformType为=%s 不一致，无法购买！", goodId, paymentRow.getPlatformType(), platformType);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        PaymentOrder order = PaymentCtrlUtils.createNewOrder(getPlayerCtrl().getTarget(), args, paymentRow);
        PAYMENT_ORDER_DAO.insertIfExistThenReplace(order); // 订单直接入库
        SenderFunc.sendInner(this, Sm_Payment.class, Sm_Payment.Builder.class, Action.RESP_CREATE, (b, br) -> {
            b.setOrder(PaymentCtrlProtos.create_Sm_Payment_Order(order));
        });
    }

    @Override
    public void onProcessPayment(In_ProcessPayment.Request request) {
        LOGGER.warn("收到了支付请求 request={} !", request);
        String outerOrderId = request.getOuterOrderId(); // 游戏外部订单号 | 第三方游戏平台订单ID
        String innerOrderId = request.getInnerOrderId(); // 游戏内部订单号
        String playerId = request.getPlayerId(); // 玩家据色ObjectId
        Integer goodId = request.getGoodId();// 购买物品的Id
        String goodName = request.getGoodName();// 购买物品的Id
        Long serverTime = request.getServerTime();// 服务器时间
        boolean rs = request.isRs(); // 交易是否成功
        if (!getPlayerCtrl().getPlayerId().equals(playerId)) {
            LOGGER.warn("第三方返回的充值数据 > 发送的PlayerId={}和接收到数据的玩家playerId={}不一致 !", getPlayerCtrl().getPlayerId(), playerId);
            return;
        }
        if (serverTime != null) {
            String dateSend = WsDateUtils.dateToFormatStr(new Date(serverTime), WsDateFormatEnum.yyyy_MM_dd$HH_mm_ss);
            String dateGet = WsDateUtils.dateToFormatStr(new Date(), WsDateFormatEnum.yyyy_MM_dd$HH_mm_ss);
            LOGGER.warn("第三方返回的充值数据 > 处理时间： 支付时的时间为{}, 接收到支付数据并开始处理时的时间为={} ", dateSend, dateGet);
        }
        Table_Payment_Row paymentRow = RootTc.get(Table_Payment_Row.class).get(goodId);
        if (paymentRow == null) { // 非法数据
            LOGGER.warn("第三方返回的充值数据 > 当前订单的商品Id不存在，innerOrderId={}  outerOrderId={} goodId={} !", innerOrderId, outerOrderId, goodId);
            return;
        }
        PaymentOrder order = PAYMENT_ORDER_DAO.findOneByInnerOrderId(innerOrderId);
        if (order == null) { // 非法数据
            LOGGER.warn("第三方返回的充值数据 > 当前订单不存在，innerOrderId={}  outerOrderId={} ", innerOrderId, outerOrderId);
            return;
        }
        if (goodId != order.getGoodId()) { // 非法数据
            LOGGER.warn("第三方返回的充值数据 > 平台返回的[商品Id]和玩家在服务器购买时的[商品Id]不一致！ order.GoodId={} request.GoodId={}", order.getGoodId(), goodId);
            return;
        }
        if (!order.getGoodName().equals(goodName)) { // 非法数据
            LOGGER.warn("第三方返回的充值数据 > 平台返回的[商品Name]和玩家在服务器购买时的[商品Name]不一致！ order.GoodName={} request.goodName={}", order.getGoodName(), goodName);
            return;
        }
        // 订单已经有明确的结果了，不需要再处理
        if (order.getOrderStatus() == OrderStatusEnum.OD_SUCCESS || order.getOrderStatus() == OrderStatusEnum.OD_FAILED) {
            syncOrderToClient(innerOrderId, order.getOrderStatus());
            LOGGER.warn("第三方返回的充值数据 > 当前订单已经处理结束了，支付成功了！innerOrderId={}  outerOrderId={} status={}！", order.getInnerOrderId(), outerOrderId, order.getOrderStatus());
            return;
        }
        handleThirdPartyReturnMsg(paymentRow, order, outerOrderId, innerOrderId, rs);
    }


    @Override
    public MonthCardInfo calcuMonthCardRemainDays(MonthCardTypeEnum type, boolean hasReceiveToday) {
        int remainDays = 0;
        if (type != MonthCardTypeEnum.MT_250 && type != MonthCardTypeEnum.MT_880) {
            return new MonthCardInfo(0, 0);
        }
        if (target.getTypeToStartDay().containsKey(type)) {
            String starDay = target.getTypeToStartDay().get(type);
            String today = GlobalInjector.getInstance(DayChanged.class).getDayChangedStr();
            int days = RelationshipCommonUtils.daysBetween(today, starDay);
            remainDays = MagicNumbers.MONTH_CARD_SUM_DAYS - days;       // 当日未领取过奖励
            remainDays = hasReceiveToday ? remainDays - 1 : remainDays; // 当日已经领取过奖励
            remainDays = remainDays <= 0 ? 0 : remainDays;
        }
        int hasPay = getMonthCardHasPay(type);
        return new MonthCardInfo(hasPay, remainDays);
    }


    private void syncOrderToClient(String innerOrderId, OrderStatusEnum orderStatus) {
        SenderFunc.sendInner(this, Sm_Payment.class, Sm_Payment.Builder.class, Action.RESP_SYNC_ORDER, (b, br) -> {
            b.setOrderStatus(orderStatus);
            b.setInnerOrderId(innerOrderId);
        });
    }


    /**
     * 新订单充值成功通知
     *
     * @param innerOrderId
     * @param order
     * @param paymentRow
     */
    private void syncOrderToClientNewOrderSuccess(String innerOrderId, PaymentOrder order, Table_Payment_Row paymentRow) {
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        if (getPaymentIdBuyTimes(order.getGoodId()) == 1) { // 首次充值
            idMaptoCount.addAll(itemIoExtp.getControlerForUpdate(Action.RESP_SYNC_ORDER).addItem(new IdAndCount(ResourceTypeEnum.RES_VIPMONEY_VALUE, paymentRow.getFirstVipMoney())));
        }
        SenderFunc.sendInner(this, Sm_Payment.class, Sm_Payment.Builder.class, Action.RESP_SYNC_ORDER, (b, br) -> {
            b.setOrderStatus(order.getOrderStatus());
            b.setInnerOrderId(innerOrderId);
            b.addAllPaymentInfo(PaymentCtrlProtos.create_Sm_Payment_PaymentInfo_Lis(target.getPaymentIdToTimes()));
            itemIoCtrl.refreshItemAddToResponse(idMaptoCount, br);
        });
    }


    private void handleThirdPartyReturnMsg(Table_Payment_Row paymentRow, PaymentOrder order, String outerOrderId, String innerOrderId, boolean rs) {
        order.setOuterOrderId(outerOrderId);
        if (rs) {
            order.setOrderStatus(OrderStatusEnum.OD_SUCCESS);
        } else {
            order.setOrderStatus(OrderStatusEnum.OD_FAILED);
        }
        Date date = new Date();
        order.setEndDate(Integer.valueOf(WsDateUtils.dateToFormatStr(date, WsDateFormatEnum.yyyyMMdd)));
        order.setEndTime(Integer.valueOf(WsDateUtils.dateToFormatStr(date, WsDateFormatEnum.HHmmss)));
        PAYMENT_ORDER_DAO.insertIfExistThenReplace(order);
        if (!rs) { // 充值失败
            syncOrderToClient(innerOrderId, order.getOrderStatus());
            return;
        }
        // 充值成功
        target.setPayTs(target.getPayTs() + 1);
        addPaymentIdBuyTimes(order.getGoodId());
        getPlayerCtrl().addVipMoneyByRecharge(order.getVipMoneyCount(), Sm_Payment.Action.RESP_SYNC_ORDER);
        setMonthCardInfo(order.getVipMoneyCount());
        statisticsDaliyPay(order.getVipMoneyCount());
        syncOrderToClientNewOrderSuccess(innerOrderId, order, paymentRow);
    }

    /**
     * 增加商品购买的次数
     *
     * @param goodId
     */
    private void addPaymentIdBuyTimes(Integer goodId) {
        target.getPaymentIdToTimes().put(goodId, getPaymentIdBuyTimes(goodId) + 1);
    }


    /**
     * 获取商品购买的次数
     *
     * @param goodId
     * @return
     */
    private int getPaymentIdBuyTimes(Integer goodId) {
        if (!target.getPaymentIdToTimes().containsKey(goodId)) {
            target.getPaymentIdToTimes().put(goodId, 0);
        }
        return target.getPaymentIdToTimes().get(goodId);
    }


    /**
     * 设置月卡信息
     *
     * @param vipMoney
     */
    private void setMonthCardInfo(int vipMoney) {
        String today = GlobalInjector.getInstance(DayChanged.class).getDayChangedStr();
        tryOpenMonthCard(MonthCardTypeEnum.MT_250, vipMoney, today);
        tryOpenMonthCard(MonthCardTypeEnum.MT_880, vipMoney, today);
    }


    /**
     * 尝试开启月卡
     *
     * @param type
     * @param vipMoney
     * @param today
     */
    private void tryOpenMonthCard(MonthCardTypeEnum type, int vipMoney, String today) {
        int hasPay = getMonthCardHasPay(type);
        target.getTypeToHasPay().put(type, (hasPay + vipMoney));
        if (hasPay < type.getNumber()) {
            if (getMonthCardHasPay(type) >= type.getNumber()) {
                target.getTypeToStartDay().put(type, today);
            }
        }
    }

    private int getMonthCardHasPay(MonthCardTypeEnum type) {
        if (!target.getTypeToHasPay().containsKey(type)) {
            target.getTypeToHasPay().put(type, 0);
        }
        return target.getTypeToHasPay().get(type);
    }


    /**
     * 重置所有到期的月卡
     */
    private void resetAllMonthCardExpired() {
        resetMonthCardExpired(MonthCardTypeEnum.MT_250);
        resetMonthCardExpired(MonthCardTypeEnum.MT_880);
    }


    private void resetMonthCardExpired(MonthCardTypeEnum type) {
        // 月卡最后一天，未使用的情况，剩余1天
        MonthCardInfo cardInfo = calcuMonthCardRemainDays(type, false);
        if (cardInfo.getRemainDays() <= 0) {
            if (getMonthCardHasPay(type) >= type.getNumber()) { // 开启了月卡，剩余0天
                target.getTypeToHasPay().remove(type);
                target.getTypeToStartDay().remove(type);
            }
        }
    }


    /**
     * 统计每日充值的数量
     */
    private void statisticsDaliyPay(int vipMoney) {
        if (target.getPayTs() == MagicNumbers.DEFAULT_ONE) { // 首充
            sendPrivateMsg(new Pr_NotifyMsg(PrivateNotifyTypeEnum.Payment_FirstPay, MagicNumbers.DEFAULT_ONE));
        }
        sendPrivateMsg(new Pr_NotifyMsg(PrivateNotifyTypeEnum.Payment_DaliyPay, vipMoney));
    }

}
