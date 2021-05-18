package ws.gameServer.features.standalone.extp.newGuild.msg.base;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/9/17.
 */
public class In_NewGuildAgree {
    public static class Request extends AbstractInnerMsg {

        private static final long serialVersionUID = 1471985623273889489L;

        private String guildId;
        private SimplePlayer simplePlayer;
        private SimplePlayer addSimplePlayer;


        public Request(String guildId, SimplePlayer simplePlayer, SimplePlayer addSimplePlayer) {
            this.guildId = guildId;
            this.simplePlayer = simplePlayer;
            this.addSimplePlayer = addSimplePlayer;
        }


        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }

        public String getGuildId() {
            return guildId;
        }

        public SimplePlayer getAddSimplePlayer() {
            return addSimplePlayer;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = 4577306737790616025L;
        private Request request;
        private NewGuild guild;

        public Response(Request request, NewGuild guild) {
            this.request = request;
            this.guild = guild;
        }

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;

        }

        public NewGuild getGuild() {
            return guild;
        }

        public Request getRequest() {
            return request;
        }
    }
}
