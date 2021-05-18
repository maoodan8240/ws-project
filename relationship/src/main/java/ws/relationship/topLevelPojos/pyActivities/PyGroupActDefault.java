package ws.relationship.topLevelPojos.pyActivities;

import java.util.LinkedHashMap;

/**
 * Created by zhangweiwei on 17-6-21.
 */
public class PyGroupActDefault extends PyGroupAct {
    private static final long serialVersionUID = 5930293274062284680L;
    private LinkedHashMap<Integer, PySubAct> idToSubAct = new LinkedHashMap<>();    // 子活动Id -- 子活动

    public PyGroupActDefault() {
    }

    public PyGroupActDefault(int realmAcId, int groupAcId) {
        super(realmAcId, groupAcId);
    }

    public LinkedHashMap<Integer, PySubAct> getIdToSubAct() {
        return idToSubAct;
    }

    public void setIdToSubAct(LinkedHashMap<Integer, PySubAct> idToSubAct) {
        this.idToSubAct = idToSubAct;
    }
}
