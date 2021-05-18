package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.EnumsProtos.ShopTypeEnum;
import ws.relationship.table.RootTc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table_ShopGoods_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 商店类型
     */
    private Integer shopType;
    /**
     * int 等级范围
     */
    private TupleCell<Integer> level;
    /**
     * int 可以购买的
     */
    private Integer canBuyTimes;
    /**
     * int 物品位置
     */
    private Integer goodsPos;
    /**
     * int 货币类型
     */
    private Integer coinType;
    /**
     * int 物品库
     */
    private Integer goodsLibrary;
    /**
     * int 折扣:概率
     */
    private TupleCell<Integer> discount;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"ID"}
        shopType = CellParser.parseSimpleCell("ShopType", map, Integer.class); //int
        level = CellParser.parseTupleCell("Level", map, Integer.class); //int
        canBuyTimes = CellParser.parseSimpleCell("CanBuyTimes", map, Integer.class); //int
        goodsPos = CellParser.parseSimpleCell("GoodsPos", map, Integer.class); //int
        coinType = CellParser.parseSimpleCell("CoinType", map, Integer.class); //int
        goodsLibrary = CellParser.parseSimpleCell("GoodsLibrary", map, Integer.class); //int
        discount = CellParser.parseTupleCell("Discount", map, Integer.class); //string
    }

    public ShopTypeEnum getShopType() {
        return ShopTypeEnum.valueOf(shopType);
    }

    public TupleCell<Integer> getLevel() {
        return level;
    }

    public Integer getGoodsPos() {
        return goodsPos;
    }

    public ResourceTypeEnum getCoinType() {
        return ResourceTypeEnum.valueOf(coinType);
    }

    public Integer getGoodsLibrary() {
        return goodsLibrary;
    }

    public TupleCell<Integer> getDiscount() {
        return discount;
    }

    public Integer getCanBuyTimes() {
        return canBuyTimes;
    }

    /**
     * 获取符合玩家等级的所有shopType行
     *
     * @param shopType
     * @param playerLevel
     * @return
     */
    public static List<Table_ShopGoods_Row> curLevelRows(ShopTypeEnum shopType, int playerLevel) {
        List<Table_ShopGoods_Row> rows = new ArrayList<>();
        for (Table_ShopGoods_Row row : RootTc.get(Table_ShopGoods_Row.class).values()) {
            int minLevel = row.getLevel().get(TupleCell.FIRST);
            int maxLevel = row.getLevel().get(TupleCell.SECOND);
            if (row.getShopType() == shopType && (playerLevel >= minLevel && playerLevel <= maxLevel)) {
                rows.add(row);
            }
        }
        return rows;
    }


}
