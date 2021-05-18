package ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.itemBag;

import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.extp.itemBag.ItemBagExtp;
import ws.gameServer.features.standalone.extp.itemBag.ctrl.ItemBagCtrl;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.CanAddOrCanReduceAction;
import ws.relationship.base.IdMaptoCount;

public class CanRemoveFromItemBag implements CanAddOrCanReduceAction {
    @Override
    public boolean can(PlayerCtrl playerCtrl, IdMaptoCount idMaptoCount) throws Exception {
        ItemBagExtp itemBagExtp = playerCtrl.getExtension(ItemBagExtp.class);
        ItemBagCtrl itemBagCtrl = itemBagExtp.getControlerForQuery();
        return itemBagCtrl.canRemoveItem(idMaptoCount);
    }
}
