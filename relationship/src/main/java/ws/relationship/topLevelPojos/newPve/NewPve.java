package ws.relationship.topLevelPojos.newPve;

import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

import java.util.LinkedHashMap;

/**
 * Created by leetony on 16-11-3.
 */
public class NewPve extends PlayerTopLevelPojo {
    private static final long serialVersionUID = -6438522937529376215L;
    /**
     * 已经开启的副本
     */
    private LinkedHashMap<Integer, Chapter> idToChapter = new LinkedHashMap<>();
    /**
     * 最后一次挑战的关卡Id
     * 如果再次挑战的ID以这个不同,表示玩家后台杀死了游戏,重新挑战了其他的关卡,连同lastAttackTime一起重置
     */
    private int lastAttackStageId = -1;

    /**
     * 最后一次挑战的时间
     */
    private long lastAttackTime = -1;


    private String lastResetDay = "0";// 重置的最后日期数

    public NewPve() {
    }

    public NewPve(String playerId) {
        super(playerId);
    }

    public LinkedHashMap<Integer, Chapter> getIdToChapter() {
        return idToChapter;
    }

    public void setIdToChapter(LinkedHashMap<Integer, Chapter> idToChapter) {
        this.idToChapter = idToChapter;
    }

    public int getLastAttackStageId() {
        return lastAttackStageId;
    }

    public void setLastAttackStageId(int lastAttackStageId) {
        this.lastAttackStageId = lastAttackStageId;
    }

    public long getLastAttackTime() {
        return lastAttackTime;
    }

    public void setLastAttackTime(long lastAttackTime) {
        this.lastAttackTime = lastAttackTime;
    }

    @Override
    public String getLastResetDay() {
        return lastResetDay;
    }

    @Override
    public void setLastResetDay(String lastResetDay) {
        this.lastResetDay = lastResetDay;
    }
}
