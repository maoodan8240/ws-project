package ws.gameServer.features.actor.register.utils.create.ext;


import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.relationship.topLevelPojos.ultimateTest.UltimateTest;

//NewPve
public class _999_UltimateTestExtIniter implements ExtensionIniter {
    @Override
    public void init(ExtCommonData commonData) throws Exception {
        UltimateTest ultimateTest = new UltimateTest();
        ultimateTest.setPlayerId(commonData.getPlayer().getPlayerId());
        commonData.addPojo(ultimateTest);
    }
}
