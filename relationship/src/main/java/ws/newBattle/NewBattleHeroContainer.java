package ws.newBattle;

import ws.relationship.base.HeroAttrs;
import ws.relationship.topLevelPojos.formations.Formation;
import ws.relationship.topLevelPojos.heros.Heros;

import java.util.Map;

/**
 * Created by lee on 17-5-18.
 */
public class NewBattleHeroContainer {
    private Formation formation;
    private Heros heros;
    private Map<Integer, HeroAttrs> heroAttrsMap;

    public NewBattleHeroContainer(Formation formation, Heros heros, Map<Integer, HeroAttrs> heroAttrsMap) {
        this.formation = formation;
        this.heros = heros;
        this.heroAttrsMap = heroAttrsMap;
    }

    public Formation getFormation() {
        return formation;
    }

    public Heros getHeros() {
        return heros;
    }

    public Map<Integer, HeroAttrs> getHeroAttrsMap() {
        return heroAttrsMap;
    }
}
