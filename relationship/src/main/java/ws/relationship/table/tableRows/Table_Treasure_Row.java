package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.ListCell;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.ColorDetailTypeEnum;

import java.util.Map;

public class Table_Treasure_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 宝物名称
     */
    private Integer treasureNameId;
    /**
     * int 宝物描述
     */
    private Integer treasureExplainId;
    /**
     * string 宝物ICON
     */
    private String treasureIcon;
    /**
     * int 宝物部位
     */
    private Integer treasurePos;
    /**
     * int 宝物品质
     */
    private Integer treasureQuality;
    /**
     * int 攻击
     */
    private Integer attack;
    /**
     * int 防御
     */
    private Integer defense;
    /**
     * int 生命
     */
    private Integer hP;
    /**
     * int 攻击步长
     */
    private Integer attackStep;
    /**
     * int 防御步长
     */
    private Integer defenseStep;
    /**
     * int 生命步长
     */
    private Integer hPStep;
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
     * int 攻击加成步长
     */
    private Integer attackPlusStep;
    /**
     * int 防御加成步长
     */
    private Integer defensePlusStep;
    /**
     * int 生命加成步长
     */
    private Integer hPPlusStep;
    /**
     * int 精炼攻击步长
     */
    private Integer refineAttack;
    /**
     * int 精炼攻击加成步长
     */
    private Integer refineAttackPlusStep;
    /**
     * int 精炼生命步长
     */
    private Integer refineHP;
    /**
     * int 精炼生命加成步长
     */
    private Integer refineHPPlusStep;
    /**
     * int 宝物经验
     */
    private Integer treasureEXP;
    /**
     * int 宝物碎片
     */
    private ListCell<Integer> treasurePiece1;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"宝物ID"}
        treasureNameId = CellParser.parseSimpleCell("TreasureNameId", map, Integer.class); //int
        treasureExplainId = CellParser.parseSimpleCell("TreasureExplainId", map, Integer.class); //int
        treasureIcon = CellParser.parseSimpleCell("TreasureIcon", map, String.class); //string
        treasurePos = CellParser.parseSimpleCell("TreasurePos", map, Integer.class); //int
        treasureQuality = CellParser.parseSimpleCell("TreasureQuality", map, Integer.class); //int
        attack = CellParser.parseSimpleCell("Attack", map, Integer.class); //int
        defense = CellParser.parseSimpleCell("Defense", map, Integer.class); //int
        hP = CellParser.parseSimpleCell("HP", map, Integer.class); //int
        attackStep = CellParser.parseSimpleCell("AttackStep", map, Integer.class); //int
        defenseStep = CellParser.parseSimpleCell("DefenseStep", map, Integer.class); //int
        hPStep = CellParser.parseSimpleCell("HPStep", map, Integer.class); //int
        attackPlus = CellParser.parseSimpleCell("AttackPlus", map, Integer.class); //int
        defensePlus = CellParser.parseSimpleCell("DefensePlus", map, Integer.class); //int
        hPPlus = CellParser.parseSimpleCell("HPPlus", map, Integer.class); //int
        attackPlusStep = CellParser.parseSimpleCell("AttackPlusStep", map, Integer.class); //int
        defensePlusStep = CellParser.parseSimpleCell("DefensePlusStep", map, Integer.class); //int
        hPPlusStep = CellParser.parseSimpleCell("HPPlusStep", map, Integer.class); //int
        refineAttack = CellParser.parseSimpleCell("RefineAttack", map, Integer.class); //int
        refineAttackPlusStep = CellParser.parseSimpleCell("RefineAttackPlusStep", map, Integer.class); //int
        refineHP = CellParser.parseSimpleCell("RefineHP", map, Integer.class); //int
        refineHPPlusStep = CellParser.parseSimpleCell("RefineHPPlusStep", map, Integer.class); //int
        treasureEXP = CellParser.parseSimpleCell("TreasureEXP", map, Integer.class); //int
        treasurePiece1 = CellParser.parseListCell("TreasurePiece1", map, Integer.class); //int
    }


    public Integer getTreasureNameId() {
        return treasureNameId;
    }

    public Integer getTreasureExplainId() {
        return treasureExplainId;
    }

    public String getTreasureIcon() {
        return treasureIcon;
    }

    public Integer getTreasurePos() {
        return treasurePos;
    }

    public ColorDetailTypeEnum getTreasureQuality() {
        return ColorDetailTypeEnum.valueOf(treasureQuality);
    }

    public Integer getAttack() {
        return attack;
    }

    public Integer getDefense() {
        return defense;
    }

    public Integer gethP() {
        return hP;
    }

    public Integer getAttackStep() {
        return attackStep;
    }

    public Integer getDefenseStep() {
        return defenseStep;
    }

    public Integer gethPStep() {
        return hPStep;
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

    public Integer getAttackPlusStep() {
        return attackPlusStep;
    }

    public Integer getDefensePlusStep() {
        return defensePlusStep;
    }

    public Integer gethPPlusStep() {
        return hPPlusStep;
    }

    public Integer getRefineAttack() {
        return refineAttack;
    }

    public Integer getRefineAttackPlusStep() {
        return refineAttackPlusStep;
    }

    public Integer getRefineHP() {
        return refineHP;
    }

    public Integer getRefineHPPlusStep() {
        return refineHPPlusStep;
    }

    public Integer getTreasureEXP() {
        return treasureEXP;
    }

    public ListCell<Integer> getTreasurePiece1() {
        return treasurePiece1;
    }
}
