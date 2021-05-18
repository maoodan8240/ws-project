package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;

import java.util.List;
import java.util.Map;

public class Table_PointReward_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 积分类型
     */
    private Integer pointType;
    /**
     * int 品质
     */
    private Integer quality;
    /**
     * int 积分数量
     */
    private Integer needPoints;
    /**
     * int 奖励
     */
    private Integer prize;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"ID"}
        pointType = CellParser.parseSimpleCell("PointType", map, Integer.class); //int
        quality = CellParser.parseSimpleCell("Quality", map, Integer.class); //int
        needPoints = CellParser.parseSimpleCell("NeedPoints", map, Integer.class); //int
        prize = CellParser.parseSimpleCell("Prize", map, Integer.class); //int
    }


    public Integer getPointType() {
        return pointType;
    }

    public Integer getQuality() {
        return quality;
    }

    public Integer getNeedPoints() {
        return needPoints;
    }

    public Integer getPrize() {
        return prize;
    }


    /**
     * 获取竞技场排名奖励列表(只允许领取一次)
     *
     * @return
     */
    public static Table_PointReward_Row getArenaRankRewardRow(int rank) {
        List<Table_PointReward_Row> tables = RootTc.get(Table_PointReward_Row.class).values();
        for (Table_PointReward_Row row : tables) {
            if (row.getPointType() != MagicNumbers.POINTREWARD_TYPE_ARENA_POINT) {
                continue;
            }
            if (row.getNeedPoints() == rank) {
                return row;
            }
        }
        String msg = String.format("无法获取当前竞技场排名的奖励行. rank=%s ", rank);
        throw new BusinessLogicMismatchConditionException(msg);
    }


    public static Table_PointReward_Row getUltimateTestRow(int score) {
        List<Table_PointReward_Row> tables = RootTc.get(Table_PointReward_Row.class).values();
        for (Table_PointReward_Row row : tables) {
            if (row.getPointType() != MagicNumbers.POINTREWARD_TYPE_ULTIMATETEST_POINT) {
                continue;
            }
            if (row.getNeedPoints() == score) {
                return row;
            }
        }
        String msg = String.format("无法获取当前终极试炼积分的奖励行. score=%s ", score);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 通过掉落库掉落
     *
     * @param curPlayerLv
     * @return
     */
    public IdMaptoCount getRewards(int curPlayerLv) {
        return Table_DropLibrary_Row.drop(this.prize, curPlayerLv);
    }
}
