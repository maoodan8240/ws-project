package ws.relationship.topLevelPojos.itemBag;

import ws.protos.ItemBagProtos.Sm_ItemBag_SpecialItemExtInfo.KeyType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SpecialCell implements Serializable {
    private static final long serialVersionUID = 1615408701968670732L;

    private int id;
    private int tpId;
    private boolean useCell;
    private Map<KeyType, String> extInfo = new HashMap<>();

    public SpecialCell() {
    }

    public SpecialCell(int id, int tpId, boolean useCell) {
        this.id = id;
        this.tpId = tpId;
        this.useCell = useCell;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTpId() {
        return tpId;
    }

    public void setTpId(int tpId) {
        this.tpId = tpId;
    }

    public boolean isUseCell() {
        return useCell;
    }

    public void setUseCell(boolean useCell) {
        this.useCell = useCell;
    }

    public Map<KeyType, String> getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(Map<KeyType, String> extInfo) {
        this.extInfo = extInfo;
    }
}
