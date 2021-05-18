package ws.chatServer.features.actor.chat;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.chatServer.features.actor.message.ConnectionContainer;
import ws.relationship.base.PlayerRealmPair;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.In_MessagePassToOtherServer;
import ws.relationship.base.msg.chat.ChatServerInnerMsg;
import ws.relationship.chatServer.Cm_Chat_Msg_WithPlayerId;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangweiwei on 17-5-4.
 */
public class ChatManagerActor extends WsActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatManagerActor.class);
    private Map<Integer, ActorRef> innerRealmIdToChat = new HashMap<>();


    public ChatManagerActor() {
    }

    @Override

    public void onRecv(Object msg) throws Exception {
        if (msg instanceof In_MessagePassToOtherServer) {
            onMessagePassToOtherServer((In_MessagePassToOtherServer) msg);
        } else if (msg instanceof ChatServerInnerMsg) {
            onChatServerInnerMsg((ChatServerInnerMsg) msg);
        }
    }


    private void onChatServerInnerMsg(ChatServerInnerMsg msg) {
        tryAddInnerRealmChatActor(msg.getInnerRealmId());
        if (innerRealmIdToChat.containsKey(msg.getInnerRealmId())) {
            innerRealmIdToChat.get(msg.getInnerRealmId()).tell(msg, getSender());
        } else {
            LOGGER.debug("有消息发送到ChatManagerActor, InnerRealmId={}, 但是没有对应的InnerRealmChatActor!", msg.getInnerRealmId());
        }
    }

    private void onMessagePassToOtherServer(In_MessagePassToOtherServer messagePassToOtherServer) {
        String playerId = messagePassToOtherServer.getPlayerId();
        PlayerRealmPair realmPair = ConnectionContainer.getRealmPairByGameId(playerId);
        if (realmPair == null) {
            LOGGER.debug("playerId={} 发送消息，但是未查询到该玩家的登录信息!", playerId);
            return;
        }
        tryAddInnerRealmChatActor(realmPair.getInnerRealmId());
        if (innerRealmIdToChat.containsKey(realmPair.getInnerRealmId())) {
            Cm_Chat_Msg_WithPlayerId cmChatMsgWithPlayerId = new Cm_Chat_Msg_WithPlayerId(messagePassToOtherServer.getMessage(), playerId);
            innerRealmIdToChat.get(realmPair.getInnerRealmId()).tell(cmChatMsgWithPlayerId, ActorRef.noSender());
        }
    }

    private void tryAddInnerRealmChatActor(int innerRealmId) {
        if (!innerRealmIdToChat.containsKey(innerRealmId)) {
            String innerRealmChatActorName = ActorSystemPath.WS_ChatServer_InnerRealmChat + innerRealmId;
            ActorRef ref = getContext().actorOf(Props.create(InnerRealmChatActor.class, innerRealmId), innerRealmChatActorName);
            getContext().watch(ref);
            innerRealmIdToChat.put(innerRealmId, ref);
            LOGGER.debug("初始化服聊天innerRealmId:{} <--> Actor:{} ", innerRealmId, ref);
        }
    }
}
