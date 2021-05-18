package ws.relationship.topLevelPojos.newGuildCenter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lee on 16-11-30.
 */
public class NewGuildWorldCenter implements Serializable {
    private static final long serialVersionUID = 905886556595324860L;

    private Map<Integer, NewGuildCenter> outRealmIdToGuildCenter = new HashMap<>();


    public Map<Integer, NewGuildCenter> getOutRealmIdToGuildCenter() {
        return outRealmIdToGuildCenter;
    }

    public void setOutRealmIdToGuildCenter(Map<Integer, NewGuildCenter> outRealmIdToGuildCenter) {
        this.outRealmIdToGuildCenter = outRealmIdToGuildCenter;
    }
}
