package ws.relationship.topLevelPojos.newPve;

import java.io.Serializable;

/**
 * Created by leetony on 16-11-3.
 */
public class NewStage implements Serializable {


    private static final long serialVersionUID = -4354237018104852242L;
    private int stageId;
    /**
     * 今日挑战的次数(不会受到resetTimes影响) 每日回零 胜利失败都增加
     */
    private int dailySumAttackTimes;
    /**
     * 今日挑战的次数(会受到resetTimes影响)每日回零 胜利增加
     */
    private int dailyAttackTimes;
    /**
     * 今日重置的次数 每日回零
     */
    private int resetTimes;
    /**
     * 总挑战次数 胜利增加
     */
    private int sumAttackTimes;
    /**
     * 最大星数
     */
    private int maxStar;
    /**
     * 首次通关时间
     */
    private long firstPassTime;
    /**
     * 关卡后面的宝箱是否领取过
     */
    private boolean isOpenBox;

    public NewStage() {
    }

    public boolean isOpenBox() {
        return isOpenBox;
    }

    public void setOpenBox(boolean openBox) {
        isOpenBox = openBox;
    }

    public int getStageId() {
        return stageId;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    public int getDailySumAttackTimes() {
        return dailySumAttackTimes;
    }

    public void setDailySumAttackTimes(int dailySumAttackTimes) {
        this.dailySumAttackTimes = dailySumAttackTimes;
    }

    public int getDailyAttackTimes() {
        return dailyAttackTimes;
    }

    public void setDailyAttackTimes(int dailyAttackTimes) {
        this.dailyAttackTimes = dailyAttackTimes;
    }

    public int getResetTimes() {
        return resetTimes;
    }

    public void setResetTimes(int resetTimes) {
        this.resetTimes = resetTimes;
    }

    public int getSumAttackTimes() {
        return sumAttackTimes;
    }

    public void setSumAttackTimes(int sumAttackTimes) {
        this.sumAttackTimes = sumAttackTimes;
    }

    public int getMaxStar() {
        return maxStar;
    }

    public void setMaxStar(int maxStar) {
        this.maxStar = maxStar;
    }

    public long getFirstPassTime() {
        return firstPassTime;
    }

    public void setFirstPassTime(long firstPassTime) {
        this.firstPassTime = firstPassTime;
    }
}
