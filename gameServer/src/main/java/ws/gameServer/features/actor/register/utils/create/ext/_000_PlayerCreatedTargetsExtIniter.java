package ws.gameServer.features.actor.register.utils.create.ext;

import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.relationship.topLevelPojos.common.PlayerCreatedTargets;

public class _000_PlayerCreatedTargetsExtIniter implements ExtensionIniter {
    @Override
    public void init(ExtCommonData commonData) throws Exception {
        PlayerCreatedTargets createdTargets = new PlayerCreatedTargets(commonData.getPlayer().getPlayerId());
        commonData.setCreatedTargets(createdTargets);
        commonData.addPojo(createdTargets);
    }
}
