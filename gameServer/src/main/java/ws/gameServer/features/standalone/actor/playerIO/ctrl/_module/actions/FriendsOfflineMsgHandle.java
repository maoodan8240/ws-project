package ws.gameServer.features.standalone.actor.playerIO.ctrl._module.actions;

import akka.actor.ActorRef;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.gameServer.features.standalone.actor.playerIO.ctrl._module.Action;
import ws.gameServer.features.standalone.extp.friends.msg.In_AgreeApply;
import ws.gameServer.features.standalone.extp.friends.msg.In_ApplyFriend;
import ws.gameServer.features.standalone.extp.friends.msg.In_DelFriend;
import ws.gameServer.features.standalone.extp.friends.msg.In_GiveFriendEnergy;
import ws.gameServer.features.standalone.extp.friends.utils.FriendsOnlineOfflineUtils;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.centerPlayer.CenterPlayerDao;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;

/**
 * Created by lee on 16-12-16.
 */
public class FriendsOfflineMsgHandle implements Action {
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final CenterPlayerDao CENTER_PLAYER_DAO = DaoContainer.getDao(CenterPlayer.class);

    static {
        CENTER_PLAYER_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
    }


    @Override
    public void handleOfflineMsg(String selfPlayerId, Object msg, ActorRef oriSender) {
        if (msg instanceof In_AgreeApply.Request) {
            onAgreeApplyRequst(selfPlayerId, (In_AgreeApply.Request) msg, oriSender);
        } else if (msg instanceof In_ApplyFriend.Request) {
            onApplyFriendRequest(selfPlayerId, (In_ApplyFriend.Request) msg, oriSender);
        } else if (msg instanceof In_DelFriend.Request) {
            onDelFriendRequest(selfPlayerId, (In_DelFriend.Request) msg, oriSender);
        } else if (msg instanceof In_GiveFriendEnergy.Request) {
            onGiveFriendEnergyRequest(selfPlayerId, (In_GiveFriendEnergy.Request) msg, oriSender);
        }
    }

    private void onGiveFriendEnergyRequest(String selfPlayerId, In_GiveFriendEnergy.Request request, ActorRef oriSender) {
        FriendsOnlineOfflineUtils.onGiveFriendEnergy(getCenterPlayer(selfPlayerId), request, oriSender);
    }

    private void onDelFriendRequest(String selfPlayerId, In_DelFriend.Request request, ActorRef oriSender) {
        FriendsOnlineOfflineUtils.onDelFriend(getCenterPlayer(selfPlayerId), request, oriSender);
    }

    private void onApplyFriendRequest(String selfPlayerId, In_ApplyFriend.Request request, ActorRef oriSender) {
        FriendsOnlineOfflineUtils.onApplyFriend(getCenterPlayer(selfPlayerId), request, oriSender);
    }

    private void onAgreeApplyRequst(String selfPlayerId, In_AgreeApply.Request request, ActorRef oriSender) {
        FriendsOnlineOfflineUtils.onAgreeApply(getCenterPlayer(selfPlayerId), request, oriSender);
    }

    private static CenterPlayer getCenterPlayer(String selfPlayerId) {
        return CENTER_PLAYER_DAO.findCenterPlayer(selfPlayerId);
    }
}
