package ws.gameServer.features.standalone.extp.newGuild.msg.base;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenterPlayer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/19/17.
 */
public class In_NewGuildGiveJob {
    public static class Request extends AbstractInnerMsg {

        private static final long serialVersionUID = -2084454853346867958L;

        private SimplePlayer simplePlayer;
        private SimplePlayer giveSimplePlayer;

        public Request(SimplePlayer simplePlayer, SimplePlayer giveSimplePlayer) {
            this.simplePlayer = simplePlayer;
            this.giveSimplePlayer = giveSimplePlayer;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }

        public SimplePlayer getGiveSimplePlayer() {
            return giveSimplePlayer;
        }
    }

    public static class Response extends AbstractInnerMsg {

        private static final long serialVersionUID = -5811922391691444920L;
        private Request request;
        private NewGuildCenterPlayer guildCenterPlayer;
        private NewGuildCenterPlayer beGiveJobGuildCenterPlayer;
        private NewGuild guild;

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }

        public Response(Request request, NewGuildCenterPlayer guildCenterPlayer, NewGuildCenterPlayer beGiveJobGuildCenterPlayer, NewGuild guild) {
            this.request = request;
            this.guildCenterPlayer = guildCenterPlayer;
            this.beGiveJobGuildCenterPlayer = beGiveJobGuildCenterPlayer;
            this.guild = guild;
        }

        public Request getRequest() {
            return request;
        }


        public NewGuildCenterPlayer getGuildCenterPlayer() {
            return guildCenterPlayer;
        }


        public NewGuildCenterPlayer getBeGiveJobGuildCenterPlayer() {
            return beGiveJobGuildCenterPlayer;
        }

        public NewGuild getGuild() {
            return guild;
        }
    }
}
