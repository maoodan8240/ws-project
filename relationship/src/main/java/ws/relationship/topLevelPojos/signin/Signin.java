package ws.relationship.topLevelPojos.signin;

import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

/**
 * Created by lee on 17-4-13.
 */
public class Signin extends PlayerTopLevelPojo {
    private static final long serialVersionUID = -4680009964059978142L;

    private String lastSignDate;                                                            // 上一次的签到日期
    private boolean isSignin;                                                               // 今日是否签到 --每日重置为false (每日24点重置)
    private int vipLv;                                                                      // 今日签到时的Vip等级
    private int month;                                                                      // 月份
    private int allSigninTimes;                                                             // 累计签到次数
    private int monthSigninTimes;                                                           // 当月签到次数
    private int cumulatvieGift;                                                             // 累计领取累计奖励的次数

    public Signin() {
    }

    public Signin(String playerId) {
        super(playerId);
    }

    public boolean isSignin() {
        return isSignin;
    }

    public void setSignin(boolean isSignin) {
        this.isSignin = isSignin;
    }

    public int getMonthSigninTimes() {
        return monthSigninTimes;
    }

    public void setMonthSigninTimes(int monthSigninTimes) {
        this.monthSigninTimes = monthSigninTimes;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getAllSigninTimes() {
        return allSigninTimes;
    }

    public void setAllSigninTimes(int allSigninTimes) {
        this.allSigninTimes = allSigninTimes;
    }

    public int getCumulatvieGift() {
        return cumulatvieGift;
    }

    public void setCumulatvieGift(int cumulatvieGift) {
        this.cumulatvieGift = cumulatvieGift;
    }

    public String getLastSignDate() {
        return lastSignDate;
    }

    public void setLastSignDate(String lastSignDate) {
        this.lastSignDate = lastSignDate;
    }

    public int getVipLv() {
        return vipLv;
    }

    public void setVipLv(int vipLv) {
        this.vipLv = vipLv;
    }
}

