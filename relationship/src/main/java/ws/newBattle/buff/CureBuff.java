package ws.newBattle.buff;

import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.skill.NewBattleSkill;
import ws.newBattle.utils.NewBattleAllEnums.NewAttrContainerType;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.base.MagicNumbers;
import ws.relationship.table.tableRows.Table_New_Buff_Row;

/**
 * 治疗 (立即执行，不支持回合性)
 */
public class CureBuff extends NewBattleBuffWrap {

    public CureBuff() {
    }

    public CureBuff(BattleContext context, int uniId, int id, Table_New_Buff_Row buffRow, NewBattleHeroWrap belongHero, NewBattleHeroWrap fromHero, NewBattleSkill fromSkill, int whichRoundAdd, int lastReduceRound, int roundOri, int round, long effectRate, long effectFixValue) {
        super(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue);
    }

    @Override
    public void immediatelyTick() {
        this.tick();
        check();  // 立即移除
    }

    @Override
    protected void tick0() {
        long toAdd = calcuCureValue();
        this.belongHero.addAttr(new HeroAttr(HeroAttrTypeEnum.HP, toAdd), NewAttrContainerType.CUR);
        this.round = 0;
        this.context.getBattleProto().addBuffEffect_Hp(this.getId(), this.fromHero.getId(), this.belongHero, toAdd);
        System.out.println("Buff Tick：belong->" + this.belongHero + "--->" + this + "--->add[HP]:" + toAdd);
    }

    // 计算治疗量
    private long calcuCureValue() {
        long part1 = (fromHero.curHeroAttr(HeroAttrTypeEnum.Attack) * effectRate) / MagicNumbers.RANDOM_BASE_VALUE;
        long part2 = effectFixValue;
        long beCureRatio = belongHero.curHeroAttr(HeroAttrTypeEnum.Cure);
        long value = ((part1 + part2) * beCureRatio) / MagicNumbers.RANDOM_BASE_VALUE;
        return value <= 1 ? 1 : value; // 最低治疗量1点
    }

    @Override
    public String toString() {
        return "[治疗:" + this.id + "--" + this.getUniId() + "--<<<<" + this.belongHero + this.fromHero + ">>>>]";
    }

    @Override
    public NewBattleBuffWrap clone() {
        return new CureBuff(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue);
    }
}
