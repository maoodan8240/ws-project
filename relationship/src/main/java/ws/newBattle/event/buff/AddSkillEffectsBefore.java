package ws.newBattle.event.buff;

import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.event.Event;
import ws.newBattle.skill.NewBattleSkill;

import java.util.List;

/**
 * 开始添加技能作用效果之前
 */
public class AddSkillEffectsBefore implements Event {
    private NewBattleHeroWrap attack; // 攻击者
    private NewBattleSkill skill;
    private List<NewBattleHeroWrap> skillTargets;

    public AddSkillEffectsBefore(NewBattleHeroWrap attack, NewBattleSkill skill, List<NewBattleHeroWrap> skillTargets) {
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
