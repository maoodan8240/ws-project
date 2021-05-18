package ws.gameServer.features.actor.world.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;

import java.util.List;

public class In_KickPlayersToOfflineRequest extends AbstractInnerMsg {
    private static final long serialVersionUID = 7824065874936922281L;
    private List<String> playerIds;

    public In_KickPlayersToOfflineRequest(List<String> playerIds) {
        this.playerIds = playerIds;
    }

    public List<String> getPlayerIds() {
        return playerIds;
    }
}
