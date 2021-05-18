package ws.gameServer.features.actor.register.utils.create.ext;


import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.relationship.topLevelPojos.pvp.arena.Arena;

//竞技场
public class _999_ArenaExtIniter implements ExtensionIniter {
    @Override
    public void init(ExtCommonData commonData) throws Exception {
        Arena arena = new Arena(commonData.getPlayer().getPlayerId());
        commonData.addPojo(arena);
    }
}
