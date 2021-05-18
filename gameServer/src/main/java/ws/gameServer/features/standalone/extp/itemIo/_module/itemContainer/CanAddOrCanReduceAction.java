package ws.gameServer.features.standalone.extp.itemIo._module.itemContainer;

import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.relationship.base.IdMaptoCount;

@FunctionalInterface
public interface CanAddOrCanReduceAction {
    /**
     * 返回是否允许执行
     *
     * @param playerCtrl
     * @param idMaptoCount
     * @return
     * @throws Exception
     */
    boolean can(PlayerCtrl playerCtrl, IdMaptoCount idMaptoCount) throws Exception;
}
