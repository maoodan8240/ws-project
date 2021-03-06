package ws.newBattle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.newBattle.buff.CannotActionBuff;
import ws.newBattle.buff.CannotUseBigSkillBuff;
import ws.newBattle.buff.NewBattleBuffManager;
import ws.newBattle.buff.NewBattleBuffWrap;
import ws.newBattle.event.BattleBegin;
import ws.newBattle.event.Event;
import ws.newBattle.event.HeroDie;
import ws.newBattle.event.Resurgence;
import ws.newBattle.event.buff.BeSkillAttackAfter;
import ws.newBattle.event.buff.CannotAction;
import ws.newBattle.event.hero.ActionBefore;
import ws.newBattle.event.hero.ActionEnd;
import ws.newBattle.event.hero.RoundBegin;
import ws.newBattle.event.hero.RoundEnd;
import ws.newBattle.skill.NewBattleSkill;
import ws.newBattle.skill.NewBattleSkillManager;
import ws.newBattle.utils.NewBattleAllEnums.BuffContainerTypeEnum;
import ws.newBattle.utils.NewBattleAllEnums.NewAttrContainerType;
import ws.newBattle.utils.NewBattleCalcu.BattleCalcuAttackResult;
import ws.protos.EnumsProtos.BattlePos;
import ws.protos.EnumsProtos.BattleSideEnum;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.protos.EnumsProtos.SkillTypeEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.base.HeroAttrs;
import ws.relationship.base.MagicNumbers;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_New_Card_Row;
import ws.relationship.topLevelPojos.heros.Hero;

