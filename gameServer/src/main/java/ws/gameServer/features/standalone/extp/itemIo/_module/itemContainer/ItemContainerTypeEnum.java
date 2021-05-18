package ws.gameServer.features.standalone.extp.itemIo._module.itemContainer;

import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.heros.AddToHeros;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.heros.CanAddToHeros;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.heros.CanRemoveFromHeros;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.heros.RefreshHeros;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.heros.RemoveFromHeros;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.itemBag.AddToItemBag;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.itemBag.CanAddToItemBag;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.itemBag.CanRemoveFromItemBag;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.itemBag.RefreshItemBag;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.itemBag.RemoveFromItemBag;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.resourcePoint.AddToResourcePoint;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.resourcePoint.CanAddToResourcePoint;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.resourcePoint.CanRemoveFromResourcePoint;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.resourcePoint.RefreshResourcePoint;
import ws.gameServer.features.standalone.extp.itemIo._module.itemContainer.resourcePoint.RemoveFromResourcePoint;
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
     * 武将
     */
    Heros(//
            new AddToHeros(), new CanAddToHeros(), new CanRemoveFromHeros(), new RefreshHeros(), new RemoveFromHeros() //
            , IdItemTypeEnum.HERO
            //
    ),
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
    ),;
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
