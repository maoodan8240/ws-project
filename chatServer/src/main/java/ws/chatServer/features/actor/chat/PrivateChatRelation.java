package ws.chatServer.features.actor.chat;

import ws.relationship.chatServer.ChatMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 17-5-4.
 */
public class PrivateChatRelation {
    private long relationId;
    private String playerIdA;
    private String playerIdB;
    private List<ChatMsg> chatMsgList = new ArrayList<>();
    private long lastUpdateTime;

    public PrivateChatRelation(long relationId, String playerIdA, String playerIdB) {
        this.relationId = relationId;
        this.playerIdA = playerIdA;
        this.playerIdB = playerIdB;
    }

    public long getRelationId() {
        return relationId;
    }

    public String getPlayerIdA() {
        return playerIdA;
    }

    public String getPlayerIdB() {
        return playerIdB;
    }

    public void addChatMsg(ChatMsg chatMsg) {
        ChatUtils.removeOverflowMsg(this.getChatMsgList());
        this.getChatMsgList().add(chatMsg);
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public List<ChatMsg> getChatMsgList() {
        return chatMsgList;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
}
