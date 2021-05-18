package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.table.table.utils.CellParser;

import java.util.Map;

public class Table_Maps_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * string 地图背景
     */
    private String mapBG;
    /**
     * string 地图路线
     */
    private String mapWay;
    /**
     * int 地图等级需求
     */
    private Integer mapLevelNeed;
    /**
     * int 地图类型
     */
    private Integer mapType;
    /**
     * int 前置地图
     */
    private Integer beforeMap;
    /**
     * int 地图关卡起始ID
     */
    private Integer startStage;
    /**
     * int 宝箱1星数
     */
    private Integer chest1Star;
    /**
     * int 宝箱2星数
     */
    private Integer chest2Star;
    /**
     * int 宝箱3星数
     */
    private Integer chest3Star;
    /**
     * string 宝箱1
     */
    private TupleListCell<Integer> chest1;
    /**
     * string 宝箱2
     */
    private TupleListCell<Integer> chest2;
    /**
     * string 宝箱3
     */
    private TupleListCell<Integer> chest3;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {

        chest1 = CellParser.parseTupleListCell("Chest1", map, Integer.class); //string
        chest2 = CellParser.parseTupleListCell("Chest2", map, Integer.class); //string
        chest3 = CellParser.parseTupleListCell("Chest3", map, Integer.class); //string

        // id column = {columnName:"Id", columnDesc:"地图ID"}
        mapBG = CellParser.parseSimpleCell("MapBG", map, String.class); //string
        mapWay = CellParser.parseSimpleCell("MapWay", map, String.class); //string
        mapLevelNeed = CellParser.parseSimpleCell("MapLevelNeed", map, Integer.class); //int
        mapType = CellParser.parseSimpleCell("MapType", map, Integer.class); //int
        beforeMap = CellParser.parseSimpleCell("BeforeMap", map, Integer.class); //int

        startStage = CellParser.parseSimpleCell("StartStage", map, Integer.class); //int
        chest1Star = CellParser.parseSimpleCell("Chest1Star", map, Integer.class); //int
        chest2Star = CellParser.parseSimpleCell("Chest2Star", map, Integer.class); //int
        chest3Star = CellParser.parseSimpleCell("Chest3Star", map, Integer.class); //int


    }


    public String getMapBG() {
        return mapBG;
    }

    public String getMapWay() {
        return mapWay;
    }

    public Integer getMapLevelNeed() {
        return mapLevelNeed;
    }

    public Integer getMapType() {
        return mapType;
    }

    public Integer getBeforeMap() {
        return beforeMap;
    }


    public Integer getStartStage() {
        return startStage;
    }

    public Integer getChest1Star() {
        return chest1Star;
    }

    public Integer getChest2Star() {
        return chest2Star;
    }

    public Integer getChest3Star() {
        return chest3Star;
    }

    public TupleListCell<Integer> getChest1() {
        return chest1;
    }

    public TupleListCell<Integer> getChest2() {
        return chest2;
    }

    public TupleListCell<Integer> getChest3() {
        return chest3;
    }

}
