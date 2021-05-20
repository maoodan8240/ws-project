package ws.gameServer.features.standalone.extp.utils;

import ws.relationship.topLevelPojos.common.LevelUpObj;

/**
 * Created by lee on 16-10-10.
 */
public class UpgradeLevel {
    /**
     * 调用前，确认oldLevel不大于最大等级
     * 根据提供的经验值升级升级(溢出的经验值直接忽略)
     */
    public static void levelUp(LevelUpObj levelUpObj, long expOffered, int curMaxLevel, NextLevelNeedExp nextLevelNeedExp) {
        levelUp(levelUpObj, expOffered, curMaxLevel, nextLevelNeedExp, null, null, false);
    }

    /**
     * * 调用前，确认oldLevel不大于最大等级
     * 根据提供的经验值升级升级(溢出的经验值直接忽略)
     *  @param levelUpObj
     * @param expOffered
     * @param curMaxLevel
     * @param nextLevelNeedExp
     * @param afterEveryTimeAddExp 每次增加经验后回调
     * @param afterEveryTimeLvUp   每次升级后回调
     */
    public static void levelUp(LevelUpObj levelUpObj, long expOffered, int curMaxLevel, NextLevelNeedExp nextLevelNeedExp, AfterEveryTimeAddExp afterEveryTimeAddExp, AfterEveryTimeLvUp afterEveryTimeLvUp) {
        levelUp(levelUpObj, expOffered, curMaxLevel, nextLevelNeedExp, afterEveryTimeAddExp, afterEveryTimeLvUp, false);
    }

    /**
     * 调用前，确认oldLevel不大于最大等级
     * 根据提供的经验值升级升级(保留溢出的经验值)
     */
    public static void levelUpKeepOvf(LevelUpObj levelUpObj, long expOffered, int curMaxLevel, NextLevelNeedExp nextLevelNeedExp) {
        levelUp(levelUpObj, expOffered, curMaxLevel, nextLevelNeedExp, null, null, true);
    }

    /**
     * 调用前，确认oldLevel不大于最大等级
     * 根据提供的经验值升级升级(保留溢出的经验值)
     *
     * @param levelUpObj
     * @param expOffered
     * @param curMaxLevel
     * @param nextLevelNeedExp
     * @param afterEveryTimeAddExp 每次增加经验后回调
     * @param afterEveryTimeLvUp   每次升级后回调
     */
    public static void levelUpKeepOvf(LevelUpObj levelUpObj, long expOffered, int curMaxLevel, NextLevelNeedExp nextLevelNeedExp, AfterEveryTimeAddExp afterEveryTimeAddExp, AfterEveryTimeLvUp afterEveryTimeLvUp) {
        levelUp(levelUpObj, expOffered, curMaxLevel, nextLevelNeedExp, afterEveryTimeAddExp, afterEveryTimeLvUp, true);
    }

    private static void levelUp(LevelUpObj levelUpObj, long expOffered, int curMaxLevel, NextLevelNeedExp nextLevelNeedExp, AfterEveryTimeAddExp afterEveryTimeAddExp, AfterEveryTimeLvUp afterEveryTimeLvUp, boolean keep) {
        long allExp = levelUpObj.getOvfExp() + expOffered;
        int oldLevel = levelUpObj.getLevel();
        int needExpToNextLevel = nextLevelNeedExp.getExp(oldLevel);
        if (oldLevel > curMaxLevel) {
            return;
        } else if (oldLevel == curMaxLevel) {
            levelUpObj.setOvfExp(keep ? allExp : Math.min(allExp, needExpToNextLevel));
            _afterEveryTimeAddExp(afterEveryTimeAddExp);
            return;
        } else {
            int newLevel = oldLevel + 1;
            long remainExp = allExp - needExpToNextLevel;
            levelUpObj.setOvfExp(0);
            if (needExpToNextLevel <= allExp) {
                levelUpObj.setLevel(newLevel);
                levelUpObj.setOvfExp(remainExp);
                _afterEveryTimeAddExp(afterEveryTimeAddExp);
                if (afterEveryTimeLvUp != null) {
                    if (!afterEveryTimeLvUp.execute()) {
                        return;
                    }
                }
                levelUp(levelUpObj, 0, curMaxLevel, nextLevelNeedExp, afterEveryTimeAddExp, afterEveryTimeLvUp, keep);
            } else {
                levelUpObj.setOvfExp(allExp);
                _afterEveryTimeAddExp(afterEveryTimeAddExp);
                return;
            }
        }
    }


    private static void _afterEveryTimeAddExp(AfterEveryTimeAddExp afterEveryTimeAddExp) {
        if (afterEveryTimeAddExp != null) {
            afterEveryTimeAddExp.execute();
        }
    }


    public interface NextLevelNeedExp {
        int getExp(int oldLevel);
    }

    public interface AfterEveryTimeLvUp {
        boolean execute();
    }

    public interface AfterEveryTimeAddExp {
        void execute();
    }
}
