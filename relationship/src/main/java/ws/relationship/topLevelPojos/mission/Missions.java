package ws.relationship.topLevelPojos.mission;

import ws.protos.EnumsProtos.MissionTypeEnum;
import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务
 */
public class Missions extends PlayerTopLevelPojo {
    private static final long serialVersionUID = -1715699176114051557L;

    // Key1:MissionTypeEnum,Key2:任务Id
    private Map<MissionTypeEnum, Map<Integer, Mission>> typeToMissions = new HashMap<>(); // 零取的任务，尚未完成，亦未零取奖励

    public Missions() {
    }

    public Missions(String playerId) {
        super(playerId);
    }

    public Map<MissionTypeEnum, Map<Integer, Mission>> getTypeToMissions() {
        return typeToMissions;
    }

    public void setTypeToMissions(Map<MissionTypeEnum, Map<Integer, Mission>> typeToMissions) {
        this.typeToMissions = typeToMissions;
    }
}
