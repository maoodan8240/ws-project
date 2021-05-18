package ws.newBattle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.newBattle.event.BattleBegin;
import ws.newBattle.event.Event;
import ws.newBattle.event.hero.RoundBegin;
import ws.newBattle.event.hero.RoundEnd;
import ws.protos.EnumsProtos.BattleResultTypeEnum;
import ws.protos.EnumsProtos.BattleSideEnum;

import java.util.Arrays;
import java.util.Queue;

// TODO: 16-12-30 轮换怒气
public class NewBattleWrap extends NewBattle {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewBattleWrap.class);

    public void init(NewBattleSide attackSide, NewBattleSide... defenseSides) {
        this.attackSide = attackSide;
        this.defenseSides.addAll(Arrays.asList(defenseSides));
    }

    public void waveBattle() {
        int curWaveNum = 1;
        while (curWaveNum <= this.battleConfig.getWaves()) {
            createNewWave(curWaveNum);
            if (isRoundOver()) {
                break;
            }
            this.context.getBattleProto().setCurWaveBuilder();
            for (int roundNum = 1; roundNum <= battleConfig.getRunRound(); roundNum++) {
                if (isRoundOver()) {
                    break;
                }
                this.curWave.setCurRoundNum(roundNum);
                this.context.getLog().add("\n       新的战斗:curWaveNum=%s curRoundNum=%s 准备开始...", this.curWave.curWaveNum, this.curWave.curRoundNum);
                roundBattle();
            }
            this.context.getBattleProto().onCurWaveEnd();
            curWaveNum++;
        }

        this.context.getBattleProto().setBattleResult(getBattleResult());
    }

    private void roundBattle() {
        initSideToAttackOrder();
        if (getCurRoundNum() == 1) {
            notifyAllHeroes(new BattleBegin());
        }
        notifyAllHeroes(new RoundBegin(getCurRoundNum()));
        this.context.getBattleProto().setCurRoundBuilder();
        try {
            //----------------攻击方出手----------------
            heroTick(BattleSideEnum.ATTACK, NewBattleHeroWrap::angerTick);
            heroTick(BattleSideEnum.ATTACK, NewBattleHeroWrap::simpleTick);
            //----------------防御方出手----------------
            heroTick(BattleSideEnum.DEFENSE, NewBattleHeroWrap::angerTick);
            heroTick(BattleSideEnum.DEFENSE, NewBattleHeroWrap::simpleTick);
        } finally {
            initSideToAttackOrder();
            notifyAllHeroes(new RoundEnd(getCurRoundNum()));
            this.context.getBattleProto().onCurRoundEnd();
        }
    }


    private void initSideToAttackOrder() {
        this.sideToAttackOrder.clear();
        this.sideToAttackOrder.put(BattleSideEnum.ATTACK, this.curWave.sideMap.get(BattleSideEnum.ATTACK).allAliveHero());
        this.sideToAttackOrder.put(BattleSideEnum.DEFENSE, this.curWave.sideMap.get(BattleSideEnum.DEFENSE).allAliveHero());
    }

    private void createNewWave(int curWaveNum) {
        this.curWave = new NewBattleWave();
        this.curWave.sideMap.put(BattleSideEnum.DEFENSE, this.defenseSides.get(curWaveNum - 1));
        this.curWave.curWaveNum = curWaveNum;
        if (!this.battleConfig.isKeepStatusBetweenWaves()) {
            this.attackSide.resetHerosStatus();
        }
        this.curWave.sideMap.put(BattleSideEnum.ATTACK, this.attackSide);
        this.context.getLog().add("\n       新的战斗:curWaveNum=%s 准备开始...", this.curWave.curWaveNum);
    }

    private Queue<NewBattleHeroWrap> getQueue(BattleSideEnum side) {
        return this.sideToAttackOrder.get(side);
    }

    public void notifyAllHeroes(Event event) {
        sideToAttackOrder.values().forEach(queue -> queue.forEach(hero -> {
            hero.onNotify(event);
        }));
    }

    public int getCurRoundNum() {
        return super.getCurWave().getCurRoundNum();
    }

    private boolean isRoundOver() {
        int aliveNumAttack = this.curWave.sideMap.get(BattleSideEnum.ATTACK).allAliveHero().size();
        int aliveNumDefense = this.curWave.sideMap.get(BattleSideEnum.DEFENSE).allAliveHero().size();

        boolean rs = aliveNumAttack <= 0 || aliveNumDefense <= 0;
        if (rs) {
            this.context.getLog().add("       新的战斗:战斗结束了. curWaveNum=%s curRoundNum=%s ", this.curWave.curWaveNum, this.curWave.curRoundNum);
        }
        return rs;
    }


    private BattleResultTypeEnum getBattleResult() {
        int aliveNumAttack = this.curWave.sideMap.get(BattleSideEnum.ATTACK).allAliveHero().size();
        int aliveNumDefense = this.curWave.sideMap.get(BattleSideEnum.DEFENSE).allAliveHero().size();
        if (aliveNumAttack <= 0 && aliveNumDefense <= 0) {
            return BattleResultTypeEnum.BATTLE_RS_DRAW;
        } else if (aliveNumDefense > 0) {
            return BattleResultTypeEnum.BATTLE_RS_FAIL;
        } else {
            return BattleResultTypeEnum.BATTLE_RS_VICTORY;
        }
    }

    public void playAllAnger() {
        System.out.println("--------");
        sideToAttackOrder.values().forEach(queue -> queue.forEach(hero -> {
                }
        ));
        System.out.println("--------");
    }

    private void heroTick(BattleSideEnum side, HeroTick heroTick) {
        initSideToAttackOrder();
        Queue<NewBattleHeroWrap> queue = getQueue(side);
        while (!queue.isEmpty()) {
            if (isRoundOver()) {
                return;
            }
            NewBattleHeroWrap hero = queue.remove();
            this.context.getLog().add("\n");
            heroTick.chooseTick(hero);
        }
    }

    private interface HeroTick {
        void chooseTick(NewBattleHeroWrap hero);
    }
}
