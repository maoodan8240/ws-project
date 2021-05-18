package ws.gameServer.features.standalone.extp.payment.utils;

import ws.protos.PaymentProtos.Sm_Payment_Order;
import ws.protos.PaymentProtos.Sm_Payment_PaymentInfo;
import ws.relationship.topLevelPojos.paymentOrder.PaymentOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PaymentCtrlProtos {

    public static Sm_Payment_Order create_Sm_Payment_Order(PaymentOrder order) {
        Sm_Payment_Order.Builder b = Sm_Payment_Order.newBuilder();
        b.setInnerOrderId(order.getInnerOrderId());
        b.setGoodId(order.getGoodId());
        b.setGoodName(order.getGoodName());
        b.setVipMoneyCount(order.getVipMoneyCount());
        b.setRmb(order.getRmb());
        b.setPlatformType(order.getPlatformType());
        b.setArgs(order.getArgs());
        b.setStatus(order.getOrderStatus());
        return b.build();
    }


    public static List<Sm_Payment_PaymentInfo> create_Sm_Payment_PaymentInfo_Lis(Map<Integer, Integer> paymentIdToTimes) {
        List<Sm_Payment_PaymentInfo> bs = new ArrayList<>();
        Sm_Payment_PaymentInfo.Builder b = Sm_Payment_PaymentInfo.newBuilder();
        for (Entry<Integer, Integer> kv : paymentIdToTimes.entrySet()) {
            b.clear();
            b.setPaymentId(kv.getKey());
            b.setTimes(kv.getValue());
            bs.add(b.build());
        }
        return bs;
    }
}
