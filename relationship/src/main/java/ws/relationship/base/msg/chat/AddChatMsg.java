package ws.relationship.base.msg.chat;

import ws.relationship.chatServer.ChatMsg;

/**
 * Created by zhangweiwei on 17-5-5.
 */
public class AddChatMsg {
    public static class Request extends ChatServerInnerMsg {
        private static final long serialVersionUID = 1412131573382205661L;
        private ChatMsg chatMsg;

        public Request(int innerRealmId, ChatMsg chatMsg) {
            super(innerRealmId);
            this.chatMsg = chatMsg;
        }

        public ChatMsg getChatMsg() {
            return chatMsg;
        }
    }


    public static class Response extends ChatServerInnerMsg {
        private static final long serialVersionUID = 6670654983207931848L;

        public Response(int innerRealmId) {
            super(innerRealmId);
        }
    }
}
