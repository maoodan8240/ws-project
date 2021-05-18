package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.ColorDetailTypeEnum;
import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;

import java.util.List;
import java.util.Map;

public class Table_BadgeCheatsUpQualityLvConsumes_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 特殊装备部位
     */
    private Integer specialEquPos;
    /**
     * int 需求人物等级
     */
    private Integer needPlayerLv;
    /**
     * string 特殊装备品质等级
     */
    private Integer specialEquQuality;
    /**
     * int 材料1ID
     */
    private Integer material1Id;
    /**
     * int 材料1数量
     */
    private Integer material1Num;
    /**
     * int 材料2ID
     */
    private Integer material2Id;
    /**
     * int 材料2数量
     */
    private Integer material2Num;
    /**
     * int 材料3ID
     */
    private Integer material3Id;
    /**
     * int 材料3数量
     */
    private Integer material3Num;
    /**
     * int 材料4ID
     */
    private Integer material4Id;
    /**
     * int 材料4数量
     */
    private Integer material4Num;
    /**
     * int 材料5ID
     */
    private Integer material5Id;
    /**
     * int 材料5数量
     */
    private Integer material5Num;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"基础ID"}
        specialEquPos = CellParser.parseSimpleCell("SpecialEquPos", map, Integer.class); //int
        needPlayerLv = CellParser.parseSimpleCell("NeedPlayerLv", map, Integer.class); //int
        specialEquQuality = CellParser.parseSimpleCell("SpecialEquQuality", map, Integer.class); //string
        material1Id = CellParser.parseSimpleCell("Material1Id", map, Integer.class); //int
        material1Num = CellParser.parseSimpleCell("Material1Num", map, Integer.class); //int
        material2Id = CellParser.parseSimpleCell("Material2Id", map, Integer.class); //int
        material2Num = CellParser.parseSimpleCell("Material2Num", map, Integer.class); //int
        material3Id = CellParser.parseSimpleCell("Material3Id", map, Integer.class); //int
        material3Num = CellParser.parseSimpleCell("Material3Num", map, Integer.class); //int
        material4Id = CellParser.parseSimpleCell("Material4Id", map, Integer.class); //int
        material4Num = CellParser.parseSimpleCell("Material4Num", map, Integer.class); //int
        material5Id = CellParser.parseSimpleCell("Material5Id", map, Integer.class); //int
        material5Num = CellParser.parseSimpleCell("Material5Num", map, Integer.class); //int
    }


    public EquipmentPositionEnum getSpecialEquPos() {
        return EquipmentPositionEnum.valueOf(specialEquPos);
    }


    public ColorDetailTypeEnum getSpecialEquQuality() {
        return ColorDetailTypeEnum.valueOf(specialEquQuality);
    }

    /**
     * 徽章秘籍： 位置升级一个品级的消耗
     *
     * @param curColor 当前品级
     * @param position
     * @return
     */
    public static IdMaptoCount getUpOneQualityLvConsumes(ColorDetailTypeEnum curColor, EquipmentPositionEnum position) {
        IdMaptoCount ret = new IdMaptoCount();
        Table_BadgeCheatsUpQualityLvConsumes_Row row = getUpOneQualityRow(curColor, position);
        ret.add(new IdAndCount(row.material1Id, row.material1Num));
        ret.add(new IdAndCount(row.material2Id, row.material2Num));
        ret.add(new IdAndCount(row.material3Id, row.material3Num));
        ret.add(new IdAndCount(row.material4Id, row.material4Num));
        ret.add(new IdAndCount(row.material5Id, row.material5Num));
        return ret;
    }

    /**
     * 查找合适的升品行
     *
     * @param curColor
     * @param position
     * @return
     */
    public static Table_BadgeCheatsUpQualityLvConsumes_Row getUpOneQualityRow(ColorDetailTypeEnum curColor, EquipmentPositionEnum position) {
        List<Table_BadgeCheatsUpQualityLvConsumes_Row> rows = RootTc.getAll(Table_BadgeCheatsUpQualityLvConsumes_Row.class);
        for (Table_BadgeCheatsUpQualityLvConsumes_Row row : rows) {
            if (row.getSpecialEquPos() == position && row.getSpecialEquQuality() == curColor) {
                return row;
            }
        }
        String msg = String.format("未找到合适的升品消耗行! curColor=%s position=%s ", curColor, position);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 徽章秘籍： 位置升级多个品级的消耗
     *
     * @param needUpColor
     * @param position
     * @return
     */
    public static IdMaptoCount getUpSomeQualityConsumes(List<ColorDetailTypeEnum> needUpColor, EquipmentPositionEnum position) {
        IdMaptoCount ret = new IdMaptoCount();
        List<Table_BadgeCheatsUpQualityLvConsumes_Row> rows = RootTc.getAll(Table_BadgeCheatsUpQualityLvConsumes_Row.class);
        for (Table_BadgeCheatsUpQualityLvConsumes_Row row : rows) {
            if (row.getSpecialEquPos() == position) {
                if (needUpColor.contains(row.getSpecialEquQuality())) {
                    ret.add(new IdAndCount(row.material1Id, row.material1Num));
                    ret.add(new IdAndCount(row.material2Id, row.material2Num));
                    ret.add(new IdAndCount(row.material3Id, row.material3Num));
                    ret.add(new IdAndCount(row.material4Id, row.material4Num));
                    ret.add(new IdAndCount(row.material5Id, row.material5Num));
                }
            }
        }
        return ret;
    }

    public Integer getNeedPlayerLv() {
        return needPlayerLv;
    }
}
