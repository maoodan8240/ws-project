package ws.newBattle.buff;

import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.skill.NewBattleSkill;
import ws.newBattle.utils.NewBattleAllEnums.BuffContainerTypeEnum;
import ws.protos.EnumsProtos.EffectTypeEnum;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.relationship.base.MagicNumbers;
import ws.relationship.table.tableRows.Table_New_Buff_Row;
import ws.relationship.utils.attrs.HeroAttrsUtils;

/**
 * 该buff会立即转换为HeroAttrChangeInnerBuff，分别加到belongHero和fromHero
 * <pre>
 * 吸取攻击百分比
 * 吸取防御百分比
 * 吸取伤害率
 * 吸取免伤率
 * </pre>
 */
public class DrawAttrPercentBuff extends NewBattleBuffWrap {
    private HeroAttrTypeEnum attrType;

    public DrawAttrPercentBuff(HeroAttrTypeEnum attrType) {
        this.attrType = attrType;
    }

    public DrawAttrPercentBuff(BattleContext context, int uniId, int id, Table_New_Buff_Row buffRow, NewBattleHeroWrap belongHero, NewBattleHeroWrap fromHero, NewBattleSkill fromSkill, int whichRoundAdd, int lastReduceRound, int roundOri, int round, long effectRate, long effectFixValue, HeroAttrTypeEnum attrType) {
        super(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue);
        this.attrType = attrType;
    }

    @Override
    public void immediatelyTick() {
        this.tick();
        this.round = 0;
    }


    @Override
    protected void tick0() {
        HeroAttrChangeBuff add = new HeroAttrChangeBuff(EffectTypeEnum.BUFF_EFFECT, attrType, this.round);
        HeroAttrChangeBuff reduce = new HeroAttrChangeBuff(EffectTypeEnum.DEBUFF_EFFECT, attrType, this.round);

        long effectRate_add = effectRate;
        long effectFixValue_add = 0;

        long effectRate_reduce = effectRate;
        long effectFixValue_reduce = 0;

        if (HeroAttrsUtils.isFixedAttr(attrType)) {
            effectFixValue_add = effectFixValue_reduce = this.belongHero.getHeroAttrsCur().get(attrType) * this.effectRate / MagicNumbers.RANDOM_BASE_VALUE;
            effectRate_add = effectRate_reduce = 0;
        }

        add.init(this.context, this.id, this.fromHero, this.fromHero, this.fromSkill, round, effectRate_add, effectFixValue_add);
        reduce.init(this.context, this.id, this.belongHero, this.fromHero, this.fromSkill, round, effectRate_reduce, effectFixValue_reduce);

        // 不需要clone
        belongHero.addBuff(reduce, BuffContainerTypeEnum.TMP);
        fromHero.addBuff(add, BuffContainerTypeEnum.TMP);
        System.out.println("BUFF 使用-->导致另外的buff：" + this.belongHero + "--->" + this + "--->reduce");
        System.out.println("BUFF 使用-->导致另外的buff：" + this.fromHero + "--->" + this + "--->add");
    }


    @Override
    public String toString() {
        return "[吸取XX百分比:" + attrType + "--" + this.id + "--" + this.getUniId() + "--<<<<" + this.belongHero + this.fromHero + ">>>>]";
    }

    @Override
    public NewBattleBuffWrap clone() {
        return new DrawAttrPercentBuff(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue, attrType);
    }
}
