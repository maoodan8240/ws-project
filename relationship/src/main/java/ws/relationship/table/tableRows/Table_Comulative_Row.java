package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.table.table.utils.CellParser;

import java.util.Map;

public class Table_Comulative_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 累计签到天数
     */
    private Integer totalSignDay;
    /**
     * string 奖品ID数量
     */
    private TupleListCell<Integer> rewardIdAndCount;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"ID"}
        totalSignDay = CellParser.parseSimpleCell("TotalSignDay", map, Integer.class); //int
        rewardIdAndCount = CellParser.parseTupleListCell("RewardIdAndCount", map, Integer.class); //string

    }

    public Integer getTotalSignDay() {
        return totalSignDay;
    }

    public TupleListCell<Integer> getRewardIdAndCount() {
        return rewardIdAndCount;
    }
}
