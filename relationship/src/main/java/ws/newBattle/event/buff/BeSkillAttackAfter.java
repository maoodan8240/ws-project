package ws.newBattle.event.buff;

import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.event.Event;
import ws.newBattle.skill.NewBattleSkill;

import java.util.List;

/**
 * 被打之后（扣除伤害之后）
 */
public class BeSkillAttackAfter implements Event {
    private NewBattleHeroWrap attack; // 攻击者
    private NewBattleSkill skill;
    private List<NewBattleHeroWrap> skillTargets;
    private long lossHp;

    public BeSkillAttackAfter(NewBattleHeroWrap attack, NewBattleSkill skill, List<NewBattleHeroWrap> skillTargets, long lossHp) {
        this.attack = attack;
        this.skill = skill;
        this.skillTargets = skillTargets;
        this.lossHp = lossHp;
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

    public long getLossHp() {
        return lossHp;
    }
}
