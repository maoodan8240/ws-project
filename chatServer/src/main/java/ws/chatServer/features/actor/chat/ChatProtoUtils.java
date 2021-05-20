package ws.chatServer.features.actor.chat;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.protos.ChatProtos.Sm_Chat;
import ws.protos.ChatProtos.Sm_Chat_BaseMsg;
import ws.protos.ChatProtos.Sm_Chat_GroupMsg;
import ws.protos.ChatProtos.Sm_Chat_PlayerInfo;
import ws.protos.ChatProtos.Sm_Chat_PrivateMsg;
import ws.protos.ChatProtos.Sm_Chat_PrivateMsgRelation;
import ws.protos.ChatProtos.Sm_Chat_SystemMsg;
import ws.relationship.chatServer.ChatMsg;
import ws.relationship.chatServer.GroupChatMsg;
import ws.relationship.chatServer.PrivateChatMsg;
import ws.relationship.chatServer.SystemChatMsg;
import ws.relationship.topLevelPojos.simpleGuild.SimpleGuild;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.RelationshipCommonUtils;
import ws.relationship.utils.SimplePojoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lee on 17-5-5.
 */
public class ChatProtoUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatProtoUtils.class);

    public static void fill_Sm_Chat(Sm_Chat.Builder b, List<ChatMsg> chatMsgList, Map<String, SimplePlayer> playerIdToSimplePlayer) {
        Map<Long, List<PrivateChatMsg>> relationIdToPrivateChatMsgLis = new HashMap<>();
        for (ChatMsg chatMsg : chatMsgList) {
            if (chatMsg instanceof GroupChatMsg) {
                b.addGroupMsgLis(create_Sm_Chat_GroupMsg((GroupChatMsg) chatMsg, playerIdToSimplePlayer));
            } else if (chatMsg instanceof PrivateChatMsg) {
                PrivateChatMsg privateChatMsg = (PrivateChatMsg) chatMsg;
                List<PrivateChatMsg> privateChatMsgList = ChatUtils.getListByKey(relationIdToPrivateChatMsgLis, privateChatMsg.getRelationId());
                privateChatMsgList.add(privateChatMsg);
            }
        }
        b.addAllPrivateMsgRelationLis(create_Sm_Chat_PrivateMsgRelationLis(relationIdToPrivateChatMsgLis, playerIdToSimplePlayer));
        relationIdToPrivateChatMsgLis.clear();
    }


    public static Sm_Chat_GroupMsg create_Sm_Chat_GroupMsg(GroupChatMsg groupChatMsg, Map<String, SimplePlayer> playerIdToSimplePlayer) {
        Sm_Chat_GroupMsg.Builder b = Sm_Chat_GroupMsg.newBuilder();
        b.setBaseInfo(create_Sm_Chat_BaseMsg(groupChatMsg));
        b.setGroupId(RelationshipCommonUtils.converNullToEmpty(groupChatMsg.getGroupId()));
        if (groupChatMsg.getSystemChatMsg() != null) {
            b.setSystemMsg(create_Sm_Chat_SystemMsg(groupChatMsg.getSystemChatMsg()));
        } else {
            SimplePlayer sender = playerIdToSimplePlayer.get(groupChatMsg.getSenderPlayerId());
            if (sender != null) {
                b.setSenderPlayer(create_Sm_Chat_PlayerInfo(sender));
            } else {
                LOGGER.debug("groupChatMsg sender 未查询到玩家={}的SimplePlayer", groupChatMsg.getSenderPlayerId());
            }
            b.setContent(groupChatMsg.getContent());
            b.setUseHorn(groupChatMsg.isUseHorn());
        }
        return b.build();
    }

    private static List<Sm_Chat_PrivateMsgRelation> create_Sm_Chat_PrivateMsgRelationLis(Map<Long, List<PrivateChatMsg>> relationIdToPrivateChatMsgLis, Map<String, SimplePlayer> playerIdToSimplePlayer) {
        List<Sm_Chat_PrivateMsgRelation> bs = new ArrayList<>();
        relationIdToPrivateChatMsgLis.forEach((relationId, lis) -> {
            if (lis.size() > 0) {
                bs.add(create_Sm_Chat_PrivateMsgRelation(lis, playerIdToSimplePlayer));
            }
        });
        return bs;
    }

    private static Sm_Chat_PrivateMsgRelation create_Sm_Chat_PrivateMsgRelation(List<PrivateChatMsg> privateChatMsgLis, Map<String, SimplePlayer> playerIdToSimplePlayer) {
        PrivateChatMsg privateChatMsg = privateChatMsgLis.get(0);
        Sm_Chat_PrivateMsgRelation.Builder b = Sm_Chat_PrivateMsgRelation.newBuilder();
        b.setRelationId(privateChatMsg.getRelationId());
        SimplePlayer sender = playerIdToSimplePlayer.get(privateChatMsg.getSenderPlayerId());
        if (sender != null) {
            b.setPa(create_Sm_Chat_PlayerInfo(sender));
        } else {
            LOGGER.debug("privateChatMsg sender 未查询到玩家={}的SimplePlayer", privateChatMsg.getSenderPlayerId());
        }
        SimplePlayer rev = playerIdToSimplePlayer.get(privateChatMsg.getRevPlayerId());
        if (rev != null) {
            b.setPb(create_Sm_Chat_PlayerInfo(rev));
        } else {
            LOGGER.debug("privateChatMsg rev 未查询到玩家={}的SimplePlayer", privateChatMsg.getRevPlayerId());
        }
        privateChatMsgLis.forEach((msg) -> {
            b.addPrivateMsgLis(create_Sm_Chat_PrivateMsg(msg, playerIdToSimplePlayer));
        });
        return b.build();
    }

    public static Sm_Chat_PrivateMsg create_Sm_Chat_PrivateMsg(PrivateChatMsg privateChatMsg, Map<String, SimplePlayer> playerIdToSimplePlayer) {
        Sm_Chat_PrivateMsg.Builder b = Sm_Chat_PrivateMsg.newBuilder();
        b.setBaseInfo(create_Sm_Chat_BaseMsg(privateChatMsg));
        b.setSenderPlayerId(RelationshipCommonUtils.converNullToEmpty(privateChatMsg.getSenderPlayerId()));
        if (privateChatMsg.getSystemChatMsg() != null) {
            b.setSystemMsg(create_Sm_Chat_SystemMsg(privateChatMsg.getSystemChatMsg()));
        } else {
            b.setContent(privateChatMsg.getContent());
        }
        return b.build();
    }

    public static Sm_Chat_BaseMsg create_Sm_Chat_BaseMsg(ChatMsg chatMsg) {
        Sm_Chat_BaseMsg.Builder b = Sm_Chat_BaseMsg.newBuilder();
        b.setChatId(chatMsg.getChatId());
        b.setType(chatMsg.getType());
        b.setSendTime(chatMsg.getTime());
        return b.build();
    }

    public static Sm_Chat_SystemMsg create_Sm_Chat_SystemMsg(SystemChatMsg chatMsg) {
        Sm_Chat_SystemMsg.Builder b = Sm_Chat_SystemMsg.newBuilder();
        b.setChatTpId(chatMsg.getChatTpId());
        b.addAllArgs(chatMsg.getArgs());
        return b.build();
    }

    public static Sm_Chat_PlayerInfo create_Sm_Chat_PlayerInfo(SimplePlayer simplePlayer) {
        Sm_Chat_PlayerInfo.Builder b = Sm_Chat_PlayerInfo.newBuilder();
        b.setPlayerId(simplePlayer.getPlayerId());
        b.setName(simplePlayer.getPlayerName());
        b.setLv(simplePlayer.getLv());
        b.setIconId(simplePlayer.getIcon());
        b.setSimplePlayerId(simplePlayer.getSimplePlayerId());
        b.setGuildId(RelationshipCommonUtils.converNullToEmpty(simplePlayer.getGuildId()));
        if (!StringUtils.isBlank(simplePlayer.getGuildId())) {
            SimpleGuild simpleGuild = SimplePojoUtils.querySimpleGuild(simplePlayer.getGuildId(), simplePlayer.getInnerRealmId());
            b.setGuildName(RelationshipCommonUtils.converNullToEmpty(simpleGuild.getGuildName()));
        }
        return b.build();
    }
}
