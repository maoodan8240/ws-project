package ws.relationship.topLevelPojos.newGuildCenter;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by lee on 5/4/17.
 */
public class NewGuildApplyInfo implements Serializable {
    private static final long serialVersionUID = -2027561832671860044L;
    private String playerId;
    private long applyTime;
    @JSONField(serialize = false)
    private long battleValue;
    @JSONField(serialize = false)
    private int lv;

    public NewGuildApplyInfo() {
    }

    public NewGuildApplyInfo(String playerId, long applyTime) {
        this.playerId = playerId;
        this.applyTime = applyTime;
    }


    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public long getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(long applyTime) {
        this.applyTime = applyTime;
    }

    public long getBattleValue() {
        return battleValue;
    }

    public void setBattleValue(long battleValue) {
        this.battleValue = battleValue;
    }

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        this.lv = lv;
    }
}
