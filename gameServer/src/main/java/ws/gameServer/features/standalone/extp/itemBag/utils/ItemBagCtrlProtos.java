package ws.gameServer.features.standalone.extp.itemBag.utils;

import ws.protos.ItemBagProtos.Sm_ItemBag;
import ws.protos.ItemBagProtos.Sm_ItemBag_PlainCell;
import ws.protos.ItemBagProtos.Sm_ItemBag_SpecialCell;
import ws.protos.ItemBagProtos.Sm_ItemBag_SpecialItemExtInfo;
import ws.protos.ItemBagProtos.Sm_ItemBag_SpecialItemExtInfo.KeyType;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.topLevelPojos.itemBag.ItemBag;
import ws.relationship.topLevelPojos.itemBag.PlainCell;
import ws.relationship.topLevelPojos.itemBag.SpecialCell;

import java.util.Map.Entry;

public class ItemBagCtrlProtos {
    /**
     * 构建Sm_ItemBag.Builder
     *
     * @param itemBag
     * @return
     */
    public static Sm_ItemBag.Builder fill_Sm_ItemBag_All(Sm_ItemBag.Builder b, ItemBag itemBag) {
        Sm_ItemBag_PlainCell.Builder b$Plain = Sm_ItemBag_PlainCell.newBuilder();
        Sm_ItemBag_SpecialCell.Builder b$Special = Sm_ItemBag_SpecialCell.newBuilder();
        Sm_ItemBag_SpecialItemExtInfo.Builder b$Sm_SpecialItemExtInfo = Sm_ItemBag_SpecialItemExtInfo.newBuilder();
        // 背包最大格子数
        b.setMaxCellCount(0);
        // 背包中的普通物品
        itemBag.getTpIdToPlainCell().values().forEach(plainCell -> {
            addPlain(plainCell, b, b$Plain);
        });
        // 背包中的特殊物品

        itemBag.getIdToSpecialCell().values().forEach(specialCell -> {
            addSpecial(specialCell, b, b$Special, b$Sm_SpecialItemExtInfo);
        });
        return b;
    }

    /**
     * 构建Sm_ItemBag.Builder
     *
     * @param itemBag
     * @return
     */
    public static Sm_ItemBag.Builder create_Sm_ItemBag_Dynamic(ItemBag itemBag, IdMaptoCount idMaptoCount) {
        Sm_ItemBag.Builder b = Sm_ItemBag.newBuilder();
        b.setAction(Sm_ItemBag.Action.RESP_SYNC_PART);
        Sm_ItemBag_PlainCell.Builder b$Plain = Sm_ItemBag_PlainCell.newBuilder();
        Sm_ItemBag_SpecialCell.Builder b$Special = Sm_ItemBag_SpecialCell.newBuilder();
        Sm_ItemBag_SpecialItemExtInfo.Builder b$Sm_SpecialItemExtInfo = Sm_ItemBag_SpecialItemExtInfo.newBuilder();
        for (Integer idOrTpId : idMaptoCount.getAllKeys()) {
            if (ItemBagUtils.isSpecialItemId(idOrTpId)) {
                int itemId = idOrTpId;
                if (!ItemBagUtils.containsItemId(itemBag, itemId)) {// 背包中[不含有]这个特殊物品
                    b.addDeleteIds(idOrTpId);
                    continue;
                }
                SpecialCell specialCell = ItemBagUtils.getSpecialCell(itemBag, itemId);
                addSpecial(specialCell, b, b$Special, b$Sm_SpecialItemExtInfo);
            } else {// 不是特殊物品的ItemId，则必然是普通物品的TemplateId，特殊物品只能以ItemId形式出现
                int tpId = idOrTpId;
                if (!ItemBagUtils.containsItemOneTemplateId(itemBag, tpId)) {// 背包中[不含有]这个普通物品
                    b.addDeleteIds(idOrTpId);
                    continue;
                }
                PlainCell plainCell = ItemBagUtils.getPlainCell(itemBag, tpId);
                addPlain(plainCell, b, b$Plain);
            }
        }
        return b;
    }

    /**
     * 增加特殊物品到 Sm_ItemBag_SpecialCell.Builder
     *
     * @param specialCell
     * @param b
     * @param b$Special
     * @param b$Sm_SpecialItemExtInfo
     */
    private static void addSpecial(SpecialCell specialCell, Sm_ItemBag.Builder b, Sm_ItemBag_SpecialCell.Builder b$Special, Sm_ItemBag_SpecialItemExtInfo.Builder b$Sm_SpecialItemExtInfo) {
        b$Special.clear();
        b$Special.setItemTemplateId(specialCell.getTpId());
        b$Special.setItemId(specialCell.getId());
        for (Entry<KeyType, String> entry : specialCell.getExtInfo().entrySet()) {
            b$Sm_SpecialItemExtInfo.clear();
            b$Sm_SpecialItemExtInfo.setKey(entry.getKey());
            b$Sm_SpecialItemExtInfo.setValue(entry.getValue());
            b$Special.addExtInfo(b$Sm_SpecialItemExtInfo.build());
        }
        b.addSpecialCells(b$Special.build());
    }

    /**
     * 增加普通物品到 Sm_ItemBag_PlainCell.Builder
     *
     * @param plainCell
     * @param b
     * @param b$Plain
     */
    private static void addPlain(PlainCell plainCell, Sm_ItemBag.Builder b, Sm_ItemBag_PlainCell.Builder b$Plain) {
        b$Plain.clear();
        b$Plain.setItemTemplateId(plainCell.getItemTemplateId());
        b$Plain.setCurrentStackSize(plainCell.getStackSize());
        b.addPlainCells(b$Plain.build());
    }
}
