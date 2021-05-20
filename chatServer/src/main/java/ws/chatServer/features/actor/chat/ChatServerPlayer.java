package ws.chatServer.features.actor.chat;

import java.io.Serializable;

/**
 * Created by lee on 17-5-4.
 */
public class ChatServerPlayer implements Serializable {
    private static final long serialVersionUID = -733925659197112533L;
    private String playerId;            // 玩家Id

    private int innerRealmId;           // 内部服Id
    private int outerRealmId;           // 显示服Id
    private int sendMsgCount;           // 发送消息的数量
    private boolean online;             // 是否在线

    public ChatServerPlayer() {
    }


    public ChatServerPlayer(String playerId, int innerRealmId, int outerRealmId) {
        this.playerId = playerId;
        this.innerRealmId = innerRealmId;
        this.outerRealmId = outerRealmId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getOuterRealmId() {
        return outerRealmId;
    }

    public int getInnerRealmId() {
        return innerRealmId;
    }


    public void addSendMsgCount() {
        sendMsgCount++;
    }

    public void reduceSendMsgCount() {
        sendMsgCount--;
    }


    public int getSendMsgCount() {
        return sendMsgCount;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
