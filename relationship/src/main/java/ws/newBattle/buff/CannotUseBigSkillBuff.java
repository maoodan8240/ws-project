package ws.newBattle.buff;

import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.event.hero.RoundEnd;
import ws.newBattle.skill.NewBattleSkill;
import ws.relationship.table.tableRows.Table_New_Buff_Row;

/**
 * 不能释放怒气技能和觉醒技能
 */
public class CannotUseBigSkillBuff extends NewBattleBuffWrap {

    public CannotUseBigSkillBuff() {
    }

    public CannotUseBigSkillBuff(BattleContext context, int uniId, int id, Table_New_Buff_Row buffRow, NewBattleHeroWrap belongHero, NewBattleHeroWrap fromHero, NewBattleSkill fromSkill, int whichRoundAdd, int lastReduceRound, int roundOri, int round, long effectRate, long effectFixValue) {
        super(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue);
    }

    @Override
    protected void tick0() {
    }


    @Override
    protected void onRoundEnd(RoundEnd event) {
        reduceRound();
    }

    @Override
    public String toString() {
        return "[不能释放怒气技能:" + this.id + "--" + this.getUniId() + "--<<<<" + this.belongHero + this.fromHero + ">>>>]";
    }

    @Override
    public NewBattleBuffWrap clone() {
        return new CannotUseBigSkillBuff(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue);
    }
}
