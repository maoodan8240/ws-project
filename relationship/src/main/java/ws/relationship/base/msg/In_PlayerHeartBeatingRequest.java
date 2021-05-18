package ws.relationship.base.msg;

public class In_PlayerHeartBeatingRequest extends AbstractGateWayServerInnerMsg {
    private static final long serialVersionUID = 1L;

    public In_PlayerHeartBeatingRequest(String connFlag, String playerId) {
        super(connFlag, playerId);
    }

}
