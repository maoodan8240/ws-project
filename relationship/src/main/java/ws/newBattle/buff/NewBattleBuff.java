package ws.newBattle.buff;

import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.event.EventListener;
import ws.newBattle.skill.NewBattleSkill;
import ws.relationship.table.tableRows.Table_New_Buff_Row;

/**
 *
 */
public abstract class NewBattleBuff implements EventListener {
    protected BattleContext context;
    protected int uniId;
    protected int id;
    protected Table_New_Buff_Row buffRow;
    protected NewBattleHeroWrap belongHero;
    protected NewBattleHeroWrap fromHero;// buff的释法者或者反击者
    protected NewBattleSkill fromSkill;
    protected int whichRoundAdd;   // 哪个回合添加的Buff
    protected int lastReduceRound; // 上一个结算buff的回合数

    protected int roundOri;        // 原本的持续回合数
    protected int round;           // 持续回合数

    protected long effectRate;
    protected long effectFixValue;


    public NewBattleBuff() {
    }

    public NewBattleBuff(BattleContext context, int uniId, int id, Table_New_Buff_Row buffRow, NewBattleHeroWrap belongHero, NewBattleHeroWrap fromHero, NewBattleSkill fromSkill, int whichRoundAdd, int lastReduceRound, int roundOri, int round, long effectRate, long effectFixValue) {
        this.context = context;
        this.uniId = uniId;
        this.id = id;
        this.buffRow = buffRow;
        this.belongHero = belongHero;
        this.fromHero = fromHero;
        this.fromSkill = fromSkill;
        this.whichRoundAdd = whichRoundAdd;
        this.lastReduceRound = lastReduceRound;
        this.roundOri = roundOri;
        this.round = round;
        this.effectRate = effectRate;
        this.effectFixValue = effectFixValue;
    }

    public NewBattleHeroWrap getBelongHero() {
        return belongHero;
    }

    public NewBattleHeroWrap getFromHero() {
        return fromHero;
    }

    public NewBattleSkill getFromSkill() {
        return fromSkill;
    }

    public int getUniId() {
        return uniId;
    }

    public void setUniId(int uniId) {
        this.uniId = uniId;
    }

    public int getId() {
        return id;
    }
}