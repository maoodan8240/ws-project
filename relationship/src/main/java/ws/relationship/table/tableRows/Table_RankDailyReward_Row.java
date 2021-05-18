package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.table.table.utils.CellParser;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table_RankDailyReward_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;

    /**
     * int 排名类型
     */
    private Integer rankType;
    /**
     * int 排名上限
     */
    private Integer rankUp;
    /**
     * int 排名下限
     */
    private Integer rankDown;
    /**
     * int 积分
     */
    private Integer point;
    /**
     * string 奖励
     */
    private TupleListCell<Integer> reward;
    /**
     * int Vip限制
     */
    private Integer limitVip;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"主ID"}
        rankType = CellParser.parseSimpleCell("RankType", map, Integer.class); //int
        rankUp = CellParser.parseSimpleCell("RankUp", map, Integer.class); //int
        rankDown = CellParser.parseSimpleCell("RankDown", map, Integer.class); //int
        point = CellParser.parseSimpleCell("Point", map, Integer.class); //int
        reward = CellParser.parseTupleListCell("Reward", map, Integer.class); //string
        limitVip = CellParser.parseSimpleCell("LimitVip", map, Integer.class); //int
    }

    public IdMaptoCount getReward() {
        return IdMaptoCount.parse(reward);
    }

    public Integer getPoint() {
        return point;
    }

    public Integer getLimitVip() {
        return limitVip;
    }

    /**
     * 获取排名对应的奖励
     *
     * @param curRank
     * @return
     */
    public static IdMaptoCount getArenaRankRewards(int curRank) {
        List<Table_RankDailyReward_Row> rows = RootTc.get(Table_RankDailyReward_Row.class).values();
        for (Table_RankDailyReward_Row row : rows) {
            if (row.rankType != MagicNumbers.DAILYREWARD_TYPE_ARENA_RANK) {
                continue;
            }
            if (row.rankUp <= curRank && row.rankDown >= curRank) {
                return row.getReward();
            }
        }
        String msg = String.format("无法获取竞技场每日排名的结算奖励. curRank=%s ", curRank);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 获取积分行
     *
     * @param curPoint
     * @return
     */
    public static Table_RankDailyReward_Row getArenaPointRow(int curPoint) {
        List<Table_RankDailyReward_Row> rows = RootTc.get(Table_RankDailyReward_Row.class).values();
        for (Table_RankDailyReward_Row row : rows) {
            if (row.rankType != MagicNumbers.DAILYREWARD_TYPE_ARENA_POINT) {
                continue;
            }
            if (row.point == curPoint) {
                return row;
            }
        }
        String msg = String.format("无法获取当前竞技场每日积分的行. curPoint=%s ", curPoint);
        throw new BusinessLogicMismatchConditionException(msg);
    }


    /**
     * 获取所有积分行
     *
     * @return
     */
    public static List<Table_RankDailyReward_Row> getArenaPointRows() {
        List<Table_RankDailyReward_Row> re = new ArrayList<>();
        List<Table_RankDailyReward_Row> rows = RootTc.get(Table_RankDailyReward_Row.class).values();
        for (Table_RankDailyReward_Row row : rows) {
            if (row.rankType != MagicNumbers.DAILYREWARD_TYPE_ARENA_POINT) {
                continue;
            }
            re.add(row);
        }
        return re;
    }
}
