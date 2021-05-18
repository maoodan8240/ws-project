package ws.newBattle.buff;

import ws.common.table.table.interfaces.cell.TupleCell;
import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.event.buff.AddSkillEffectsBefore;
import ws.newBattle.event.buff.CannotAction;
import ws.newBattle.event.buff.SkillAttackBefore;
import ws.newBattle.skill.NewBattleSkill;
import ws.newBattle.utils.NewBattleAllEnums.HpJudgeType;
import ws.newBattle.utils.NewBattleAllEnums.NewAttrContainerType;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.tableRows.Table_New_Buff_Row;

/**
 * 根据目标生命 判断是否使用 斩杀 致命 不屈
 */
public class TargetHPJudgeBuff extends NewBattleBuffWrap {
    private HpJudgeType hpJudgeType;

    public TargetHPJudgeBuff(HpJudgeType hpJudgeType) {
        this.hpJudgeType = hpJudgeType;
    }


    public TargetHPJudgeBuff(BattleContext context, int uniId, int id, Table_New_Buff_Row buffRow, NewBattleHeroWrap belongHero, NewBattleHeroWrap fromHero, NewBattleSkill fromSkill, int whichRoundAdd, int lastReduceRound, int roundOri, int round, long effectRate, long effectFixValue, HpJudgeType hpJudgeType) {
        super(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue);
        this.hpJudgeType = hpJudgeType;
    }

    @Override
    protected void onSkillAttackBefore(SkillAttackBefore event) {
        this.tick();
    }


    @Override
    protected void onCannotAction(CannotAction event) {
        reduceRound();
    }

    @Override
    protected void onAddSkillEffectsBefore(AddSkillEffectsBefore event) {
        reduceRound();
    }


    @Override
    protected void tick0() {
        if (hpJudgeType == HpJudgeType.UNYIELDING) {// 自身生命小于50%，伤害提升30%
            TupleCell<Integer> unyieldingConfig = AllServerConfig.Battle_FeatureValue_Unyielding.getConfig();
            if (this.belongHero.getCurHpPrecentage() < unyieldingConfig.get(TupleCell.FIRST)) {
                this.belongHero.addAttr(new HeroAttr(HeroAttrTypeEnum.Damage, unyieldingConfig.get(TupleCell.SECOND).longValue()), NewAttrContainerType.ATK_TMP);
                System.out.println("BUFF 不屈：" + this.belongHero + "--->" + this + "--->add[Damage]:" + unyieldingConfig.get(TupleCell.SECOND).longValue());
            }
            return;
        }
        for (NewBattleHeroWrap h : this.context.getBeAttacks()) {
            switch (hpJudgeType) {
                case BEHEAD: {    // 当敌方生命小于45%，伤害加成20%
                    TupleCell<Integer> beheadConfig = AllServerConfig.Battle_FeatureValue_Behead.getConfig();
                    if (h.getCurHpPrecentage() < beheadConfig.get(TupleCell.FIRST)) {
                        h.reduceAttr(new HeroAttr(HeroAttrTypeEnum.Damage, beheadConfig.get(TupleCell.SECOND).longValue()), NewAttrContainerType.ATK_TMP);
                        System.out.println("BUFF 斩杀：" + this.belongHero + "--->" + this + "--->reduce[Damage]:" + beheadConfig.get(TupleCell.SECOND).longValue());
                    }
                    break;
                }
                case FATAL: {     // 当敌方生命大于50%时，伤害加成25%
                    TupleCell<Integer> fatalConfig = AllServerConfig.Battle_FeatureValue_Fatal.getConfig();
                    if (h.getCurHpPrecentage() > fatalConfig.get(TupleCell.FIRST)) {
                        this.belongHero.reduceAttr(new HeroAttr(HeroAttrTypeEnum.Damage, fatalConfig.get(TupleCell.SECOND).longValue()), NewAttrContainerType.ATK_TMP);
                        System.out.println("BUFF 致命：" + this.belongHero + "--->" + this + "--->reduce[Damage]:" + fatalConfig.get(TupleCell.SECOND).longValue());
                    }
                    break;
                }
            }
        }
    }


    @Override
    public String toString() {
        return "[斩杀/致命/不屈:" + hpJudgeType + "--" + this.id + "--" + this.getUniId() + "--<<<<" + this.belongHero + this.fromHero + ">>>>]";
    }

    @Override
    public NewBattleBuffWrap clone() {
        return new TargetHPJudgeBuff(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue, hpJudgeType);
    }
}
