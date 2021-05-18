package ws.chatServer.features.actor.chat;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.chatServer.features.actor.chat.ctrl.InnerRealmChatCtrl;
import ws.chatServer.features.actor.chat.ctrl._InnerRealmChatCtrl;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.chat.AddChatGroupMember;
import ws.relationship.base.msg.chat.AddChatMsg;
import ws.relationship.base.msg.chat.AddChatServerPlayer;
import ws.relationship.base.msg.chat.RemoveChatGroupMember;
import ws.relationship.base.msg.chat.RemoveChatServerPlayer;
import ws.relationship.base.msg.chat.RemoveOverTimePrivateMsg;
import ws.relationship.base.msg.chat.SendCacheMsgs;
import ws.relationship.chatServer.Cm_Chat_Msg_WithPlayerId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangweiwei on 17-5-4.
 */
public class InnerRealmChatActor extends WsActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(InnerRealmChatActor.class);
    private Map<String, ChatServerPlayer> playerIdToChatInfo = new ConcurrentHashMap<>();
    private static final int SENDER_COUNT = 5;
    private List<ActorRef> msgSenderLis = new ArrayList<>();
    private InnerRealmChatCtrl ctrl;
    private int innerRealmId;

    public InnerRealmChatActor(int innerRealmId) {
        this.innerRealmId = innerRealmId;
        _createSenderActors();
        ctrl = new _InnerRealmChatCtrl(self(), getContext(), innerRealmId, playerIdToChatInfo, msgSenderLis);
    }

    @Override
    public void onRecv(Object msg) throws Exception {
        if (msg instanceof Cm_Chat_Msg_WithPlayerId) {
            onCm_Chat_Msg_WithPlayerId((Cm_Chat_Msg_WithPlayerId) msg);
        }

        // 组
        else if (msg instanceof AddChatGroupMember.Request) {
            onAddGuildMember((AddChatGroupMember.Request) msg);
        } else if (msg instanceof RemoveChatGroupMember.Request) {
            onRemoveGuildMember((RemoveChatGroupMember.Request) msg);
        }

        // 增加聊天玩家
        else if (msg instanceof AddChatServerPlayer.Request) {
            onAddChatServerPlayer((AddChatServerPlayer.Request) msg);
        } else if (msg instanceof RemoveChatServerPlayer.Request) {
            onRemoveChatServerPlayer((RemoveChatServerPlayer.Request) msg);
        }
        // 发送缓存消息
        else if (msg instanceof SendCacheMsgs.Request) {
            onSendCacheMsgs((SendCacheMsgs.Request) msg);
        }
        // 移除过期私聊
        else if (msg instanceof RemoveOverTimePrivateMsg.Request) {
            onRemoveOverTimePrivateMsg((RemoveOverTimePrivateMsg.Request) msg);
        }


        // 其他聊天消息
        else if (msg instanceof AddChatMsg.Request) {
            onAddChatMsg((AddChatMsg.Request) msg);
        }
    }

    private void onRemoveOverTimePrivateMsg(RemoveOverTimePrivateMsg.Request request) {
        ctrl.onRemoveOverTimePrivateMsg();
    }

    private void onCm_Chat_Msg_WithPlayerId(Cm_Chat_Msg_WithPlayerId msg) {
        ctrl.onCm_Chat(msg);
    }

    private void onAddChatServerPlayer(AddChatServerPlayer.Request request) {
        ctrl.onAddChatServerPlayer(request.getGameId(), request.getInnerRealmId(), request.getOuterRealmId());
    }


    private void onRemoveChatServerPlayer(RemoveChatServerPlayer.Request request) {
        ctrl.onRemoveChatServerPlayer(request.getPlayerId(), request.getInnerRealmId());
    }


    private void onAddChatMsg(AddChatMsg.Request request) {
        ctrl.onAddChatMsg(request);
    }


    private void onAddGuildMember(AddChatGroupMember.Request request) {
        ctrl.onAddGroupMember(request);

    }

    private void onRemoveGuildMember(RemoveChatGroupMember.Request request) {
        ctrl.onRemoveGroupMember(request);
    }


    private void onSendCacheMsgs(SendCacheMsgs.Request request) {
        ctrl.onSendCacheMsgs(request);
    }

    private void _createSenderActors() {
        for (int i = 0; i < SENDER_COUNT; i++) {
            String actorName = ActorSystemPath.WS_ChatServer_ChatMsgSender + i;
            ActorRef ref = getContext().actorOf(Props.create(ChatMsgSenderActor.class, innerRealmId, playerIdToChatInfo), actorName);
            getContext().watch(ref);
            msgSenderLis.add(ref);
        }
    }
}
