package ws.newBattle.buff;

import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.event.buff.AddSkillEffectsBefore;
import ws.newBattle.event.buff.BeSkillAttackBefore;
import ws.newBattle.event.buff.CannotAction;
import ws.newBattle.event.buff.SkillAttackBefore;
import ws.newBattle.event.hero.RoundEnd;
import ws.newBattle.skill.NewBattleSkill;
import ws.newBattle.utils.NewBattleAllEnums.NewAttrContainerType;
import ws.protos.EnumsProtos.EffectTypeEnum;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.table.tableRows.Table_New_Buff_Row;
import ws.relationship.utils.attrs.HeroAttrsUtils;

/**
 * 直接改变属性的buff
 */
public class HeroAttrChangeBuff extends NewBattleBuffWrap {
    private EffectTypeEnum effectTypeEnum;
    private HeroAttrTypeEnum attrType;

    public HeroAttrChangeBuff(EffectTypeEnum effectTypeEnum, HeroAttrTypeEnum attrType) {
        this.effectTypeEnum = effectTypeEnum;
        this.attrType = attrType;
    }

    public HeroAttrChangeBuff(EffectTypeEnum effectTypeEnum, HeroAttrTypeEnum attrType, int round) {
        this.effectTypeEnum = effectTypeEnum;
        this.attrType = attrType;
        this.roundOri = this.round = round;
    }


    public HeroAttrChangeBuff(BattleContext context, int uniId, int id, Table_New_Buff_Row buffRow, NewBattleHeroWrap belongHero, NewBattleHeroWrap fromHero, NewBattleSkill fromSkill, int whichRoundAdd, int lastReduceRound, int roundOri, int round, long effectRate, long effectFixValue, EffectTypeEnum effectTypeEnum, HeroAttrTypeEnum attrType) {
        super(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue);
        this.effectTypeEnum = effectTypeEnum;
        this.attrType = attrType;
    }

    @Override
    protected void onBeSkillAttackBefore(BeSkillAttackBefore event) {
        this.tick();
    }

    @Override
    protected void onSkillAttackBefore(SkillAttackBefore event) {
        this.tick();
    }

    @Override
    protected void onCannotAction(CannotAction event) {
        if (HeroAttrsUtils.isAttackAttr(this.attrType)) {
            reduceRound();
        }
    }

    @Override
    protected void onAddSkillEffectsBefore(AddSkillEffectsBefore event) {
        if (HeroAttrsUtils.isAttackAttr(this.attrType)) {
            reduceRound();
        }
    }

    @Override
    protected void onRoundEnd(RoundEnd event) {
        if (!HeroAttrsUtils.isAttackAttr(this.attrType)) {
            reduceRound();
        }
    }

    @Override
    protected void tick0() {
        NewAttrContainerType containerType = NewAttrContainerType.ATK_TMP;
        if (this.attrType == HeroAttrTypeEnum.HP || this.attrType == HeroAttrTypeEnum.Anger) { // 改变 生命/怒气 的立即生效，不支持回合
            containerType = NewAttrContainerType.CUR; // 永久性改变
            this.round = 0;
        }
        HeroAttr attr;
        if (HeroAttrsUtils.isFixedAttr(attrType)) {
            attr = new HeroAttr(attrType, effectFixValue);
        } else {
            attr = new HeroAttr(attrType, effectRate);
        }
        if (effectTypeEnum == EffectTypeEnum.BUFF_EFFECT) {
            this.belongHero.addAttr(attr, containerType);
            System.out.println("Buff Tick：belong->" + this.belongHero + "--->" + this + "--->add[" + attr.getAttrType() + "]:" + attr.getAttrValue());
        } else if (effectTypeEnum == EffectTypeEnum.DEBUFF_EFFECT) {
            this.belongHero.reduceAttr(attr, containerType);
            System.out.println("Buff Tick：belong->" + this.belongHero + "--->" + this + "--->reduce[" + attr.getAttrType() + "]:" + attr.getAttrValue());
        }
    }

    @Override
    public String toString() {
        return "[直接改变属性:" + effectTypeEnum + ":" + attrType + "--" + this.id + "--" + this.getUniId() + "--<<<<" + this.belongHero + this.fromHero + ">>>>]";
    }

    @Override
    public NewBattleBuffWrap clone() {
        return new HeroAttrChangeBuff(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue, effectTypeEnum, attrType);
    }
}
