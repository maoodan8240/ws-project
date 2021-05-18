package ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.heros;

import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.extp.heros.HerosExtp;
import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.AddOrReduceAction;
import ws.gameServer.features.standalone.utils.LogHandler;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.enums.ItemOpEnum;

public class RemoveFromHeros implements AddOrReduceAction {
    @Override
    public IdMaptoCount execute(PlayerCtrl playerCtrl, IdMaptoCount idMaptoCount, Enum<?> caller) throws Exception {
        HerosExtp herosExtp = playerCtrl.getExtension(HerosExtp.class);
        HerosCtrl herosCtrl = herosExtp.getControlerForUpdate(caller);
        LogHandler.itemLog(playerCtrl.getTarget(), caller, idMaptoCount, ItemOpEnum.REDUCE);
        return herosCtrl.removeHeros(idMaptoCount);
    }
}
