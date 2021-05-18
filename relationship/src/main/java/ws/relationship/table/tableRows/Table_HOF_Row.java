package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.ListCell;
import ws.common.table.table.utils.CellParser;
import ws.relationship.table.RootTc;

import java.util.List;
import java.util.Map;

public class Table_HOF_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 卡片ID
     */
    private Integer cardId;
    /**
     * string 食品ID
     */
    private ListCell<Integer> foodsId;
    /**
     * int 属性1编号
     */
    private Integer quality1Id;
    /**
     * int 属性1值
     */
    private Integer quality1Number;
    /**
     * int 属性2编号
     */
    private Integer quality2Id;
    /**
     * int 属性2值
     */
    private Integer quality2Number;
    /**
     * int 属性3编号
     */
    private Integer quality3Id;
    /**
     * int 属性3值
     */
    private Integer quality3Number;
    /**
     * int 属性4编号
     */
    private Integer quality4Id;
    /**
     * int 属性4值
     */
    private Integer quality4Number;
    /**
     * int 属性5编号
     */
    private Integer quality5Id;
    /**
     * int 属性5值
     */
    private Integer quality5Number;


    public Integer getCardId() {
        return cardId;
    }

    public ListCell<Integer> getFoodsId() {
        return foodsId;
    }

    public Integer getQuality1Id() {
        return quality1Id;
    }

    public Integer getQuality1Number() {
        return quality1Number;
    }

    public Integer getQuality2Id() {
        return quality2Id;
    }

    public Integer getQuality2Number() {
        return quality2Number;
    }

    public Integer getQuality3Id() {
        return quality3Id;
    }

    public Integer getQuality3Number() {
        return quality3Number;
    }

    public Integer getQuality4Id() {
        return quality4Id;
    }

    public Integer getQuality4Number() {
        return quality4Number;
    }

    public Integer getQuality5Id() {
        return quality5Id;
    }

    public Integer getQuality5Number() {
        return quality5Number;
    }

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        foodsId = CellParser.parseListCell("FoodsId", map, Integer.class); //string

        // id column = {columnName:"Id", columnDesc:"战队ID"}
        cardId = CellParser.parseSimpleCell("CardId", map, Integer.class); //int
        quality1Id = CellParser.parseSimpleCell("Quality1Id", map, Integer.class); //int
        quality1Number = CellParser.parseSimpleCell("Quality1Number", map, Integer.class); //int
        quality2Id = CellParser.parseSimpleCell("Quality2Id", map, Integer.class); //int
        quality2Number = CellParser.parseSimpleCell("Quality2Number", map, Integer.class); //int
        quality3Id = CellParser.parseSimpleCell("Quality3Id", map, Integer.class); //int
        quality3Number = CellParser.parseSimpleCell("Quality3Number", map, Integer.class); //int
        quality4Id = CellParser.parseSimpleCell("Quality4Id", map, Integer.class); //int
        quality4Number = CellParser.parseSimpleCell("Quality4Number", map, Integer.class); //int
        quality5Id = CellParser.parseSimpleCell("Quality5Id", map, Integer.class); //int
        quality5Number = CellParser.parseSimpleCell("Quality5Number", map, Integer.class); //int

    }


    public static List<Integer> getFoodIdList(int heroId) {
        Table_HOF_Row row = RootTc.get(Table_HOF_Row.class).get(heroId);
        List<Integer> list = row.getFoodsId().getAll();
        return list;
    }

}
