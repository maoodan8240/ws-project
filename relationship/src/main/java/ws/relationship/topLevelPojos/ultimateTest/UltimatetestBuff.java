package ws.relationship.topLevelPojos.ultimateTest;

import java.io.Serializable;

/**
 * Created by lee on 17-4-1.
 */
public class UltimatetestBuff implements Serializable {
    private static final long serialVersionUID = -7327406551156894484L;

    private int buffId;
    private int consum;

    public UltimatetestBuff() {
    }

    public UltimatetestBuff(int buffId, int consum) {
        this.buffId = buffId;
        this.consum = consum;
    }

    public int getBuffId() {
        return buffId;
    }

    public void setBuffId(int buffId) {
        this.buffId = buffId;
    }

    public int getConsum() {
        return consum;
    }

    public void setConsum(int consum) {
        this.consum = consum;
    }
}
