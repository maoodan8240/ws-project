package ws.gameServer.features.standalone.extp.newGuild.msg.base;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/11/17.
 */
public class In_NewGuildModify {
    public static class Request extends AbstractInnerMsg {

        private static final long serialVersionUID = 8269675378709520854L;
        private SimplePlayer simplePlayer;
        private String guildNotic;
        private int guildIcon;
        private String guildName;

        private Request(SimplePlayer simplePlayer, int guildIcon) {
            this.simplePlayer = simplePlayer;
            this.guildIcon = guildIcon;
        }

        private Request(SimplePlayer simplePlayer, String guildName, String guildNotic) {
            this.simplePlayer = simplePlayer;
            this.guildNotic = guildNotic;
            this.guildName = guildName;
        }

        private Request(SimplePlayer simplePlayer, String guildNotic) {
            this.simplePlayer = simplePlayer;
            this.guildNotic = guildNotic;
        }

        public static Request createModifyNoticMsg(SimplePlayer simplePlayer, String guildNotic) {
            Request request = new Request(simplePlayer, guildNotic);
            return request;
        }

        public static Request createModifyIconMsg(SimplePlayer simplePlayer, int guildIcon) {
            Request request = new Request(simplePlayer, guildIcon);
            return request;
        }

        public static Request createModifyNameMsg(SimplePlayer simplePlayer, String guildName) {
            Request request = new Request(simplePlayer, guildName, "");
            return request;
        }

        public String getGuildNotic() {
            return guildNotic;
        }

        public int getGuildIcon() {
            return guildIcon;
        }


        public String getGuildName() {
            return guildName;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }
    }

    public static class Response extends AbstractInnerMsg {

        private static final long serialVersionUID = -3389473814808186953L;
        public Request request;
        public NewGuild guild;

        public Response(Request request, NewGuild guild) {
            this.request = request;
            this.guild = guild;
        }

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }

        public Request getRequest() {
            return request;
        }

        public NewGuild getGuild() {
            return guild;
        }
    }
}
