package ws.gameServer.features.standalone.extp.friends.utils;

import ws.relationship.base.MagicNumbers;
import ws.relationship.topLevelPojos.friends.Friend;
import ws.relationship.topLevelPojos.friends.Friends;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.RandomUtils;
import ws.relationship.utils.RelationshipCommonUtils;
import ws.relationship.utils.RelationshipCommonUtils.SortConditionValues;
import ws.relationship.utils.RelationshipCommonUtils.SortRuleEnum;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FriendsCtrlUtils {

    public static boolean containsFriend(Friends friends, String playerId) {
        return friends.getIdToFriend().containsKey(playerId);
    }

    public static boolean containsApplyPlayerId(Friends friends, String playerId) {
        return friends.getApplyLis().contains(playerId);
    }

    public static Friend getFriend(Friends friends, String playerId) {
        return friends.getIdToFriend().get(playerId);
    }

    public static Friend putNewFriend(Friends friends, String playerId) {
        Friend friend = new Friend(playerId);
        friends.getIdToFriend().put(playerId, friend);
        return friend;
    }

    public static void removeApply(Friends friends, String playerId) {
        friends.getApplyLis().remove(playerId);
    }

    public static void addApply(Friends friends, String playerId) {
        if (containsApplyPlayerId(friends, playerId)) {
            return;
        }
        friends.getApplyLis().add(playerId);
    }

    public static void removeFriend(Friends friends, String playerId) {
        friends.getIdToFriend().remove(playerId);
    }


    public static int curFriendsSize(Friends friends) {
        return friends.getIdToFriend().size();
    }

    /**
     * 是否可以领取体力
     *
     * @param friend
     * @return
     */
    public static boolean canGetEnergy(Friend friend) {
        if (friend.isBeGive() && !friend.isGet()) {
            return true;
        }
        return false;
    }

    /**
     * 随机固定个数的推荐好友的下标
     *
     * @param totalSize
     * @return
     */
    public static Set<Integer> randomIdxs(int totalSize) {
        Set<Integer> idxLis = new HashSet<>();
        int count = Math.min(MagicNumbers.RANDOM_RECOMMEND_FRIEND_NUM * 2, totalSize); // 此处*2了，防止部分simpleplayer为null
        for (int i = 0; i < count * 10; i++) {
            if (idxLis.size() >= count) {
                break;
            }
            idxLis.add(RandomUtils.dropBetweenTowNum(0, totalSize - 1));
        }
        return idxLis;
    }

    /**
     * 申请列表 缓存排序
     *
     * @param cache
     * @return
     */
    public static Map<String, SimplePlayer> sortApplyCache(Map<String, SimplePlayer> cache) {
        return RelationshipCommonUtils.sortMapByValue(cache, new SortConditionValues<SimplePlayer>() {
            @Override
            public long[] compareValues(SimplePlayer o1, SimplePlayer o2) {
                // 战斗力 > 等级
                long[] arr = new long[2];
                arr[0] = o1.getBattleValue() - o2.getBattleValue();
                arr[1] = o1.getLv() - o2.getLv();
                return arr;
            }
        }, SortRuleEnum.ESC);
    }

    /**
     * (好友，删除好友) 缓存排序
     *
     * @param cache
     * @param logOutSort 1 代表 越近的登录越排在前面; -1 代表 越近的登录越在后面
     * @return
     */
    public static Map<String, SimplePlayer> sortFriendsCache(Map<String, SimplePlayer> cache, int logOutSort) {
        return RelationshipCommonUtils.sortMapByValue(cache, new SortConditionValues<SimplePlayer>() {
            @Override
            public long[] compareValues(SimplePlayer o1, SimplePlayer o2) {
                long[] arr = new long[3];
                // 最近登出 > 战斗力 > 等级
                arr[0] = logOutSort * (o1.getLastLoginTime() - o2.getLastLoginTime());
                arr[1] = o1.getBattleValue() - o2.getBattleValue();
                arr[2] = o1.getLv() - o2.getLv();
                return arr;
            }
        }, SortRuleEnum.ESC);
    }

    /**
     * 推荐 缓存排序
     *
     * @param cache
     * @return
     */
    public static Map<String, SimplePlayer> sortRecommendCache(Map<String, SimplePlayer> cache) {
        return RelationshipCommonUtils.sortMapByValue(cache, new SortConditionValues<SimplePlayer>() {
            @Override
            public long[] compareValues(SimplePlayer o1, SimplePlayer o2) {
                long[] arr = new long[3];
                // 等级 > 战斗力
                arr[1] = o1.getLv() - o2.getLv();
                arr[2] = o1.getBattleValue() - o2.getBattleValue();
                return arr;
            }
        }, SortRuleEnum.ESC);
    }
}
