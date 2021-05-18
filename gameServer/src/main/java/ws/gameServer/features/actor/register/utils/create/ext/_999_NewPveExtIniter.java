package ws.gameServer.features.actor.register.utils.create.ext;


import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.relationship.topLevelPojos.newPve.NewPve;

//NewPve
public class _999_NewPveExtIniter implements ExtensionIniter {
    @Override
    public void init(ExtCommonData commonData) throws Exception {
        NewPve pve = new NewPve();
        pve.setPlayerId(commonData.getPlayer().getPlayerId());
        commonData.addPojo(pve);
    }
}
