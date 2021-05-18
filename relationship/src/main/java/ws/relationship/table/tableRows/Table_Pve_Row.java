package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.ListCell;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.table.table.utils.CellParser;

import java.util.Map;

public class Table_Pve_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;

    /**
     * int 所在地图ID
     */
    private Integer mapId;
    /**
     * int 关卡需求等级
     */
    private Integer stageLv;
    /**
     * int 首次通关后是否再挑战
     */
    private Integer isFirst;
    /**
     * int 每天挑战次数
     */
    private Integer dailyNumber;
    /**
     * string 关卡背景
     */
    private String stageBG;
    /**
     * string 关卡图标
     */
    private String stageIcon;
    /**
     * int 前置关卡
     */
    private ListCell<Integer> beforeStage;
    /**
     * string 首次掉落
     */
    private ListCell<Integer> firstDrop;
    /**
     * string 活动掉落
     */
    private ListCell<Integer> activityDrop;
    /**
     * string 关卡掉落
     */
    private ListCell<Integer> stageDrop;
    /**
     * int 关卡宝箱
     */
    private TupleListCell<Integer> stageChest;
    /**
     * string 扫荡额外掉落
     */
    private ListCell<Integer> sweepDrop;
    /**
     * string 关卡开启时间
     */
    private ListCell<Integer> stageStart;
    /**
     * int 体力消耗
     */
    private Integer spiritUse;
    /**
     * int 通关角色经验
     */
    private Integer stageRoleExp;
    /**
     * int 通关卡片经验
     */
    private Integer stageCardExp;
    /**
     * int 通关金钱
     */
    private Integer stageGold;
    /**
     * int 推荐战斗力
     */
    private Integer recFight;
    /**
     * int 关卡回合
     */
    private Integer stageRound;
    /**
     * int 1回合前排怪1
     */
    private Integer rd1Before1;
    /**
     * int 1回合前排怪2
     */
    private Integer rd1Before2;
    /**
     * int 1回合前排怪3
     */
    private Integer rd1Before3;
    /**
     * int 1回合后排1
     */
    private Integer rd1After1;
    /**
     * int 1回合后排2
     */
    private Integer rd1After2;
    /**
     * int 1回合后排3
     */
    private Integer rd1After3;
    /**
     * int 2回合前排怪1
     */
    private Integer rd2Before1;
    /**
     * int 2回合前排怪2
     */
    private Integer rd2Before2;
    /**
     * int 2回合前排怪3
     */
    private Integer rd2Before3;
    /**
     * int 2回合后排1
     */
    private Integer rd2After1;
    /**
     * int 2回合后排2
     */
    private Integer rd2After2;
    /**
     * int 2回合后排3
     */
    private Integer rd2After3;
    /**
     * int 3回合前排怪1
     */
    private Integer rd3Before1;
    /**
     * int 3回合前排怪2
     */
    private Integer rd3Before2;
    /**
     * int 3回合前排怪3
     */
    private Integer rd3Before3;
    /**
     * int 3回合后排1
     */
    private Integer rd3After1;
    /**
     * int 3回合后排2
     */
    private Integer rd3After2;
    /**
     * int 3回合后排3
     */
    private Integer rd3After3;
    /**
     * string 掉落显示
     */
    private String dropShow;
    /**
     * int 怪物显示
     */
    private ListCell<Integer> monsterBoss;

    public Integer getMapId() {
        return mapId;
    }

    public Integer getStageLv() {
        return stageLv;
    }

    public Integer getIsFirst() {
        return isFirst;
    }

    public Integer getDailyNumber() {
        return dailyNumber;
    }

    public String getStageBG() {
        return stageBG;
    }

    public String getStageIcon() {
        return stageIcon;
    }

    public ListCell<Integer> getBeforeStage() {
        return beforeStage;
    }

    public ListCell<Integer> getFirstDrop() {
        return firstDrop;
    }

    public ListCell<Integer> getActivityDrop() {
        return activityDrop;
    }

    public ListCell<Integer> getStageDrop() {
        return stageDrop;
    }

    public TupleListCell<Integer> getStageChest() {
        return stageChest;
    }

    public ListCell<Integer> getSweepDrop() {
        return sweepDrop;
    }

    public ListCell<Integer> getStageStart() {
        return stageStart;
    }

    public Integer getSpiritUse() {
        return spiritUse;
    }

    public Integer getStageRoleExp() {
        return stageRoleExp;
    }

    public Integer getStageCardExp() {
        return stageCardExp;
    }

    public Integer getStageGold() {
        return stageGold;
    }

    public Integer getRecFight() {
        return recFight;
    }

    public Integer getStageRound() {
        return stageRound;
    }

    public Integer getRd1Before1() {
        return rd1Before1;
    }

    public Integer getRd1Before2() {
        return rd1Before2;
    }

    public Integer getRd1Before3() {
        return rd1Before3;
    }

    public Integer getRd1After1() {
        return rd1After1;
    }

    public Integer getRd1After2() {
        return rd1After2;
    }

    public Integer getRd1After3() {
        return rd1After3;
    }

    public Integer getRd2Before1() {
        return rd2Before1;
    }

    public Integer getRd2Before2() {
        return rd2Before2;
    }

    public Integer getRd2Before3() {
        return rd2Before3;
    }

    public Integer getRd2After1() {
        return rd2After1;
    }

    public Integer getRd2After2() {
        return rd2After2;
    }

    public Integer getRd2After3() {
        return rd2After3;
    }

    public Integer getRd3Before1() {
        return rd3Before1;
    }

    public Integer getRd3Before2() {
        return rd3Before2;
    }

    public Integer getRd3Before3() {
        return rd3Before3;
    }

    public Integer getRd3After1() {
        return rd3After1;
    }

    public Integer getRd3After2() {
        return rd3After2;
    }

    public Integer getRd3After3() {
        return rd3After3;
    }

    public String getDropShow() {
        return dropShow;
    }

    public ListCell<Integer> getMonsterBoss() {
        return monsterBoss;
    }

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        firstDrop = CellParser.parseListCell("FirstDrop", map, Integer.class); //string
        activityDrop = CellParser.parseListCell("ActivityDrop", map, Integer.class); //string
        stageDrop = CellParser.parseListCell("StageDrop", map, Integer.class); //string
        stageChest = CellParser.parseTupleListCell("StageChest", map, Integer.class); //int
        beforeStage = CellParser.parseListCell("BeforeStage", map, Integer.class); //int
        monsterBoss = CellParser.parseListCell("MonsterBoss", map, Integer.class); //int
        stageStart = CellParser.parseListCell("StageStart", map, Integer.class); //string
        sweepDrop = CellParser.parseListCell("SweepDrop", map, Integer.class); //string

        // id column = {columnName:"Id", columnDesc:"关卡ID"}
        mapId = CellParser.parseSimpleCell("MapId", map, Integer.class); //int
        stageLv = CellParser.parseSimpleCell("StageLv", map, Integer.class); //int
        isFirst = CellParser.parseSimpleCell("IsFirst", map, Integer.class); //int
        dailyNumber = CellParser.parseSimpleCell("DailyNumber", map, Integer.class); //int
        stageBG = CellParser.parseSimpleCell("StageBG", map, String.class); //string
        stageIcon = CellParser.parseSimpleCell("StageIcon", map, String.class); //string
        spiritUse = CellParser.parseSimpleCell("SpiritUse", map, Integer.class); //int
        stageRoleExp = CellParser.parseSimpleCell("StageRoleExp", map, Integer.class); //int
        stageCardExp = CellParser.parseSimpleCell("StageCardExp", map, Integer.class); //int
        stageGold = CellParser.parseSimpleCell("StageGold", map, Integer.class); //int
        recFight = CellParser.parseSimpleCell("RecFight", map, Integer.class); //int
        stageRound = CellParser.parseSimpleCell("StageRound", map, Integer.class); //int
        rd1Before1 = CellParser.parseSimpleCell("Rd1Before1", map, Integer.class); //int
        rd1Before2 = CellParser.parseSimpleCell("Rd1Before2", map, Integer.class); //int
        rd1Before3 = CellParser.parseSimpleCell("Rd1Before3", map, Integer.class); //int
        rd1After1 = CellParser.parseSimpleCell("Rd1After1", map, Integer.class); //int
        rd1After2 = CellParser.parseSimpleCell("Rd1After2", map, Integer.class); //int
        rd1After3 = CellParser.parseSimpleCell("Rd1After3", map, Integer.class); //int
        rd2Before1 = CellParser.parseSimpleCell("Rd2Before1", map, Integer.class); //int
        rd2Before2 = CellParser.parseSimpleCell("Rd2Before2", map, Integer.class); //int
        rd2Before3 = CellParser.parseSimpleCell("Rd2Before3", map, Integer.class); //int
        rd2After1 = CellParser.parseSimpleCell("Rd2After1", map, Integer.class); //int
        rd2After2 = CellParser.parseSimpleCell("Rd2After2", map, Integer.class); //int
        rd2After3 = CellParser.parseSimpleCell("Rd2After3", map, Integer.class); //int
        rd3Before1 = CellParser.parseSimpleCell("Rd3Before1", map, Integer.class); //int
        rd3Before2 = CellParser.parseSimpleCell("Rd3Before2", map, Integer.class); //int
        rd3Before3 = CellParser.parseSimpleCell("Rd3Before3", map, Integer.class); //int
        rd3After1 = CellParser.parseSimpleCell("Rd3After1", map, Integer.class); //int
        rd3After2 = CellParser.parseSimpleCell("Rd3After2", map, Integer.class); //int
        rd3After3 = CellParser.parseSimpleCell("Rd3After3", map, Integer.class); //int
        dropShow = CellParser.parseSimpleCell("DropShow", map, String.class); //string


    }

}