public class NewBattleHeroWrap extends NewBattleHero {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewBattleHeroWrap.class);
    public static final NewBattleHeroWrap NULL = new NewBattleHeroWrap();


    public void init(BattleContext context, BattlePos pos, Hero hero, NewBattleSide battleSide, HeroAttrs heroAttrs) {
        this.context = context;
        this.pos = pos;
        this.hero = hero;
        this.belongBattleSide = battleSide;
        this.heroAttrsOri.addAll(heroAttrs);
        this.heroAttrsCur.addAll(heroAttrs);
        this.id = hero.getId();
        this.cardRow = RootTc.get(Table_New_Card_Row.class, this.hero.getTpId());
        this.sideEnum = belongBattleSide.getCurSide();
        this.skillManager = new NewBattleSkillManager();
        skillManager.init(context, this);
        this.buffManager = new NewBattleBuffManager();
        this.buffManager.init(context, this);
    }


    public boolean isDie() {
        return waitForResurgence || this.getHeroAttrsCur().get(HeroAttrTypeEnum.HP) <= 0;
    }

    public long curHeroAttr(HeroAttrTypeEnum attrType) {
        return this.heroAttrsCur.get(attrType) + this.heroAttrsAttackTmp.get(attrType);
    }


    public boolean isAlive() {
        return getCurHp() > 0 && !waitForResurgence;
    }

    public long getCurHp() {
        return this.getHeroAttrsCur().get(HeroAttrTypeEnum.HP);
    }

    public long getOriHp() {
        return this.getHeroAttrsOri().get(HeroAttrTypeEnum.HP);
    }

    public long getCurAnger() {
        return this.getHeroAttrsCur().get(HeroAttrTypeEnum.Anger);
    }


    /**
     * ??????????????? ????????? 0.51 ??????5100
     *
     * @return
     */
    public long getCurHpPrecentage() {
        return getCurHp() * 10000 / heroAttrsOri.get(HeroAttrTypeEnum.HP);
    }


    public void addAttr(HeroAttr attr, NewAttrContainerType containerType) {
        if (attr.getAttrValue() <= 0) {
            return;
        }
        getAttrContainer(containerType).add(attr);
    }

    public void reduceAttr(HeroAttr attr, NewAttrContainerType containerType) {
        if (attr.getAttrValue() <= 0) {
            return;
        }
        getAttrContainer(containerType).reduce(attr);
    }


    // ????????????????????????
    private HeroAttrs getAttrContainer(NewAttrContainerType containerType) {
        switch (containerType) {
            case CUR:
                return this.heroAttrsCur;
            case ATK_TMP:
                return this.heroAttrsAttackTmp;
        }
        return null;
    }


    // ???????????????????????????
    public NewBattleSide getOppositeBattleSide() {
        if (sideEnum == BattleSideEnum.ATTACK) {
            return this.context.getBattle().getCurWave().getSideMap().get(BattleSideEnum.DEFENSE);
        }
        return this.context.getBattle().getCurWave().getSideMap().get(BattleSideEnum.ATTACK);
    }


    public void addBuff(NewBattleBuffWrap buff, BuffContainerTypeEnum containerType) {
        buffManager.addBuff(buff, containerType);
    }


    public void handleAttackResult(NewBattleHeroWrap attack, NewBattleSkill attackSkill, BattleCalcuAttackResult result) {
        long damageHp = result.getHp();
        if (attackSkill.getSkill_row().getSkillType() == SkillTypeEnum.ULTIMATE ||
                attackSkill.getSkill_row().getSkillType() == SkillTypeEnum.AWAKE) { //  ????????????/????????????
            damageHp = (damageHp * (MagicNumbers.RANDOM_BASE_VALUE + attack.curHeroAttr(HeroAttrTypeEnum.AngerSkillDamage) - this.curHeroAttr(HeroAttrTypeEnum.AngerSkillDamageOppose))) / MagicNumbers.RANDOM_BASE_VALUE;
        }
        this.reduceAttr(new HeroAttr(HeroAttrTypeEnum.HP, damageHp), NewAttrContainerType.CUR);
        long suckHp = damageHp * attack.curHeroAttr(HeroAttrTypeEnum.Suck) / MagicNumbers.RANDOM_BASE_VALUE; // ??????
        attack.addAttr(new HeroAttr(HeroAttrTypeEnum.HP, suckHp), NewAttrContainerType.CUR);
        long thornsHp = damageHp * this.curHeroAttr(HeroAttrTypeEnum.Thorns) / MagicNumbers.RANDOM_BASE_VALUE; // ??????
        attack.reduceAttr(new HeroAttr(HeroAttrTypeEnum.HP, thornsHp), NewAttrContainerType.CUR);
    }

    public void handleCureResult(NewBattleHeroWrap curer, NewBattleSkill cureSkill, long hp) {
        if (cureSkill.getSkill_row().getSkillType() == SkillTypeEnum.ULTIMATE ||
                cureSkill.getSkill_row().getSkillType() == SkillTypeEnum.AWAKE) { //  ????????????/????????????
            hp = (hp * (MagicNumbers.RANDOM_BASE_VALUE + curer.curHeroAttr(HeroAttrTypeEnum.AngerSkillDamage))) / MagicNumbers.RANDOM_BASE_VALUE;
        }
        this.addAttr(new HeroAttr(HeroAttrTypeEnum.HP, hp), NewAttrContainerType.CUR);
    }


    public void simpleTick() {
        this.onNotify(new ActionBefore());
        if (!canAction()) {
            this.context.getLog().add("       ????????????:sideEnum=%s pos=%s id=%s ????????????!", this.sideEnum, this.pos, this.hero.getId());
            this.onNotify(new CannotAction());
            return;
        }
        this.skillManager.simpleTick();
        this.onNotify(new ActionEnd()); // ????????????????????????????????????
    }

    public void angerTick() {
        if (!canAction()) {
            this.context.getLog().add("       ????????????:sideEnum=%s pos=%s id=%s ????????????!", this.sideEnum, this.pos, this.hero.getId());
            return;
        }
        this.context.getLog().add("       ????????????:sideEnum=%s pos=%s id=%s ??????????????????!", this.sideEnum, this.pos, this.hero.getId());
        this.skillManager.angerTick();
    }

    //---------------------------???????????? - start ---------------------------------

    /**
     * ????????????????????? ????????? ????????????????????????????????????
     *
     * @return
     */
    public boolean canAction() {
        return !buffManager.containsBuff(CannotActionBuff.class);
    }

    /**
     * ??????????????????????????? ??????????????????
     *
     * @return
     */
    public boolean canUseBigSkill() {
        return !buffManager.containsBuff(CannotUseBigSkillBuff.class);
    }

    //---------------------------???????????? - end ---------------------------------


    //---------------------------???????????? - start ---------------------------------


    @Override
    public void onNotify(Event event) {
        if (event instanceof BattleBegin) {
            onBattleBegin();
        } else if (event instanceof RoundBegin) {
            onRoundBegin();
        } else if (event instanceof BeSkillAttackAfter) {
            onBeSkillAttackAfter((BeSkillAttackAfter) event);
        } else if (event instanceof HeroDie) {
            onHeroDie((HeroDie) event);
        } else if (event instanceof ActionEnd) {
            onActionEnd();
        } else if (event instanceof RoundEnd) {
            onRoundEnd();
        }
        skillManager.onNotify(event);
        buffManager.onNotify(event);
    }


    //---------------------------???????????? - end ---------------------------------

    private void onBattleBegin() {
        long startAnger = this.getHeroAttrsOri().get(HeroAttrTypeEnum.StartAnger);
        addAttr(new HeroAttr(HeroAttrTypeEnum.Anger, startAnger), NewAttrContainerType.CUR);// ?????????????????????????????????
    }

    private void onRoundBegin() {
        if (this.waitForResurgence) {
            this.waitForResurgence = false;
            this.onNotify(new Resurgence());
        }
    }


    private void onRoundEnd() {
        if (this.isDie()) {
            return;
        }
        // ???????????????
        long num = this.curHeroAttr(HeroAttrTypeEnum.RoundAnger);
        this.addAttr(new HeroAttr(HeroAttrTypeEnum.Anger, num), NewAttrContainerType.CUR);
    }


    private void onActionEnd() {
        if (this.isDie()) {
            return;
        }
        // ????????????
        long num = this.curHeroAttr(HeroAttrTypeEnum.HitAnger);
        this.addAttr(new HeroAttr(HeroAttrTypeEnum.Anger, num), NewAttrContainerType.CUR);
    }

    private void onBeSkillAttackAfter(BeSkillAttackAfter event) {
        if (this.isDie()) {
            return;
        }
        long hpPrecent = (event.getLossHp() * MagicNumbers.RANDOM_BASE_VALUE) / this.getOriHp(); // ???????????????
        long num = this.curHeroAttr(HeroAttrTypeEnum.HitedAnger); // ???????????????
        long add = (NewBattleConstants.MAX_ANGER_NUM * ((hpPrecent * num) / MagicNumbers.RANDOM_BASE_VALUE)) / MagicNumbers.RANDOM_BASE_VALUE;
        // ????????????
        this.addAttr(new HeroAttr(HeroAttrTypeEnum.Anger, add), NewAttrContainerType.CUR);
    }


    private void onHeroDie(HeroDie event) {
        if (event.getDie().equals(this)) {
            this.buffManager.clearBuffOnDie();
        }
        if (this.isDie()) {
            return;
        }
        long num = 0;
        if (event.getDie().sideEnum == this.sideEnum) { // ??????????????????
            num += this.curHeroAttr(HeroAttrTypeEnum.FriendDieAnger);
        } else { // ??????????????????
            num += this.curHeroAttr(HeroAttrTypeEnum.EnemyDieAnger);
        }
        if (event.getAttack().equals(this)) { // ????????????
            num += this.curHeroAttr(HeroAttrTypeEnum.KillAnger);
        }
        this.addAttr(new HeroAttr(HeroAttrTypeEnum.Anger, num), NewAttrContainerType.CUR);
    }

    @Override
    public String toString() {
        return "[" + this.id + "-" + this.sideEnum + "-" + this.pos + "]";
    }
}


