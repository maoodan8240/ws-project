package ws.relationship.topLevelPojos.hof;

import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lee on 17-2-6.
 */

//名人堂
public class HOF extends PlayerTopLevelPojo {
    private static final long serialVersionUID = 6313506625918128758L;

    /**
     * id对应名人堂信息
     */
    private Map<Integer, HOF_Info> idToHOFInfo = new HashMap<>();


    public HOF() {
    }

    public HOF(String playerId) {
        super(playerId);
    }

    public Map<Integer, HOF_Info> getIdToHOFInfo() {
        return idToHOFInfo;
    }

    public void setIdToHOFInfo(Map<Integer, HOF_Info> idToHOFInfo) {
        this.idToHOFInfo = idToHOFInfo;
    }
}
