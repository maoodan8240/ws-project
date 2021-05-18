package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;

import java.util.Map;

public class Table_Ultimate_Monster_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 怪物ID
     */
    private Integer monsterID;
    /**
     * int 攻击倍率
     */
    private Integer attackPro;
    /**
     * int 防御倍率
     */
    private Integer defencePro;
    /**
     * int 生命倍率
     */
    private Integer hpPro;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"ID", columnDesc:"ID"}
        monsterID = CellParser.parseSimpleCell("MonsterID", map, Integer.class); //int
        attackPro = CellParser.parseSimpleCell("AttackPro", map, Integer.class); //int
        defencePro = CellParser.parseSimpleCell("DefencePro", map, Integer.class); //int
        hpPro = CellParser.parseSimpleCell("HpPro", map, Integer.class); //int

    }

    public Integer getMonsterID() {
        return monsterID;
    }

    public Integer getAttackPro() {
        return attackPro;
    }

    public Integer getDefencePro() {
        return defencePro;
    }

    public Integer getHpPro() {
        return hpPro;
    }
}
