package ws.gameServer.features.standalone.extp.resourcePoint.msg;

import ws.common.utils.message.implement.AbstractPrivateMsg;

/**
 * Created by lee on 17-5-22.
 */
public class Pr_EnergyValueChanged extends AbstractPrivateMsg {
    private static final long serialVersionUID = 3594882576716088378L;
    private long before;
    private long now;

    public Pr_EnergyValueChanged(long before, long now) {
        this.before = before;
        this.now = now;
    }

    public long getBefore() {
        return before;
    }

    public long getNow() {
        return now;
    }
}
