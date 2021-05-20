package ws.relationship.topLevelPojos.pyActivities;

import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lee on 17-6-21.
 */
public class PyActivities extends PlayerTopLevelPojo {
    private static final long serialVersionUID = -2151735830802167139L;

    private boolean hasBuyFund;                                                // 已经购买了基金
    private Map<Integer, PyGroupActDefault> raIdToDefault = new HashMap<>();   // [服活动Id]--组活动（默认）
    private List<Integer> specialRaIds = new ArrayList<>();                    // 特殊的[服活动Id]集合
    private List<Integer> hasGetRaIds = new ArrayList<>();                     // 已经领取的[服活动Id]集合


    public PyActivities() {
    }

    public PyActivities(String playerId) {
        super(playerId);
    }

    public boolean isHasBuyFund() {
        return hasBuyFund;
    }

    public void setHasBuyFund(boolean hasBuyFund) {
        this.hasBuyFund = hasBuyFund;
    }

    public Map<Integer, PyGroupActDefault> getRaIdToDefault() {
        return raIdToDefault;
    }

    public void setRaIdToDefault(Map<Integer, PyGroupActDefault> raIdToDefault) {
        this.raIdToDefault = raIdToDefault;
    }

    public List<Integer> getSpecialRaIds() {
        return specialRaIds;
    }

    public void setSpecialRaIds(List<Integer> specialRaIds) {
        this.specialRaIds = specialRaIds;
    }

    public List<Integer> getHasGetRaIds() {
        return hasGetRaIds;
    }

    public void setHasGetRaIds(List<Integer> hasGetRaIds) {
        this.hasGetRaIds = hasGetRaIds;
    }
}
