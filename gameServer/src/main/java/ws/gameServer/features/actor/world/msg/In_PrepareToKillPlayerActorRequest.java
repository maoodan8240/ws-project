package ws.gameServer.features.actor.world.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;

public class In_PrepareToKillPlayerActorRequest extends AbstractInnerMsg {
    private static final long serialVersionUID = 1L;
    private String targetPlayerId;

    public In_PrepareToKillPlayerActorRequest(String targetPlayerId) {
        this.targetPlayerId = targetPlayerId;
    }

    public String getTargetPlayerId() {
        return targetPlayerId;
    }
}
