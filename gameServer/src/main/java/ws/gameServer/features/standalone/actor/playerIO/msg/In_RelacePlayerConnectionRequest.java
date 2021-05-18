package ws.gameServer.features.standalone.actor.playerIO.msg;

import akka.actor.ActorRef;
import ws.common.utils.message.implement.AbstractInnerMsg;

public class In_RelacePlayerConnectionRequest extends AbstractInnerMsg {
    private static final long serialVersionUID = 1L;
    private String playerId;
    private String connFlag;
    private ActorRef gatewaySender;

    public In_RelacePlayerConnectionRequest(String playerId, String connFlag, ActorRef gatewaySender) {
        this.playerId = playerId;
        this.connFlag = connFlag;
        this.gatewaySender = gatewaySender;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getConnFlag() {
        return connFlag;
    }

    public ActorRef getGatewaySender() {
        return gatewaySender;
    }
}
