package ws.gameServer.features.standalone.extp.itemIo._module.itemContainer;

import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.itemBag.*;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.resourcePoint.*;
import ws.gameServer.features.standalone.extp.itemIo.exception.ParseItemContainerTypeFailedException;
import ws.relationship.enums.item.IdItemTypeEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Item容器类型枚举
 */
public enum ItemContainerTypeEnum {
   
    /**
     * 背包
     */
    ItemBag(//
            new AddToItemBag(), new CanAddToItemBag(), new CanRemoveFromItemBag(), new RefreshItemBag(), new RemoveFromItemBag()//
            , IdItemTypeEnum.EQUIPMENT, IdItemTypeEnum.SPECIAL_EQUIP, IdItemTypeEnum.ITEM
            //
    ),
    /**
     * 资源点
     */
    ResourcePoint(//
            new AddToResourcePoint(), new CanAddToResourcePoint(), new CanRemoveFromResourcePoint(), new RefreshResourcePoint(), new RemoveFromResourcePoint()//
            , IdItemTypeEnum.RESOURCE
            //
    ),
    ;
    private AddOrReduceAction add;
    private CanAddOrCanReduceAction canAdd;
    private CanAddOrCanReduceAction canRemove;
    private RefreshAction refresh;
    private AddOrReduceAction remove;
    private List<IdItemTypeEnum> idItemTypes;

    private ItemContainerTypeEnum(AddOrReduceAction add, CanAddOrCanReduceAction canAdd, CanAddOrCanReduceAction canRemove, RefreshAction refresh, AddOrReduceAction remove, IdItemTypeEnum... idItemTypes) {
        this.add = add;
        this.canAdd = canAdd;
        this.canRemove = canRemove;
        this.refresh = refresh;
        this.remove = remove;
        this.idItemTypes = new ArrayList<>(Arrays.asList(idItemTypes));
    }

    public CanAddOrCanReduceAction getCanAdd() {
        return canAdd;
    }

    public AddOrReduceAction getAdd() {
        return add;
    }

    public CanAddOrCanReduceAction getCanRemove() {
        return canRemove;
    }

    public AddOrReduceAction getRemove() {
        return remove;
    }

    public RefreshAction getRefresh() {
        return refresh;
    }

    public static ItemContainerTypeEnum parse(IdItemTypeEnum itemType) {
        for (ItemContainerTypeEnum containerType : values()) {
            if (containerType.idItemTypes.contains(itemType)) {
                return containerType;
            }
        }
        throw new ParseItemContainerTypeFailedException(Arrays.toString(values()), itemType);
    }
}
