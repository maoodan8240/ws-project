package ws.newBattle;

import ws.newBattle.skill.NewBattleSkill;
import ws.newBattle.utils.NewBattleLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lee on 16-12-22.
 */
public class BattleContext {
    private NewBattleWrap battle;
    private NewBattleHeroWrap curAttack;
    private List<NewBattleHeroWrap> beAttacks = new ArrayList<>();
    private NewBattleSkill skill;
    private int nextBuffUniId;
    protected Map<Integer, NewBattleHeroWrap> idToBuffChangeHero = new HashMap<>();
    private NewBattleProto battleProto = new NewBattleProto(this);
    private NewBattleLog log;

    public NewBattleWrap getBattle() {
        return battle;
    }

    public void setBattle(NewBattleWrap battle) {
        this.battle = battle;
    }

    public NewBattleHeroWrap getCurAttack() {
        return curAttack;
    }

    public void setCurAttack(NewBattleHeroWrap curAttack) {
        this.curAttack = curAttack;
    }

    public List<NewBattleHeroWrap> getBeAttacks() {
        return beAttacks;
    }

    public void setBeAttacks(List<NewBattleHeroWrap> beAttacks) {
        this.beAttacks = beAttacks;
    }

    public NewBattleSkill getSkill() {
        return skill;
    }

    public void setSkill(NewBattleSkill skill) {
        this.skill = skill;
    }

    public int getNextBuffUniId() {
        return ++nextBuffUniId;
    }


    public Map<Integer, NewBattleHeroWrap> getIdToBuffChangeHero() {
        return idToBuffChangeHero;
    }

    public void clearBuffChangeHero() {
        idToBuffChangeHero.clear();
    }

    public void putBuffChangeHero(NewBattleHeroWrap heroWrap) {
        idToBuffChangeHero.put(heroWrap.getId(), heroWrap);
    }

    public NewBattleProto getBattleProto() {
        return battleProto;
    }


    public void setLog(NewBattleLog log) {
        this.log = log;
    }

    public NewBattleLog getLog() {
        return log;
    }
}
