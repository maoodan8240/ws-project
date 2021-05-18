package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.table.table.utils.CellParser;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.exception.CmMessageIllegalArgumentException;
import ws.relationship.table.RootTc;

import java.util.List;
import java.util.Map;

public class Table_Favorable_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 阶段满级
     */
    private Integer classLevel;
    /**
     * int 后置阶段ID
     */
    private Integer afterClass;
    /**
     * string 好感度提升礼物
     */
    private TupleListCell<Integer> present;
    /**
     * int 阶段女性名称
     */
    private Integer classWomanName;
    /**
     * int 阶段男性名称
     */
    private Integer classManName;


    public Integer getClassLevel() {
        return classLevel;
    }

    public Integer getAfterClass() {
        return afterClass;
    }

    public TupleListCell<Integer> getPresent() {
        return present;
    }

    public Integer getClassWomanName() {
        return classWomanName;
    }

    public Integer getClassManName() {
        return classManName;
    }

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        present = CellParser.parseTupleListCell("present", map, Integer.class); //string

        // id column = {columnName:"Id", columnDesc:"阶段ID"}
        classLevel = CellParser.parseSimpleCell("ClassLevel", map, Integer.class); //int
        afterClass = CellParser.parseSimpleCell("AfterClass", map, Integer.class); //int
        classWomanName = CellParser.parseSimpleCell("ClassWomanName", map, Integer.class); //int
        classManName = CellParser.parseSimpleCell("ClassManName", map, Integer.class); //int

    }

    public static IdMaptoCount getBreakthroughPresent(int favorStage) {
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        if (!RootTc.get(Table_Favorable_Row.class).has(favorStage)) {
            String msg = String.format("Table_Favorable_Row favorStage =%s 不存在", favorStage);
            throw new CmMessageIllegalArgumentException(msg);
        }
        List<TupleCell<Integer>> tupleCellList = RootTc.get(Table_Favorable_Row.class).get(favorStage).getPresent().getAll();
        for (TupleCell<Integer> tuple : tupleCellList) {
            idMaptoCount.add(new IdAndCount(tuple.get(TupleCell.FIRST), tuple.get(TupleCell.SECOND)));
        }
        return idMaptoCount;
    }

}
