package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;

import java.util.Map;

public class Table_TreasureRefining_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 宝物ID
     */
    private Integer treasureId;
    /**
     * int 宝物精炼等级
     */
    private Integer refineLevel;
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

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"基础ID"}
        treasureId = CellParser.parseSimpleCell("TreasureId", map, Integer.class); //int
        refineLevel = CellParser.parseSimpleCell("RefineLevel", map, Integer.class); //int
        material1Id = CellParser.parseSimpleCell("Material1Id", map, Integer.class); //int
        material1Num = CellParser.parseSimpleCell("Material1Num", map, Integer.class); //int
        material2Id = CellParser.parseSimpleCell("Material2Id", map, Integer.class); //int
        material2Num = CellParser.parseSimpleCell("Material2Num", map, Integer.class); //int
        material3Id = CellParser.parseSimpleCell("Material3Id", map, Integer.class); //int
        material3Num = CellParser.parseSimpleCell("Material3Num", map, Integer.class); //int

    }

    public Integer getTreasureId() {
        return treasureId;
    }

    public Integer getRefineLevel() {
        return refineLevel;
    }

    public Integer getMaterial1Id() {
        return material1Id;
    }

    public Integer getMaterial1Num() {
        return material1Num;
    }

    public Integer getMaterial2Id() {
        return material2Id;
    }

    public Integer getMaterial2Num() {
        return material2Num;
    }

    public Integer getMaterial3Id() {
        return material3Id;
    }

    public Integer getMaterial3Num() {
        return material3Num;
    }


    private IdMaptoCount getRefiningAllConsumes() {
        IdMaptoCount re = new IdMaptoCount();
        if (getMaterial1Id() > 0 && getMaterial1Num() > 0) {
            re.add(new IdAndCount(getMaterial1Id(), getMaterial1Num()));
        }
        if (getMaterial2Id() > 0 && getMaterial2Num() > 0) {
            re.add(new IdAndCount(getMaterial2Id(), getMaterial2Num()));
        }
        if (getMaterial3Id() > 0 && getMaterial3Num() > 0) {
            re.add(new IdAndCount(getMaterial3Id(), getMaterial3Num()));
        }
        return re;
    }

    /**
     * 宝物精炼消耗的材料
     *
     * @param treasureTpId
     * @param refiningLevel
     * @return
     */
    public static IdMaptoCount getRefiningAllConsumes(int treasureTpId, int refiningLevel) {
        for (Table_TreasureRefining_Row row : RootTc.get(Table_TreasureRefining_Row.class).values()) {
            if (row.getTreasureId() == treasureTpId && row.getRefineLevel() == refiningLevel) {
                return row.getRefiningAllConsumes();
            }
        }
        String msg = String.format("根据 treasureTpId=%s refiningLevel=%s 找不到合适的Table_TreasureRefining_Row！", treasureTpId, refiningLevel);
        throw new BusinessLogicMismatchConditionException(msg);
    }
}
