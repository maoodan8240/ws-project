package ws.gameServer.features.standalone.extp.newGuild.msg.base;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/4/17.
 */
public class In_NewGuildJoin {

    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = -5437525837231407810L;
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
        private static final long serialVersionUID = 5560974919495998350L;
        private In_NewGuildJoin.Request request;
        private NewGuild guild;
        private boolean isIn;

     
        public Response(ResultCode resultCode, In_NewGuildJoin.Request request) {
            super(resultCode);
            this.request = request;
        }

        public In_NewGuildJoin.Request getRequest() {
            return request;
        }

        public NewGuild getGuild() {
            return guild;
        }

        public Response(Request request, NewGuild guild, boolean isIn) {
            this.request = request;
            this.guild = guild;
            this.isIn = isIn;
        }

        public boolean isIn() {
            return isIn;
        }
    }
}
