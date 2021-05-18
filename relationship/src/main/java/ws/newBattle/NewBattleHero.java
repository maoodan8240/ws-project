package ws.newBattle;

import ws.newBattle.buff.NewBattleBuffManager;
import ws.newBattle.event.EventListener;
import ws.newBattle.skill.NewBattleSkillManager;
import ws.protos.EnumsProtos.BattlePos;
import ws.protos.EnumsProtos.BattleSideEnum;
import ws.relationship.base.HeroAttrs;
import ws.relationship.table.tableRows.Table_New_Card_Row;
import ws.relationship.topLevelPojos.heros.Hero;


public abstract class NewBattleHero implements EventListener {
    protected BattleContext context;
    protected int id;
    protected Hero hero;
    protected Table_New_Card_Row cardRow;
    protected NewBattleSide belongBattleSide;
    protected BattleSideEnum sideEnum;
    protected BattlePos pos;
    protected HeroAttrs heroAttrsOri = new HeroAttrs(); // 初始属性
    protected HeroAttrs heroAttrsCur = new HeroAttrs(); // 初始属性
    protected HeroAttrs heroAttrsAttackTmp = new HeroAttrs(); // 攻击临时属性 攻击后清除

    protected NewBattleSkillManager skillManager = new NewBattleSkillManager();
    protected NewBattleBuffManager buffManager = new NewBattleBuffManager();

    protected boolean waitForResurgence; // 等待复活

    //=========================================================================================
    //=========================================================================================


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public Table_New_Card_Row getCardRow() {
        return cardRow;
    }

    public void setCardRow(Table_New_Card_Row cardRow) {
        this.cardRow = cardRow;
    }


    public NewBattleSide getBelongBattleSide() {
        return belongBattleSide;
    }

    public void setBelongBattleSide(NewBattleSide belongBattleSide) {
        this.belongBattleSide = belongBattleSide;
    }

    public BattleSideEnum getSideEnum() {
        return sideEnum;
    }

    public void setSideEnum(BattleSideEnum sideEnum) {
        this.sideEnum = sideEnum;
    }

    public BattlePos getPos() {
        return pos;
    }

    public void setPos(BattlePos pos) {
        this.pos = pos;
    }

    public HeroAttrs getHeroAttrsOri() {
        return heroAttrsOri;
    }

    public void setHeroAttrsOri(HeroAttrs heroAttrsOri) {
        this.heroAttrsOri = heroAttrsOri;
    }

    public HeroAttrs getHeroAttrsCur() {
        return heroAttrsCur;
    }

    public void setHeroAttrsCur(HeroAttrs heroAttrsCur) {
        this.heroAttrsCur = heroAttrsCur;
    }

    public HeroAttrs getHeroAttrsAttackTmp() {
        return heroAttrsAttackTmp;
    }

    public void setHeroAttrsAttackTmp(HeroAttrs heroAttrsAttackTmp) {
        this.heroAttrsAttackTmp = heroAttrsAttackTmp;
    }

    public NewBattleSkillManager getSkillManager() {
        return skillManager;
    }

    public void setSkillManager(NewBattleSkillManager skillManager) {
        this.skillManager = skillManager;
    }

    public NewBattleBuffManager getBuffManager() {
        return buffManager;
    }

    public void setBuffManager(NewBattleBuffManager buffManager) {
        this.buffManager = buffManager;
    }

    public boolean isWaitForResurgence() {
        return waitForResurgence;
    }

    public void setWaitForResurgence(boolean waitForResurgence) {
        this.waitForResurgence = waitForResurgence;
    }

    public BattleContext getContext() {
        return context;
    }

    public void setContext(BattleContext context) {
        this.context = context;
    }
}
