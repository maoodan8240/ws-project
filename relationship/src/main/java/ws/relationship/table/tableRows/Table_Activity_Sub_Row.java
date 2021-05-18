package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.table.table.utils.CellParser;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;

import java.util.Map;

public class Table_Activity_Sub_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 组活动Id
     */
    private Integer groupId;
    /**
     * string 活动完成类型
     */
    private String completeType;
    /**
     * string 活动完成数量需求
     */
    private String completeCondition;
    /**
     * int 完成条件-玩家等级需求
     */
    private Integer completePlayerLevel;
    /**
     * int 完成条件-玩家VIP等级需求
     */
    private Integer completePlayerVipLevel;
    /**
     * int 兑换消耗
     */
    private TupleListCell<Integer> exchangeConsume;
    /**
     * int 兑换的次数
     */
    private Integer canExchangeTimes;
    /**
     * int 奖励1
     */
    private Integer reward1Id;
    /**
     * int 奖励1数量
     */
    private Integer reward1Number;
    /**
     * int 奖励2
     */
    private Integer reward2Id;
    /**
     * int 奖励2数量
     */
    private Integer reward2Number;
    /**
     * int 奖励3
     */
    private Integer reward3Id;
    /**
     * int 奖励3数量
     */
    private Integer reward3Number;
    /**
     * int 奖励4
     */
    private Integer reward4Id;
    /**
     * int 奖励4数量
     */
    private Integer reward4Number;
    /**
     * int 奖励5
     */
    private Integer reward5Id;
    /**
     * int 奖励5数量
     */
    private Integer reward5Number;
    /**
     * string 子活动的描述
     */
    private String activityDes;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"Id"}
        groupId = CellParser.parseSimpleCell("GroupId", map, Integer.class); //int
        completeType = CellParser.parseSimpleCell("CompleteType", map, String.class); //string
        completeCondition = CellParser.parseSimpleCell("CompleteCondition", map, String.class); //string
        completePlayerLevel = CellParser.parseSimpleCell("CompletePlayerLevel", map, Integer.class); //int
        completePlayerVipLevel = CellParser.parseSimpleCell("CompletePlayerVipLevel", map, Integer.class); //int
        exchangeConsume = CellParser.parseTupleListCell("ExchangeConsume", map, Integer.class); //string
        canExchangeTimes = CellParser.parseSimpleCell("CanExchangeTimes", map, Integer.class); //int
        reward1Id = CellParser.parseSimpleCell("Reward1Id", map, Integer.class); //int
        reward1Number = CellParser.parseSimpleCell("Reward1Number", map, Integer.class); //int
        reward2Id = CellParser.parseSimpleCell("Reward2Id", map, Integer.class); //int
        reward2Number = CellParser.parseSimpleCell("Reward2Number", map, Integer.class); //int
        reward3Id = CellParser.parseSimpleCell("Reward3Id", map, Integer.class); //int
        reward3Number = CellParser.parseSimpleCell("Reward3Number", map, Integer.class); //int
        reward4Id = CellParser.parseSimpleCell("Reward4Id", map, Integer.class); //int
        reward4Number = CellParser.parseSimpleCell("Reward4Number", map, Integer.class); //int
        reward5Id = CellParser.parseSimpleCell("Reward5Id", map, Integer.class); //int
        reward5Number = CellParser.parseSimpleCell("Reward5Number", map, Integer.class); //int
        activityDes = CellParser.parseSimpleCell("ActivityDes", map, String.class); //string
    }

    public Integer getGroupId() {
        return groupId;
    }

    public String getCompleteType() {
        return completeType;
    }

    public String getCompleteCondition() {
        return completeCondition;
    }

    public Integer getCompletePlayerLevel() {
        return completePlayerLevel;
    }

    public Integer getCompletePlayerVipLevel() {
        return completePlayerVipLevel;
    }

    public IdMaptoCount getExchangeConsume() {
        return IdMaptoCount.parse(exchangeConsume);
    }

    public Integer getCanExchangeTimes() {
        return canExchangeTimes;
    }

    public String getActivityDes() {
        return activityDes;
    }


    public IdMaptoCount getRewards() {
        // IdMaptoCount 回自动过滤id或者count==0的项
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        idMaptoCount.add(new IdAndCount(reward1Id, reward1Number));
        idMaptoCount.add(new IdAndCount(reward2Id, reward2Number));
        idMaptoCount.add(new IdAndCount(reward3Id, reward3Number));
        idMaptoCount.add(new IdAndCount(reward4Id, reward4Number));
        idMaptoCount.add(new IdAndCount(reward5Id, reward5Number));
        return idMaptoCount;

    }
}
