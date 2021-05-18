package ws.chatServer.features.actor.chat;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.chatServer.features.actor.message.ConnectionContainer;
import ws.common.network.server.handler.tcp.MessageSendHolder;
import ws.protos.ChatProtos.Sm_Chat;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.msg.chat.NeedToSendMsgs;
import ws.relationship.chatServer.GroupChatMsg;
import ws.relationship.chatServer.PrivateChatMsg;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.ProtoUtils;
import ws.relationship.utils.SimplePojoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangweiwei on 17-5-4.
 */
public class ChatMsgSenderActor extends WsActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatMsgSenderActor.class);
    private int innerRealmId;
    private Map<String, ChatServerPlayer> playerIdToChatInfo;

    public ChatMsgSenderActor(int innerRealmId, Map<String, ChatServerPlayer> playerIdToChatInfo) {
        this.innerRealmId = innerRealmId;
        this.playerIdToChatInfo = playerIdToChatInfo;
    }

    @Override
    public void onRecv(Object msg) throws Exception {
        if (msg instanceof NeedToSendMsgs.Request) {
            onNeedToSendMsgs((NeedToSendMsgs.Request) msg);
        }
    }

    private void onNeedToSendMsgs(NeedToSendMsgs.Request request) {
        LOGGER.debug("开始同步消息了....接收着数量={} 发送消息的数量={}", request.getSendToPlayerIds().size(), request.getChatMsgList().size());
        Map<String, SimplePlayer> playerIdToSimplePlayer = getSenderSimplePlayer(getOuterRealmIdToPlayerIds(request));
        request.getSendToPlayerIds().forEach(playerId -> {
            if (ConnectionContainer.containsGameId_GTF(playerId)) {
                String flag = ConnectionContainer.getFlagByGameId_GTF(playerId);
                if (ConnectionContainer.containsFlag_FTC(flag)) {
                    Response.Builder br = ProtoUtils.create_Response(Code.Sm_Chat, request.getAction());
                    br.setResult(true);
                    Sm_Chat.Builder b = Sm_Chat.newBuilder();
                    b.setAction(request.getAction());

                    ChatProtoUtils.fill_Sm_Chat(b, request.getChatMsgList(), playerIdToSimplePlayer);
                    br.setSmChat(b);
                    MessageSendHolder messageSendHolder = new MessageSendHolder(br.build(), br.getSmMsgAction(), new ArrayList<>());
                    ConnectionContainer.getConnectionByFlag_FTC(flag).send(messageSendHolder);
                } else {
                    LOGGER.debug("给玩家playerId={} flag={} 发送消息, 但是玩家的 FLAG_TO_CONNECTION 关系不存在.", playerId, flag);
                }
            } else {
                LOGGER.debug("给玩家playerId={} 发送消息, 但是玩家的 GAMEID_TO_FLAG 关系不存在.", playerId);
            }
        });
    }


    private Map<Integer, List<String>> getOuterRealmIdToPlayerIds(NeedToSendMsgs.Request request) {
        Map<Integer, List<String>> outerRealmIdToPlayerIds = new HashMap<>();
        request.getChatMsgList().forEach(chatMsg -> {
            String senderPlayerId = null;
            String revPlayerId = null;
            if (chatMsg instanceof PrivateChatMsg) {
                PrivateChatMsg msg = (PrivateChatMsg) chatMsg;
                senderPlayerId = msg.getSenderPlayerId();
                revPlayerId = msg.getRevPlayerId();
            } else if (chatMsg instanceof GroupChatMsg) {
                GroupChatMsg msg = (GroupChatMsg) chatMsg;
                senderPlayerId = msg.getSenderPlayerId();
            }
            addPlayerId(outerRealmIdToPlayerIds, senderPlayerId);
            addPlayerId(outerRealmIdToPlayerIds, revPlayerId);
        });
        return outerRealmIdToPlayerIds;
    }

    private Map<String, SimplePlayer> getSenderSimplePlayer(Map<Integer, List<String>> outerRealmIdToPlayerIds) {
        Map<String, SimplePlayer> idToSimplePlayer = new HashMap<>();
        outerRealmIdToPlayerIds.forEach((outerRealmId, playerIds) -> {
            Map<String, SimplePlayer> tmp = SimplePojoUtils.querySimplePlayerLisByIds(playerIds, outerRealmId);
            idToSimplePlayer.putAll(tmp);
        });
        return idToSimplePlayer;
    }

    // todo 有bug，当时发送的人早就离线离。
    private void addPlayerId(Map<Integer, List<String>> outerRealmIdToPlayerIds, String playerId) {
        if (!StringUtils.isEmpty(playerId)) {
            ChatServerPlayer chatServerPlayer = playerIdToChatInfo.get(playerId);
            if (chatServerPlayer != null) {
                List<String> list = ChatUtils.getListByKey(outerRealmIdToPlayerIds, chatServerPlayer.getOuterRealmId());
                if (!list.contains(playerId)) {
                    list.add(playerId);
                }
            } else {
                LOGGER.debug("playerId={} 不在chatServer内存中.", playerId);
            }
        }
    }
}
