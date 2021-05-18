package ws.newBattle.buff;

import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.event.hero.ActionBefore;
import ws.newBattle.event.hero.RoundEnd;
import ws.newBattle.skill.NewBattleSkill;
import ws.newBattle.utils.NewBattleAllEnums.NewAttrContainerType;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.base.MagicNumbers;
import ws.relationship.table.tableRows.Table_New_Buff_Row;

/**
 * 灼烧
 */
public class BurnBuff extends NewBattleBuffWrap {

    public BurnBuff() {
    }

    public BurnBuff(BattleContext context, int uniId, int id, Table_New_Buff_Row buffRow, NewBattleHeroWrap belongHero, NewBattleHeroWrap fromHero, NewBattleSkill fromSkill, int whichRoundAdd, int lastReduceRound, int roundOri, int round, long effectRate, long effectFixValue) {
        super(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue);
    }

    @Override
    protected void tick0() {
        long damage = 0;
        // SpecialBuffEnum-灼烧
        damage = calcuBurnDamage();
        if (damage > 0) {
            this.belongHero.reduceAttr(new HeroAttr(HeroAttrTypeEnum.HP, damage), NewAttrContainerType.CUR);
            System.out.println("Buff Tick：belong->" + this.belongHero + "--->" + this + "--->reduce[HP]:" + damage);
            this.context.getBattleProto().addBuffEffect_Hp(this.getId(), this.fromHero.getId(), this.belongHero, -damage);
        }
    }

    @Override
    public void onActionBefore(ActionBefore event) {
        this.tick();
    }

    @Override
    protected void onRoundEnd(RoundEnd event) {
        reduceRound();
    }

    /**
     * 计算灼烧伤害量
     */
    private long calcuBurnDamage() {
        long part1 = (fromHero.curHeroAttr(HeroAttrTypeEnum.Attack) * effectRate) / MagicNumbers.RANDOM_BASE_VALUE;
        long part2 = effectFixValue;
        long value = (part1 + part2);
        return value <= 1 ? 1 : value; // 最低1点
    }


    @Override
    public String toString() {
        return "[灼烧:" + this.id + "--" + this.getUniId() + "--<<<<" + this.belongHero + this.fromHero + ">>>>]";
    }

    @Override
    public NewBattleBuffWrap clone() {
        return new BurnBuff(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue);
    }
}
