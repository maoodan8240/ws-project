package ws.gameServer.features.actor.world.pojo;

import ws.gameServer.features.actor.world.playerIOStatus.PlayerIOStatus;

import java.util.HashMap;
import java.util.Map;

public class World {
    // 玩家id
    // 玩家PlayerIO
    private Map<String, PlayerIOStatus> playerIdToIOStatus = new HashMap<>();

    public Map<String, PlayerIOStatus> getPlayerIdToIOStatus() {
        return playerIdToIOStatus;
    }
}
