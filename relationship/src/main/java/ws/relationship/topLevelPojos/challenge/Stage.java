package ws.relationship.topLevelPojos.challenge;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lee on 17-3-28.
 */
public class Stage implements Serializable {

    private static final long serialVersionUID = -3871184599109856276L;
    private Map<Integer, Boolean> openStageIdAndWin = new HashMap<>();         //已经开启的关卡,对应是否通关
    private int attackTimes;                                                   //每日挑战的次数 --每日重置
    private long cdTime;                                                       //冷却时间

    public Stage() {
    }


    public Map<Integer, Boolean> getOpenStageIdAndWin() {
        return openStageIdAndWin;
    }

    public void setOpenStageIdAndWin(Map<Integer, Boolean> openStageIdAndWin) {
        this.openStageIdAndWin = openStageIdAndWin;
    }

    public int getAttackTimes() {
        return attackTimes;
    }

    public void setAttackTimes(int attackTimes) {
        this.attackTimes = attackTimes;
    }

    public long getCdTime() {
        return cdTime;
    }

    public void setCdTime(long cdTime) {
        this.cdTime = cdTime;
    }
}
