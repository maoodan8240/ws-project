package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.common.utils.general.TrueParser;
import ws.protos.EnumsProtos.MissionSmallTypeEnum;
import ws.protos.EnumsProtos.MissionTypeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;

import java.util.Map;

public class Table_Missions_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 任务类型
     */
    private Integer taskType;
    /**
     * int 任务小类型
     */
    private Integer taskSmallType;
    /**
     * int 是否启用
     */
    private Integer enable;

    /**
     * int 完成后继续显示
     */
    private Integer display;

    /**
     * int 任务开启等级
     */
    private Integer levelNeed;
    /**
     * int 前置任务
     */
    private Integer beforeTask;
    /**
     * int 后置任务
     */
    private Integer afterTask;
    /**
     * string 任务完成类型
     */
    private String completeType;
    /**
     * string 任务完成数量需求
     */
    private String completeCondition;
    /**
     * int 任务奖励1
     */
    private Integer taskReward1Id;
    /**
     * int 任务奖励1数量
     */
    private Integer taskReward1Number;
    /**
     * int 任务奖励2
     */
    private Integer taskReward2Id;
    /**
     * int 任务奖励2数量
     */
    private Integer taskReward2Number;
    /**
     * int 任务奖励3
     */
    private Integer taskReward3Id;
    /**
     * int 任务奖励3数量
     */
    private Integer taskReward3Number;
    /**
     * int 任务奖励4
     */
    private Integer taskReward4Id;
    /**
     * int 任务奖励4数量
     */
    private Integer taskReward4Number;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"任务ID"}
        taskType = CellParser.parseSimpleCell("TaskType", map, Integer.class); //int
        taskSmallType = CellParser.parseSimpleCell("TaskSmallType", map, Integer.class); //int
        enable = CellParser.parseSimpleCell("Enable", map, Integer.class); //int
        display = CellParser.parseSimpleCell("Display", map, Integer.class); //int
        levelNeed = CellParser.parseSimpleCell("LevelNeed", map, Integer.class); //int
        beforeTask = CellParser.parseSimpleCell("BeforeTask", map, Integer.class); //int
        afterTask = CellParser.parseSimpleCell("AfterTask", map, Integer.class); //int
        completeType = CellParser.parseSimpleCell("CompleteType", map, String.class); //int
        completeCondition = CellParser.parseSimpleCell("CompleteCondition", map, String.class); //int
        taskReward1Id = CellParser.parseSimpleCell("TaskReward1Id", map, Integer.class); //int
        taskReward1Number = CellParser.parseSimpleCell("TaskReward1Number", map, Integer.class); //int
        taskReward2Id = CellParser.parseSimpleCell("TaskReward2Id", map, Integer.class); //int
        taskReward2Number = CellParser.parseSimpleCell("TaskReward2Number", map, Integer.class); //int
        taskReward3Id = CellParser.parseSimpleCell("TaskReward3Id", map, Integer.class); //int
        taskReward3Number = CellParser.parseSimpleCell("TaskReward3Number", map, Integer.class); //int
        taskReward4Id = CellParser.parseSimpleCell("TaskReward4Id", map, Integer.class); //int
        taskReward4Number = CellParser.parseSimpleCell("TaskReward4Number", map, Integer.class); //int
    }


    public MissionTypeEnum getTaskType() {
        return MissionTypeEnum.valueOf(taskType);
    }

    public MissionSmallTypeEnum getTaskSmallType() {
        return MissionSmallTypeEnum.valueOf(taskSmallType);
    }

    public boolean getEnable() {
        return TrueParser.isTrue(enable);
    }

    public boolean getDisplay() {
        return TrueParser.isTrue(display);
    }

    public Integer getLevelNeed() {
        return levelNeed;
    }

    public Integer getAfterTask() {
        return afterTask;
    }

    public String getCompleteType() {
        return completeType;
    }

    public String getCompleteCondition() {
        return completeCondition;
    }

    public Integer getBeforeTask() {
        return beforeTask;
    }

    public IdMaptoCount getTaskReward() {
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        idMaptoCount.add(new IdAndCount(taskReward1Id, taskReward1Number));
        idMaptoCount.add(new IdAndCount(taskReward2Id, taskReward2Number));
        idMaptoCount.add(new IdAndCount(taskReward3Id, taskReward3Number));
        idMaptoCount.add(new IdAndCount(taskReward4Id, taskReward4Number));
        return idMaptoCount;
    }

}
