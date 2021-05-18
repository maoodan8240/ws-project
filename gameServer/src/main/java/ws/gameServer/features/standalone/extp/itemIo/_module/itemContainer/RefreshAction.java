package ws.gameServer.features.standalone.extp.itemIo._module.itemContainer;

import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.base.IdMaptoCount;

@FunctionalInterface
public interface RefreshAction {
    /**
     * @param playerCtrl
     * @param IdMaptoCount 刷新到客户端的物品
     * @param br           回复的Response
     * @throws Exception
     */
    void refresh(PlayerCtrl playerCtrl, IdMaptoCount idMaptoCount, Response.Builder br) throws Exception;
}
