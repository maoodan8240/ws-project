package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.protos.EnumsProtos.SystemModuleTypeEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.base.HeroAttrs;

import java.util.Map;

public class Table_CardWarSoul_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * string 战魂名称
     */
    private String warSoulNameId;
    /**
     * string 战魂描述
     */
    private String warSoulExplainId;
    /**
     * string 战魂ICON
     */
    private String warSoulIcon;
    /**
     * string 影响玩法类型
     */
    private Integer playType;
    /**
     * int 战魂位置
     */
    private Integer warSoulPos;
    /**
     * int 属性1编号
     */
    private Integer quality1Id;
    /**
     * int 属性1值
     */
    private Integer quality1Number;
    /**
     * int 属性1步长
     */
    private Integer quality1Step;
    /**
     * int 属性2编号
     */
    private Integer quality2Id;
    /**
     * int 属性2值
     */
    private Integer quality2Number;
    /**
     * int 属性2步长
     */
    private Integer quality2Step;
    /**
     * int 属性3编号
     */
    private Integer quality3Id;
    /**
     * int 属性3值
     */
    private Integer quality3Number;
    /**
     * int 属性3步长
     */
    private Integer quality3Step;
    /**
     * int 属性4编号
     */
    private Integer quality4Id;
    /**
     * int 属性4值
     */
    private Integer quality4Number;
    /**
     * int 属性4步长
     */
    private Integer quality4Step;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"战魂ID"}
        warSoulNameId = CellParser.parseSimpleCell("WarSoulNameId", map, String.class); //string
        warSoulExplainId = CellParser.parseSimpleCell("WarSoulExplainId", map, String.class); //string
        warSoulIcon = CellParser.parseSimpleCell("WarSoulIcon", map, String.class); //string
        playType = CellParser.parseSimpleCell("PlayType", map, Integer.class); //string
        warSoulPos = CellParser.parseSimpleCell("WarSoulPos", map, Integer.class); //int
        quality1Id = CellParser.parseSimpleCell("Quality1Id", map, Integer.class); //int
        quality1Number = CellParser.parseSimpleCell("Quality1Number", map, Integer.class); //int
        quality1Step = CellParser.parseSimpleCell("Quality1Step", map, Integer.class); //int
        quality2Id = CellParser.parseSimpleCell("Quality2Id", map, Integer.class); //int
        quality2Number = CellParser.parseSimpleCell("Quality2Number", map, Integer.class); //int
        quality2Step = CellParser.parseSimpleCell("Quality2Step", map, Integer.class); //int
        quality3Id = CellParser.parseSimpleCell("Quality3Id", map, Integer.class); //int
        quality3Number = CellParser.parseSimpleCell("Quality3Number", map, Integer.class); //int
        quality3Step = CellParser.parseSimpleCell("Quality3Step", map, Integer.class); //int
        quality4Id = CellParser.parseSimpleCell("Quality4Id", map, Integer.class); //int
        quality4Number = CellParser.parseSimpleCell("Quality4Number", map, Integer.class); //int
        quality4Step = CellParser.parseSimpleCell("Quality4Step", map, Integer.class); //int

    }


    public String getWarSoulNameId() {
        return warSoulNameId;
    }

    public String getWarSoulExplainId() {
        return warSoulExplainId;
    }

    public String getWarSoulIcon() {
        return warSoulIcon;
    }

    public SystemModuleTypeEnum getPlayType() {
        return SystemModuleTypeEnum.valueOf(playType);
    }

    public Integer getWarSoulPos() {
        return warSoulPos;
    }


    public HeroAttrs getHeroAttr(int curLv) {
        HeroAttrs heroAttrs = new HeroAttrs();
        heroAttrs.add(new HeroAttr(HeroAttrTypeEnum.valueOf(quality1Id), (curLv - 1) * quality1Step + quality1Number));
        heroAttrs.add(new HeroAttr(HeroAttrTypeEnum.valueOf(quality2Id), (curLv - 1) * quality2Step + quality2Number));
        heroAttrs.add(new HeroAttr(HeroAttrTypeEnum.valueOf(quality3Id), (curLv - 1) * quality3Step + quality3Number));
        heroAttrs.add(new HeroAttr(HeroAttrTypeEnum.valueOf(quality4Id), (curLv - 1) * quality4Step + quality4Number));
        return heroAttrs;
    }
}