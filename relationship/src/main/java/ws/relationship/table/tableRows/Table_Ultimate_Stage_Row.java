package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.HardTypeEnum;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;
import ws.relationship.topLevelPojos.common.Iac;
import ws.relationship.utils.RandomUtils;
import ws.relationship.utils.RandomUtils.Entity;

import java.util.List;
import java.util.Map;

public class Table_Ultimate_Stage_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 层类型
     */
    private Integer levelType;
    /**
     * int 简单战斗力百分比
     */
    private Integer easy;
    /**
     * string 简单等级浮动
     */
    private TupleCell<Integer> easyArea;
    /**
     * string 简单怪物星级浮动
     */
    private TupleCell<Integer> easyMonsterStar;
    /**
     * int 中等战斗力百分比
     */
    private Integer normal;
    /**
     * string 中等等级浮动
     */
    private TupleCell<Integer> normalArea;
    /**
     * string 中等怪物星级浮动
     */
    private TupleCell<Integer> normalMonsterStar;
    /**
     * int 困难战斗力百分比
     */
    private Integer hard;
    /**
     * string 困难等级浮动
     */
    private TupleCell<Integer> hardArea;
    /**
     * string 困难怪物星级浮动
     */
    private TupleCell<Integer> hardMonsterStar;
    /**
     * string 奖励
     */
    private Integer prize;
    /**
     * string 开箱子
     */
    private Integer boxPrize;
    /**
     * string 状态1消耗积分
     */
    private TupleListCell<Integer> buff1Score;
    /**
     * string 状态2消耗积分
     */
    private TupleListCell<Integer> buff2Score;
    /**
     * string 状态3消耗积分
     */
    private TupleListCell<Integer> buff3Score;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        easyArea = CellParser.parseTupleCell("EasyArea", map, Integer.class); //string
        easyMonsterStar = CellParser.parseTupleCell("EasyMonsterStar", map, Integer.class); //string
        normalArea = CellParser.parseTupleCell("NormalArea", map, Integer.class); //string
        normalMonsterStar = CellParser.parseTupleCell("NormalMonsterStar", map, Integer.class); //string
        hardArea = CellParser.parseTupleCell("HardArea", map, Integer.class); //string
        hardMonsterStar = CellParser.parseTupleCell("HardMonsterStar", map, Integer.class); //string
        buff1Score = CellParser.parseTupleListCell("Buff1Score", map, Integer.class); //string
        buff2Score = CellParser.parseTupleListCell("Buff2Score", map, Integer.class); //string
        buff3Score = CellParser.parseTupleListCell("Buff3Score", map, Integer.class); //string
        boxPrize = CellParser.parseSimpleCell("BoxPrize", map, Integer.class); //string

        // id column = {columnName:"ID", columnDesc:"ID"}
        prize = CellParser.parseSimpleCell("Prize", map, Integer.class); //int
        levelType = CellParser.parseSimpleCell("LevelType", map, Integer.class); //int
        easy = CellParser.parseSimpleCell("Easy", map, Integer.class); //int
        normal = CellParser.parseSimpleCell("Normal", map, Integer.class); //int
        hard = CellParser.parseSimpleCell("Hard", map, Integer.class); //int

    }

    public Integer getLevelType() {
        return levelType;
    }

    public Integer getEasy() {
        return easy;
    }


    public Integer getNormal() {
        return normal;
    }


    public Integer getHard() {
        return hard;
    }


    public Integer getPrize() {
        return prize;
    }

    public Integer getBoxPrize() {
        return boxPrize;
    }

    public TupleCell<Integer> getEasyArea() {
        return easyArea;
    }

    public TupleCell<Integer> getEasyMonsterStar() {
        return easyMonsterStar;
    }

    public TupleCell<Integer> getNormalArea() {
        return normalArea;
    }

    public TupleCell<Integer> getNormalMonsterStar() {
        return normalMonsterStar;
    }

    public TupleCell<Integer> getHardArea() {
        return hardArea;
    }

    public TupleCell<Integer> getHardMonsterStar() {
        return hardMonsterStar;
    }


    public Iac getBuff1Score() {
        return randomBuff(buff1Score);
    }

    public Iac getBuff2Score() {
        return randomBuff(buff2Score);
    }

    public Iac getBuff3Score() {
        return randomBuff(buff3Score);
    }

    /**
     * 获取匹配战斗力
     *
     * @param stageLevel
     * @param hardLevel
     * @return
     */
    public static int getMathBattleByHardLevelandStageLevel(int stageLevel, HardTypeEnum hardLevel) {
        Table_Ultimate_Stage_Row stageRow = RootTc.get(Table_Ultimate_Stage_Row.class).get(stageLevel);
        switch (hardLevel) {
            case EASY:
                return stageRow.getEasy();
            case NORMAL:
                return stageRow.getNormal();
            case HARD:
                return stageRow.getHard();

        }
        String msg = String.format("爬塔难度传入错误 stageLevel=%s,hardLevel=%s", stageLevel, hardLevel);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 获取怪物星级浮动
     *
     * @param stageLevel
     * @param hardLevel
     * @return
     */
    public static TupleCell<Integer> getMonsterStarByHardLevelandStageLevel(int stageLevel, HardTypeEnum hardLevel) {
        Table_Ultimate_Stage_Row stageRow = RootTc.get(Table_Ultimate_Stage_Row.class).get(stageLevel);
        switch (hardLevel) {
            case EASY:
                return stageRow.getEasyMonsterStar();
            case NORMAL:
                return stageRow.getNormalMonsterStar();
            case HARD:
                return stageRow.getHardMonsterStar();
        }
        String msg = String.format("爬塔难度传入错误 stageLevel=%s,hardLevel=%s", stageLevel, hardLevel);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 获取等级浮动
     *
     * @param stageLevel
     * @param hardLevel
     * @return
     */
    public static TupleCell<Integer> getStageLevelArea(int stageLevel, HardTypeEnum hardLevel) {
        Table_Ultimate_Stage_Row stageRow = RootTc.get(Table_Ultimate_Stage_Row.class).get(stageLevel);
        switch (hardLevel) {
            case EASY:
                return stageRow.getEasyArea();
            case NORMAL:
                return stageRow.getNormalArea();
            case HARD:
                return stageRow.getHardArea();
        }
        String msg = String.format("爬塔难度传入错误 stageLevel=%s,hardLevel=%s", stageLevel, hardLevel);
        throw new BusinessLogicMismatchConditionException(msg);
    }


    public Iac randomBuff(TupleListCell<Integer> tupleListCell) {
        List<Entity<Iac>> entityList = RandomUtils.parseTupleList(tupleListCell);
        Entity<Iac> entity = RandomUtils.random(entityList);
        if (entity == null) {
            String msg = "Table_Ultimate_Stage_Row , BufferScore权重掉落为空，检查表是否有错！";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        return entity.getObject();
    }
}
