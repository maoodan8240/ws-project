package ws.gameServer.features.standalone.extp.newGuild.msg.base;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 16-12-1.
 */
public class In_NewGuildCreate {

    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = -6928234470791879261L;
        private String guildName;
        private int guildIcon;
        private SimplePlayer simplePlayer;


        public String getGuildName() {
            return guildName;
        }

        public int getGuildIcon() {
            return guildIcon;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }

        public Request(String guildName, int guildIcon, SimplePlayer simplePlayer) {
            this.guildName = guildName;
            this.guildIcon = guildIcon;
            this.simplePlayer = simplePlayer;
        }

    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = -1874535548783829294L;
        private In_NewGuildCreate.Request request;
        private NewGuild guild;


        public In_NewGuildCreate.Request getRequest() {
            return request;
        }

        public NewGuild getGuild() {
            return guild;
        }

        public Response(In_NewGuildCreate.Request request, NewGuild guild) {
            this.request = request;
            this.guild = guild;
        }

        public Response(ResultCode resultCode, In_NewGuildCreate.Request request) {
            super(resultCode);
            this.request = request;
        }
    }
}
