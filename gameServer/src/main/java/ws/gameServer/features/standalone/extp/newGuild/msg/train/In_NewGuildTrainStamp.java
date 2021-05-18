package ws.gameServer.features.standalone.extp.newGuild.msg.train;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenterPlayer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/22/17.
 */
public class In_NewGuildTrainStamp {
    public static class Request extends AbstractInnerMsg {

        private static final long serialVersionUID = -1308412015789046010L;
        private SimplePlayer simplePlayer;
        private String playerId;

        public Request(SimplePlayer simplePlayer, String playerId) {
            this.simplePlayer = simplePlayer;
            this.playerId = playerId;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }

        public String getPlayerId() {
            return playerId;
        }
    }

    public static class Response extends AbstractInnerMsg {

        private static final long serialVersionUID = 545783963013092755L;
        private Request request;
        private NewGuildCenterPlayer stampGuildCenterPlayer;
        private NewGuildCenterPlayer guildCenterPlayer;


        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }

        public Response(Request request, NewGuildCenterPlayer stampGuildCenterPlayer, NewGuildCenterPlayer guildCenterPlayer) {
            this.request = request;
            this.stampGuildCenterPlayer = stampGuildCenterPlayer;
            this.guildCenterPlayer = guildCenterPlayer;
        }

        public Request getRequest() {
            return request;
        }


        public NewGuildCenterPlayer getStampGuildCenterPlayer() {
            return stampGuildCenterPlayer;
        }

        public NewGuildCenterPlayer getGuildCenterPlayer() {
            return guildCenterPlayer;
        }
    }
}
