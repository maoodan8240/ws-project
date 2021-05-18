package ws.gameServer.features.actor.register.utils.create.ext;


import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.relationship.topLevelPojos.challenge.Challenge;

//NewPve
public class _999_ChallengeExtIniter implements ExtensionIniter {
    @Override
    public void init(ExtCommonData commonData) throws Exception {
        Challenge challenge = new Challenge();
        challenge.setPlayerId(commonData.getPlayer().getPlayerId());
        commonData.addPojo(challenge);
    }
}
