package ws.gameServer.features.standalone.extp.itemIo.ctrl;

import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.topLevelPojos.common.TopLevelHolder;

public interface ItemIoCtrl extends PlayerExteControler<TopLevelHolder> {

    /**
     * 是否可以添加物品
     *
     * @param idAndCount
     * @return
     */
    boolean canAdd(IdAndCount idAndCount);

    /**
     * 是否可以添加物品
     *
     * @param idMaptoCount
     * @return
     */
    boolean canAdd(IdMaptoCount idMaptoCount);

    /**
     * 添加物品
     *
     * @param idAndCount
     */
    IdMaptoCount addItem(IdAndCount idAndCount);

    /**
     * 添加物品
     *
     * @param idMaptoCount
     */
    IdMaptoCount addItem(IdMaptoCount idMaptoCount);

    /**
     * 是否可以删除物品
     *
     * @param idAndCount
     * @return
     */
    boolean canRemove(IdAndCount idAndCount);

    /**
     * 是否可以删除物品
     *
     * @param idMaptoCount
     * @return
     */
    boolean canRemove(IdMaptoCount idMaptoCount);


    /**
     * 删除物品
     *
     * @param idAndCount
     */
    IdMaptoCount removeItem(IdAndCount idAndCount);

    /**
     * 删除物品
     *
     * @param idMaptoCount
     */
    IdMaptoCount removeItem(IdMaptoCount idMaptoCount);

    /**
     * 同步
     *
     * @param
     */
    void refreshItem(IdMaptoCount idMaptoCount);

    /**
     * 同步信息中 添加刷新的物品
     *
     * @param
     * @param br
     */
    void refreshItemAddToResponse(IdMaptoCount idMaptoCount, Response.Builder br);
}
