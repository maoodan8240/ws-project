package ws.gameServer.features.standalone.extp.itemIo.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.itemBag.ItemBagExtp;
import ws.gameServer.features.standalone.extp.itemBag.ctrl.ItemBagCtrl;
import ws.gameServer.features.standalone.extp.itemBag.utils.ItemBagUtils;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.ItemContainerTypeEnum;
import ws.gameServer.features.standalone.extp.itemIo.exception.ItemIoAddException;
import ws.gameServer.features.standalone.extp.itemIo.exception.ItemIoCanAddException;
import ws.gameServer.features.standalone.extp.itemIo.exception.ItemIoCanReduceException;
import ws.gameServer.features.standalone.extp.itemIo.exception.ItemIoIdMaptoCountSplitException;
import ws.gameServer.features.standalone.extp.itemIo.exception.ItemIoModifyOperationNeedCallerActionException;
import ws.gameServer.features.standalone.extp.itemIo.exception.ItemIoReduceException;
import ws.gameServer.features.standalone.extp.itemIo.exception.ItemIoRefreshException;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.enums.item.IdItemTypeEnum;
import ws.relationship.table.RootTc;
import ws.relationship.topLevelPojos.common.TopLevelHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class _ItemIoCtrl extends AbstractPlayerExteControler<TopLevelHolder> implements ItemIoCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_ItemIoCtrl.class);
    private ItemBagCtrl itemBagCtrl;

    @Override
    public void _initReference() throws Exception {
        itemBagCtrl = getPlayerCtrl().getExtension(ItemBagExtp.class).getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() {
    }

    @Override
    public void sync() {
    }

    @Override
    public boolean canAdd(IdAndCount idAndCount) {
        Objects.requireNonNull(idAndCount);
        return canAdd(new IdMaptoCount(idAndCount));
    }

    @Override
    public boolean canAdd(IdMaptoCount idMaptoCount) {
        Objects.requireNonNull(idMaptoCount);
        if (!RootTc.containsItemTemplateRows(idMaptoCount)) {// 策划表里面有没有这些ItemTemplateId
            return false;
        }
        return _canAddItems(split(idMaptoCount));
    }


    @Override
    public IdMaptoCount addItem(IdAndCount idAndCount) {
        Objects.requireNonNull(idAndCount);
        IdMaptoCount re = addItem(new IdMaptoCount(idAndCount));
        return re;
    }

    @Override
    public IdMaptoCount addItem(IdMaptoCount idMaptoCount) {
        Objects.requireNonNull(idMaptoCount);
        requireNonCallerAction();
        IdMaptoCount re = _addItems(split(idMaptoCount));
        LOGGER.debug("本次【增加】的结果为: {} ", re);
        return re;
    }

    private boolean _canAddItems(Map<ItemContainerTypeEnum, IdMaptoCount> map) {
        try {
            for (ItemContainerTypeEnum containerType : map.keySet()) {
                IdMaptoCount toCheck = map.get(containerType);
                if (!containerType.getCanAdd().can(getPlayerCtrl(), toCheck)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw new ItemIoCanAddException(e);
        }
    }

    private IdMaptoCount _addItems(Map<ItemContainerTypeEnum, IdMaptoCount> map) {
        try {
            IdMaptoCount idMaptoCount = new IdMaptoCount();
            for (ItemContainerTypeEnum containerType : map.keySet()) {
                IdMaptoCount toAdd = map.get(containerType);
                IdMaptoCount tmp = containerType.getAdd().execute(getPlayerCtrl(), toAdd, getCallerAction());
                idMaptoCount.addAll(tmp.getAll());
            }
            return idMaptoCount;
        } catch (Exception e) {
            throw new ItemIoAddException(e);
        }
    }

    @Override
    public boolean canRemove(IdAndCount idAndCount) {
        Objects.requireNonNull(idAndCount);
        return canRemove(new IdMaptoCount(idAndCount));
    }

    @Override
    public boolean canRemove(IdMaptoCount idMaptoCount) {
        Objects.requireNonNull(idMaptoCount);
        return _canRemoveItems(split(idMaptoCount));
    }


    @Override
    public IdMaptoCount removeItem(IdAndCount idAndCount) {
        Objects.requireNonNull(idAndCount);
        IdMaptoCount re = removeItem(new IdMaptoCount(idAndCount));
        return re;
    }

    @Override
    public IdMaptoCount removeItem(IdMaptoCount idMaptoCount) {
        Objects.requireNonNull(idMaptoCount);
        requireNonCallerAction();
        IdMaptoCount re = _removeItems(split(idMaptoCount));
        LOGGER.debug("本次【减少】的结果为: {} ", re);
        return re;
    }

    private boolean _canRemoveItems(Map<ItemContainerTypeEnum, IdMaptoCount> map) {
        try {
            for (ItemContainerTypeEnum containerType : map.keySet()) {
                IdMaptoCount toCheck = map.get(containerType);
                if (!containerType.getCanRemove().can(getPlayerCtrl(), toCheck)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            throw new ItemIoCanReduceException(e);
        }
    }

    private IdMaptoCount _removeItems(Map<ItemContainerTypeEnum, IdMaptoCount> map) {
        try {
            IdMaptoCount idMaptoCount = new IdMaptoCount();
            for (ItemContainerTypeEnum containerType : map.keySet()) {
                IdMaptoCount toRemove = map.get(containerType);
                IdMaptoCount tmp = containerType.getRemove().execute(getPlayerCtrl(), toRemove, getCallerAction());
                idMaptoCount.addAll(tmp.getAll());
            }
            return idMaptoCount;
        } catch (Exception e) {
            throw new ItemIoReduceException(e);
        }
    }

    @Override
    public void refreshItem(IdMaptoCount idMaptoCount) {
        Objects.requireNonNull(idMaptoCount);
        _refreshItems(split(idMaptoCount), null);
    }

    @Override
    public void refreshItemAddToResponse(IdMaptoCount idMaptoCount, Response.Builder br) {
        Objects.requireNonNull(idMaptoCount);
        _refreshItems(split(idMaptoCount), br);
    }

    private void _refreshItems(Map<ItemContainerTypeEnum, IdMaptoCount> map, Response.Builder br) {
        try {
            for (ItemContainerTypeEnum containerType : map.keySet()) {
                IdMaptoCount toRefesh = map.get(containerType);
                containerType.getRefresh().refresh(getPlayerCtrl(), toRefesh, br);
            }
        } catch (Exception e) {
            throw new ItemIoRefreshException(e);
        }
    }

    /**
     * 按{@link ItemContainerTypeEnum}分离
     *
     * @param idMaptoCount
     * @return
     */
    private Map<ItemContainerTypeEnum, IdMaptoCount> split(IdMaptoCount idMaptoCount) {
        try {
            Map<ItemContainerTypeEnum, IdMaptoCount> map = new HashMap<>();
            for (IdAndCount idAndCount : idMaptoCount.getAll()) {
                int id = idAndCount.getId();
                IdItemTypeEnum itemType = ItemBagUtils.getIdItemType(id);
                ItemContainerTypeEnum containerType = ItemContainerTypeEnum.parse(itemType);
                if (map.containsKey(containerType)) {
                    map.get(containerType).add(idAndCount);
                } else {
                    map.put(containerType, new IdMaptoCount(idAndCount));
                }
            }
            return map;
        } catch (Exception e) {
            throw new ItemIoIdMaptoCountSplitException(idMaptoCount, e);
        }
    }

    private void requireNonCallerAction() {
        if (getCallerAction() == null) {
            throw new ItemIoModifyOperationNeedCallerActionException();
        }
    }
}
