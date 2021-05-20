package ws.relationship.utils;

import akka.actor.ActorContext;
import ws.relationship.base.ServerRoleEnum;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.chat.AddChatMsg;
import ws.relationship.chatServer.GroupChatMsg;

/**
 * Created by lee on 17-5-12.
 */
public class BroadcastSystemMsgUtils {

    public static void broad(ActorContext actorContext, int innerRealmId, GroupChatMsg groupChatMsg) {
        AddChatMsg.Request addChatMsg = new AddChatMsg.Request(innerRealmId, groupChatMsg);
        ClusterMessageSender.sendMsgToServer(actorContext, ServerRoleEnum.WS_ChatServer, addChatMsg, ActorSystemPath.WS_ChatServer_Selection_ChatManager);
    }
}
