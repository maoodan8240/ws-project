package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.common.utils.general.TrueParser;
import ws.protos.EnumsProtos.ShopEnableTypeEnum;
import ws.protos.EnumsProtos.ShopTypeEnum;
import ws.relationship.table.RootTc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table_Shop_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 启用方式
     */
    private Integer enableType;
    /**
     * int 开启等级
     */
    private Integer openLevel;

    /**
     * int 强制刷新序列号
     */
    private Integer forceRefreshNo;
    /**
     * int 是否可以手动刷新
     */
    private Integer canManualRefresh;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"商店ID"}
        enableType = CellParser.parseSimpleCell("EnableType", map, Integer.class); //int
        openLevel = CellParser.parseSimpleCell("OpenLevel", map, Integer.class); //int
        forceRefreshNo = CellParser.parseSimpleCell("ForceRefreshNo", map, Integer.class); //int
        canManualRefresh = CellParser.parseSimpleCell("CanManualRefresh", map, Integer.class); //int
    }

    public ShopTypeEnum getShopType() {
        return ShopTypeEnum.valueOf(id);
    }

    public Integer getForceRefreshNo() {
        return forceRefreshNo;
    }

    public boolean getCanManualRefresh() {
        return TrueParser.isTrue(canManualRefresh);
    }


    public ShopEnableTypeEnum getEnableType() {
        return ShopEnableTypeEnum.valueOf(enableType);
    }

    public Integer getOpenLevel() {
        return openLevel;
    }

    /**
     * 获取所有可用类型的商店
     *
     * @return
     */
    public static List<ShopTypeEnum> shopTypes(int curLevel) {
        List<ShopTypeEnum> types = new ArrayList<>();
        for (Table_Shop_Row row : RootTc.get(Table_Shop_Row.class).values()) {
            if (curLevel < row.getOpenLevel()) {
                continue;
            }
            if (row.getEnableType() != ShopEnableTypeEnum.SHOP_ENABLE_AUTO) {
                continue;
            }
            ShopTypeEnum shopType = row.getShopType();
            if (!types.contains(shopType)) {
                types.add(shopType);
            }
        }
        return types;
    }


}
