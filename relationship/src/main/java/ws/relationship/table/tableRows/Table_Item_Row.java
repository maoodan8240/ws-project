package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.table.table.utils.CellParser;
import ws.common.utils.general.TrueParser;
import ws.protos.EnumsProtos.ItemSmallTypeEnum;
import ws.protos.EnumsProtos.ItemUseTypeEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;

import java.util.Map;

public class Table_Item_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * string 道具名称ID
     */
    private String itemNameId;
    /**
     * string 道具描述ID
     */
    private String itemExplainId;
    /**
     * string 道具图标
     */
    private String itemIcon;
    /**
     * int 道具类型
     */
    private Integer itemType;
    /**
     * int 道具的背包位置
     */
    private Integer itemBag;
    /**
     * int 道具品级
     */
    private Integer itemColor;
    /**
     * int 使用类型
     */
    private Integer useId;
    /**
     * int 资源ID
     */
    private Integer resourceId;
    /**
     * int 道具使用值
     */
    private Integer useItemNumber;

    /**
     * int 是否可以合成
     */
    private Integer canComposite;
    /**
     * int 合成需求和目标
     */
    private TupleCell<Integer> compositeNumToTarget;
    /**
     * int 是否可以叠加
     */
    private Integer isOverlap;
    /**
     * int 是否可以出售
     */
    private Integer isSale;
    /**
     * int 是否为战魂经验
     */
    private Integer isWarSoulExp;
    /**
     * int 战魂经验
     */
    private Integer warSoulExp;
    /**
     * int 卡片经验
     */
    private Integer cardExp;
    /**
     * int 徽章经验
     */
    private Integer badgeExp;
    /**
     * int 秘籍经验
     */
    private Integer cheatsExp;
    /**
     * int 道具出售价格
     */
    private Integer saleNumber;
    /**
     * int 道具金币价格
     */
    private Integer itemGold;
    /**
     * int 道具钻石价格
     */
    private Integer itemJiamond;
    /**
     * int 道具荣誉价格
     */
    private Integer itemHonor;
    /**
     * int 道具试练币价格
     */
    private Integer itemTrain;
    /**
     * int 道具公会币价格
     */
    private Integer itemGuild;
    /**
     * int 觉醒碎片价格
     */
    private Integer itemAwakeFragment;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"道具ID"}
        itemNameId = CellParser.parseSimpleCell("ItemNameId", map, String.class); //int
        itemExplainId = CellParser.parseSimpleCell("ItemExplainId", map, String.class); //int
        itemIcon = CellParser.parseSimpleCell("ItemIcon", map, String.class); //string
        itemType = CellParser.parseSimpleCell("ItemType", map, Integer.class); //int
        itemBag = CellParser.parseSimpleCell("ItemBag", map, Integer.class); //int
        itemColor = CellParser.parseSimpleCell("ItemColor", map, Integer.class); //int
        useId = CellParser.parseSimpleCell("UseId", map, Integer.class); //int
        resourceId = CellParser.parseSimpleCell("ResourceId", map, Integer.class); //int
        useItemNumber = CellParser.parseSimpleCell("UseItemNumber", map, Integer.class); //int
        canComposite = CellParser.parseSimpleCell("CanComposite", map, Integer.class); //int
        compositeNumToTarget = CellParser.parseTupleCell("CompositeNumToTarget", map, Integer.class); //int
        isOverlap = CellParser.parseSimpleCell("IsOverlap", map, Integer.class); //int
        isSale = CellParser.parseSimpleCell("IsSale", map, Integer.class); //int
        isWarSoulExp = CellParser.parseSimpleCell("IsWarSoulExp", map, Integer.class); //int
        warSoulExp = CellParser.parseSimpleCell("WarSoulExp", map, Integer.class); //int
        cardExp = CellParser.parseSimpleCell("CardExp", map, Integer.class); //int
        badgeExp = CellParser.parseSimpleCell("BadgeExp", map, Integer.class); //int
        cheatsExp = CellParser.parseSimpleCell("CheatsExp", map, Integer.class); //int
        saleNumber = CellParser.parseSimpleCell("SaleNumber", map, Integer.class); //int
        itemGold = CellParser.parseSimpleCell("ItemGold", map, Integer.class); //int
        itemJiamond = CellParser.parseSimpleCell("ItemJiamond", map, Integer.class); //int
        itemHonor = CellParser.parseSimpleCell("ItemHonor", map, Integer.class); //int
        itemTrain = CellParser.parseSimpleCell("ItemTrain", map, Integer.class); //int
        itemGuild = CellParser.parseSimpleCell("ItemGuild", map, Integer.class); //int
        itemAwakeFragment = CellParser.parseSimpleCell("ItemAwakeFragment", map, Integer.class); //int
    }

    public String getItemNameId() {
        return itemNameId;
    }

    public String getItemExplainId() {
        return itemExplainId;
    }

    public String getItemIcon() {
        return itemIcon;
    }

    public ItemSmallTypeEnum getItemType() {
        return ItemSmallTypeEnum.valueOf(itemType);
    }

    public Integer getItemBag() {
        return itemBag;
    }

    public Integer getItemColor() {
        return itemColor;
    }

    public ItemUseTypeEnum getUseId() {
        return ItemUseTypeEnum.valueOf(useId);
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public Integer getUseItemNumber() {
        return useItemNumber;
    }

    public Integer getIsOverlap() {
        return isOverlap;
    }

    public boolean getIsSale() {
        return TrueParser.isTrue(isSale);
    }

    public boolean getIsWarSoulExp() {
        return TrueParser.isTrue(isWarSoulExp);
    }

    public Integer getWarSoulExp() {
        return warSoulExp;
    }

    public Integer getCardExp() {
        return cardExp;
    }

    public Integer getBadgeExp() {
        return badgeExp;
    }

    public Integer getCheatsExp() {
        return cheatsExp;
    }

    public Integer getSaleNumber() {
        return saleNumber;
    }

    public Integer getItemGold() {
        return itemGold;
    }

    public Integer getItemJiamond() {
        return itemJiamond;
    }

    public Integer getItemHonor() {
        return itemHonor;
    }

    public Integer getItemTrain() {
        return itemTrain;
    }

    public Integer getItemGuild() {
        return itemGuild;
    }

    public Integer getItemAwakeFragment() {
        return itemAwakeFragment;
    }


    public boolean getCanComposite() {
        return TrueParser.isTrue(canComposite);
    }

    public TupleCell<Integer> getCompositeNumToTarget() {
        return compositeNumToTarget;
    }

    /**
     * 合成目标的Id
     *
     * @return
     */
    public int getCompositeNumToTarget_TargetId() {
        return compositeNumToTarget.get(TupleCell.SECOND);
    }

    /**
     * 合成需要的数量
     *
     * @return
     */
    public int getCompositeNumToTarget_NeedNum() {
        return compositeNumToTarget.get(TupleCell.FIRST);
    }


    /**
     * 获取购买道具所需指定资源的数量
     *
     * @param resourceType
     * @param count
     * @param discount
     * @return
     */
    public IdAndCount getBuyItemPrice(ResourceTypeEnum resourceType, long count, int discount) {
        switch (resourceType) {
            case RES_MONEY:
                return new IdAndCount(resourceType.getNumber(), (count * itemGold * discount) / MagicNumbers.WITHOUT_DISCOUNT);
            case RES_VIPMONEY:
                return new IdAndCount(resourceType.getNumber(), (count * itemJiamond * discount) / MagicNumbers.WITHOUT_DISCOUNT);
            case RES_ARENA_MONEY:
                return new IdAndCount(resourceType.getNumber(), (count * itemHonor * discount) / MagicNumbers.WITHOUT_DISCOUNT);
            case RES_TEST_MONEY:
                return new IdAndCount(resourceType.getNumber(), (count * itemTrain * discount) / MagicNumbers.WITHOUT_DISCOUNT);
            case RES_GUILD_MONEY:
                return new IdAndCount(resourceType.getNumber(), (count * itemGuild * discount) / MagicNumbers.WITHOUT_DISCOUNT);
            case RES_AWAKE_FRAGMENT:
                return new IdAndCount(resourceType.getNumber(), (count * itemAwakeFragment * discount) / MagicNumbers.WITHOUT_DISCOUNT);
        }
        String msg = String.format("不支持的道具购买货币resourceType=%s", resourceType);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 获取购买道具所需指定资源的数量
     *
     * @param resourceType
     * @param count
     * @return
     */
    public IdAndCount getBuyItemPrice(ResourceTypeEnum resourceType, long count) {
        return getBuyItemPrice(resourceType, count, MagicNumbers.WITHOUT_DISCOUNT);
    }
}
