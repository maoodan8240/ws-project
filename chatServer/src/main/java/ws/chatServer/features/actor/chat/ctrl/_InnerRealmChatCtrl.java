package ws.chatServer.features.actor.chat.ctrl;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.chatServer.features.actor.chat.ChatIdManager;
import ws.chatServer.features.actor.chat.ChatServerPlayer;
import ws.chatServer.features.actor.chat.ChatUtils;
import ws.chatServer.features.actor.chat.PrivateChatRelation;
import ws.common.mongoDB.handler.PojoHandler;
import ws.common.utils.mc.controler.AbstractControler;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.protos.ChatProtos.Cm_Chat;
import ws.protos.ChatProtos.Cm_Chat.Action;
import ws.protos.ChatProtos.Sm_Chat;
import ws.protos.EnumsProtos.ChatTypeEnum;
import ws.relationship.base.msg.chat.AddChatGroupMember;
import ws.relationship.base.msg.chat.AddChatMsg;
import ws.relationship.base.msg.chat.NeedToSendMsgs;
import ws.relationship.base.msg.chat.RemoveChatGroupMember;
import ws.relationship.base.msg.chat.RemoveOverTimePrivateMsg;
import ws.relationship.base.msg.chat.SendCacheMsgs;
import ws.relationship.base.msg.chat.SendCacheMsgs.Request;
import ws.relationship.chatServer.ChatGroupInfo;
import ws.relationship.chatServer.ChatMsg;
import ws.relationship.chatServer.Cm_Chat_Msg_WithPlayerId;
import ws.relationship.chatServer.GroupChatMsg;
import ws.relationship.chatServer.PrivateChatMsg;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.RandomUtils;
import ws.relationship.utils.SimplePojoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lee on 17-5-4.
 */
