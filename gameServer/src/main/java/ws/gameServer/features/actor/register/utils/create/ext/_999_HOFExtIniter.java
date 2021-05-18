package ws.gameServer.features.actor.register.utils.create.ext;


import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.relationship.topLevelPojos.hof.HOF;

//Pve
public class _999_HOFExtIniter implements ExtensionIniter {
    @Override
    public void init(ExtCommonData commonData) throws Exception {
        HOF hof = new HOF(commonData.getPlayer().getPlayerId());

        commonData.addPojo(hof);
    }
}
