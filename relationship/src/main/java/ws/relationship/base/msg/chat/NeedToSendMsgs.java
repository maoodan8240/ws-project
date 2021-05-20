package ws.relationship.base.msg.chat;

import ws.protos.ChatProtos.Sm_Chat;
import ws.protos.ChatProtos.Sm_Chat.Action;
import ws.relationship.chatServer.ChatMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 17-5-5.
 */
public class NeedToSendMsgs {
    public static class Request extends ChatServerInnerMsg {
        private static final long serialVersionUID = 1412131573382205661L;
        private Sm_Chat.Action action = Action.RESP_SYNC_PART;
        private List<ChatMsg> chatMsgList = new ArrayList<>();
        private List<String> sendToPlayerIds = new ArrayList<>();


        /**
         * 消息发送给指定的人
         *
         * @param innerRealmId
         * @param action
         * @param chatMsg
         * @param sendToPlayerId
         */
        public Request(int innerRealmId, Sm_Chat.Action action, ChatMsg chatMsg, String sendToPlayerId) {
            super(innerRealmId);
            this.action = action;
            this.chatMsgList.add(chatMsg);
            this.sendToPlayerIds.add(sendToPlayerId);
        }

        /**
         * 消息发送给指定的人
         *
         * @param innerRealmId
         * @param action
         * @param chatMsg
         * @param sendToPlayerIds
         */
        public Request(int innerRealmId, Sm_Chat.Action action, ChatMsg chatMsg, List<String> sendToPlayerIds) {
            super(innerRealmId);
            this.action = action;
            this.chatMsgList.add(chatMsg);
            this.sendToPlayerIds = sendToPlayerIds;
        }

        /**
         * 打包消息发送给同一个人
         *
         * @param innerRealmId
         * @param action
         * @param chatMsgList
         * @param sendToPlayerId
         */
        public Request(int innerRealmId, Sm_Chat.Action action, List<ChatMsg> chatMsgList, String sendToPlayerId) {
            super(innerRealmId);
            this.action = action;
            this.chatMsgList = chatMsgList;
            this.sendToPlayerIds.add(sendToPlayerId);
        }

        /**
         * 消息发送给部分人
         *
         * @param innerRealmId
         * @param action
         * @param chatMsgList
         * @param sendToPlayerIds
         */
        public Request(int innerRealmId, Sm_Chat.Action action, List<ChatMsg> chatMsgList, List<String> sendToPlayerIds) {
            super(innerRealmId);
            this.action = action;
            this.chatMsgList = chatMsgList;
            this.sendToPlayerIds = sendToPlayerIds;
        }

        public Action getAction() {
            return action;
        }

        public List<ChatMsg> getChatMsgList() {
            return chatMsgList;
        }

        public List<String> getSendToPlayerIds() {
            return sendToPlayerIds;
        }
    }


    public static class Response extends ChatServerInnerMsg {
        private static final long serialVersionUID = 6670654983207931848L;

        public Response(int innerRealmId) {
            super(innerRealmId);
        }
    }
}
