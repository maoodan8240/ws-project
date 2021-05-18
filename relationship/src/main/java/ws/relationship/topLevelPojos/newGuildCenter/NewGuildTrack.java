package ws.relationship.topLevelPojos.newGuildCenter;

import ws.relationship.base.WsCloneable;
import ws.relationship.utils.CloneUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 16-11-30.
 */
public class NewGuildTrack implements Serializable, WsCloneable {
    private static final long serialVersionUID = -752496961400354590L;

    private long time;
    private List<String> args = new ArrayList<>();
    private int tpId;

    public int getTpId() {
        return tpId;
    }

    public void setTpId(int tpId) {
        this.tpId = tpId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }


    public NewGuildTrack() {
    }

    /**
     * for clone
     * @param time
     * @param args
     * @param tpId
     */
    public NewGuildTrack(long time, List<String> args, int tpId) {
        this.time = time;
        this.args = args;
        this.tpId = tpId;
    }

    @Override
    public NewGuildTrack clone() {
        return new NewGuildTrack(time, CloneUtils.cloneStringList(args), tpId);
    }
}
