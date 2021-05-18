package ws.gameServer.features.standalone.extp.newGuild.msg.base;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/9/17.
 */
public class In_NewGuildDisband {
    public static class Request extends AbstractInnerMsg {

        private static final long serialVersionUID = 5535321317065644664L;

        private String guildId;
        private SimplePlayer simplePlayer;

        public Request(String guildId, SimplePlayer simplePlayer) {
            this.guildId = guildId;
            this.simplePlayer = simplePlayer;
        }

        public String getGuildId() {
            return guildId;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }
    }

    public static class Response extends AbstractInnerMsg {

        private static final long serialVersionUID = 2240135644226182702L;
        private Request request;

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }

        public Response(Request request) {
            this.request = request;
        }

        public Request getRequest() {
            return request;
        }

    }
}
