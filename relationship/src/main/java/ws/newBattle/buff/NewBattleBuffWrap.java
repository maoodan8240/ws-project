package ws.newBattle.buff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.event.Event;
import ws.newBattle.event.HeroDie;
import ws.newBattle.event.buff.AddSkillEffectsBefore;
import ws.newBattle.event.buff.BeSkillAttackAfter;
import ws.newBattle.event.buff.BeSkillAttackBefore;
import ws.newBattle.event.buff.CannotAction;
import ws.newBattle.event.buff.SkillAttackAfter;
import ws.newBattle.event.buff.SkillAttackBefore;
import ws.newBattle.event.hero.ActionBefore;
import ws.newBattle.event.hero.ActionEnd;
import ws.newBattle.event.hero.RoundBegin;
import ws.newBattle.event.hero.RoundEnd;
import ws.newBattle.skill.NewBattleSkill;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_New_Buff_Row;

public abstract class NewBattleBuffWrap extends NewBattleBuff {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewBattleBuffWrap.class);

    public NewBattleBuffWrap() {
    }

    public NewBattleBuffWrap(BattleContext context, int uniId, int id, Table_New_Buff_Row buffRow, NewBattleHeroWrap belongHero, NewBattleHeroWrap fromHero, NewBattleSkill fromSkill, int whichRoundAdd, int lastReduceRound, int roundOri, int round, long effectRate, long effectFixValue) {
        super(context, uniId, id, buffRow, belongHero, fromHero, fromSkill, whichRoundAdd, lastReduceRound, roundOri, round, effectRate, effectFixValue);
    }

    public void init(BattleContext context, int id, //
                     NewBattleHeroWrap belongHero, NewBattleHeroWrap fromHero, NewBattleSkill fromSkill, //
                     int roundOri, long effectRate, long effectFixValue) {
        this.context = context;
        this.id = id;
        if (id > 0) {
            this.buffRow = RootTc.get(Table_New_Buff_Row.class, id);
        }
        this.belongHero = belongHero;
        this.fromHero = fromHero;
        this.fromSkill = fromSkill;
        this.whichRoundAdd = context.getBattle().getCurRoundNum();
        this.roundOri = this.round = roundOri;
        this.effectRate = effectRate;
        this.effectFixValue = effectFixValue;
    }


    /**
     * 在添加完Buff后立即执行
     */
    public void immediatelyTick() {
    }

    protected void tick() {
        try {
            tick0();
        } catch (Exception e) {
            LOGGER.error("tick 异常！", e);
        }
    }

    /**
     * buff逻辑处理方法
     */
    protected abstract void tick0();

    public void check() {
        if (this.round <= 0) {
            this.belongHero.getBuffManager().rempoveBuff(this);
            this.context.putBuffChangeHero(this.belongHero);
        }
    }

    protected void reduceRound() {
        if (this.lastReduceRound == this.context.getBattle().getCurRoundNum()) {
            return;
        }
        this.round -= 1;
        this.lastReduceRound = this.context.getBattle().getCurRoundNum();
        check();
    }

    //---------------------------事件处理 - start ---------------------------------


    @Override
    public void onNotify(Event event) {
        if (event instanceof RoundBegin) {
            onRoundBegin((RoundBegin) event);
        }
        //
        if (event instanceof ActionBefore) {
            onActionBefore((ActionBefore) event);
        }
        if (event instanceof CannotAction) {
            onCannotAction((CannotAction) event);
        }
        //
        if (event instanceof SkillAttackBefore) {
            onSkillAttackBefore((SkillAttackBefore) event);
        }
        //
        if (event instanceof BeSkillAttackBefore) {
            onBeSkillAttackBefore((BeSkillAttackBefore) event);
        }
        //
        if (event instanceof BeSkillAttackAfter) {
            onBeSkillAttackAfter((BeSkillAttackAfter) event);
        }
        //
        if (event instanceof SkillAttackAfter) {
            onSkillAttackAfter((SkillAttackAfter) event);
        }
        if (event instanceof AddSkillEffectsBefore) {
            onAddSkillEffectsBefore((AddSkillEffectsBefore) event);
        }
        //
        if (event instanceof ActionEnd) {
            onActionEnd((ActionEnd) event);
        }
        //
        if (event instanceof HeroDie) {
            onHeroDie((HeroDie) event);
        }
        //
        if (event instanceof RoundEnd) {
            onRoundEnd((RoundEnd) event);
        }
    }


    protected void onRoundBegin(RoundBegin event) {
    }

    protected void onActionBefore(ActionBefore event) {
    }

    protected void onCannotAction(CannotAction event) {
    }

    protected void onSkillAttackBefore(SkillAttackBefore event) {
    }

    protected void onBeSkillAttackBefore(BeSkillAttackBefore event) {
    }

    protected void onBeSkillAttackAfter(BeSkillAttackAfter event) {
    }

    protected void onSkillAttackAfter(SkillAttackAfter event) {
    }

    protected void onAddSkillEffectsBefore(AddSkillEffectsBefore event) {

    }

    protected void onActionEnd(ActionEnd event) {
    }


    protected void onHeroDie(HeroDie event) {
    }

    protected void onRoundEnd(RoundEnd event) {
    }

    //---------------------------事件处理 - end ---------------------------------

    public abstract NewBattleBuffWrap clone();
}