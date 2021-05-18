package ws.gameServer.features.standalone.extp.newGuild.msg.base;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 17-4-11.
 */
public class In_NewGuildSearch {

    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = -406558858323202614L;
        private SimplePlayer simplePlayer;
        private String searchArg;
        private String guildId;

        public Request(SimplePlayer simplePlayer, String searchArg, String guildId) {
            this.simplePlayer = simplePlayer;
            this.searchArg = searchArg;
            this.guildId = guildId;
        }

        public Request(SimplePlayer simplePlayer, String searchArg) {
            this.simplePlayer = simplePlayer;
            this.searchArg = searchArg;
        }



        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }


        public String getGuildId() {
            return guildId;
        }

        public String getSearchArg() {
            return searchArg;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = -9144943317466227682L;
        private Request request;
        private NewGuild guild;

        public Response(NewGuild guild) {
            this.guild = guild;
        }

        public Response(ResultCode resultCode, Request msg) {
            super(resultCode);
            this.request = msg;
        }

        public NewGuild getGuild() {
            return guild;
        }

    }
}
