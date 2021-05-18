package ws.newBattle.buff;

import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.event.buff.AddSkillEffectsBefore;
import ws.newBattle.event.buff.CannotAction;
import ws.newBattle.event.buff.SkillAttackBefore;
import ws.newBattle.skill.NewBattleSkill;
import ws.newBattle.utils.NewBattleAllEnums.NewAttrContainerType;
import ws.protos.EnumsProtos.HeroAttrPosTypeEnum;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.table.tableRows.Table_New_Buff_Row;

/**
 * 针对 攻/防/技 的目标伤害提升
 */
public class HeroAttrsPosTypeBuff extends NewBattleBuffWrap {
    private HeroAttrPosTypeEnum heroAttrPosType;

    public HeroAttrsPosTypeBuff(HeroAttrPosTypeEnum heroAttrPosType) {
        this.heroAttrPosType = heroAttrPosType;
    }

    public HeroAttrsPosTypeBuff(BattleContext context, int uniId, int id, Table_New_Buff_Row buffRow, NewBattleHeroWrap belongHero, NewBattleHeroWrap fromHero, NewBattleSkill fromSkill, int whichRoundAdd, int lastReduceRound, int roundOri, int round, long effectRate, long effectFixValue, HeroAttrPosTypeEnum heroAttrPosType) {
        super(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue);
        this.heroAttrPosType = heroAttrPosType;
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
        for (NewBattleHeroWrap h : this.context.getBeAttacks()) {
            if (h.getCardRow().getCardType() == heroAttrPosType) {
                h.reduceAttr(new HeroAttr(HeroAttrTypeEnum.DamageOppose, (long) this.effectRate), NewAttrContainerType.ATK_TMP);
                System.out.println("Buff Tick：beAttacker->" + h + "--->" + this + "--->reduce[" + HeroAttrTypeEnum.DamageOppose + "]:" + this.effectRate);
            }
        }
    }

    @Override
    public String toString() {
        return "[针对 攻/防/技 的目标伤害提升:" + heroAttrPosType + "--" + this.id + "--" + this.getUniId() + "--<<<<" + this.belongHero + this.fromHero + ">>>>]";
    }

    @Override
    public NewBattleBuffWrap clone() {
        return new HeroAttrsPosTypeBuff(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue, heroAttrPosType);
    }
}
