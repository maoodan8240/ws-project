package ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.itemBag;

import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.extp.itemBag.ItemBagExtp;
import ws.gameServer.features.standalone.extp.itemBag.ctrl.ItemBagCtrl;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.AddOrReduceAction;
import ws.gameServer.features.standalone.utils.LogHandler;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.enums.ItemOpEnum;

public class AddToItemBag implements AddOrReduceAction {
    @Override
    public IdMaptoCount execute(PlayerCtrl playerCtrl, IdMaptoCount idMaptoCount, Enum<?> caller) throws Exception {
        ItemBagExtp itemBagExtp = playerCtrl.getExtension(ItemBagExtp.class);
        ItemBagCtrl itemBagCtrl = itemBagExtp.getControlerForUpdate(caller);
        LogHandler.itemLog(playerCtrl.getTarget(), caller, idMaptoCount, ItemOpEnum.ADD);
        return itemBagCtrl.addItem(idMaptoCount);
    }
}