package ws.newBattle.utils;

import ws.common.utils.random.MersenneTwister;
import ws.newBattle.NewBattleHeroWrap;
import ws.newBattle.NewBattleSide;
import ws.protos.EnumsProtos.BattlePos;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 当前战斗步阵的位置索引
 * D   A   |  A   D
 * E   B   |  B   E
 * F   C   |  C   F
 * 值
 * 21  11  |  11  21
 * 22  12  |  12  22
 * 23  13  |  13  23
 * </pre>
 */
public class NewBattleHeroRange {
    private static final MersenneTwister MERSENNE_TWISTER = new MersenneTwister();

    /**
     * 当前最大的纵向的 Idx (纵向-排)
     */
    public static final int CURRENT_MAX_ROW_IDX = 2;
    /**
     * 当前最大的横向的 Idx (横向-列)
     */
    public static final int CURRENT_MAX_CLOUMN_IDX = 3;


    /**
     * 获取全体武将【存在且活着】
     *
     * @param battleSide
     * @return
     */
    public static List<NewBattleHeroWrap> getAllAliveHeros(NewBattleSide battleSide) {
        List<NewBattleHeroWrap> poses = new ArrayList<>();
        for (int rIdx = 1; rIdx <= CURRENT_MAX_ROW_IDX; rIdx++) {
            for (int cIdx = 1; cIdx <= CURRENT_MAX_CLOUMN_IDX; cIdx++) {
                BattlePos pos = BattlePos.valueOf(rIdx * 10 + cIdx);
                if (NewBattleUtils.containsAndAlive(battleSide, pos)) {
                    poses.add(battleSide.getPosToHero().get(pos));
                }
            }
        }
        return poses;
    }


    /**
     * 获取前排武将【存在且活着】
     *
     * @return
     */
    public static List<NewBattleHeroWrap> getFrontRowHeros(NewBattleSide battleSide) {
        List<NewBattleHeroWrap> poses = new ArrayList<>();
        for (int rIdx = 1; rIdx <= CURRENT_MAX_ROW_IDX; rIdx++) {
            for (int cIdx = 1; cIdx <= CURRENT_MAX_CLOUMN_IDX; cIdx++) {
                BattlePos pos = BattlePos.valueOf(rIdx * 10 + cIdx);
                if (NewBattleUtils.containsAndAlive(battleSide, pos)) {
                    poses.add(battleSide.getPosToHero().get(pos));
                }
            }
            if (poses.size() > 0) {
                return poses;
            }
            // 第一排为空时，继续第二排...
        }
        return new ArrayList<>();
    }


    /**
     * 获取后排武将【存在且活着】
     *
     * @return
     */
    public static List<NewBattleHeroWrap> getBackRowHeros(NewBattleSide battleSide) {
        List<NewBattleHeroWrap> poses = new ArrayList<>();
        for (int rIdx = CURRENT_MAX_ROW_IDX; rIdx >= 1; rIdx--) {
            for (int cIdx = 1; cIdx <= CURRENT_MAX_CLOUMN_IDX; cIdx++) {
                BattlePos pos = BattlePos.valueOf(rIdx * 10 + cIdx);
                if (NewBattleUtils.containsAndAlive(battleSide, pos)) {
                    poses.add(battleSide.getPosToHero().get(pos));
                }
            }
            if (poses.size() > 0) {
                return poses;
            }
            // 最后一排为空时，继续倒数第二排...
        }
        return new ArrayList<>();
    }


    /**
     * 获取前排随机单体【存在且活着】
     *
     * @return
     */
    public static NewBattleHeroWrap getFrontRandomHero(NewBattleSide battleSide) {
        List<NewBattleHeroWrap> poses = getFrontRowHeros(battleSide);
        if (poses.size() <= 0) {
            return NewBattleHeroWrap.NULL;
        }
        int randomIdx = MERSENNE_TWISTER.nextInt(poses.size());
        return poses.get(randomIdx);
    }


    /**
     * 获取后排随机单体【存在且活着】
     *
     * @return
     */
    public static NewBattleHeroWrap getBackRandomHero(NewBattleSide battleSide) {
        List<NewBattleHeroWrap> poses = getBackRowHeros(battleSide);
        if (poses.size() <= 0) {
            return NewBattleHeroWrap.NULL;
        }
        int randomIdx = MERSENNE_TWISTER.nextInt(poses.size());
        return poses.get(randomIdx);
    }


    /**
     * 获取一列
     * <p>
     * 先获取前排随机单体，并且获取其所在列的所有人【存在且活着】
     *
     * @param battleSide
     * @return
     */
    public static List<NewBattleHeroWrap> getRandomCloumnHeros(NewBattleSide battleSide) {
        NewBattleHeroWrap hero = getFrontRandomHero(battleSide);
        return NewBattleHeroRangeCondition.getTargetCloumnHeros(hero);
    }

    // 获取横向坐标--排的索引,第几排
    public static int getRowIdx(int idx) {
        return idx / 10;
    }

    // 获取纵向坐标--列的索引，第几列
    public static int getCloumnIdx(int idx) {
        return idx % 10;
    }
}
