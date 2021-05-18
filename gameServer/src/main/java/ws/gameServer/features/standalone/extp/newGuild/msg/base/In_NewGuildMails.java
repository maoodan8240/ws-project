package ws.gameServer.features.standalone.extp.newGuild.msg.base;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/19/17.
 */
public class In_NewGuildMails {
    public static class Request extends AbstractInnerMsg {

        private static final long serialVersionUID = 5551278913329248467L;
        private SimplePlayer simplePlayer;
        private String content;
        private String title;

        public Request(SimplePlayer simplePlayer, String content, String title) {
            this.simplePlayer = simplePlayer;
            this.content = content;
            this.title = title;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }

        public String getContent() {
            return content;
        }

        public String getTitle() {
            return title;
        }
    }

    public static class Response extends AbstractInnerMsg {

        private static final long serialVersionUID = 3268791232775656052L;
        private Request request;
        private NewGuild guild;

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }

        public Response(Request request, NewGuild guild) {
            this.request = request;
            this.guild = guild;
        }

        public Request getRequest() {
            return request;
        }

        public NewGuild getGuild() {
            return guild;
        }
    }

}
