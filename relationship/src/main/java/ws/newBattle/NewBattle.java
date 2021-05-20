package ws.newBattle;


import ws.protos.EnumsProtos.BattleSideEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Created by lee on 16-8-24.
 */
public class NewBattle {
    protected BattleContext context;
    protected NewBattleConfig battleConfig;
    protected List<NewBattleSide> defenseSides = new ArrayList<>();
    protected NewBattleSide attackSide;
    protected NewBattleWave curWave;
    protected Map<BattleSideEnum, Queue<NewBattleHeroWrap>> sideToAttackOrder = new HashMap<>();

    //=========================================================================================


    public void setContext(BattleContext context) {
        this.context = context;
    }

    public NewBattleConfig getBattleConfig() {
        return battleConfig;
    }

    public void setBattleConfig(NewBattleConfig battleConfig) {
        this.battleConfig = battleConfig;
    }

    public List<NewBattleSide> getDefenseSides() {
        return defenseSides;
    }

    public void setDefenseSides(List<NewBattleSide> defenseSides) {
        this.defenseSides = defenseSides;
    }

    public NewBattleSide getAttackSide() {
        return attackSide;
    }

    public void setAttackSide(NewBattleSide attackSide) {
        this.attackSide = attackSide;
    }

    public NewBattleWave getCurWave() {
        return curWave;
    }

    public void setCurWave(NewBattleWave curWave) {
        this.curWave = curWave;
    }

    public Map<BattleSideEnum, Queue<NewBattleHeroWrap>> getSideToAttackOrder() {
        return sideToAttackOrder;
    }

    public void setSideToAttackOrder(Map<BattleSideEnum, Queue<NewBattleHeroWrap>> sideToAttackOrder) {
        this.sideToAttackOrder = sideToAttackOrder;
    }

    public BattleContext getContext() {
        return context;
    }
}
