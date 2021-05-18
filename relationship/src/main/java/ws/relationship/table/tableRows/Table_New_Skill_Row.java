package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.ListCell;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.FightTargetConditonEnum;
import ws.protos.EnumsProtos.FightTargetEnum;
import ws.protos.EnumsProtos.SkillEffectTypeEnum;
import ws.protos.EnumsProtos.SkillPositionEnum;
import ws.protos.EnumsProtos.SkillTypeEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.base.HeroAttrs;
import ws.relationship.table.RootTc;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.utils.RelationshipCommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Table_New_Skill_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;

    /**
     * int 技能大类型
     */
    private Integer skillType;
    /**
     * int 技能实际类型
     */
    private Integer skillEffectType;
    /**
     * int 技能范围
     */
    private Integer skillRange;
    /**
     * int 技能范围条件
     */
    private Integer skillRangeCondition;
    /**
     * int 技能范围值
     */
    private Integer skillRangeValue;
    /**
     * int 技能系数
     */
    private Integer skillRate;
    /**
     * int 技能固定值
     */
    private Integer skillValue;
    /**
     * int 系数步长
     */
    private Integer rateStep;
    /**
     * int 固定值步长
     */
    private Integer valueStep;
    /**
     * string BUFF触发点
     */
    private ListCell<Integer> bUFFStartPoint;
    /**
     * string BUFFID
     */
    private ListCell<Integer> bUFFId;
    /**
     * string BUFF实际类型
     */
    private ListCell<Integer> buffEffectType;
    /**
     * string BUFF触发条件
     */
    private ListCell<Integer> bUFFTriggerCondition;
    /**
     * string BUFF触发值
     */
    private ListCell<Integer> bUFFTriggerNumber;
    /**
     * string BUFF概率
     */
    private ListCell<Integer> bUFFPro;
    /**
     * string BUFF概率步长
     */
    private ListCell<Integer> bUFFProStep;
    /**
     * string BUFF范围
     */
    private ListCell<Integer> buffRange;
    /**
     * string BUFF范围条件
     */
    private ListCell<Integer> buffRangeCondition;
    /**
     * string BUFF范围值
     */
    private ListCell<Integer> buffRangeValue;
    /**
     * string BUFF回合
     */
    private ListCell<Integer> bUFFRound;
    /**
     * string BUFF系数
     */
    private ListCell<Integer> bUFFRate;
    /**
     * string BUFF固定值
     */
    private ListCell<Integer> bUFFValue;
    /**
     * string BUFF系数步长
     */
    private ListCell<Integer> bUFFRateStep;
    /**
     * string BUFF固定值步长
     */
    private ListCell<Integer> bUFFValueStep;
    /**
     * 属性技能
     */
    private static final List<SkillPositionEnum> attrSkillPoses = new ArrayList<SkillPositionEnum>() {
        private static final long serialVersionUID = 6959701074549862171L;

        {
            add(SkillPositionEnum.Skill_POS_3);
            add(SkillPositionEnum.Skill_POS_5);
            add(SkillPositionEnum.Skill_POS_6);
        }
    };


    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"技能ID"}
        skillType = CellParser.parseSimpleCell("SkillType", map, Integer.class); //int
        skillEffectType = CellParser.parseSimpleCell("SkillEffectType", map, Integer.class); //int
        skillRange = CellParser.parseSimpleCell("SkillRange", map, Integer.class); //int
        skillRangeCondition = CellParser.parseSimpleCell("SkillRangeCondition", map, Integer.class); //int
        skillRangeValue = CellParser.parseSimpleCell("SkillRangeValue", map, Integer.class); //int
        skillRate = CellParser.parseSimpleCell("SkillRate", map, Integer.class); //int
        skillValue = CellParser.parseSimpleCell("SkillValue", map, Integer.class); //int
        rateStep = CellParser.parseSimpleCell("RateStep", map, Integer.class); //int
        valueStep = CellParser.parseSimpleCell("ValueStep", map, Integer.class); //int
        bUFFStartPoint = CellParser.parseListCell("BUFFStartPoint", map, Integer.class); //string
        bUFFId = CellParser.parseListCell("BUFFId", map, Integer.class); //string
        buffEffectType = CellParser.parseListCell("BuffEffectType", map, Integer.class); //string
        bUFFTriggerCondition = CellParser.parseListCell("BUFFTriggerCondition", map, Integer.class); //string
        bUFFTriggerNumber = CellParser.parseListCell("BUFFTriggerNumber", map, Integer.class); //string
        bUFFPro = CellParser.parseListCell("BUFFPro", map, Integer.class); //string
        bUFFProStep = CellParser.parseListCell("BUFFProStep", map, Integer.class); //string
        buffRange = CellParser.parseListCell("BuffRange", map, Integer.class); //string
        buffRangeCondition = CellParser.parseListCell("BuffRangeCondition", map, Integer.class); //string
        buffRangeValue = CellParser.parseListCell("BuffRangeValue", map, Integer.class); //string
        bUFFRound = CellParser.parseListCell("BUFFRound", map, Integer.class); //string
        bUFFRate = CellParser.parseListCell("BUFFRate", map, Integer.class); //string
        bUFFValue = CellParser.parseListCell("BUFFValue", map, Integer.class); //string
        bUFFRateStep = CellParser.parseListCell("BUFFRateStep", map, Integer.class); //string
        bUFFValueStep = CellParser.parseListCell("BUFFValueStep", map, Integer.class); //string
    }

    public SkillTypeEnum getSkillType() {
        return SkillTypeEnum.valueOf(skillType);
    }

    public SkillEffectTypeEnum getSkillEffectType() {
        return SkillEffectTypeEnum.valueOf(skillEffectType);
    }

    public FightTargetEnum getSkillRange() {
        return FightTargetEnum.valueOf(skillRange);
    }

    public FightTargetConditonEnum getSkillRangeCondition() {
        return FightTargetConditonEnum.valueOf(skillRangeCondition);
    }

    public Integer getSkillRangeValue() {
        return skillRangeValue;
    }

    public Integer getSkillRate() {
        return skillRate;
    }

    public Integer getSkillValue() {
        return skillValue;
    }

    public Integer getRateStep() {
        return rateStep;
    }

    public Integer getValueStep() {
        return valueStep;
    }

    public List<Integer> getbUFFStartPoint() {
        return bUFFStartPoint.getAll();
    }

    public List<Integer> getbUFFId() {
        return bUFFId.getAll();
    }

    public List<Integer> getBuffEffectType() {
        return buffEffectType.getAll();
    }

    public List<Integer> getbUFFTriggerCondition() {
        return bUFFTriggerCondition.getAll();
    }

    public List<Integer> getbUFFTriggerNumber() {
        return bUFFTriggerNumber.getAll();
    }

    public List<Integer> getbUFFPro() {
        return bUFFPro.getAll();
    }

    public List<Integer> getbUFFProStep() {
        return bUFFProStep.getAll();
    }

    public List<Integer> getBuffRange() {
        return buffRange.getAll();
    }

    public List<Integer> getBuffRangeCondition() {
        return buffRangeCondition.getAll();
    }

    public List<Integer> getBuffRangeValue() {
        return buffRangeValue.getAll();
    }

    public List<Integer> getbUFFRound() {
        return bUFFRound.getAll();
    }

    public List<Integer> getbUFFRate() {
        return bUFFRate.getAll();
    }

    public List<Integer> getbUFFValue() {
        return bUFFValue.getAll();
    }

    public List<Integer> getbUFFRateStep() {
        return bUFFRateStep.getAll();
    }

    public List<Integer> getbUFFValueStep() {
        return bUFFValueStep.getAll();
    }


    /**
     * 获取武将所有技能[被动1，被动3，被动4]增加的属性
     *
     * @param hero
     * @return
     */
    public static HeroAttrs getHeroAllSkillHeroAttrs(Hero hero) {
        HeroAttrs heroAttrs = new HeroAttrs();
        for (Entry<SkillPositionEnum, Integer> kv : hero.getSkills().entrySet()) {
            if (!attrSkillPoses.contains(kv.getKey())) {
                continue;
            }
            Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, hero.getTpId());
            boolean awake = RelationshipCommonUtils.isHeroAwake(hero);
            int skillId = cardRow.getSkillId(kv.getKey(), awake);
            Table_New_Skill_Row skillRow = RootTc.get(Table_New_Skill_Row.class, skillId);
            heroAttrs.addAll(skillRow.getSkillHeroAttrs(kv.getValue()));
        }
        return heroAttrs;
    }

    /**
     * 获取武将某一个技能[被动1，被动3，被动4]增加的属性
     *
     * @param curSkillLv
     * @return
     */
    private HeroAttrs getSkillHeroAttrs(int curSkillLv) {
        HeroAttrs heroAttrs = new HeroAttrs();
        for (int i = 0; i < this.getbUFFId().size(); i++) {
            int buffId = this.getbUFFId().get(i);
            Table_New_Buff_Row buffRow = RootTc.get(Table_New_Buff_Row.class, buffId);
            if (this.getbUFFRate().get(i) > 0) {
                HeroAttr HeroAttrTmp = new HeroAttr(buffRow.getbUFFProperty(), this.getbUFFRate().get(i) + (curSkillLv - 1) * this.getbUFFRateStep().get(i));
                heroAttrs.add(HeroAttrTmp);
            }
            if (this.getbUFFValue().get(i) > 0) {
                HeroAttr HeroAttrTmp = new HeroAttr(buffRow.getbUFFProperty(), this.getbUFFValue().get(i) + (curSkillLv - 1) * this.getbUFFValueStep().get(i));
                heroAttrs.add(HeroAttrTmp);
            }
        }
        return heroAttrs;
    }
}
