package ws.gameServer.features.standalone.extp.itemIo._module.itemContainer;

import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.relationship.base.IdMaptoCount;

@FunctionalInterface
public interface AddOrReduceAction {
    /**
     * 执行s
     *
     * @param playerCtrl
     * @param idMaptoCount 修改的物品
     * @param caller       发起执行者
     * @throws Exception
     */
    IdMaptoCount execute(PlayerCtrl playerCtrl, IdMaptoCount idMaptoCount, Enum<?> caller) throws Exception;
}
