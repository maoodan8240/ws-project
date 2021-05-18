package ws.gameServer.features.standalone.extp.energyRole.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.relationship.topLevelPojos.energyRole.EnergyRole;

public interface EnergyRoleCtrl extends PlayerExteControler<EnergyRole> {


    /**
     * 购买体力
     */
    void buyEnergy();


    /**
     * 结算自动增长的体力
     */
    void settleAutoIncrease();


    /**
     * 玩家升级体力达到最大值
     */
    void setMaxEnergyValueWhenPlayerLvUp();

    /**
     * 体力发生变化
     *
     * @param before
     * @param now
     */
    void onEnergyValueChanged(long before, long now);
}