public class _InnerRealmChatCtrl extends AbstractControler<PojoHandler> implements InnerRealmChatCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_InnerRealmChatCtrl.class);
    private static final int DEFALUT_INITIAL_CAPACITY = 100;
    private static final long delay = 2000;                           // 延迟2秒
    private static final long period = 1000;                          // 每1秒钟一个周期
    private static final long period2 = DateUtils.MILLIS_PER_MINUTE;  // 每1分钟一个周期
    private Timer timer = new Timer();
    private ActorRef self;
    private ActorContext context;
    private int innerRealmId;
    private Map<String, ChatServerPlayer> playerIdToChatPlayer; // 被共享，只读
    private List<ActorRef> msgSenderLis;


    // <组的类型 -- <组的id -- 组中的玩家Id集合>>
    private Map<ChatTypeEnum, Map<String, List<String>>> chatTypeToGroupIdToPlayerIds = new HashMap<>(DEFALUT_INITIAL_CAPACITY);
    // <组的类型 -- <玩家id -- 玩家组的Id>>
    private Map<ChatTypeEnum, Map<String, String>> chatTypeToPlayerIdToGroupId = new HashMap<>(DEFALUT_INITIAL_CAPACITY);

    // 所有消息
    private Map<ChatTypeEnum, Map<String, List<ChatMsg>>> typeToGroupIdToMsgLis = new HashMap<>(DEFALUT_INITIAL_CAPACITY); //
    // 缓存未发送的消息
    private Map<ChatTypeEnum, Map<String, List<ChatMsg>>> typeToGroupIdToMsgLis_CacheToSend = new HashMap<>(DEFALUT_INITIAL_CAPACITY); //

    // for 私聊
    private Map<String, Map<Long, Byte>> playerIdToRelationIds = new HashMap<>();
    private Map<Long, PrivateChatRelation> relationIdToRelation = new HashMap<>();

    public _InnerRealmChatCtrl(ActorRef self, ActorContext context, int innerRealmId, Map<String, ChatServerPlayer> playerIdToChatPlayer, List<ActorRef> msgSenderLis) {
        this.self = self;
        this.context = context;
        this.innerRealmId = innerRealmId;
        this.playerIdToChatPlayer = playerIdToChatPlayer;
        this.msgSenderLis = msgSenderLis;
        timer.schedule(new MsgSendTask(), delay, period);
        timer.schedule(new RemovePrivateMsgTask(), delay, period2);
    }

    @Override
    public void onAddGroupMember(AddChatGroupMember.Request request) {
        _addPlayerToNewGroup(request.getType(), request.getGuildId(), request.getPlayerId());
    }


    @Override
    public void onRemoveGroupMember(RemoveChatGroupMember.Request request) {
        ChatTypeEnum type = request.getType();
        Map<String, List<String>> guildIdToPlayerIds = ChatUtils.getMapByKey(chatTypeToGroupIdToPlayerIds, type);
        ChatUtils.getListByKey(guildIdToPlayerIds, request.getGuildId()).remove(request.getPlayerId());
        Map<String, String> playerIdToGuildId = ChatUtils.getMapByKey(chatTypeToPlayerIdToGroupId, type);
        playerIdToGuildId.remove(request.getPlayerId());
    }


    @Override
    public void onAddChatMsg(AddChatMsg.Request request) {
        ChatMsg chatMsg = request.getChatMsg().clone();
        chatMsg.setChatId(ChatIdManager.getChatId());
        if (chatMsg.getType() == ChatTypeEnum.CHAT_PRIVATE) {  // 私人聊天
            handlePrivateMsg((PrivateChatMsg) chatMsg);
        } else {
            GroupChatMsg groupChatMsg = (GroupChatMsg) chatMsg;
            String groupId = getGroupIdByGroupId(groupChatMsg.getType(), groupChatMsg.getGroupId());
            Map<String, List<ChatMsg>> groupIdToMsgList = ChatUtils.getMapByKey(typeToGroupIdToMsgLis, chatMsg.getType());
            List<ChatMsg> msgList = ChatUtils.getListByKey(groupIdToMsgList, groupId);
            GroupChatMsg msgTmp = (GroupChatMsg) ChatUtils.removeOverflowMsg(msgList);
            if (msgTmp != null) {
                tryToRemoveChatServerPlayer(msgTmp.getSenderPlayerId());
            }
            msgList.add(chatMsg.clone());
            // 缓存发送
            Map<String, List<ChatMsg>> groupIdToMsgList_CacheToSend = ChatUtils.getMapByKey(typeToGroupIdToMsgLis_CacheToSend, chatMsg.getType());
            List<ChatMsg> msgList_CacheToSend = ChatUtils.getListByKey(groupIdToMsgList_CacheToSend, groupId);
            msgList_CacheToSend.add(chatMsg.clone());
            if (!StringUtils.isBlank(groupChatMsg.getSenderPlayerId()) && playerIdToChatPlayer.containsKey(groupChatMsg.getSenderPlayerId())) { // 非系统发送的
                playerIdToChatPlayer.get(groupChatMsg.getSenderPlayerId()).addSendMsgCount();
            }
        }
    }

    @Override
    public void onAddChatServerPlayer(String playerId, int innerRealmId, int outerRealmId) {
        SimplePlayer simplePlayer = SimplePojoUtils.querySimplePlayerById(playerId, outerRealmId);
        if (simplePlayer != null) {
            LOGGER.debug("Chatserver中增加了玩家={} innerRealmId={} ", playerId, innerRealmId);
            if (!playerIdToChatPlayer.containsKey(playerId)) {
                ChatServerPlayer chatServerPlayer = new ChatServerPlayer(playerId, innerRealmId, outerRealmId);
                playerIdToChatPlayer.put(playerId, chatServerPlayer);
            }
            playerIdToChatPlayer.get(playerId).setOnline(true);
            if (!StringUtils.isEmpty(simplePlayer.getGuildId())) {
                // todo 暂未开放其他聊天，但是已经支持。
                // updateGroupInfo(new ChatGroupInfo(ChatTypeEnum.CHAT_GUILD, simplePlayer.getGuildId(), simplePlayer.getGuildName()), playerId);
            }
        } else {
            LOGGER.debug("未查询到玩家的SimplePlayer信息，Chatserver中无法增加了玩家={} innerRealmId={} ", playerId, innerRealmId);
        }
    }

    @Override
    public void onRemoveChatServerPlayer(String playerId, int innerRealmId) {
        LOGGER.debug("准备从Chatserver中移除了玩家playerId={} innerRealmId={} ", playerId, innerRealmId);
        if (!playerIdToChatPlayer.containsKey(playerId)) {
            LOGGER.debug("玩家playerId={} innerRealmId={} 下线了，但是不在ChatServer内存中.", playerId, innerRealmId);
            return;
        }
        playerIdToChatPlayer.get(playerId).setOnline(false);
        if (playerIdToChatPlayer.get(playerId).getSendMsgCount() > 0) {
            LOGGER.debug("玩家playerId={} innerRealmId={} 存有发送的消息，暂时无法移除.", playerId, innerRealmId);
            return;
        }
        if (playerIdToRelationIds.containsKey(playerId)) {
            LOGGER.debug("玩家playerId={} innerRealmId={} 存有私聊消息了，暂时无法移除.", playerId, innerRealmId);
            return;
        }
        LOGGER.debug("玩家playerId={} innerRealmId={} 没有任何消息了，安全移除.", playerId, innerRealmId);
        playerIdToChatPlayer.remove(playerId);
    }

    @Override
    public void onSendCacheMsgs(Request request) {
        if (typeToGroupIdToMsgLis_CacheToSend.size() <= 0) {
            return;
        }
        LOGGER.debug("开始发送缓存消息......");
        Map<ChatTypeEnum, Map<String, List<ChatMsg>>> typeToGroupIdToCacheToSendMsgLis = new HashMap<>();  // 各个组缓存未发送的消息
        List<ChatMsg> toAllPlayerMsgLis = new ArrayList<>();// 发送给所有人的消息
        fillEachMsg(typeToGroupIdToCacheToSendMsgLis, toAllPlayerMsgLis);
        sendEachMsg(typeToGroupIdToCacheToSendMsgLis, toAllPlayerMsgLis);
    }


    @Override
    public void onRemoveOverTimePrivateMsg() {
        long curTime = System.currentTimeMillis();
        List<Long> keys = new ArrayList<>(relationIdToRelation.keySet());
        keys.forEach(relationId -> {
            PrivateChatRelation relation = relationIdToRelation.get(relationId);
            if ((curTime - relation.getLastUpdateTime()) >= 2 * DateUtils.MILLIS_PER_MINUTE) {
                LOGGER.debug("私聊关系过期了，移除. relationId={} playerIdA={} playerIdB={} .", relation.getRelationId(), relation.getPlayerIdA(), relation.getPlayerIdB());
                relationIdToRelation.remove(relationId);
                removePlayerPrivateRelation(relation.getPlayerIdA(), relationId);
                removePlayerPrivateRelation(relation.getPlayerIdB(), relationId);
                tryToRemoveChatServerPlayer(relation.getPlayerIdA());
                tryToRemoveChatServerPlayer(relation.getPlayerIdB());
            }
        });
    }

    @Override
    public void onCm_Chat(Cm_Chat_Msg_WithPlayerId msg) {
        if (!(msg.getMessage() instanceof Cm_Chat)) {
            return;
        }
        Cm_Chat chatMsg = (Cm_Chat) msg.getMessage();
        LOGGER.debug("收到={} 发送的={} 信息={}", msg.getPlayerId(), chatMsg.getType(), chatMsg.getContent());
        if (chatMsg.getAction() == Cm_Chat.Action.SYNC) {
            syncPlayerMsg(msg.getPlayerId());
        } else if (chatMsg.getAction() == Action.SEND) {
            if (chatMsg.getType() == ChatTypeEnum.CHAT_PRIVATE) {
                PrivateChatMsg privateChatMsg = new PrivateChatMsg(chatMsg.getRelationId(), msg.getPlayerId(), chatMsg.getChatTargetId(), chatMsg.getContent());
                onAddChatMsg(new AddChatMsg.Request(innerRealmId, privateChatMsg));
            } else {
                String groupId = getGroupIdByPlayerId(chatMsg.getType(), msg.getPlayerId());
                if (StringUtils.isEmpty(groupId)) {
                    return; // 组Id缺失，发送失败
                }
                GroupChatMsg groupChatMsg = new GroupChatMsg(chatMsg.getType(), groupId, msg.getPlayerId(), chatMsg.getContent(), chatMsg.getUseHorn());
                onAddChatMsg(new AddChatMsg.Request(innerRealmId, groupChatMsg));
            }
        }
    }

    private void fillEachMsg(Map<ChatTypeEnum, Map<String, List<ChatMsg>>> typeToGroupIdToCacheToSendMsgLis, List<ChatMsg> toAllPlayerMsgLis) {
        typeToGroupIdToMsgLis_CacheToSend.forEach((type, groupIdToMsgLis) -> {
            if (isNotToAllPlayerChatType(type)) {
                Map<String, List<ChatMsg>> groupIdToNeedSendMsgLis = ChatUtils.getMapByKey(typeToGroupIdToCacheToSendMsgLis, type);
                for (Entry<String, List<ChatMsg>> kv : groupIdToNeedSendMsgLis.entrySet()) {
                    ChatUtils.getListByKey(groupIdToNeedSendMsgLis, kv.getKey()).addAll(kv.getValue());
                }
            } else {
                for (Entry<String, List<ChatMsg>> kv : groupIdToMsgLis.entrySet()) {
                    toAllPlayerMsgLis.addAll(kv.getValue());
                }
            }
        });
        typeToGroupIdToMsgLis_CacheToSend.clear();
    }

    private void sendEachMsg(Map<ChatTypeEnum, Map<String, List<ChatMsg>>> chatTypeToGroupIdToNeedSendMsgLis, List<ChatMsg> toAllPlayerMsgLis) {
        chatTypeToGroupIdToNeedSendMsgLis.forEach((type, map) -> {
            Map<String, List<ChatMsg>> groupIdToNeedSendMsgLis = map;
            Map<String, List<String>> guildIdToPlayerIds = ChatUtils.getMapByKey(chatTypeToGroupIdToPlayerIds, type);
            groupIdToNeedSendMsgLis.forEach((groupId, msgLis) -> {
                NeedToSendMsgs.Request needToSendMsgs = new NeedToSendMsgs.Request(innerRealmId, Sm_Chat.Action.RESP_SYNC_PART, msgLis, new ArrayList<>(guildIdToPlayerIds.get(groupId)));
                randomSend(needToSendMsgs);
            });
        });
        NeedToSendMsgs.Request needToSendMsgs = new NeedToSendMsgs.Request(innerRealmId, Sm_Chat.Action.RESP_SYNC_PART, toAllPlayerMsgLis, new ArrayList<>(playerIdToChatPlayer.keySet()));
        randomSend(needToSendMsgs);
    }


    private void updateGroupInfo(ChatGroupInfo groupInfo, String playerId) {
        Map<String, List<String>> groupIdToPlayerIds = ChatUtils.getMapByKey(chatTypeToGroupIdToPlayerIds, groupInfo.getType());
        Map<String, String> playerIdToGroupId = ChatUtils.getMapByKey(chatTypeToPlayerIdToGroupId, groupInfo.getType());
        String oldGuildId = playerIdToGroupId.get(playerId);
        String newGuildId = groupInfo.getGroupId();
        if (!StringUtils.isEmpty(oldGuildId)) {
            if (!oldGuildId.equals(newGuildId)) {
                ChatUtils.getListByKey(groupIdToPlayerIds, oldGuildId).remove(playerId);
                _addPlayerToNewGroup(groupInfo.getType(), newGuildId, playerId);
            }
        } else {
            _addPlayerToNewGroup(groupInfo.getType(), newGuildId, playerId);
        }
    }

    private void _addPlayerToNewGroup(ChatTypeEnum type, String newGroupId, String playerId) {
        Map<String, List<String>> groupIdToPlayerIds = ChatUtils.getMapByKey(chatTypeToGroupIdToPlayerIds, type);
        Map<String, String> playerIdToGroupId = ChatUtils.getMapByKey(chatTypeToPlayerIdToGroupId, type);
        if (!StringUtils.isEmpty(newGroupId)) {
            ChatUtils.getListByKey(groupIdToPlayerIds, newGroupId).remove(playerId);
            ChatUtils.getListByKey(groupIdToPlayerIds, newGroupId).add(playerId);
            playerIdToGroupId.put(playerId, newGroupId);
        } else {
            playerIdToGroupId.remove(playerId);
        }
    }


    private void handlePrivateMsg(PrivateChatMsg chatMsg) {
        if (!relationIdToRelation.containsKey(chatMsg.getRelationId())) {
            ChatUtils.getMapByKey(playerIdToRelationIds, chatMsg.getSenderPlayerId()).remove(chatMsg.getRelationId());
            ChatUtils.getMapByKey(playerIdToRelationIds, chatMsg.getRevPlayerId()).remove(chatMsg.getRelationId());
            chatMsg.setRelationId(ChatIdManager.getChatRelationId());
            PrivateChatRelation relation = new PrivateChatRelation(chatMsg.getRelationId(), chatMsg.getSenderPlayerId(), chatMsg.getRevPlayerId());
            relationIdToRelation.put(chatMsg.getRelationId(), relation);
            ChatUtils.getMapByKey(playerIdToRelationIds, chatMsg.getSenderPlayerId()).put(chatMsg.getRelationId(), (byte) 1);
            ChatUtils.getMapByKey(playerIdToRelationIds, chatMsg.getRevPlayerId()).put(chatMsg.getRelationId(), (byte) 1);
        }
        relationIdToRelation.get(chatMsg.getRelationId()).addChatMsg(chatMsg);

        List<String> toPlayerIds = new ArrayList<>();
        toPlayerIds.add(chatMsg.getRevPlayerId());
        toPlayerIds.add(chatMsg.getSenderPlayerId());
        NeedToSendMsgs.Request needToSendMsgs = new NeedToSendMsgs.Request(innerRealmId, Sm_Chat.Action.RESP_SYNC_PART, chatMsg, toPlayerIds);
        randomSend(needToSendMsgs);
    }


    private void syncPlayerMsg(String playerId) {
        List<ChatMsg> all = new ArrayList<>();
        fillPlayerGroupMsgLis(playerId, all);
        fillPlayerPrivateMsgLis(playerId, all);
        NeedToSendMsgs.Request needToSendMsgs = new NeedToSendMsgs.Request(innerRealmId, Sm_Chat.Action.RESP_SYNC, all, playerId);
        randomSend(needToSendMsgs);
    }

    private void fillPlayerPrivateMsgLis(String playerId, List<ChatMsg> all) {
        if (playerIdToRelationIds.containsKey(playerId)) {
            ChatUtils.getMapByKey(playerIdToRelationIds, playerId).forEach((relationId, v) -> {
                if (relationIdToRelation.containsKey(relationId)) {
                    all.addAll(relationIdToRelation.get(relationId).getChatMsgList());
                }
            });
        }
    }

    private void fillPlayerGroupMsgLis(String playerId, List<ChatMsg> all) {
        typeToGroupIdToMsgLis.forEach((type, groupIdToMsgLis) -> {
            String groupId = getGroupIdByPlayerId(type, playerId);
            if (!StringUtils.isEmpty(groupId) && groupIdToMsgLis.containsKey(groupId)) {
                all.addAll(ChatUtils.getListByKey(groupIdToMsgLis, groupId));
            }
        });
    }


    private void randomSend(NeedToSendMsgs.Request request) {
        int idx = RandomUtils.dropBetweenTowNum(0, msgSenderLis.size() - 1);
        msgSenderLis.get(idx).tell(request, ActorRef.noSender());
    }


    private String getGroupIdByPlayerId(ChatTypeEnum type, String playerId) {
        String groupId = GroupChatMsg.TO_ALL_PLAYER_GROUP_ID;
        if (isNotToAllPlayerChatType(type)) {
            Map<String, String> playerIdToGroupId = ChatUtils.getMapByKey(chatTypeToPlayerIdToGroupId, type);
            groupId = playerIdToGroupId.get(playerId);
        }
        return groupId;
    }


    /**
     * 不是发送给所有玩家的聊天类型，因该有自己的组Id，不能使用 GroupChatMsg.TO_ALL_PLAYER_GROUP_ID;
     *
     * @param type
     * @param groupId
     * @return
     */
    private String getGroupIdByGroupId(ChatTypeEnum type, String groupId) {
        if (isNotToAllPlayerChatType(type)) {
            if (GroupChatMsg.TO_ALL_PLAYER_GROUP_ID.equals(groupId)) {
                return null;
            }
        }
        return GroupChatMsg.TO_ALL_PLAYER_GROUP_ID;
    }


    /**
     * 移除玩家私聊relationId，如果没有私聊关系了，则都移除
     *
     * @param playerId
     * @param relationId
     */
    private void removePlayerPrivateRelation(String playerId, long relationId) {
        ChatUtils.getMapByKey(playerIdToRelationIds, playerId).remove(relationId);
        if (ChatUtils.getMapByKey(playerIdToRelationIds, playerId).size() <= 0) {
            playerIdToRelationIds.remove(playerId);
        }
    }


    /**
     * 尝试移除ChatServrPlayer
     *
     * @param playerId
     */
    private void tryToRemoveChatServerPlayer(String playerId) {
        if (!playerIdToRelationIds.containsKey(playerId) && playerIdToChatPlayer.containsKey(playerId)) {
            ChatServerPlayer chatServerPlayer = playerIdToChatPlayer.get(playerId);
            if (chatServerPlayer.getSendMsgCount() <= 0 && !chatServerPlayer.isOnline()) { // 不在线，且没有消息了
                playerIdToChatPlayer.remove(playerId);
                LOGGER.debug("玩家playerId={}不在线，且没有任何相关的消息了，移除chatServerPlayer", playerId);
            }
        }
    }


    @Override
    public void sendPrivateMsg(PrivateMsg privateMsg) {

    }

    private class MsgSendTask extends TimerTask {

        @Override
        public void run() {
            self.tell(new SendCacheMsgs.Request(), ActorRef.noSender());
        }
    }

    private class RemovePrivateMsgTask extends TimerTask {

        @Override
        public void run() {
            self.tell(new RemoveOverTimePrivateMsg.Request(), ActorRef.noSender());
        }
    }

    /**
     * 不是发送给所有玩家的聊天类型
     *
     * @param type
     * @return
     */
    public static boolean isNotToAllPlayerChatType(ChatTypeEnum type) {
        if (type == ChatTypeEnum.CHAT_GUILD || type == ChatTypeEnum.CHAT_TEAM) { // 目前有组Id的类型，其他都是全员发送
            return true;
        }
        return false;
    }
}