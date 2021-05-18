package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.table.table.utils.CellParser;
import ws.common.utils.general.TrueParser;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;

import java.util.List;
import java.util.Map;

public class Table_Sign_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 月份
     */
    private Integer month;
    /**
     * int 签到天数
     */
    private Integer signDay;
    /**
     * int 是否双倍
     */
    private Integer isDouble;
    /**
     * int 双倍需要VIP等级
     */
    private Integer vIPLv;
    /**
     * string 奖品ID数量
     */
    private TupleListCell<Integer> rewardIdAndCount;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"ID"}
        month = CellParser.parseSimpleCell("Month", map, Integer.class); //int
        signDay = CellParser.parseSimpleCell("SignDay", map, Integer.class); //int
        isDouble = CellParser.parseSimpleCell("IsDouble", map, Integer.class); //int
        vIPLv = CellParser.parseSimpleCell("VIPLv", map, Integer.class); //int
        rewardIdAndCount = CellParser.parseTupleListCell("RewardIdAndCount", map, Integer.class); //string
    }

    public boolean getIsDouble() {
        return TrueParser.isTrue(isDouble);
    }

    public Integer getvIPLv() {
        return vIPLv;
    }

    public IdMaptoCount getRewardIdAndCount() {
        return IdMaptoCount.parse(rewardIdAndCount);
    }


    /**
     * 获取签到行
     *
     * @param month       当前月份
     * @param nextSignDay 下一个签到的天数
     * @return
     */
    public static Table_Sign_Row getSignRow(int month, int nextSignDay) {
        List<Table_Sign_Row> rows = RootTc.get(Table_Sign_Row.class).values();
        for (Table_Sign_Row row : rows) {
            if (row.month == month && row.signDay == nextSignDay) {
                return row;
            }
        }
        String msg = String.format("未找到合适的签到行! month=%s nextSignDay=%s", month, nextSignDay);
        throw new BusinessLogicMismatchConditionException(msg);
    }

}
