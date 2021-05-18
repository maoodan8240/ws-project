package ws.relationship.topLevelPojos.pyActivities;

import java.io.Serializable;

/**
 * Created by zhangweiwei on 17-6-21.
 */
public class PySubAct implements Serializable {
    private static final long serialVersionUID = -2865592894667699993L;

    private int subAcId;     // 子活动Id
    private int exchangeTs;  // 已经兑换的次数
    private long value;      // 已经完成的值 OR 结束时间-(体力任务使用)
    private boolean get;     // 已经领取过奖励

    public PySubAct() {
    }

    public PySubAct(int subAcId) {
        this.subAcId = subAcId;
    }

    public int getSubAcId() {
        return subAcId;
    }

    public void setSubAcId(int subAcId) {
        this.subAcId = subAcId;
    }

    public int getExchangeTs() {
        return exchangeTs;
    }

    public void setExchangeTs(int exchangeTs) {
        this.exchangeTs = exchangeTs;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public boolean isGet() {
        return get;
    }

    public void setGet(boolean get) {
        this.get = get;
    }
}
