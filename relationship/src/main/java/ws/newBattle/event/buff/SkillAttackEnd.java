package ws.newBattle.event.buff;

import ws.newBattle.event.Event;
import ws.newBattle.skill.NewBattleSkill;

/**
 * 结束使用技能
 */
public class SkillAttackEnd implements Event {
    private NewBattleSkill skill;

    public SkillAttackEnd(NewBattleSkill skill) {
        this.skill = skill;
    }

    public NewBattleSkill getSkill() {
        return skill;
    }
}
