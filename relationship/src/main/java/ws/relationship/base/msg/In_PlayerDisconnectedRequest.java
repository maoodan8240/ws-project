package ws.relationship.base.msg;

public class In_PlayerDisconnectedRequest extends AbstractGateWayServerInnerMsg {
    private static final long serialVersionUID = 1L;

    public In_PlayerDisconnectedRequest(String connFlag, String playerId) {
        super(connFlag, playerId);
    }

}
