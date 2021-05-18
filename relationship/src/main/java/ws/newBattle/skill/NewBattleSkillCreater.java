package ws.newBattle.skill;

import ws.newBattle.BattleContext;
import ws.newBattle.NewBattleHeroWrap;

public class NewBattleSkillCreater {

    public static NewBattleSkill createSkill(BattleContext context, NewBattleHeroWrap hero, int skillId, int level) {
        NewBattleDefaultSkill defaultSkill = new NewBattleDefaultSkill();
        defaultSkill.init(context, hero, skillId, level);
        return defaultSkill;
    }
}
