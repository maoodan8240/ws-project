package ws.relationship.topLevelPojos.challenge;

import ws.protos.EnumsProtos.ChallengeTypeEnum;
import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by leetony on 16-11-8.
 */
public class Challenge extends PlayerTopLevelPojo {
    private static final long serialVersionUID = 8605256909390983938L;


    private Map<ChallengeTypeEnum, Stage> stageTypeToStage = new HashMap<>();                   // 关卡类型对应该类型关卡的信息
    private long lastAttackTime;                                                               // 正在挑战的开始时间
    private int lastAttackStageId;                                                             // 正在挑战关卡ID


    public Challenge() {
    }

    public Challenge(String playerId) {
        super(playerId);
    }


    public Map<ChallengeTypeEnum, Stage> getStageTypeToStage() {
        return stageTypeToStage;
    }

    public void setStageTypeToStage(Map<ChallengeTypeEnum, Stage> stageTypeToStage) {
        this.stageTypeToStage = stageTypeToStage;
    }

    public long getLastAttackTime() {
        return lastAttackTime;
    }

    public void setLastAttackTime(long lastAttackTime) {
        this.lastAttackTime = lastAttackTime;
    }

    public int getLastAttackStageId() {
        return lastAttackStageId;
    }

    public void setLastAttackStageId(int lastAttackStageId) {
        this.lastAttackStageId = lastAttackStageId;
    }
}
