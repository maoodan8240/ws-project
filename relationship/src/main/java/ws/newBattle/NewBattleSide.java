package ws.newBattle;


import ws.protos.EnumsProtos.BattlePos;
import ws.protos.EnumsProtos.BattleSideEnum;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Created by zhangweiwei on 16-8-23.
 */
public class NewBattleSide {
    private BattleSideEnum curSide;
    private Map<BattlePos, NewBattleHeroWrap> posToHero = new HashMap<>();

    public NewBattleSide(BattleSideEnum curSide) {
        this.curSide = curSide;
    }

    public void init(Map<BattlePos, NewBattleHeroWrap> posToHero) {
        this.posToHero.putAll(posToHero);
    }


    //=========================================================================================
    //=========================================================================================

    public void resetHerosStatus() {
        for (NewBattleHeroWrap hero : this.posToHero.values()) {
            // TODO: 16-12-21 heroWrap reset
        }
    }

    public Queue<NewBattleHeroWrap> allAliveHero() {
        Queue<NewBattleHeroWrap> heros = new LinkedList<>();
        for (BattlePos pos : BattlePos.values()) {
            if (this.posToHero.containsKey(pos)) {
                NewBattleHeroWrap hero = this.posToHero.get(pos);
                if (!hero.isDie()) {
                    heros.add(hero);
                }
            }
        }
        return heros;
    }


    public BattleSideEnum getCurSide() {
        return curSide;
    }

    public void setCurSide(BattleSideEnum curSide) {
        this.curSide = curSide;
    }

    public Map<BattlePos, NewBattleHeroWrap> getPosToHero() {
        return posToHero;
    }

    public void setPosToHero(Map<BattlePos, NewBattleHeroWrap> posToHero) {
        this.posToHero = posToHero;
    }
}
