package ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.heros;

import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.extp.heros.HerosExtp;
import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.RefreshAction;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.base.IdMaptoCount;

public class RefreshHeros implements RefreshAction {
    @Override
    public void refresh(PlayerCtrl playerCtrl, IdMaptoCount idMaptoCount, Response.Builder br) throws Exception {
        HerosExtp herosExtp = playerCtrl.getExtension(HerosExtp.class);
        HerosCtrl herosCtrl = herosExtp.getControlerForQuery();
        if (br == null) {
            herosCtrl.refreshItem(idMaptoCount);
        } else {
            herosCtrl.refreshItemAddToResponse(idMaptoCount, br);
        }
    }
}
