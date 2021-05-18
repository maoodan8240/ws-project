package ws.relationship.topLevelPojos.pickCards;

import java.io.Serializable;

public class PickCard implements Serializable {
    private static final long serialVersionUID = 7141231424862936238L;

    private int id;                  // pickCardId

    private int sum1Times;    // 历史(1连)抽取的次数     (免费/道具/金币/钻石抽取)
    private int sum10Times;   // 历史(10连)抽取的次数
    private int sum100Times;  // 历史(100连)抽取的总次数

    private int daliy1Times;    // 今日(1连)抽取的次数     (免费/道具/金币/钻石抽取)
    private int daliy10Times;   // 今日(10连)抽取的次数
    private int daliy100Times;  // 今日(100连)抽取的总次数

    private int useFreeTimes;    // 已经使用的免费抽奖次数
    private long nextFreeTime;   // 下次免费的

    public PickCard() {
    }

    public PickCard(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSum1Times() {
        return sum1Times;
    }

    public void setSum1Times(int sum1Times) {
        this.sum1Times = sum1Times;
    }

    public int getSum10Times() {
        return sum10Times;
    }

    public void setSum10Times(int sum10Times) {
        this.sum10Times = sum10Times;
    }

    public int getSum100Times() {
        return sum100Times;
    }

    public void setSum100Times(int sum100Times) {
        this.sum100Times = sum100Times;
    }

    public int getDaliy1Times() {
        return daliy1Times;
    }

    public void setDaliy1Times(int daliy1Times) {
        this.daliy1Times = daliy1Times;
    }

    public int getDaliy10Times() {
        return daliy10Times;
    }

    public void setDaliy10Times(int daliy10Times) {
        this.daliy10Times = daliy10Times;
    }

    public int getDaliy100Times() {
        return daliy100Times;
    }

    public void setDaliy100Times(int daliy100Times) {
        this.daliy100Times = daliy100Times;
    }

    public int getUseFreeTimes() {
        return useFreeTimes;
    }

    public void setUseFreeTimes(int useFreeTimes) {
        this.useFreeTimes = useFreeTimes;
    }

    public long getNextFreeTime() {
        return nextFreeTime;
    }

    public void setNextFreeTime(long nextFreeTime) {
        this.nextFreeTime = nextFreeTime;
    }
}
