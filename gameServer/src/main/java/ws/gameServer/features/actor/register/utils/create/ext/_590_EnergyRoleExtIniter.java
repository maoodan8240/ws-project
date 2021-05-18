package ws.gameServer.features.actor.register.utils.create.ext;


import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.relationship.topLevelPojos.energyRole.EnergyRole;

public class _590_EnergyRoleExtIniter implements ExtensionIniter {
    @Override
    public void init(ExtCommonData commonData) throws Exception {
        EnergyRole energyRole = new EnergyRole();
        energyRole.setPlayerId(commonData.getPlayer().getPlayerId());
        commonData.addPojo(energyRole);
    }
}
