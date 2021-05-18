package ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.heros;

import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.extp.heros.HerosExtp;
import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.CanAddOrCanReduceAction;
import ws.relationship.base.IdMaptoCount;

public class CanRemoveFromHeros implements CanAddOrCanReduceAction {
    @Override
    public boolean can(PlayerCtrl playerCtrl, IdMaptoCount idMaptoCount) throws Exception {
        HerosExtp herosExtp = playerCtrl.getExtension(HerosExtp.class);
        HerosCtrl herosCtrl = herosExtp.getControlerForQuery();
        return herosCtrl.canRemoveHeros(idMaptoCount);
    }
}
