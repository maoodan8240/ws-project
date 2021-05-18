package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.EnumsProtos.SkillPositionEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;

import java.util.Map;

public class Table_SkillUpgradeConsumes_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 小技能
     */
    private Integer smallSkill;
    /**
     * int 怒气技能
     */
    private Integer angerSkill;
    /**
     * int 被动1
     */
    private Integer skill1;
    /**
     * int 低资质被动2
     */
    private Integer lowerSkill2;
    /**
     * int 高资质被动2
     */
    private Integer seniorSkill2;
    /**
     * int 被动3
     */
    private Integer skill3;
    /**
     * int 被动4
     */
    private Integer skill4;
    /**
     * int 神卡小技能
     */
    private Integer bossSmallSkill;
    /**
     * int 神卡怒气技能
     */
    private Integer bossAngerSkill;
    /**
     * int 神卡被动1
     */
    private Integer bossSkill1;
    /**
     * int 神卡被动2
     */
    private Integer bossSkill2;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"ID"}
        smallSkill = CellParser.parseSimpleCell("SmallSkill", map, Integer.class); //int
        angerSkill = CellParser.parseSimpleCell("AngerSkill", map, Integer.class); //int
        skill1 = CellParser.parseSimpleCell("Skill1", map, Integer.class); //int
        lowerSkill2 = CellParser.parseSimpleCell("LowerSkill2", map, Integer.class); //int
        seniorSkill2 = CellParser.parseSimpleCell("SeniorSkill2", map, Integer.class); //int
        skill3 = CellParser.parseSimpleCell("Skill3", map, Integer.class); //int
        skill4 = CellParser.parseSimpleCell("Skill4", map, Integer.class); //int
        bossSmallSkill = CellParser.parseSimpleCell("BossSmallSkill", map, Integer.class); //int
        bossAngerSkill = CellParser.parseSimpleCell("BossAngerSkill", map, Integer.class); //int
        bossSkill1 = CellParser.parseSimpleCell("BossSkill1", map, Integer.class); //int
        bossSkill2 = CellParser.parseSimpleCell("BossSkill2", map, Integer.class); //int
    }


    /**
     * 技能升级消耗
     *
     * @param heroTpId
     * @param skillPos
     * @param level
     * @return
     */
    public static IdAndCount getConsumes(int heroTpId, SkillPositionEnum skillPos, int level) {
        Table_SkillUpgradeConsumes_Row row = RootTc.get(Table_SkillUpgradeConsumes_Row.class, level);
        return new IdAndCount(ResourceTypeEnum.RES_MONEY_VALUE, chooseConsumes(row, heroTpId, skillPos));
    }


    // 不同资质的武将选择对应的消耗
    private static int chooseConsumes(Table_SkillUpgradeConsumes_Row row, int heroTpId, SkillPositionEnum skillPos) {
        Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, heroTpId);
        int aptitude = cardRow.getCardAptitude();
        switch (skillPos) {
            case Skill_POS_1: // 小技能
                if (aptitude == MagicNumbers.APTITUDE_14) { // 14资质
                    return row.bossSmallSkill;
                }
                return row.smallSkill;

            case Skill_POS_2:
                if (aptitude == MagicNumbers.APTITUDE_14) { // 14资质
                    return row.bossAngerSkill;
                }
                return row.angerSkill;

            case Skill_POS_3: // 被动1
                if (aptitude == MagicNumbers.APTITUDE_14) { // 14资质
                    return row.bossSkill1;
                }
                return row.skill1;

            case Skill_POS_4: // 被动2
                if (aptitude == MagicNumbers.APTITUDE_14) { // 14资质
                    return row.bossSkill2;
                } else if (aptitude == MagicNumbers.APTITUDE_13) { // 13资质
                    return row.seniorSkill2;
                } else if (aptitude < MagicNumbers.APTITUDE_13) { // 14资质
                    return row.lowerSkill2;
                }
            case Skill_POS_5: // 被动3
                return row.skill3;
            case Skill_POS_6: // 被动4
                return row.skill4;
        }
        String msg = String.format("heroTpId=%s aptitude=%s，不支持的技能类型=%s！", heroTpId, aptitude, skillPos);
        throw new BusinessLogicMismatchConditionException(msg);
    }
}
