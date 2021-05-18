package ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.resourcePoint;

import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.CanAddOrCanReduceAction;
import ws.gameServer.features.standalone.extp.resourcePoint.ResourcePointExtp;
import ws.gameServer.features.standalone.extp.resourcePoint.ctrl.ResourcePointCtrl;
import ws.relationship.base.IdMaptoCount;

public class CanRemoveFromResourcePoint implements CanAddOrCanReduceAction {
    @Override
    public boolean can(PlayerCtrl playerCtrl, IdMaptoCount idMaptoCount) {
        ResourcePointExtp resourcePointExtp = playerCtrl.getExtension(ResourcePointExtp.class);
        ResourcePointCtrl resourcePointCtrl = resourcePointExtp.getControlerForQuery();
        return resourcePointCtrl.canReduceResourcePoint(idMaptoCount);
    }
}
