package ws.gameServer.features.actor.register.utils.create.ext;


import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.relationship.topLevelPojos.pyActivities.PyActivities;

public class _595_PyActivitiesExtIniter implements ExtensionIniter {
    @Override
    public void init(ExtCommonData commonData) throws Exception {
        PyActivities pyActivities = new PyActivities();
        pyActivities.setPlayerId(commonData.getPlayer().getPlayerId());
        commonData.addPojo(pyActivities);
    }
}
