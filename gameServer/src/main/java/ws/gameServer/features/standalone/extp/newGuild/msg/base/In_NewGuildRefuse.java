package ws.gameServer.features.standalone.extp.newGuild.msg.base;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/9/17.
 */
public class In_NewGuildRefuse {
    public static class Request extends AbstractInnerMsg {

        private static final long serialVersionUID = 5307065877880554688L;
        private String guildId;
        private SimplePlayer simplePlayer;
        private SimplePlayer refuseSimplePlayer;

        public Request(String guildId, SimplePlayer simplePlayer, SimplePlayer refuseSimplePlayer) {
            this.guildId = guildId;
            this.simplePlayer = simplePlayer;
            this.refuseSimplePlayer = refuseSimplePlayer;
        }

        public String getGuildId() {
            return guildId;
        }

        public void setGuildId(String guildId) {
            this.guildId = guildId;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }

        public void setSimplePlayer(SimplePlayer simplePlayer) {
            this.simplePlayer = simplePlayer;
        }

        public SimplePlayer getRefuseSimplePlayer() {
            return refuseSimplePlayer;
        }

        public void setRefuseSimplePlayer(SimplePlayer refuseSimplePlayer) {
            this.refuseSimplePlayer = refuseSimplePlayer;
        }
    }

    public static class Response extends AbstractInnerMsg {

        private static final long serialVersionUID = -3531608370567148522L;

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
