package ws.gameServer.features.actor.register.utils.create.ext;


import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.relationship.topLevelPojos.friends.Friends;

public class _580_FriendsExtIniter implements ExtensionIniter {
    @Override
    public void init(ExtCommonData commonData) throws Exception {
        Friends friends = new Friends();
        friends.setPlayerId(commonData.getPlayer().getPlayerId());
        commonData.addPojo(friends);
    }
}
