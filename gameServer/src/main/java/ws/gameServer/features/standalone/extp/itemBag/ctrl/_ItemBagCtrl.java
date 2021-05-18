package ws.gameServer.features.standalone.extp.itemBag.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.itemBag.utils.ItemBagCtrlProtos;
import ws.gameServer.features.standalone.extp.itemBag.utils.ItemBagUtils;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.EnumsProtos.ItemUseTypeEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.ItemBagProtos.Sm_ItemBag;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.MessageHandlerProtos.Response.Builder;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.enums.item.IdItemBigTypeEnum;
import ws.relationship.enums.item.IdItemTypeEnum;
import ws.relationship.exception.CmMessageIllegalArgumentException;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_DropLibrary_Row;
import ws.relationship.table.tableRows.Table_Item_Row;
import ws.relationship.topLevelPojos.itemBag.ItemBag;
import ws.relationship.topLevelPojos.itemBag.PlainCell;
import ws.relationship.topLevelPojos.itemBag.SpecialCell;
import ws.relationship.utils.ProtoUtils;

import java.util.List;

public class _ItemBagCtrl extends AbstractPlayerExteControler<ItemBag> implements ItemBagCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_ItemBagCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {

    }

    @Override
    public void sync() {
        SenderFunc.sendInner(this, Sm_ItemBag.class, Sm_ItemBag.Builder.class, Sm_ItemBag.Action.RESP_SYNC, (b, br) -> {
            ItemBagCtrlProtos.fill_Sm_ItemBag_All(b, target);
        });
    }

    @Override
    public void refreshItem(IdMaptoCount idMaptoCount) {
        Response.Builder br = ProtoUtils.create_Response(Code.Sm_ItemBag, Sm_ItemBag.Action.RESP_SYNC_PART);
        br.setSmItemBagSyncPart(ItemBagCtrlProtos.create_Sm_ItemBag_Dynamic(target, idMaptoCount));
        br.setResult(true);
        send(br.build());
    }

    @Override
    public void refreshItemAddToResponse(IdMaptoCount idMaptoCount, Builder br) {
        br.setSmItemBagSyncPart(ItemBagCtrlProtos.create_Sm_ItemBag_Dynamic(target, idMaptoCount));
    }

    @Override
    public void extend() {
    }

    @Override
    public boolean isFull() {
        return false;//ItemBagUtils.getHasUseCellCount(target) >= target.getMaxCellCount();
    }

    @Override
    public boolean canAddItem(IdMaptoCount idMaptoCount) {
        // idMaptoCount中的ItemTemplateId是否在策划表中存在，已经在ItemCtrlIo检测过了
        return true;//ItemBagUtils.getHasUseCellCount(target) < target.getMaxCellCount();
    }

    @Override
    public IdMaptoCount addItem(IdMaptoCount idMaptoCount) {
        IdMaptoCount refresh_IdMaptoCount = new IdMaptoCount();
        for (IdAndCount idAndCount : idMaptoCount.getAll()) {
            IdItemTypeEnum itemType = IdItemTypeEnum.parseByItemTemplateId(idAndCount.getId());
            if (itemType.getBigType() == IdItemBigTypeEnum.PlainItem) {
                ItemBagUtils.addPlainItem(target, idAndCount.getId(), idAndCount.getCount(), itemType.isUseCell());
                refresh_IdMaptoCount.add(idAndCount);
            } else {
                List<SpecialCell> specialCells = ItemBagUtils.addSpecialItem(target, idAndCount.getId(), idAndCount.getCount(), itemType.isUseCell());
                for (SpecialCell specialCell : specialCells) {
                    refresh_IdMaptoCount.add(new IdAndCount(specialCell.getId()));
                }
            }
        }
        save();
        return refresh_IdMaptoCount;
    }

    @Override
    public boolean canRemoveItem(IdMaptoCount idMaptoCount) {
        for (IdAndCount idAndCount : idMaptoCount.getAll()) {
            if (ItemBagUtils.isSpecialItemId(idAndCount.getId())) { // 特殊物品
                if (idAndCount.getCount() != 1) {
                    LOGGER.debug("特殊物品通过itemId一次只能移除一个！ idAndCount={}  idMaptoCount={}", idAndCount, idMaptoCount);
                    return false;
                }
                if (!ItemBagUtils.containsItemId(target, idAndCount.getId())) {
                    LOGGER.debug("背包中不含有足够的特殊物品，不能移除！ idAndCount={}  idMaptoCount={}", idAndCount, idMaptoCount);
                    return false;
                }
            } else { // 普通物品
                IdItemTypeEnum itemType = IdItemTypeEnum.parseByItemTemplateId(idAndCount.getId());
                if (itemType.getBigType() == IdItemBigTypeEnum.PlainItem) {
                    if (!ItemBagUtils.containsItemNTemplateId(target, idAndCount.getId(), idAndCount.getCount())) {
                        LOGGER.debug("背包中不含有足够的普通物品，不能移除！ idAndCount={}  idMaptoCount={}", idAndCount, idMaptoCount);
                        return false;
                    }
                } else {
                    LOGGER.debug("特殊物品不能通过itemTemplateId移除！ idAndCount={}  idMaptoCount={}", idAndCount, idMaptoCount);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public IdMaptoCount removeItem(IdMaptoCount idMaptoCount) {
        IdMaptoCount refresh_IdMaptoCount = new IdMaptoCount();
        for (IdAndCount idAndCount : idMaptoCount.getAll()) {
            if (ItemBagUtils.isSpecialItemId(idAndCount.getId())) {
                SpecialCell specialCell = ItemBagUtils.removeSpecialItem(target, idAndCount.getId());
                refresh_IdMaptoCount.add(new IdAndCount(specialCell.getId()));
            } else {
                ItemBagUtils.removePlainCell(target, idAndCount.getId(), idAndCount.getCount());
                refresh_IdMaptoCount.add(idAndCount);
            }
        }
        save();
        return refresh_IdMaptoCount;
    }

    @Override
    public SpecialCell getSpecialCell(int itemId) {
        return ItemBagUtils.getSpecialCell(target, itemId);
    }

    @Override
    public PlainCell getPlainCell(int tpId) {
        return target.getTpIdToPlainCell().get(tpId);
    }

    @Override
    public long queryTemplateItemCount(int itemTemplateId) {
        return ItemBagUtils.templateItemCount(target, itemTemplateId);
    }

    @Override
    public void gmCommond_ClearItemBag() {

    }

    @Override
    public void useItem(int itemTemplateId, int count) {
        if (!RootTc.get(Table_Item_Row.class).has(itemTemplateId)) {
            String msg = String.format("itemTemplateId=%s Table_Item_Row 表中不存在该itemTemplateId.", itemTemplateId);
            throw new CmMessageIllegalArgumentException(msg);
        }
        if (count <= 0) {
            String msg = String.format("count=%s count必须大于0.", count);
            throw new CmMessageIllegalArgumentException(msg);
        }
        Table_Item_Row row = RootTc.get(Table_Item_Row.class, itemTemplateId);
        if (row.getUseId() == ItemUseTypeEnum.CAN_NOT_USE) {
            String msg = String.format("itemTemplateId=%s Table_Item_Row 中配置为不能使用.", itemTemplateId);
            throw new CmMessageIllegalArgumentException(msg);
        }
        IdAndCount idAndCount = new IdAndCount(itemTemplateId, count);
        LogicCheckUtils.canRemove(itemIoCtrl, new IdMaptoCount().add(idAndCount));
        IdMaptoCount idMaptoCountDrop = new IdMaptoCount();
        if (row.getUseId() == ItemUseTypeEnum.USE_DIRECT) {  // 直接掉落
            idMaptoCountDrop.add(new IdAndCount(row.getResourceId(), row.getUseItemNumber() * count));
        } else if (row.getUseId() == ItemUseTypeEnum.USE_BY_DROP_LIBRARY) {  // 直接掉落
            for (int i = 0; i < count; i++) {
                idMaptoCountDrop.addAll(Table_DropLibrary_Row.drop(row.getResourceId(), getPlayerCtrl().getCurLevel()));
            }
        }
        LogicCheckUtils.canAdd(itemIoCtrl, idMaptoCountDrop);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Sm_ItemBag.Action.RESP_USE_ITEM).removeItem(idAndCount);
        IdMaptoCount refresh_2 = itemIoExtp.getControlerForUpdate(Sm_ItemBag.Action.RESP_USE_ITEM).addItem(idMaptoCountDrop);
        SenderFunc.sendInner(this, Sm_ItemBag.class, Sm_ItemBag.Builder.class, Sm_ItemBag.Action.RESP_USE_ITEM, (b, br) -> {
            b.setIdMaptoCount(ProtoUtils.create_Sm_Common_IdMaptoCount(idMaptoCountDrop));
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).addAll(refresh_2), br);
        });
        save();
    }

    @Override
    public void sellItem(int idOrTpId, int count) {
        int itemTemplateId = idOrTpId;
        if (ItemBagUtils.isSpecialItemId(idOrTpId)) {
            String msg = String.format("idOrTpId=%s 为特殊物品Id，目前只支持道具出售.", idOrTpId);
            throw new CmMessageIllegalArgumentException(msg);
        }
        if (!RootTc.get(Table_Item_Row.class).has(itemTemplateId)) {
            String msg = String.format("itemTemplateId=%s Table_Item_Row 表中不存在该itemTemplateId.", itemTemplateId);
            throw new CmMessageIllegalArgumentException(msg);
        }
        if (count <= 0) {
            String msg = String.format("count=%s count必须大于0.", count);
            throw new CmMessageIllegalArgumentException(msg);
        }
        Table_Item_Row row = RootTc.get(Table_Item_Row.class, itemTemplateId);
        if (!row.getIsSale()) {
            String msg = String.format("itemTemplateId=%s Table_Item_Row 中配置为不能出售.", itemTemplateId);
            throw new CmMessageIllegalArgumentException(msg);
        }
        IdAndCount idAndCount = new IdAndCount(idOrTpId, count);
        LogicCheckUtils.canRemove(itemIoCtrl, new IdMaptoCount().add(idAndCount));
        IdAndCount idAndCount_Add = new IdAndCount(ResourceTypeEnum.RES_MONEY.getNumber(), 1l * row.getSaleNumber() * count);
        LogicCheckUtils.canAdd(itemIoCtrl, new IdMaptoCount(idAndCount_Add));
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Sm_ItemBag.Action.RESP_SELL_ITEM).removeItem(idAndCount);
        IdMaptoCount refresh_2 = itemIoExtp.getControlerForUpdate(Sm_ItemBag.Action.RESP_SELL_ITEM).addItem(idAndCount_Add);
        SenderFunc.sendInner(this, Sm_ItemBag.class, Sm_ItemBag.Builder.class, Sm_ItemBag.Action.RESP_SELL_ITEM, (b, br) -> {
            b.setIdMaptoCount(ProtoUtils.create_Sm_Common_IdMaptoCount(new IdMaptoCount(idAndCount_Add)));
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).addAll(refresh_2), br);
        });
        save();
    }
}
