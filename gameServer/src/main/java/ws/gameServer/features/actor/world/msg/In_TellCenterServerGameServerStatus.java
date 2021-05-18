package ws.gameServer.features.actor.world.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;

public class In_TellCenterServerGameServerStatus extends AbstractInnerMsg {
    private static final long serialVersionUID = 629404575885323777L;
    private int sumOnline = 0;
    private int sumOffline = 0;

    public In_TellCenterServerGameServerStatus(int sumOnline, int sumOffline) {
        this.sumOnline = sumOnline;
        this.sumOffline = sumOffline;
    }

    public int getSumOnline() {
        return sumOnline;
    }

    public int getSumOffline() {
        return sumOffline;
    }

    public int getSumOnlineAndOffline() {
        return sumOnline + sumOffline;
    }
}
