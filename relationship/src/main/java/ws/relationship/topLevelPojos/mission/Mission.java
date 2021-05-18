package ws.relationship.topLevelPojos.mission;

import java.io.Serializable;

public class Mission implements Serializable {
    private static final long serialVersionUID = -7309063356616212219L;

    private int mid;         // 任务Id
    private long value;      // 已经完成的值 OR 结束时间-(体力任务使用)
    private boolean get;     // 已经领取过奖励
    private int remainDays;  // 月卡字段-剩余天数

    public Mission() {
    }

    public Mission(int mid, long value) {
        this.mid = mid;
        this.value = value;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
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

    public int getRemainDays() {
        return remainDays;
    }

    public void setRemainDays(int remainDays) {
        this.remainDays = remainDays;
    }
}
