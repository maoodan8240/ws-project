package ws.relationship.topLevelPojos.ultimateTest;

import java.io.Serializable;

/**
 * Created by lee on 17-4-7.
 */
public class UltimateTestHero implements Serializable {
    private static final long serialVersionUID = 4107899921562092566L;
    private int heroId;
    private int anger;
    private int hp;
    private long battleValue;


    public UltimateTestHero() {
    }

    public UltimateTestHero(int heroId, int anger, int hp) {
        this.heroId = heroId;
        this.anger = anger;
        this.hp = hp;
    }

    public UltimateTestHero(int heroId, int anger, int hp, long battleValue) {
        this.heroId = heroId;
        this.anger = anger;
        this.hp = hp;
        this.battleValue = battleValue;
    }

    public int getHeroId() {
        return heroId;
    }

    public void setHeroId(int heroId) {
        this.heroId = heroId;
    }

    public int getAnger() {
        return anger;
    }

    public void setAnger(int anger) {
        this.anger = anger;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public long getBattleValue() {
        return battleValue;
    }

    public void setBattleValue(long battleValue) {
        this.battleValue = battleValue;
    }
}
