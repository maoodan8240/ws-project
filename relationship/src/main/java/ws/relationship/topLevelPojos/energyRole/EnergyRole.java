package ws.relationship.topLevelPojos.energyRole;

import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

/**
 * Created by lee on 17-4-12.
 */
public class EnergyRole extends PlayerTopLevelPojo {
    private static final long serialVersionUID = -692998520146291101L;

    private int hasBuyEnergyTs;                                 // 已经购买体力的次数
    private long lastUpEneryTime = System.currentTimeMillis();  // 最后结算体力的时间点

    public EnergyRole() {
    }

    public EnergyRole(String playerId) {
        super(playerId);
    }


    public int getHasBuyEnergyTs() {
        return hasBuyEnergyTs;
    }

    public void setHasBuyEnergyTs(int hasBuyEnergyTs) {
        this.hasBuyEnergyTs = hasBuyEnergyTs;
    }

    public long getLastUpEneryTime() {
        return lastUpEneryTime;
    }

    public void setLastUpEneryTime(long lastUpEneryTime) {
        this.lastUpEneryTime = lastUpEneryTime;
    }
}
