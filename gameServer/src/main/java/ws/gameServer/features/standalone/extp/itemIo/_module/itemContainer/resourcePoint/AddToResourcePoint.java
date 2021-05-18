package ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.resourcePoint;

import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.AddOrReduceAction;
import ws.gameServer.features.standalone.extp.resourcePoint.ResourcePointExtp;
import ws.gameServer.features.standalone.extp.resourcePoint.ctrl.ResourcePointCtrl;
import ws.gameServer.features.standalone.utils.LogHandler;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.enums.ItemOpEnum;

public class AddToResourcePoint implements AddOrReduceAction {
    @Override
    public IdMaptoCount execute(PlayerCtrl playerCtrl, IdMaptoCount idMaptoCount, Enum<?> caller) {
        ResourcePointExtp resourcePointExtp = playerCtrl.getExtension(ResourcePointExtp.class);
        ResourcePointCtrl resourcePointCtrl = resourcePointExtp.getControlerForUpdate(caller);
        LogHandler.itemLog(playerCtrl.getTarget(), caller, idMaptoCount, ItemOpEnum.ADD);
        return resourcePointCtrl.addResourcePoint(idMaptoCount);
    }
}