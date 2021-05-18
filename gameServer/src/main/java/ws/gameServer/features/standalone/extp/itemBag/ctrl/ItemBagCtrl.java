package ws.gameServer.features.standalone.extp.itemBag.ctrl;

import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.topLevelPojos.itemBag.ItemBag;
import ws.relationship.topLevelPojos.itemBag.PlainCell;
import ws.relationship.topLevelPojos.itemBag.SpecialCell;

public interface ItemBagCtrl extends PlayerExteControler<ItemBag> {

    /**
     * 扩充背包
     */
    void extend();

    /**
     * 背包是否已满
     *
     * @return true 背包已满 false 背包未满
     */
    boolean isFull();

    /**
     * 是否可以添加物品 tpId
     *
     * @param idMaptoCount
     * @return
     */
    boolean canAddItem(IdMaptoCount idMaptoCount);

    /**
     * 添加物品 tpId
     *
     * @param idMaptoCount
     */
    IdMaptoCount addItem(IdMaptoCount idMaptoCount);

    /**
     * 是否可以删除物品 id Or tpId
     *
     * @param idMaptoCount
     * @return
     */
    boolean canRemoveItem(IdMaptoCount idMaptoCount);

    /**
     * 删除物品 id Or tpId
     *
     * @param idMaptoCount
     */
    IdMaptoCount removeItem(IdMaptoCount idMaptoCount);


    /**
     * 获取普通物品的数量
     *
     * @param tpId
     * @return
     */
    long queryTemplateItemCount(int tpId);

    /**
     * 查询特殊物品
     *
     * @param itemId
     * @return
     */
    SpecialCell getSpecialCell(int itemId);

    /**
     * 获取普通物品
     *
     * @param tpId
     * @return
     */
    PlainCell getPlainCell(int tpId);

    /**
     * gm命令：清除背包
     */
    void gmCommond_ClearItemBag();

    /**
     * 使用物品
     *
     * @param itemTemplateId
     * @param count
     */
    void useItem(int itemTemplateId, int count);

    /**
     * 卖出物品
     *
     * @param idOrTpId
     */
    void sellItem(int idOrTpId, int count);
}
