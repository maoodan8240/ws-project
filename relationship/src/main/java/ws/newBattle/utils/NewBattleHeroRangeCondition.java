package ws.newBattle.utils;

import ws.common.utils.random.MersenneTwister;
import ws.newBattle.NewBattleHeroWrap;
import ws.protos.EnumsProtos.BattlePos;
import ws.protos.EnumsProtos.HeroAttrPosTypeEnum;
import ws.protos.EnumsProtos.SexEnum;

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
public class NewBattleHeroRangeCondition {
    private static final MersenneTwister MERSENNE_TWISTER = new MersenneTwister();

    /**
     * 以一个目标作为主体，取其十字上的目标，包括主体
     *
     * @param target
     * @return
     */
    public static List<NewBattleHeroWrap> getTargetRoundHeros(NewBattleHeroWrap target) {
        List<NewBattleHeroWrap> poses = new ArrayList<>();
        poses.add(target);
        List<Integer> crossIdxs = getCrossIdxs(target.getPos().getNumber());
        for (NewBattleHeroWrap hero : target.getBelongBattleSide().getPosToHero().values()) {
            if (hero.isAlive() && crossIdxs.contains(hero.getPos().getNumber())) {
                poses.add(hero);
            }
        }
        return poses;
    }

    /**
     * 在给定的范围，获取指定性别的武将
     *
     * @param heros
     * @param sex
     * @return
     */
    public static List<NewBattleHeroWrap> getSexHeros(List<NewBattleHeroWrap> heros, SexEnum sex) {
        List<NewBattleHeroWrap> poses = new ArrayList<>();
        for (NewBattleHeroWrap hero : heros) {
            if (hero.getCardRow().getCardSexy() == sex) {
                poses.add(hero);
            }
        }
        return poses;
    }


    /**
     * 获取 攻/防/技 属性武将
     *
     * @param heros
     * @param type
     * @return
     */
    public static List<NewBattleHeroWrap> getAttrPosTypeHeros(List<NewBattleHeroWrap> heros, HeroAttrPosTypeEnum type) {
        List<NewBattleHeroWrap> poses = new ArrayList<>();
        for (NewBattleHeroWrap hero : heros) {
            if (hero.getCardRow().getCardType() == type) {
                poses.add(hero);
            }
        }
        return poses;
    }

    /**
     * 在给定的范围，随机N武将
     *
     * @param heros
     * @param n
     * @return
     */
    public static List<NewBattleHeroWrap> random_N_Heros(List<NewBattleHeroWrap> heros, int n) {
        List<NewBattleHeroWrap> poses = new ArrayList<>();
        poses.addAll(heros);
        while (poses.size() > n) {
            int idx = MERSENNE_TWISTER.nextInt(poses.size());
            poses.remove(idx);// remove index ，not object
        }
        return poses;
    }


    /**
     * 取目标主体的身后的武将
     *
     * @param target
     * @return
     */
    public static NewBattleHeroWrap getTargetAfterHero(NewBattleHeroWrap target) {
        BattlePos pos = BattlePos.valueOf(target.getPos().getNumber() + 10); // 后面一个人
        if (NewBattleUtils.containsAndAlive(target.getBelongBattleSide(), pos)) {
            return target.getBelongBattleSide().getPosToHero().get(pos);
        }
        return NewBattleHeroWrap.NULL;
    }


    /**
     * 获取一列
     * <p>
     * 根据指定单体，并且获取其所在列的所有人【存在且活着】
     *
     * @param target
     * @return
     */
    public static List<NewBattleHeroWrap> getTargetCloumnHeros(NewBattleHeroWrap target) {
        List<NewBattleHeroWrap> poses = new ArrayList<>();
        if (target == NewBattleHeroWrap.NULL) {
            return poses;
        }
        int cloumnIdx = NewBattleHeroRange.getCloumnIdx(target.getPos().getNumber());
        int originIdx = cloumnIdx + 10;
        for (; originIdx < 10000; originIdx += 10) {
            if (originIdx > (NewBattleHeroRange.CURRENT_MAX_ROW_IDX * 10 + cloumnIdx)) { // 当前最大的索引
                return poses;
            }
            BattlePos pos = BattlePos.valueOf(originIdx);
            if (NewBattleUtils.containsAndAlive(target.getBelongBattleSide(), pos)) {
                poses.add(target.getBelongBattleSide().getPosToHero().get(pos));
            }
        }
        return poses;
    }


    /**
     * 获取一排
     * <p>
     * 根据指定单体，并且获取其所在排的所有人【存在且活着】
     *
     * @param target
     * @return
     */
    public static List<NewBattleHeroWrap> getTargetRowHeros(NewBattleHeroWrap target) {
        List<NewBattleHeroWrap> poses = new ArrayList<>();
        if (target == NewBattleHeroWrap.NULL) {
            return poses;
        }
        int originIdx = NewBattleHeroRange.getRowIdx(target.getPos().getNumber()) * 10;
        for (int i = 1; i < 100; i++) {
            originIdx = originIdx + i;
            if (originIdx > (originIdx + NewBattleHeroRange.CURRENT_MAX_CLOUMN_IDX)) { // 当前最大的索引
                return poses;
            }
            BattlePos pos = BattlePos.valueOf(originIdx);
            if (NewBattleUtils.containsAndAlive(target.getBelongBattleSide(), pos)) {
                poses.add(target.getBelongBattleSide().getPosToHero().get(pos));
            }
        }
        return poses;
    }


