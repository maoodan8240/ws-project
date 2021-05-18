package ws.gameServer.features.standalone.extp.payment.utils;

/**
 * Created by zhangweiwei on 17-5-25.
 */
public class MonthCardInfo {
    private int hasPay;
    private int remainDays;

    public MonthCardInfo(int hasPay, int remainDays) {
        this.hasPay = hasPay;
        this.remainDays = remainDays;
    }

    public int getHasPay() {
        return hasPay;
    }

    public int getRemainDays() {
        return remainDays;
    }
}
