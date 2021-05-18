package ws.newBattle.event;

import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.skill.NewBattleSkill;

/**
 * 通知所有人有武将阵亡
 */
public class HeroDie implements Event {
    private NewBattleHeroWrap attack;      // 攻击者
    private NewBattleSkill skill;          // 攻击者技能
    private NewBattleHeroWrap die;         // 阵亡者

    public HeroDie(NewBattleHeroWrap attack, NewBattleSkill skill, NewBattleHeroWrap die) {
        this.attack = attack;
        this.skill = skill;
        this.die = die;
    }

    public NewBattleHeroWrap getDie() {
        return die;
    }

    public NewBattleHeroWrap getAttack() {
        return attack;
    }

    public NewBattleSkill getSkill() {
        return skill;
    }
}
