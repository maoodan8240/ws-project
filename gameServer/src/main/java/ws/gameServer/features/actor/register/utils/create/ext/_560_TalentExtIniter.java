package ws.gameServer.features.actor.register.utils.create.ext;


import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.relationship.topLevelPojos.talent.Talent;

//NewPve
public class _560_TalentExtIniter implements ExtensionIniter {
    @Override
    public void init(ExtCommonData commonData) throws Exception {
        Talent talent = new Talent();
        talent.setPlayerId(commonData.getPlayer().getPlayerId());
        commonData.addPojo(talent);
    }
}