    /**
     * 最小血量单体
     *
     * @param heros
     * @return
     */
    public static NewBattleHeroWrap minHpHero(List<NewBattleHeroWrap> heros) {
        return _MinOrMax(Type.MIN, heros, (h) -> h.getCurHp());
    }


    /**
     * 最大血量单体
     *
     * @param heros
     * @return
     */
    public static NewBattleHeroWrap maxHpHero(List<NewBattleHeroWrap> heros) {
        return _MinOrMax(Type.MAX, heros, (h) -> h.getCurHp());
    }


    /**
     * 生命低于指定万分比值的所有武将
     *
     * @param heros
     * @param precent
     * @return
     */
    public static List<NewBattleHeroWrap> lowerHpHero(List<NewBattleHeroWrap> heros, int precent) {
        return _LessOrGreater(heros, (h) -> h.getCurHpPrecentage() < precent, 99);
    }

    /**
     * 生命高于指定万分比值的所有武将
     *
     * @param heros
     * @param precent
     * @return
     */
    public static List<NewBattleHeroWrap> higherHpHero(List<NewBattleHeroWrap> heros, int precent) {
        return _LessOrGreater(heros, (h) -> h.getCurHpPrecentage() > precent, 99);
    }


    /**
     * 最小怒气单体
     *
     * @param heros
     * @return
     */
    public static NewBattleHeroWrap minAngerHero(List<NewBattleHeroWrap> heros) {
        return _MinOrMax(Type.MIN, heros, (h) -> h.getCurAnger());
    }

    /**
     * 最大怒气单体
     *
     * @param heros
     * @return
     */
    public static NewBattleHeroWrap maxAngerHero(List<NewBattleHeroWrap> heros) {
        return _MinOrMax(Type.MAX, heros, (h) -> h.getCurAnger());
    }


    /**
     * 获取最大最小对象
     *
     * @param type
     * @param heros
     * @param valuer
     * @return
     */
    private static NewBattleHeroWrap _MinOrMax(Type type, List<NewBattleHeroWrap> heros, Valuer valuer) {
        NewBattleHeroWrap heroRe = NewBattleHeroWrap.NULL;
        for (NewBattleHeroWrap h : heros) {
            if (heroRe == NewBattleHeroWrap.NULL) {
                heroRe = h;
                continue;
            }
            if (type == Type.MIN) { // 取最前面的最小的
                if (valuer.value(h) < valuer.value(heroRe)) {
                    heroRe = h;
                }
            } else if (type == Type.MAX) { // 取最后面的最大的
                if (valuer.value(h) >= valuer.value(heroRe)) {
                    heroRe = h;
                }
            }
        }
        return heroRe;
    }

    /**
     * > 或着 <
     *
     * @param heros
     * @param compare
     * @param n
     * @return
     */
    private static List<NewBattleHeroWrap> _LessOrGreater(List<NewBattleHeroWrap> heros, Compare compare, int n) {
        List<NewBattleHeroWrap> herosRe = new ArrayList<>();
        int has = 0;
        for (NewBattleHeroWrap h : heros) {
            if (compare.compare(h)) {
                herosRe.add(h);
                has++;
                if (has >= n) {
                    break;
                }
            }
        }
        return herosRe;
    }

    private enum Type {
        MIN,
        MAX,
        LESS,
        MORE,
    }

    private interface Compare {
        boolean compare(NewBattleHeroWrap hero);
    }

    private interface Valuer {
        long value(NewBattleHeroWrap hero);

    }


    /**
     * 获取十字坐标
     *
     * @param idx
     * @return
     */
    private static List<Integer> getCrossIdxs(int idx) {
        List<Integer> idxs = new ArrayList<>();
        int newIdx = idx + 10; // 向后走一位
        if (NewBattleHeroRange.getRowIdx(newIdx) <= NewBattleHeroRange.CURRENT_MAX_ROW_IDX) {
            idxs.add(newIdx);
        }
        newIdx = idx - 10; //向前走一位
        if (NewBattleHeroRange.getRowIdx(newIdx) >= 1) {
            idxs.add(newIdx);
        }
        newIdx = idx + 1; // 向下走一位
        if (NewBattleHeroRange.getCloumnIdx(newIdx) <= NewBattleHeroRange.CURRENT_MAX_CLOUMN_IDX) {
            idxs.add(newIdx);
        }
        newIdx = idx - 1; // 向上走一位
        if (NewBattleHeroRange.getCloumnIdx(newIdx) >= 1) {
            idxs.add(newIdx);
        }
        return idxs;
    }

}
