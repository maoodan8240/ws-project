package ws.gameServer.features.standalone.extp.payment.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.gameServer.features.standalone.extp.payment.utils.MonthCardInfo;
import ws.protos.EnumsProtos.MonthCardTypeEnum;
import ws.protos.PaymentProtos.Cm_Payment_SyncOrder;
import ws.relationship.appServers.thirdPartyServer.In_ProcessPayment;
import ws.relationship.topLevelPojos.payment.Payment;

public interface PaymentCtrl extends PlayerExteControler<Payment> {


    /**
     * 同步订单
     * 同步未知状态的订单
     * 客户端发送已经支付完成但是服务器未同步状态的订单
     *
     * @param syncOrder
     */
    void syncOrder(Cm_Payment_SyncOrder syncOrder);


    /**
     * 购买vip礼包
     *
     * @param vipLv
     */
    void buyVipGift(int vipLv);

    /**
     * 创建支付订单
     */
    void create(int goodId, String args);

    /**
     * 第三方主动推送的支付信息
     *
     * @param request
     */
    void onProcessPayment(In_ProcessPayment.Request request);


    /**
     * 月卡剩余的天数，包括当天
     *
     * @param type
     * @param hasReceiveToday 今日是否已经领取离奖励
     * @return
     */
    MonthCardInfo calcuMonthCardRemainDays(MonthCardTypeEnum type, boolean hasReceiveToday);
}
