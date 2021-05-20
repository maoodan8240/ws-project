package ws.relationship.topLevelPojos.pyActivities;

import java.io.Serializable;

/**
 * Created by lee on 17-6-21.
 */
public abstract class PyGroupAct implements Serializable {
    private static final long serialVersionUID = -3739877606022746753L;

    private int realmAcId;      // 服活动Id
    private int groupAcId;      // 组活动Id

    public PyGroupAct() {
    }

    public PyGroupAct(int realmAcId, int groupAcId) {
        this.realmAcId = realmAcId;
        this.groupAcId = groupAcId;
    }

    public int getRealmAcId() {
        return realmAcId;
    }

    public void setRealmAcId(int realmAcId) {
        this.realmAcId = realmAcId;
    }

    public int getGroupAcId() {
        return groupAcId;
    }

    public void setGroupAcId(int groupAcId) {
        this.groupAcId = groupAcId;
    }
}
