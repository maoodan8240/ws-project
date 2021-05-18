package ws.relationship.table.tableRows;

import org.apache.commons.lang3.StringUtils;
import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.table.table.utils.CellParser;
import ws.common.utils.general.TrueParser;
import ws.relationship.exception.TableRowLogicCheckFailedException;
import ws.relationship.table.RootTc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table_Activity_Realm_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 是否有效的活动
     */
    private Integer isEffective;
    /**
     * string 活动开始时间1
     */
    private String startTime1;
    /**
     * string 活动结束时间1
     */
    private String endTime1;
    /**
     * string 活动展示时间1
     */
    private String showEndTime1;
    /**
     * string 活动开始时间2
     */
    private int startTime2;
    /**
     * string 活动结束时间2
     */
    private int endTime2;
    /**
     * string 活动展示时间2
     */
    private int showEndTime2;
    /**
     * int 每日是否重置
     */
    private Integer dailyReset;
    /**
     * int 开服天数需求
     */
    private Integer serverOpenDays;
    /**
     * int 玩家等级需求
     */
    private Integer playerLevel;
    /**
     * int 玩家VIP等级需求
     */
    private Integer playerVipLevel;
    /**
     * int 服务器范围
     */
    private TupleCell<Integer> serverScope;
    /**
     * int 活动组
     */
    private Integer activityGroupId;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"服活动"}
        isEffective = CellParser.parseSimpleCell("IsEffective", map, Integer.class); //int
        startTime1 = CellParser.parseSimpleCell("StartTime1", map, String.class); //string
        endTime1 = CellParser.parseSimpleCell("EndTime1", map, String.class); //string
        showEndTime1 = CellParser.parseSimpleCell("ShowEndTime1", map, String.class); //string
        startTime2 = CellParser.parseSimpleCell("StartTime2", map, Integer.class); //string
        endTime2 = CellParser.parseSimpleCell("EndTime2", map, Integer.class); //string
        showEndTime2 = CellParser.parseSimpleCell("ShowEndTime2", map, Integer.class); //string
        dailyReset = CellParser.parseSimpleCell("DailyReset", map, Integer.class); //int
        serverOpenDays = CellParser.parseSimpleCell("ServerOpenDays", map, Integer.class); //int
        playerLevel = CellParser.parseSimpleCell("PlayerLevel", map, Integer.class); //int
        playerVipLevel = CellParser.parseSimpleCell("PlayerVipLevel", map, Integer.class); //int
        serverScope = CellParser.parseTupleCell("ServerScope", map, Integer.class); //string
        activityGroupId = CellParser.parseSimpleCell("ActivityGroupId", map, Integer.class); //int
        checkRow();
    }

    public boolean getIsEffective() {
        return TrueParser.isTrue(isEffective);
    }

    public String getStartTime1() {
        return startTime1;
    }

    public String getEndTime1() {
        return endTime1;
    }

    public String getShowEndTime1() {
        return showEndTime1;
    }

    public int getStartTime2() {
        return startTime2;
    }

    public int getEndTime2() {
        return endTime2;
    }

    public int getShowEndTime2() {
        return showEndTime2;
    }

    public boolean getDailyReset() {
        return TrueParser.isTrue(dailyReset);
    }

    public Integer getServerOpenDays() {
        return serverOpenDays;
    }

    public Integer getPlayerLevel() {
        return playerLevel;
    }

    public Integer getPlayerVipLevel() {
        return playerVipLevel;
    }

    public TupleCell<Integer> getServerScope() {
        return serverScope;
    }

    public Integer getActivityGroupId() {
        return activityGroupId;
    }


    private void checkRow() {
        if (!StringUtils.isBlank(this.startTime1)) {
            if (StringUtils.isBlank(this.endTime1) || StringUtils.isBlank(this.showEndTime1)) {
                throw new TableRowLogicCheckFailedException(this.getClass(), getId(), "startTime1不为空时，endTime1和showEndTime1都不能为！");
            }
        }
        if (this.startTime2 > 0) {
            if (this.endTime2 <= 0 || this.showEndTime2 <= 0) {
                throw new TableRowLogicCheckFailedException(this.getClass(), getId(), "startTime2>0时，endTime2和showEndTime2都不能<=0！");
            }
        }
    }


    /**
     * 服的所有活动
     *
     * @param outerRealmId
     * @return
     */
    public static List<Table_Activity_Realm_Row> outerRealmActivities(int outerRealmId) {
        List<Table_Activity_Realm_Row> rows = new ArrayList<>();
        List<Table_Activity_Realm_Row> all = RootTc.get(Table_Activity_Realm_Row.class).values();
        for (Table_Activity_Realm_Row row : all) {
            if (row.getServerScope().get(TupleCell.FIRST) <= outerRealmId && outerRealmId <= row.getServerScope().get(TupleCell.SECOND)) {
                rows.add(row);
            }
        }
        return rows;
    }
}
