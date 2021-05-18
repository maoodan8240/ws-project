package ws.gameServer.features.standalone.extp.itemBag.utils;

import ws.relationship.base.MagicNumbers;
import ws.relationship.enums.item.IdItemTypeEnum;
import ws.relationship.topLevelPojos.itemBag.ItemBag;
import ws.relationship.topLevelPojos.itemBag.PlainCell;
import ws.relationship.topLevelPojos.itemBag.SpecialCell;

import java.util.ArrayList;
import java.util.List;

public class ItemBagUtils {

    // ============================================================================================

    /**
     * 获取已经使用了的背包格子数量
     *
     * @param itemBag
     * @return
     */
    public static int getHasUseCellCount(ItemBag itemBag) {
        final int[] plainCellCount = new int[]{0};
        itemBag.getTpIdToPlainCell().forEach((tpId, plainCell) -> {
            if (plainCell.isUseCell()) {
                plainCellCount[0]++;
            }
        });
        itemBag.getIdToSpecialCell().forEach((id, specialCell) -> {
            if (specialCell.isUseCell()) {
                plainCellCount[0]++;
            }
        });
        return plainCellCount[0];
    }

    // ============================================================================================

    /**
     * 获取id物品的类型
     *
     * @param id
     * @return
     */
    public static IdItemTypeEnum getIdItemType(int id) {
        IdItemTypeEnum itemType;
        if (isSpecialItemId(id)) {
            itemType = IdItemTypeEnum.parseByItemId(id);
        } else {
            itemType = IdItemTypeEnum.parseByItemTemplateId(id);
        }
        return itemType;
    }

    /**
     * 判断itemId是否是合法的itemId
     *
     * @param id
     * @return
     */
    public static boolean isSpecialItemId(int id) {
        if (id <= MagicNumbers.ITEM_ID_PREFIX_DIVISOR) {
            return false;
        }
        return true;
    }


    /**
     * 根据itemId获取itemtemplateId
     *
     * @param itemBag
     * @param itemId
     * @return
     */
    public static Integer getItemTemplateIdByItemId(ItemBag itemBag, int itemId) {
        return itemBag.getIdToSpecialCell().get(itemId).getTpId();
    }


    public static int getNextItemId(ItemBag itemBag, int tpId) {
        itemBag.setMaxIdSeq(itemBag.getMaxIdSeq() + 1);
        return IdItemTypeEnum.genItemId(tpId, itemBag.getMaxIdSeq());
    }


    /**
     * 背包中是否含有某个 itemId
     *
     * @param itemBag
     * @param itemId
     * @return
     */
    public static boolean containsItemId(ItemBag itemBag, int itemId) {
        return itemBag.getIdToSpecialCell().containsKey(itemId);
    }

    /**
     * 根据itemId获取特殊物品
     *
     * @param itemBag
     * @param itemId
     * @return
     */
    public static SpecialCell getSpecialCell(ItemBag itemBag, int itemId) {
        return itemBag.getIdToSpecialCell().get(itemId);
    }

    /**
     * 移除特殊物品itemId
     *
     * @param itemBag
     * @param itemId
     */
    public static SpecialCell removeSpecialItem(ItemBag itemBag, Integer itemId) {
        return itemBag.getIdToSpecialCell().remove(itemId);
    }

    /**
     * 移除特殊物品itemIds
     *
     * @param itemBag
     * @param itemIds
     */
    public static List<SpecialCell> removeSpecialItem(ItemBag itemBag, int... itemIds) {
        List<SpecialCell> specialCells = new ArrayList<>();
        for (int itemId : itemIds) {
            specialCells.add(removeSpecialItem(itemBag, itemId));
        }
        return specialCells;
    }

    /**
     * 添加特殊物品
     *
     * @param itemBag
     * @param tpId
     * @param count
     * @param useCell
     */
    public static List<SpecialCell> addSpecialItem(ItemBag itemBag, int tpId, long count, boolean useCell) {
        List<SpecialCell> specialCells = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            specialCells.add(addOneSpecialItem(itemBag, tpId, useCell));
        }
        return specialCells;
    }

    /**
     * 添加一个特殊物品
     *
     * @param itemBag
     * @param tpId
     * @param useCell
     */
    public static SpecialCell addOneSpecialItem(ItemBag itemBag, int tpId, boolean useCell) {
        int nextItemId = getNextItemId(itemBag, tpId);
        SpecialCell specialCell = new SpecialCell(nextItemId, tpId, useCell);
        itemBag.getIdToSpecialCell().put(nextItemId, specialCell);
        return specialCell;
    }

    // ============================================================================================

    /**
     * 获取普通物品的数量
     *
     * @param itemBag
     * @param itemTemplateId
     * @return
     */
    public static long templateItemCount(ItemBag itemBag, int itemTemplateId) {
        PlainCell plaincell = itemBag.getTpIdToPlainCell().get(itemTemplateId);
        return plaincell == null ? 0 : plaincell.getStackSize();
    }

    /**
     * 是否含有1个itemTemplateId，只针对普通物品
     *
     * @param itemBag
     * @param itemTemplateId
     * @return
     */
    public static boolean containsItemOneTemplateId(ItemBag itemBag, int itemTemplateId) {
        return containsItemNTemplateId(itemBag, itemTemplateId, 1);
    }

    /**
     * 是否含有N个itemTemplateId，只针对普通物品
     *
     * @param itemBag
     * @param itemTemplateId
     * @param count
     * @return
     */
    public static boolean containsItemNTemplateId(ItemBag itemBag, int itemTemplateId, long count) {
        if (!itemBag.getTpIdToPlainCell().containsKey(itemTemplateId)) {
            return false;
        }
        PlainCell plaincell = itemBag.getTpIdToPlainCell().get(itemTemplateId);
        return plaincell.getStackSize() >= count;
    }

    /**
     * 移除普通物品
     *
     * @param itemBag
     * @param itemTemplateId
     * @param count
     */
    public static PlainCell removePlainCell(ItemBag itemBag, int itemTemplateId, long count) {
        PlainCell plainCell = itemBag.getTpIdToPlainCell().get(itemTemplateId);
        long curStackSize = plainCell.getStackSize() - count;
        plainCell.setStackSize(curStackSize <= 0 ? 0 : curStackSize);
        if (plainCell.getStackSize() == 0) { // 该itemTemplateId没有PlainCell了,清除itemTemplateId
            itemBag.getTpIdToPlainCell().remove(itemTemplateId);
        }
        return plainCell;
    }

    /**
     * 增加普通物品
     *
     * @param itemBag
     * @param tpId
     * @param count
     * @param useCell
     */
    public static PlainCell addPlainItem(ItemBag itemBag, int tpId, long count, boolean useCell) {
        if (!itemBag.getTpIdToPlainCell().containsKey(tpId)) {
            itemBag.getTpIdToPlainCell().put(tpId, new PlainCell(tpId, useCell));
        }
        PlainCell plainCell = itemBag.getTpIdToPlainCell().get(tpId);
        plainCell.setStackSize(plainCell.getStackSize() + count);
        return plainCell;
    }

    /**
     * 获取普通物品
     *
     * @param itemBag
     * @param tpId
     * @return
     */
    public static PlainCell getPlainCell(ItemBag itemBag, Integer tpId) {
        return itemBag.getTpIdToPlainCell().get(tpId);
    }
}
