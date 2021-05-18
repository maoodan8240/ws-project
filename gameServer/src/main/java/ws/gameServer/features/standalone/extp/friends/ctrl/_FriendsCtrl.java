package ws.gameServer.features.standalone.extp.friends.ctrl;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.friends.msg.In_AgreeApply;
import ws.gameServer.features.standalone.extp.friends.msg.In_ApplyFriend;
import ws.gameServer.features.standalone.extp.friends.msg.In_DelFriend;
import ws.gameServer.features.standalone.extp.friends.msg.In_GiveFriendEnergy;
import ws.gameServer.features.standalone.extp.friends.utils.FriendsCtrlProtos;
import ws.gameServer.features.standalone.extp.friends.utils.FriendsCtrlUtils;
import ws.gameServer.features.standalone.extp.friends.utils.FriendsOnlineOfflineUtils;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.CommonProtos.Sm_Common_SimplePlayer_Base;
import ws.protos.EnumsProtos.CommonRankTypeEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.FriendProtos.Sm_Friend;
import ws.protos.FriendProtos.Sm_Friend.Action;
import ws.protos.errorCode.ErrorCodeProtos.ErrorCodeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.base.MagicWords_Redis;
import ws.relationship.base.RedisRankAndScore;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.CheckPlayerOnlineMsgRequest;
import ws.relationship.base.resultCode.ResultCodeEnum;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.simplePlayer.SimplePlayerDao;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.topLevelPojos.friends.Friend;
import ws.relationship.topLevelPojos.friends.Friends;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.ActorMsgSynchronizedExecutor;
import ws.relationship.utils.ClusterMessageSender;
import ws.relationship.utils.ProtoUtils;
import ws.relationship.utils.RedisRankUtils;
import ws.relationship.utils.RelationshipCommonUtils;
import ws.relationship.utils.SimplePojoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class _FriendsCtrl extends AbstractPlayerExteControler<Friends> implements FriendsCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_FriendsCtrl.class);
    private static final int MAX_RECOMMEND_NUM = 300;
    private MongoDBClient mongoDBClient = GlobalInjector.getInstance(MongoDBClient.class);
    private SimplePlayerDao simplePlayerDao = DaoContainer.getDao(SimplePlayer.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private Map<String, SimplePlayer> friendsCache = new HashMap<>();                // 好友列表缓存
    private Map<String, SimplePlayer> friendsCache_Sort = new HashMap<>();           // 好友列表缓存-排序-->离线时间(小)＞战力＞等级
    private Map<String, SimplePlayer> delFriendsCache_Sort = new HashMap<>();        // 好友列表缓存-排序-->离线时间(大)＞战力＞等级

    private Map<String, SimplePlayer> applyCache = new HashMap<>();                  // 申请列表缓存
    private Map<String, SimplePlayer> notFriendPlayerIdsCache = new HashMap<>();     // 不是好友的缓存列表
    private List<RedisRankAndScore> rankAndScoreList = new ArrayList<>();            // 玩家最近登录排行

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
        resetGetEnergyStatus();
    }

    @Override
    public void _initAfterChanged() throws Exception {
    }

    @Override
    public void sync() {
        clearAllCache();
        initCacheData();

        int maxGetTimes = AllServerConfig.Friends_GetEnergy_DaliyTimes.getConfig();
        int remainGetTimes = maxGetTimes - target.getGetEnergyTimes();
        SenderFunc.sendInner(this, Sm_Friend.class, Sm_Friend.Builder.class, Action.RESP_SYNC, (b, br) -> {
            _fill_Sm_Common_SimplePlayer_Base(0, MagicNumbers.ONE_PAGE_FRIEND_NUM - 1, friendsCache_Sort, b);
            b.setFriendCount(friendsCount());
            b.setApplyCount(applyListCount());
            b.setRemainGetTimes(remainGetTimes);
        });
    }

    @Override
    public void queryFriends(int start, int end) {
        SenderFunc.sendInner(this, Sm_Friend.class, Sm_Friend.Builder.class, Action.RESP_GET_FRIEND, (b, br) -> {
            _fill_Sm_Common_SimplePlayer_Base(start, end, friendsCache_Sort, b);
            b.setFriendCount(friendsCount());
        });
    }

    private void _fill_Sm_Common_SimplePlayer_Base(int start, int end, Map<String, SimplePlayer> map, Sm_Friend.Builder b) {
        if (map.size() <= 0) {
            return;
        }
        int num = 0;
        int startNew = Math.min(0, start);
        int endNew = Math.min(map.size() - 1, end);
        for (Entry<String, SimplePlayer> entry : map.entrySet()) {
            if (num >= startNew && num <= endNew) {
                Sm_Common_SimplePlayer_Base base = ProtoUtils.create_Sm_Common_SimplePlayer_Base(entry.getValue());
                b.addPlayer(FriendsCtrlProtos.create_Sm_Friend_Player(base, FriendsCtrlUtils.getFriend(target, entry.getKey()), num));
                num++;
            }
        }
    }


    @Override
    public void queryFriendsForDel(int start, int end) {
        SenderFunc.sendInner(this, Sm_Friend.class, Sm_Friend.Builder.class, Action.RESP_GET_DEL_FRIEND, (b, br) -> {
            _fill_Sm_Common_SimplePlayer_Base(start, end, delFriendsCache_Sort, b);
        });
    }

    @Override
    public void apply(List<String> playerIdLis) {
        LogicCheckUtils.validateParam(List.class, playerIdLis);
        for (String toPlayerId : playerIdLis) {
            if (isSelfId(toPlayerId)) {
                LOGGER.debug("toPlayerId={} 不能申请自己为好友.", toPlayerId);
                continue;
            }
            if (FriendsCtrlUtils.containsFriend(target, toPlayerId)) { // 已经是好友了
                LOGGER.debug("toPlayerId={} 已经是自己的好友了，不用再次申请好友.", toPlayerId);
                continue;
            }
            In_ApplyFriend.Request request = new In_ApplyFriend.Request(getPlayerCtrl().getPlayerId(), toPlayerId);
            sendCheckPlayerOnlineMsg(toPlayerId, request);
        }
        SenderFunc.sendInner(this, Sm_Friend.class, Sm_Friend.Builder.class, Action.RESP_ADD, (b, br) -> {
        });
        save();
    }

    @Override
    public void agree(List<String> playerIdLis) {
        LogicCheckUtils.validateParam(List.class, playerIdLis);
        int addNum = 0;
        for (String playerId : playerIdLis) {
            if (isSelfId(playerId)) {
                continue;
            }
            if (!FriendsCtrlUtils.containsApplyPlayerId(target, playerId)) { // 申请列表中没有该playerId，无法同意添加
                continue;
            }
            if (FriendsCtrlUtils.curFriendsSize(target) >= MagicNumbers.MAX_FRIENDS_NUM) {
                break; // 已经达到最大好友数量了
            }
            In_AgreeApply.Request request = new In_AgreeApply.Request(getPlayerCtrl().getPlayerId(), playerId);
            In_AgreeApply.Response response = sendCheckPlayerOnlineMsgSync(playerId, request);
            if (response.getResultCode() == ResultCodeEnum.SUCCESS) {
                FriendsCtrlUtils.removeApply(target, playerId);
                FriendsCtrlUtils.putNewFriend(target, playerId);

                friendsCache.put(playerId, applyCache.remove(playerId)); // 转移
                addNum++;
            }
        }
        SenderFunc.sendInner(this, Sm_Friend.class, Sm_Friend.Builder.class, Action.RESP_AGREE, (b, br) -> {
            b.setFriendCount(friendsCount());
        });
        save();
        // 重新排序
        if (addNum > 0) {
            sortFriendsCache();
            sortDelFriendsCache();
        }
    }

    @Override
    public void refuse(List<String> playerIdLis) {
        LogicCheckUtils.validateParam(List.class, playerIdLis);
        for (String playerId : playerIdLis) {
            if (!FriendsCtrlUtils.containsApplyPlayerId(target, playerId)) { // 申请列表中没有该playerId，无法拒绝
                continue;
            }
            FriendsCtrlUtils.removeApply(target, playerId);
            applyCache.remove(playerId); // 删除后依然是有序的
        }
        SenderFunc.sendInner(this, Sm_Friend.class, Sm_Friend.Builder.class, Action.RESP_REFUSE, (b, br) -> {
        });
        save();
    }

    @Override
    public void del(List<String> playerIdLis) {
        LogicCheckUtils.validateParam(List.class, playerIdLis);
        for (String playerId : playerIdLis) {
            if (!FriendsCtrlUtils.containsFriend(target, playerId)) { // 好友列表中没有该playerId，无法删除
                continue;
            }
            In_DelFriend.Request request = new In_DelFriend.Request(getPlayerCtrl().getPlayerId(), playerId);
            In_DelFriend.Response response = sendCheckPlayerOnlineMsgSync(playerId, request);
            if (response.getResultCode() == ResultCodeEnum.SUCCESS) {
                FriendsCtrlUtils.removeFriend(target, playerId);

                removeFriendCacheAfterDel(playerId);
            }
        }
        SenderFunc.sendInner(this, Sm_Friend.class, Sm_Friend.Builder.class, Action.RESP_DELETE, (b, br) -> {
            _fill_Sm_Common_SimplePlayer_Base(0, MagicNumbers.ONE_PAGE_FRIEND_NUM - 1, delFriendsCache_Sort, b);
            b.setFriendCount(friendsCount());
        });
        save();
    }

    @Override
    public void recommendFriends() {
        SenderFunc.sendInner(this, Sm_Friend.class, Sm_Friend.Builder.class, Action.RESP_ANOTHER, (b, br) -> {
            randomNotFriendAndFillBase(b);
        });
    }

    @Override
    public void giveEnergy(String targetPlayerId) {
        LogicCheckUtils.validateParam(String.class, targetPlayerId);
        if (!FriendsCtrlUtils.containsFriend(target, targetPlayerId)) { // 好友列表中没有该playerId，无法赠送体力
            String msg = String.format("好友列表中没有该toPlayerId=%s，无法赠送体力！", targetPlayerId);
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.FRIENDS_WITHOUT_THE_FRIEND);
        }
        Friend friend = _giveEnergy(targetPlayerId);
        SenderFunc.sendInner(this, Sm_Friend.class, Sm_Friend.Builder.class, Action.RESP_GIVE, (b, br) -> {
            Sm_Common_SimplePlayer_Base base = ProtoUtils.create_Sm_Common_SimplePlayer_Base(friendsCache.get(friend.getPlayerId()));
            b.addPlayer(FriendsCtrlProtos.create_Sm_Friend_Player(base, friend));
        });
        save();
    }

    @Override
    public void oneKeyGiveEnergy() {
        target.getIdToFriend().forEach((targetPlayerId, friend) -> {
            if (!friend.isGive()) { // 没有赠送过体力给该好友
                _giveEnergy(targetPlayerId);
            }
        });
        SenderFunc.sendInner(this, Sm_Friend.class, Sm_Friend.Builder.class, Action.RESP_ONE_KEY_GIVE, (b, br) -> {
        });
        save();
    }


    private Friend _giveEnergy(String targetPlayerId) {
        In_GiveFriendEnergy.Request request = new In_GiveFriendEnergy.Request(getPlayerCtrl().getPlayerId(), targetPlayerId);
        sendCheckPlayerOnlineMsg(targetPlayerId, request);
        Friend friend = FriendsCtrlUtils.getFriend(target, targetPlayerId);
        friend.setGive(true);
        return friend;
    }

    @Override
    public void getEnergy(String targetPlayerId) {
        LogicCheckUtils.validateParam(String.class, targetPlayerId);
        if (!FriendsCtrlUtils.containsFriend(target, targetPlayerId)) { // 好友列表中没有该playerId，无法领取体力
            String msg = String.format("好友列表中没有该playerId=%s，无法领取体力！", targetPlayerId);
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.FRIENDS_WITHOUT_THE_FRIEND);
        }
        Friend friend = FriendsCtrlUtils.getFriend(target, targetPlayerId);
        if (!FriendsCtrlUtils.canGetEnergy(friend)) {
            String msg = String.format("不能领取体力！ beGive={} get={}", friend.isBeGive(), friend.isGet());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int maxGetTimes = AllServerConfig.Friends_GetEnergy_DaliyTimes.getConfig();
        if (target.getGetEnergyTimes() >= maxGetTimes) {
            String msg = String.format("已经达到每日领取体力次数的上限=%s，无法领取！", maxGetTimes);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdMaptoCount toAdd = new IdMaptoCount();
        toAdd.add(_getEnergy(friend));
        int remainGetTimes = maxGetTimes - target.getGetEnergyTimes();
        LogicCheckUtils.canAdd(itemIoCtrl, toAdd);
        IdMaptoCount fresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_GET_ENERGY).addItem(toAdd);
        SenderFunc.sendInner(this, Sm_Friend.class, Sm_Friend.Builder.class, Action.RESP_GET_ENERGY, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(fresh_1, br);
            b.setRemainGetTimes(remainGetTimes);
            Sm_Common_SimplePlayer_Base base = ProtoUtils.create_Sm_Common_SimplePlayer_Base(friendsCache.get(friend.getPlayerId()));
            b.addPlayer(FriendsCtrlProtos.create_Sm_Friend_Player(base, friend));
        });
        save();
    }

    @Override
    public void oneKeyGetEnergy() {
        int hasGetTimes = target.getGetEnergyTimes();
        int maxGetTimes = AllServerConfig.Friends_GetEnergy_DaliyTimes.getConfig();
        IdMaptoCount toAdd = new IdMaptoCount();
        if (hasGetTimes < maxGetTimes) {
            for (Entry<String, Friend> entry : target.getIdToFriend().entrySet()) {
                Friend friend = entry.getValue();
                if (FriendsCtrlUtils.canGetEnergy(friend)) {
                    if (hasGetTimes >= maxGetTimes) {
                        break;
                    }
                    hasGetTimes++;
                    toAdd.add(_getEnergy(friend));
                }
            }
        }
        int remainGetTimes = maxGetTimes - target.getGetEnergyTimes();
        LogicCheckUtils.canAdd(itemIoCtrl, toAdd);
        IdMaptoCount fresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_ONE_KEY_GET).addItem(toAdd);
        SenderFunc.sendInner(this, Sm_Friend.class, Sm_Friend.Builder.class, Action.RESP_ONE_KEY_GET, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(fresh_1, br);
            b.setRemainGetTimes(remainGetTimes);
        });
        save();
    }


    private IdAndCount _getEnergy(Friend friend) {
        friend.setGet(true);
        target.setGetEnergyTimes(target.getGetEnergyTimes() + 1);
        return new IdAndCount(ResourceTypeEnum.RES_ENERGY_VALUE, MagicNumbers.ONE_TIME_GIVE_ENERGY_NUM);
    }

    @Override
    public void search(String condition) {
        LogicCheckUtils.validateParam(String.class, condition);
        int simpleId;
        try {
            simpleId = Integer.valueOf(condition);
        } catch (Exception e) {
            simpleId = 0;
        }
        SimplePlayer simplePlayer;
        if (simpleId == 0) {
            simplePlayer = getSimplePlayerDao().findByPlayerName(condition);
        } else {
            simplePlayer = getSimplePlayerDao().findBySimpleId(simpleId);
        }
        sendSearchResult(simplePlayer);
    }

    private void sendSearchResult(SimplePlayer simplePlayer) {
        SenderFunc.sendInner(this, Sm_Friend.class, Sm_Friend.Builder.class, Action.RESP_SEARCH, (b, br) -> {
            if (simplePlayer == null) {
                br.setResult(false);
                br.setErrorCode(ErrorCodeEnum.FRIENDS_NOT_EXIST_SEARCH_PLAYER);
            } else {
                b.addRecommendPlayers(ProtoUtils.create_Sm_Common_SimplePlayer_Base(simplePlayer));
            }
        });
    }


    @Override
    public void queryApplyLis(int start, int end) {
        SenderFunc.sendInner(this, Sm_Friend.class, Sm_Friend.Builder.class, Action.RESP_GET_APPLY, (b, br) -> {
            int num = 0;
            int startNew = Math.min(0, start);
            int endNew = Math.min(applyCache.size() - 1, end);
            for (Entry<String, SimplePlayer> entry : applyCache.entrySet()) {
                if (num >= startNew && num <= endNew) {
                    Sm_Common_SimplePlayer_Base base = ProtoUtils.create_Sm_Common_SimplePlayer_Base(entry.getValue());
                    b.addPlayer(FriendsCtrlProtos.create_Sm_Friend_Player(base, num));
                    num++;
                }
            }
            b.setApplyCount(applyListCount());
        });
    }


    @Override
    public void onAgreeApply(In_AgreeApply.Request request) {
        // 玩家在线时未实时同步增加的好友给客户端，等待下次Sync
        FriendsOnlineOfflineUtils.onAgreeApply(target, request);
        save();
        getPlayerCtrl().getCurSendActorRef().tell(new In_AgreeApply.Response(request), ActorRef.noSender());
    }

    @Override
    public void onApplyFriend(In_ApplyFriend.Request request) {
        // 玩家在线时未实时同步增加的申请给客户端，等待下次Sync
        FriendsOnlineOfflineUtils.onApplyFriend(target, request);
        save();
        // 不用回消息
    }

    @Override
    public void onDelFriend(In_DelFriend.Request request) {
        String operateDelPlayerId = request.getOperateDelPlayerId();   // 操作删除行为的玩家
        removeFriendCacheAfterDel(operateDelPlayerId);
        FriendsOnlineOfflineUtils.onDelFriend(target, request);
        save();
        getPlayerCtrl().getCurSendActorRef().tell(new In_DelFriend.Response(request), ActorRef.noSender());
    }

    @Override
    public void onGiveFriendEnergy(In_GiveFriendEnergy.Request request) {
        FriendsOnlineOfflineUtils.onGiveFriendEnergy(target, request);
        save();
        // 不用回消息
    }


    /**
     * 好友數量
     *
     * @return
     */
    private int friendsCount() {
        return target.getIdToFriend().size();
    }


    /**
     * 申請列表數量
     *
     * @return
     */
    private int applyListCount() {
        return applyCache.size();
    }


    /**
     * 重置领取体力状态
     */
    private void resetGetEnergyStatus() {
        target.getIdToFriend().forEach((id, friend) -> {
            friend.setBeGive(false);
            friend.setGet(false);
            friend.setGive(false);
        });
    }

    private void fillApplyCache() {
        if (applyCache.size() <= 0) {
            applyCache = SimplePojoUtils.querySimplePlayerLisByIds(target.getApplyLis(), getPlayerCtrl().getOuterRealmId());
            RelationshipCommonUtils.removeNullElements(applyCache);
        }
    }

    private void fillFriendsCache() {
        if (friendsCache.size() <= 0) {
            friendsCache = SimplePojoUtils.querySimplePlayerLisByIds(_getFriendIdLis(), getPlayerCtrl().getOuterRealmId());
            RelationshipCommonUtils.removeNullElements(friendsCache);
        }
    }

    private void fillNotFriendsCache(List<RedisRankAndScore> rankAndScoreList) {
        if (notFriendPlayerIdsCache.size() <= 0) {
            notFriendPlayerIdsCache = SimplePojoUtils.querySimplePlayerLisByIds(_getNotFriendPlayerIds_FromRankAndScoreList(rankAndScoreList), getPlayerCtrl().getOuterRealmId());
            RelationshipCommonUtils.removeNullElements(notFriendPlayerIdsCache);
        }
    }


    private void sortApplyCache() {
        applyCache = FriendsCtrlUtils.sortApplyCache(applyCache);
    }

    private void sortFriendsCache() {
        friendsCache_Sort = FriendsCtrlUtils.sortFriendsCache(friendsCache, 1);
    }

    private void sortDelFriendsCache() {
        delFriendsCache_Sort = FriendsCtrlUtils.sortFriendsCache(friendsCache, -1);
    }

    private void sortNotFriendsCache() {
        notFriendPlayerIdsCache = FriendsCtrlUtils.sortRecommendCache(notFriendPlayerIdsCache);
    }


    private List<String> _getFriendIdLis() {
        Set<String> set = target.getIdToFriend().keySet();
        return new ArrayList<>(set);
    }


    private List<String> _getNotFriendPlayerIds_FromRankAndScoreList(List<RedisRankAndScore> rankAndScoreList) {
        List<String> playerIds = new ArrayList<>();
        for (RedisRankAndScore rs : rankAndScoreList) {
            if (FriendsCtrlUtils.containsFriend(target, rs.getMember())) {
                continue;
            }
            playerIds.add(rs.getMember());
        }
        return playerIds;
    }

    public void randomNotFriendAndFillBase(Sm_Friend.Builder b) {
        if (notFriendPlayerIdsCache.size() <= 0) {
            return;
        }
        Set<Integer> idxLis = FriendsCtrlUtils.randomIdxs(notFriendPlayerIdsCache.size());
        int num = 0;
        int add = 0;
        for (Entry<String, SimplePlayer> entry : notFriendPlayerIdsCache.entrySet()) {
            if (idxLis.contains(num) &&
                    entry.getValue() != null &&
                    !isSelfId(entry.getKey()) && // 不是自己
                    !FriendsCtrlUtils.containsFriend(target, entry.getKey()) && // 不是好友
                    add < MagicNumbers.RANDOM_RECOMMEND_FRIEND_NUM) {
                Sm_Common_SimplePlayer_Base base = ProtoUtils.create_Sm_Common_SimplePlayer_Base(entry.getValue());
                b.addRecommendPlayers(base);
                add++;
            }
            num++;
        }
    }

    private <I extends InnerMsg> void sendCheckPlayerOnlineMsg(String toPlayerId, I i) {
        CheckPlayerOnlineMsgRequest<I> checkRequest = new CheckPlayerOnlineMsgRequest<>(toPlayerId, i);
        ClusterMessageSender.sendMsgToPath(getPlayerCtrl().getContext(), checkRequest, ActorSystemPath.WS_GameServer_Selection_World);
    }


    private <I extends InnerMsg, O> O sendCheckPlayerOnlineMsgSync(String toPlayerId, I i) {
        ActorSelection actorSelectio = getPlayerCtrl().getContext().actorSelection(ActorSystemPath.WS_GameServer_Selection_World);
        CheckPlayerOnlineMsgRequest<I> checkRequest = new CheckPlayerOnlineMsgRequest<>(toPlayerId, i);
        return ActorMsgSynchronizedExecutor.sendMsgToServer(actorSelectio, checkRequest);
    }

    public SimplePlayerDao getSimplePlayerDao() {
        simplePlayerDao.init(mongoDBClient, MagicWords_Redis.TopLevelPojo_Game_Prefix + getPlayerCtrl().getOuterRealmId());
        return simplePlayerDao;
    }


    private void clearAllCache() {
        rankAndScoreList.clear();
        friendsCache.clear();
        friendsCache_Sort.clear();
        delFriendsCache_Sort.clear();
        applyCache.clear();
        notFriendPlayerIdsCache.clear();
    }

    private void initCacheData() {
        rankAndScoreList = RedisRankUtils.getRankRangeWithScores(getPlayerCtrl().getOuterRealmId(), CommonRankTypeEnum.RK_PLAYERLOGINTIME, 1, MAX_RECOMMEND_NUM);
        removeHasBeFriend();
        fillNotFriendsCache(rankAndScoreList);
        fillFriendsCache();
        fillApplyCache();

        sortDelFriendsCache();
        sortFriendsCache();
        sortApplyCache();
        sortNotFriendsCache();
    }


    /**
     * 移除已经是好友关系的
     */
    private void removeHasBeFriend() {
        Iterator<RedisRankAndScore> iterator = rankAndScoreList.iterator();
        if (iterator.hasNext()) {
            RedisRankAndScore score = iterator.next();
            if (isSelfId(score.getMember())) { // 自己
                iterator.remove();
            } else if (FriendsCtrlUtils.containsFriend(target, score.getMember())) { // 已经是好友
                iterator.remove();
            }
        }
    }


    /**
     * 删除被缓存的好友Id
     *
     * @param friendPlayerId
     */
    private void removeFriendCacheAfterDel(String friendPlayerId) {
        friendsCache.remove(friendPlayerId);
        friendsCache_Sort.remove(friendPlayerId);
        delFriendsCache_Sort.remove(friendPlayerId);
    }

    private boolean isSelfId(String playerId) {
        return getPlayerCtrl().getPlayerId().equals(playerId);
    }
}
