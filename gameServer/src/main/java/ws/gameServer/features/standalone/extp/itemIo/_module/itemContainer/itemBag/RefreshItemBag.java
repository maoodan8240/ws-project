package ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.itemBag;

import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.extp.itemBag.ItemBagExtp;
import ws.gameServer.features.standalone.extp.itemBag.ctrl.ItemBagCtrl;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.RefreshAction;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.base.IdMaptoCount;

public class RefreshItemBag implements RefreshAction {
    @Override
    public void refresh(PlayerCtrl playerCtrl, IdMaptoCount idMaptoCount, Response.Builder br) throws Exception {
        ItemBagExtp itemBagExtp = playerCtrl.getExtension(ItemBagExtp.class);
        ItemBagCtrl itemBagCtrl = itemBagExtp.getControlerForQuery();
        if (br == null) {
            itemBagCtrl.refreshItem(idMaptoCount);
        } else {
            itemBagCtrl.refreshItemAddToResponse(idMaptoCount, br);
        }
    }
}
