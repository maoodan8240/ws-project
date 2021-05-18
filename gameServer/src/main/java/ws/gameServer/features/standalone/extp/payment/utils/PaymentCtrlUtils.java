package ws.gameServer.features.standalone.extp.payment.utils;

import org.bson.types.ObjectId;
import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.common.utils.general.UuidGenerator;
import ws.relationship.table.tableRows.Table_Payment_Row;
import ws.relationship.topLevelPojos.paymentOrder.PaymentOrder;
import ws.relationship.topLevelPojos.player.Player;

import java.util.Date;

public class PaymentCtrlUtils {


    /**
     * 创建新的订单
     *
     * @param player
     * @param args
     * @param paymentRow
     * @return
     */
    public static PaymentOrder createNewOrder(Player player, String args, Table_Payment_Row paymentRow) {
        PaymentOrder order = new PaymentOrder();
        String id = ObjectId.get().toString();
        String innerOrderId = UuidGenerator.newUuid();
        order.setOrderId(id);
        order.setInnerOrderId(innerOrderId);
        order.setPlayerId(player.getPlayerId());
        order.setSimpleId(player.getBase().getSimpleId());
        order.setOuterRealmId(player.getAccount().getOuterRealmId());
        order.setGoodId(paymentRow.getId());
        order.setGoodName(paymentRow.getGoodName());
        order.setVipMoneyCount(paymentRow.getVipMoneyCount());
        order.setRmb(paymentRow.getRmbPrice());
        order.setPlatformType(paymentRow.getPlatformType());
        order.setArgs(args);
        Date createDate = WsDateUtils.dateToFormatDate(player.getAccount().getCreateAt(), WsDateFormatEnum.yyyy_MM_dd$HH_mm_ss);
        order.setPyCreateAtDate(Integer.valueOf(WsDateUtils.dateToFormatStr(createDate, WsDateFormatEnum.yyyyMMdd)));
        order.setPyCreateAtTime(Integer.valueOf(WsDateUtils.dateToFormatStr(createDate, WsDateFormatEnum.HHmmss)));
        return order;
    }
}
