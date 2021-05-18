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
import ws.relationship.base.MagicNumbers;
import ws.relationship.table.tableRows.Table_New_Buff_Row;
import ws.relationship.utils.attrs.HeroAttrsUtils;

/**
 * <pre>
 * 降低当前生命百分比
 * 增加当前攻击百分比
 * 降低当前攻击百分比
 * 增加当前防御百分比
 * 降低当前防御百分比
 * </pre>
 */
public class ChangeCurrentAttrPercentBuff extends NewBattleBuffWrap {
    private EffectTypeEnum effectTypeEnum;
    private HeroAttrTypeEnum attrType;

    public ChangeCurrentAttrPercentBuff(EffectTypeEnum effectTypeEnum, HeroAttrTypeEnum attrType) {
        this.effectTypeEnum = effectTypeEnum;
        this.attrType = attrType;
    }

    public ChangeCurrentAttrPercentBuff(BattleContext context, int uniId, int id, Table_New_Buff_Row buffRow, NewBattleHeroWrap belongHero, NewBattleHeroWrap fromHero, NewBattleSkill fromSkill, int whichRoundAdd, int lastReduceRound, int roundOri, int round, long effectRate, long effectFixValue, EffectTypeEnum effectTypeEnum, HeroAttrTypeEnum attrType) {
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
        long toChange = this.belongHero.getHeroAttrsCur().get(attrType) * effectRate / MagicNumbers.RANDOM_BASE_VALUE;
        HeroAttr heroAttr = new HeroAttr(attrType, toChange);

        if (attrType == HeroAttrTypeEnum.HP) { // 改变生命的立即生效，不支持回合
            if (effectTypeEnum == EffectTypeEnum.DEBUFF_EFFECT) {
                this.belongHero.reduceAttr(heroAttr, NewAttrContainerType.CUR);
                System.out.println("Buff Tick：belong->" + this.belongHero + "--->" + this + "--->reduce[" + heroAttr.getAttrType() + "]:" + heroAttr.getAttrValue());
                this.round = 0;
            }
        } else {
            NewAttrContainerType containerType = NewAttrContainerType.ATK_TMP;
            if (this.roundOri == -1) {
                containerType = NewAttrContainerType.CUR; // 永久性改变
                this.round = 0;
            }
            if (effectTypeEnum == EffectTypeEnum.BUFF_EFFECT) {
                this.belongHero.addAttr(heroAttr, containerType);
                System.out.println("Buff Tick：belong->" + this.belongHero + "--->" + this + "--->add[" + heroAttr.getAttrType() + "]:" + heroAttr.getAttrValue());
            } else {
                this.belongHero.reduceAttr(heroAttr, containerType);
                System.out.println("Buff Tick：belong->" + this.belongHero + "--->" + this + "--->reduce[" + heroAttr.getAttrType() + "]:" + heroAttr.getAttrValue());
            }
        }
    }

    @Override
    public String toString() {
        return "[改变当前XX百分比:" + effectTypeEnum + ":" + attrType + "--" + this.id + "--" + this.getUniId() + "--<<<<" + this.belongHero + this.fromHero + ">>>>]";
    }

    @Override
    public NewBattleBuffWrap clone() {
        return new ChangeCurrentAttrPercentBuff(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue, effectTypeEnum, attrType);
    }
}
