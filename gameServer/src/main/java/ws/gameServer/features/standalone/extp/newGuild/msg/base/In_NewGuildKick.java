package ws.gameServer.features.standalone.extp.newGuild.msg.base;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/9/17.
 */
public class In_NewGuildKick {

    public static class Request extends AbstractInnerMsg {

        private static final long serialVersionUID = -3428316413862403433L;
        private String guildId;
        private SimplePlayer simplePlayer;
        private SimplePlayer kickSimplePlayer;

        public Request(String guildId, SimplePlayer simplePlayer, SimplePlayer kickPlayer) {
            this.guildId = guildId;
            this.simplePlayer = simplePlayer;
            this.kickSimplePlayer = kickPlayer;
        }

        public String getGuildId() {
            return guildId;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }

        public SimplePlayer getKickSimplePlayer() {
            return kickSimplePlayer;
        }
    }

    public static class Response extends AbstractInnerMsg {

        private static final long serialVersionUID = 6995660554458936564L;
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
