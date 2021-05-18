package ws.newBattle.buff;

import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.event.buff.BeSkillAttackBefore;
import ws.newBattle.skill.NewBattleSkill;
import ws.newBattle.utils.NewBattleAllEnums.NewAttrContainerType;
import ws.protos.EnumsProtos.HeroAttrPosTypeEnum;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.table.tableRows.Table_New_Buff_Row;

/**
 * 其他效果buff载体
 */
public class OtherEffectsCarrierBuff extends NewBattleBuffWrap {

    public OtherEffectsCarrierBuff() {
    }

    public OtherEffectsCarrierBuff(BattleContext context, int uniId, int id, Table_New_Buff_Row buffRow, NewBattleHeroWrap belongHero, NewBattleHeroWrap fromHero, NewBattleSkill fromSkill, int whichRoundAdd, int lastReduceRound, int roundOri, int round, long effectRate, long effectFixValue) {
        super(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue);
    }

    public static OtherEffectsCarrierBuff create(BattleContext context, NewBattleHeroWrap belongHero) {
        OtherEffectsCarrierBuff buff = new OtherEffectsCarrierBuff();
        buff.setUniId(context.getNextBuffUniId());
        buff.init(context, -1, belongHero, null, null, 999, 0, 0);
        return buff;
    }

    @Override
    protected void tick0() {
    }


    @Override
    protected void onBeSkillAttackBefore(BeSkillAttackBefore event) {
        HeroAttrPosTypeEnum posTypeEnum = event.getAttack().getCardRow().getCardType();
        switch (posTypeEnum) {
            case ATTACK_ATTR_POS: {  // 被攻属性格斗家攻击
                long add = this.belongHero.curHeroAttr(HeroAttrTypeEnum.BeAtByAttackDamageOpposeAdd);
                if (add > 0) {
                    this.belongHero.addAttr(new HeroAttr(HeroAttrTypeEnum.DamageOppose, add), NewAttrContainerType.ATK_TMP);
                    System.out.println("Buff Tick：belong->" + this.belongHero + "--->" + this + "--->add[DamageOppose]:" + add);
                }
                long reduce = this.belongHero.curHeroAttr(HeroAttrTypeEnum.BeAtByAttackDamageOpposeReduce);
                if (reduce > 0) {
                    this.belongHero.reduceAttr(new HeroAttr(HeroAttrTypeEnum.DamageOppose, reduce), NewAttrContainerType.ATK_TMP);
                    System.out.println("Buff Tick：belong->" + this.belongHero + "--->" + this + "--->reduce[DamageOppose]:" + reduce);
                }
                return;
            }
            case DEFEND_ATRR_POS: {  // 被防属性格斗家攻击
                long add = this.belongHero.curHeroAttr(HeroAttrTypeEnum.BeAtByDefenseDamageOpposeAdd);
                if (add > 0) {
                    this.belongHero.addAttr(new HeroAttr(HeroAttrTypeEnum.DamageOppose, add), NewAttrContainerType.ATK_TMP);
                    System.out.println("Buff Tick：belong->" + this.belongHero + "--->" + this + "--->add[DamageOppose]:" + add);
                }
                long reduce = this.belongHero.curHeroAttr(HeroAttrTypeEnum.BeAtByDefenseDamageOpposeReduce);
                if (reduce > 0) {
                    this.belongHero.reduceAttr(new HeroAttr(HeroAttrTypeEnum.DamageOppose, reduce), NewAttrContainerType.ATK_TMP);
                    System.out.println("Buff Tick：belong->" + this.belongHero + "--->" + this + "--->reduce[DamageOppose]:" + reduce);
                }
                return;
            }
            case SKILL_ATTR_POS: {  // 被技属性格斗家攻击
                long add = this.belongHero.curHeroAttr(HeroAttrTypeEnum.BeAtBySkillDamageOpposeAdd);
                if (add > 0) {
                    this.belongHero.addAttr(new HeroAttr(HeroAttrTypeEnum.DamageOppose, add), NewAttrContainerType.ATK_TMP);
                    System.out.println("Buff Tick：belong->" + this.belongHero + "--->" + this + "--->add[DamageOppose]:" + add);
                }
                long reduce = this.belongHero.curHeroAttr(HeroAttrTypeEnum.BeAtBySkillDamageOpposeReduce);
                if (reduce > 0) {
                    this.belongHero.reduceAttr(new HeroAttr(HeroAttrTypeEnum.DamageOppose, reduce), NewAttrContainerType.ATK_TMP);
                    System.out.println("Buff Tick：belong->" + this.belongHero + "--->" + this + "--->reduce[DamageOppose]:" + reduce);
                }
                return;
            }
        }
    }

    @Override
    public String toString() {
        return "[其他效果" + this.getUniId() + "--<<<<" + this.belongHero + this.fromHero + ">>>>]";
    }

    @Override
    public NewBattleBuffWrap clone() {
        return new OtherEffectsCarrierBuff(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue);
    }
}
