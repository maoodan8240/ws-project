package ws.newBattle.event.buff;

import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.event.Event;
import ws.newBattle.skill.NewBattleSkill;

import java.util.List;

/**
 * 在攻打目标之后（扣除伤害之后），眩晕状态下不会执行  <----->  技能添加buff之前
 */
public class SkillAttackAfter implements Event {
    private NewBattleHeroWrap attack; // 攻击者
    private NewBattleSkill skill;
    private List<NewBattleHeroWrap> skillTargets;

    public SkillAttackAfter(NewBattleHeroWrap attack, NewBattleSkill skill, List<NewBattleHeroWrap> skillTargets) {
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
