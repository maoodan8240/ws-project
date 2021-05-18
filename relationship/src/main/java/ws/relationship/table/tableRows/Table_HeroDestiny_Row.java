package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.relationship.base.IdAndCount;

import java.util.Map;

public class Table_HeroDestiny_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 天命经验
     */
    private Integer fatalityEXP;
    /**
     * int 每次获得经验
     */
    private Integer getEXP;
    /**
     * int 攻击加成
     */
    private Integer attackPlus;
    /**
     * int 防御加成
     */
    private Integer defensePlus;
    /**
     * int 生命加成
     */
    private Integer hPPlus;
    /**
     * int 道具ID
     */
    private Integer itemId;
    /**
     * int 每次消耗道具
     */
    private Integer itemUse;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"基础ID"}
        fatalityEXP = CellParser.parseSimpleCell("FatalityEXP", map, Integer.class); //int
        getEXP = CellParser.parseSimpleCell("GetEXP", map, Integer.class); //int
        attackPlus = CellParser.parseSimpleCell("AttackPlus", map, Integer.class); //int
        defensePlus = CellParser.parseSimpleCell("DefensePlus", map, Integer.class); //int
        hPPlus = CellParser.parseSimpleCell("HPPlus", map, Integer.class); //int
        itemId = CellParser.parseSimpleCell("ItemId", map, Integer.class); //int
        itemUse = CellParser.parseSimpleCell("ItemUse", map, Integer.class); //int

    }

    public Integer getFatalityEXP() {
        return fatalityEXP;
    }

    public Integer getGetEXP() {
        return getEXP;
    }

    public Integer getAttackPlus() {
        return attackPlus;
    }

    public Integer getDefensePlus() {
        return defensePlus;
    }

    public Integer gethPPlus() {
        return hPPlus;
    }

    public Integer getItemId() {
        return itemId;
    }

    public Integer getItemUse() {
        return itemUse;
    }

    public IdAndCount getDestinyconsumes() {
        return new IdAndCount(getItemId(), getItemUse());
    }

}
