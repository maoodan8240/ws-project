package ws.newBattle.buff;

import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.event.hero.RoundEnd;
import ws.newBattle.skill.NewBattleSkill;
import ws.relationship.table.tableRows.Table_New_Buff_Row;

/**
 * 不能行动
 */
public class CannotActionBuff extends NewBattleBuffWrap {

    public CannotActionBuff() {
    }

    public CannotActionBuff(BattleContext context, int uniId, int id, Table_New_Buff_Row buffRow, NewBattleHeroWrap belongHero, NewBattleHeroWrap fromHero, NewBattleSkill fromSkill, int whichRoundAdd, int lastReduceRound, int roundOri, int round, long effectRate, long effectFixValue) {
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
        return "[不能行动:" + this.id + "--" + this.getUniId() + "--<<<<" + this.belongHero + this.fromHero + ">>>>]";
    }

    @Override
    public NewBattleBuffWrap clone() {
        return new CannotActionBuff(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue);
    }
}
