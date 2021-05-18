package ws.newBattle.event.buff;

import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.event.Event;
import ws.newBattle.skill.NewBattleSkill;

import java.util.List;

/**
 * 在攻打目标之前（计算伤害之前）
 */
public class SkillAttackBefore implements Event {
    private NewBattleHeroWrap attack; // 攻击者
    private NewBattleSkill skill;
    private List<NewBattleHeroWrap> skillTargets;

    public SkillAttackBefore(NewBattleHeroWrap attack, NewBattleSkill skill, List<NewBattleHeroWrap> skillTargets) {
        this.attack = attack;
        this.skill = skill;
        this.skillTargets = skillTargets;
    }

    public NewBattleHeroWrap getAttack() {
        return attack;
    }

    public NewBattleSkill getSkill() {
        return skill;
    }

    public List<NewBattleHeroWrap> getSkillTargets() {
        return skillTargets;
    }
}
