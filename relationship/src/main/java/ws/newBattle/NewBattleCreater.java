package ws.newBattle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.newBattle.utils.NewBattleLog;
import ws.newBattle.utils.NewBattleUtils;
import ws.protos.BattleProtos.Sm_Battle_BackData;
import ws.protos.EnumsProtos.BattlePos;
import ws.protos.EnumsProtos.BattleSideEnum;
import ws.relationship.base.HeroAttrs;
import ws.relationship.topLevelPojos.formations.Formation;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.heros.Heros;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lee on 17-5-17.
 */
public class NewBattleCreater {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewBattleCreater.class);
    private static final int IDPREFIX_ATTACK = 11110000;
    private static final int IDPREFIX_DEFENSE = 22220000;

    // todo 需要为所有Hero重新分配唯一Id
    public static Sm_Battle_BackData runNewBattle(NewBattleHeroContainer heroContainerAttack, NewBattleHeroContainer heroContainerDefense) {
        NewBattleLog log = new NewBattleLog();
        log.add("准备开始新的战斗......");
        log.add("       新的战斗:攻击方 个数=%s", heroContainerAttack.getHeroAttrsMap().size());
        log.add("       新的战斗:防守方 个数=%s", heroContainerDefense.getHeroAttrsMap().size());
        long begin = System.currentTimeMillis();
        NewBattleWrap battleWrap = createNewBattle();
        battleWrap.getContext().setLog(log);
        initBattleHero(battleWrap, heroContainerAttack, heroContainerDefense);
        battleWrap.waveBattle();
        long end = System.currentTimeMillis();
        log.add("本次生成战斗脚本使用时间=(%s)毫秒.", (end - begin));
        log.print();
        return battleWrap.getContext().getBattleProto().getBackData();
    }

    public static void initBattleHero(NewBattleWrap battleWrap, NewBattleHeroContainer heroContainerAttack, NewBattleHeroContainer heroContainerDefense) {
        NewBattleSide sideAttack = createBattleSide(BattleSideEnum.ATTACK, battleWrap.getContext(), heroContainerAttack.getFormation(), heroContainerAttack.getHeros(), heroContainerAttack.getHeroAttrsMap());
        NewBattleSide sideDefense = createBattleSide(BattleSideEnum.DEFENSE, battleWrap.getContext(), heroContainerDefense.getFormation(), heroContainerDefense.getHeros(), heroContainerDefense.getHeroAttrsMap());
        battleWrap.init(sideAttack, sideDefense);
    }

    public static NewBattleWrap createNewBattle() {
        BattleContext context = new BattleContext();
        NewBattleConfig config = new NewBattleConfig(10, 1);
        NewBattleWrap battle = new NewBattleWrap();
        battle.setBattleConfig(config);
        context.setBattle(battle);
        battle.setContext(context);
        return battle;
    }


    private static NewBattleSide createBattleSide(BattleSideEnum battleSideEnum, BattleContext context, Formation formationAttack, Heros heros, Map<Integer, HeroAttrs> heroAttrsMapAttack) {
        NewBattleSide side = new NewBattleSide(battleSideEnum);
        Map<BattlePos, NewBattleHeroWrap> posToHero = new HashMap<>();
        formationAttack.getPosToFormationPos().forEach((pos, posObj) -> {
            int heroId = posObj.getHeroId();
            if (heroId > 0 && heros.getIdToHero().containsKey(heroId) && heroAttrsMapAttack.containsKey(heroId)) {
                Hero hero = heros.getIdToHero().get(heroId).clone();
                HeroAttrs heroAttrs = heroAttrsMapAttack.get(heroId);
                BattlePos battlePos = NewBattleUtils.convetHeroPositionEnumToBattlePos(posObj.getPos());
                hero.setId(genHeroId(battlePos, battleSideEnum));
                NewBattleHeroWrap heroWrap = createHeroWrap(context, side, battlePos, hero, heroAttrs);
                context.getLog().add("       新的战斗:初始化新的武将-> side=%s--pos=%s--id=%s ", battleSideEnum, battlePos, hero.getId());
                posToHero.put(NewBattleUtils.convetHeroPositionEnumToBattlePos(pos), heroWrap);
            }
        });
        side.init(posToHero);
        return side;
    }

    private static NewBattleHeroWrap createHeroWrap(BattleContext context, NewBattleSide side, BattlePos pos, Hero hero, HeroAttrs heroAttrs) {
        NewBattleHeroWrap heroWrap = new NewBattleHeroWrap();
        heroWrap.init(context, pos, hero, side, heroAttrs);
        return heroWrap;
    }

    private static int genHeroId(BattlePos battlePos, BattleSideEnum side) {
        if (side == BattleSideEnum.ATTACK) {
            return IDPREFIX_ATTACK + battlePos.getNumber();
        } else {
            return IDPREFIX_DEFENSE + battlePos.getNumber();
        }
    }
}
