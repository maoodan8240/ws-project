package ws.gameServer.features.standalone.extp.friends.utils;

import akka.actor.ActorRef;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.message.interfaces.ResultCode;
import ws.gameServer.features.standalone.extp.friends.enums.FriendsResultCodeEnum;
import ws.gameServer.features.standalone.extp.friends.msg.In_AgreeApply;
import ws.gameServer.features.standalone.extp.friends.msg.In_ApplyFriend;
import ws.gameServer.features.standalone.extp.friends.msg.In_DelFriend;
import ws.gameServer.features.standalone.extp.friends.msg.In_GiveFriendEnergy;
import ws.relationship.base.MagicNumbers;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.base.resultCode.ResultCodeEnum;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.centerPlayer.CenterPlayerDao;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;
import ws.relationship.topLevelPojos.friends.Friend;
import ws.relationship.topLevelPojos.friends.Friends;
import ws.relationship.utils.DBUtils;

/**
 * Created by zhangweiwei on 17-5-12.
 */
public class FriendsOnlineOfflineUtils {
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final CenterPlayerDao CENTER_PLAYER_DAO = DaoContainer.getDao(CenterPlayer.class);

    static {
        CENTER_PLAYER_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
    }


    public static void onAgreeApply(CenterPlayer centerPlayer, In_AgreeApply.Request request, ActorRef oriSender) {
        Friends friends = _getFriendsFromDb(centerPlayer);
        boolean rs = onAgreeApply(friends, request);
        _saveFriendsToDb(centerPlayer, friends);
        ResultCode code = rs ? ResultCodeEnum.SUCCESS : FriendsResultCodeEnum.REACH_MAX_FRIENDS_NUM;
        oriSender.tell(new In_AgreeApply.Response(request, code), ActorRef.noSender());
    }

    public static void onApplyFriend(CenterPlayer centerPlayer, In_ApplyFriend.Request request, ActorRef oriSender) {
        Friends friends = _getFriendsFromDb(centerPlayer);
        onApplyFriend(friends, request);
        _saveFriendsToDb(centerPlayer, friends);
        oriSender.tell(new In_ApplyFriend.Response(request), ActorRef.noSender());
    }

    public static void onDelFriend(CenterPlayer centerPlayer, In_DelFriend.Request request, ActorRef oriSender) {
        Friends friends = _getFriendsFromDb(centerPlayer);
        onDelFriend(friends, request);
        _saveFriendsToDb(centerPlayer, friends);
        oriSender.tell(new In_DelFriend.Response(request), ActorRef.noSender());
    }

    public static void onGiveFriendEnergy(CenterPlayer centerPlayer, In_GiveFriendEnergy.Request request, ActorRef oriSender) {
        Friends friends = _getFriendsFromDb(centerPlayer);
        onGiveFriendEnergy(friends, request);
        _saveFriendsToDb(centerPlayer, friends);
        oriSender.tell(new In_GiveFriendEnergy.Response(request), ActorRef.noSender());
    }

    private static Friends _getFriendsFromDb(CenterPlayer centerPlayer) {
        return DBUtils.getHashPojo(centerPlayer.getGameId(), centerPlayer.getOuterRealmId(), Friends.class);
    }

    private static void _saveFriendsToDb(CenterPlayer centerPlayer, Friends friends) {
        DBUtils.saveHashPojo(centerPlayer.getOuterRealmId(), friends);
    }

    //================================================================================================

    /**
     * 对面的玩家同意了申请
     *
     * @param friends
     * @param request
     */
    public static boolean onAgreeApply(Friends friends, In_AgreeApply.Request request) {
        if (FriendsCtrlUtils.curFriendsSize(friends) >= MagicNumbers.MAX_FRIENDS_NUM) {
            return false;
        }
        String playerId = request.getOperateAgreerPlayerId();
        FriendsCtrlUtils.putNewFriend(friends, playerId); // 就算已经有这个好友，也会再次被覆盖
        FriendsCtrlUtils.removeApply(friends, playerId);
        return true;

    }


    /**
     * 接受到了别的玩家申请
     *
     * @param friends
     * @param request
     */
    public static void onApplyFriend(Friends friends, In_ApplyFriend.Request request) {
        FriendsCtrlUtils.addApply(friends, request.getApplyerPlayerId());
    }


    /**
     * 对面的玩家删除了好友关系
     *
     * @param friends
     * @param request
     */
    public static void onDelFriend(Friends friends, In_DelFriend.Request request) {
        String operateDelPlayerId = request.getOperateDelPlayerId();   // 操作删除行为的玩家
        FriendsCtrlUtils.removeFriend(friends, operateDelPlayerId);
        FriendsCtrlUtils.removeApply(friends, operateDelPlayerId);
    }


    /**
     * 对面的好友赠了体力
     *
     * @param friends
     * @param request
     */
    public static void onGiveFriendEnergy(Friends friends, In_GiveFriendEnergy.Request request) {
        if (FriendsCtrlUtils.containsFriend(friends, request.getGiverPlayerId())) {
            Friend friend = FriendsCtrlUtils.getFriend(friends, request.getGiverPlayerId());
            friend.setBeGive(true);
        }
    }
}
