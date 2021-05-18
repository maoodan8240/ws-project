package ws.relationship.topLevelPojos.common;

import ws.relationship.base.WsCloneable;

import java.io.Serializable;

/**
 * Created by zhangweiwei on 16-10-8..
 */
public class LevelUpObj implements Serializable, WsCloneable<LevelUpObj> {
    private static final long serialVersionUID = 2590615314049641662L;

    private int level;
    private long ovfExp;

    public LevelUpObj() {
    }

    public LevelUpObj(int level, long ovfExp) {
        this.level = level;
        this.ovfExp = ovfExp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getOvfExp() {
        return ovfExp;
    }

    public void setOvfExp(long ovfExp) {
        this.ovfExp = ovfExp;
    }

    @Override
    public LevelUpObj clone() {
        return new LevelUpObj(this.level, this.ovfExp);
    }
}
