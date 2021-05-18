package ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.resourcePoint;

import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.RefreshAction;
import ws.gameServer.features.standalone.extp.resourcePoint.ResourcePointExtp;
import ws.gameServer.features.standalone.extp.resourcePoint.ctrl.ResourcePointCtrl;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.base.IdMaptoCount;

public class RefreshResourcePoint implements RefreshAction {
    @Override
    public void refresh(PlayerCtrl playerCtrl, IdMaptoCount idMaptoCount, Response.Builder br) throws Exception {
        ResourcePointExtp resourcePointExtp = playerCtrl.getExtension(ResourcePointExtp.class);
        ResourcePointCtrl resourcePointCtrl = resourcePointExtp.getControlerForQuery();
        if (br == null) {
            resourcePointCtrl.sync();
        } else {
            resourcePointCtrl.refreshItemAddToResponse(null, br);
        }
    }
}
