package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.CardAptitudeTypeEnum;
import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;

import java.util.List;
import java.util.Map;

public class Table_EquipmentUpLvConsumes_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 卡片资质类型
     */
    private Integer cardType;
    /**
     * int 装备等级
     */
    private Integer equipmentLevel;
    /**
     * int 武器金币消耗
     */
    private Integer weaponUse;
    /**
     * int 衣服金币消耗
     */
    private Integer clothesUse;
    /**
     * int 裤子金币消耗
     */
    private Integer trousersUse;
    /**
     * int 鞋金币消耗
     */
    private Integer shoesUse;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"基础ID"}
        cardType = CellParser.parseSimpleCell("CardType", map, Integer.class); //int
        equipmentLevel = CellParser.parseSimpleCell("EquipmentLevel", map, Integer.class); //int
        weaponUse = CellParser.parseSimpleCell("WeaponUse", map, Integer.class); //int
        clothesUse = CellParser.parseSimpleCell("ClothesUse", map, Integer.class); //int
        trousersUse = CellParser.parseSimpleCell("TrousersUse", map, Integer.class); //int
        shoesUse = CellParser.parseSimpleCell("ShoesUse", map, Integer.class); //int
    }

    public CardAptitudeTypeEnum getCardType() {
        return CardAptitudeTypeEnum.valueOf(cardType);
    }

    private static int chooseConsume(Table_EquipmentUpLvConsumes_Row row, EquipmentPositionEnum position) {
        switch (position) {
            case POS_A:
                return row.weaponUse;

            case POS_B:
                return row.clothesUse;
            case POS_C:
                return row.trousersUse;
            case POS_D:
                return row.shoesUse;
        }
        String msg = String.format("不支持的位置类型=%s", position);
        throw new BusinessLogicMismatchConditionException(msg);
    }


    /**
     * ABCD：装备升级
     *
     * @param heroTpId
     * @param position
     * @param curLv    当前等级
     * @return
     */
    public static int getConsumes(int heroTpId, EquipmentPositionEnum position, int curLv) {
        Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, heroTpId);
        List<Table_EquipmentUpLvConsumes_Row> rows = RootTc.getAll(Table_EquipmentUpLvConsumes_Row.class);
        CardAptitudeTypeEnum heroAptitudeType = Table_CardUpgradeQuality_Row.getHeroAptitudeType(cardRow.getCardAptitude());
        for (Table_EquipmentUpLvConsumes_Row row : rows) {
            if (row.getCardType() == heroAptitudeType && row.equipmentLevel == curLv) {
                return chooseConsume(row, position);
            }
        }
        String msg = String.format("未找到装备强化消耗行 heroTpId=%s heroAptitudeType=%s curLv=%s !", position, heroAptitudeType, curLv);
        throw new BusinessLogicMismatchConditionException(msg);
    }

}
