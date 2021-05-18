package ws.gameServer.features.actor.world.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.relationship.base.ServerRoleEnum;

public class In_SyncGameServerStatus extends AbstractInnerMsg {
    private static final long serialVersionUID = 1L;
    private ServerRoleEnum toServerRoleEnum;

    public In_SyncGameServerStatus(ServerRoleEnum toServerRoleEnum) {
        this.toServerRoleEnum = toServerRoleEnum;
    }

    public ServerRoleEnum getToServerRoleEnum() {
        return toServerRoleEnum;
    }
}
