package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;

import java.util.Map;

public class Table_New_Buff_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int BUFF名称
     */
    private String bUFFNameId;
    /**
     * int BUFF描述
     */
    private String bUFFExplainId;
    /**
     * string BUFF特效
     */
    private String bUFFSpecial;
    /**
     * string 英文对照
     */
    private String english;
    /**
     * int 特殊引用
     */
    private Integer specialIndex;
    /**
     * int 影响属性编号
     */
    private Integer bUFFProperty;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"BUFFID"}
        bUFFNameId = CellParser.parseSimpleCell("BUFFNameId", map, String.class); //int
        bUFFExplainId = CellParser.parseSimpleCell("BUFFExplainId", map, String.class); //int
        bUFFSpecial = CellParser.parseSimpleCell("BUFFSpecial", map, String.class); //string
        english = CellParser.parseSimpleCell("English", map, String.class); //string
        specialIndex = CellParser.parseSimpleCell("SpecialIndex", map, Integer.class); //int
        bUFFProperty = CellParser.parseSimpleCell("BUFFProperty", map, Integer.class); //int

    }

    public String getbUFFNameId() {
        return bUFFNameId;
    }

    public String getbUFFExplainId() {
        return bUFFExplainId;
    }

    public String getbUFFSpecial() {
        return bUFFSpecial;
    }

    public String getEnglish() {
        return english;
    }

    public Integer getSpecialIndex() {
        return specialIndex;
    }

    public HeroAttrTypeEnum getbUFFProperty() {
        return HeroAttrTypeEnum.valueOf(bUFFProperty);
    }
}
