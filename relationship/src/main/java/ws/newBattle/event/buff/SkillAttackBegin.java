package ws.newBattle.event.buff;

import ws.newBattle.event.Event;
import ws.newBattle.skill.NewBattleSkill;

/**
 * 准备开始使用技能
 */
public class SkillAttackBegin implements Event {
    private NewBattleSkill skill;

    public SkillAttackBegin(NewBattleSkill skill) {
        this.skill = skill;
    }

    public NewBattleSkill getSkill() {
        return skill;
    }
}
