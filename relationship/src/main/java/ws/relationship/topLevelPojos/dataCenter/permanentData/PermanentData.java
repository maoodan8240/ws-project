package ws.relationship.topLevelPojos.dataCenter.permanentData;

import ws.relationship.topLevelPojos.PlayerTopLevelPojo;
import ws.relationship.topLevelPojos.dataCenter.stageDaliyData.Data;

public class PermanentData extends PlayerTopLevelPojo {
    private static final long serialVersionUID = 8519078810590411267L;

    private Data data = new Data();

    public PermanentData() {
    }

    public PermanentData(String playerId) {
        super(playerId);
    }


    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
