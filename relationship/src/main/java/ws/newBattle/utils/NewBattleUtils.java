package ws.newBattle.utils;

import ws.newBattle.NewBattleSide;
import ws.protos.EnumsProtos.BattlePos;
import ws.protos.EnumsProtos.HeroPositionEnum;

/**
 * Created by lee on 16-9-8.
 */
public class NewBattleUtils {


    /**
     * 含有武将且活着
     *
     * @param side
     * @param pos
     * @return
     */
    public static boolean containsAndAlive(NewBattleSide side, BattlePos pos) {
        if (side.getPosToHero().containsKey(pos) && side.getPosToHero().get(pos).isAlive()) {
            return true;
        }
        return false;
    }


    /**
     * 位置转换
     *
     * @param position
     * @return
     */
    public static BattlePos convetHeroPositionEnumToBattlePos(HeroPositionEnum position) {
        switch (position) {
            case HERO_POSITION_ONE:
                return BattlePos.A;
            case HERO_POSITION_TWO:
                return BattlePos.B;
            case HERO_POSITION_THREE:
                return BattlePos.C;
            case HERO_POSITION_FOUR:
                return BattlePos.D;
            case HERO_POSITION_FIVE:
                return BattlePos.E;
            case HERO_POSITION_SIX:
                return BattlePos.F;
            default:
                return null;
        }
    }

}
