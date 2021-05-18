package ws.gameServer.features.standalone.extp.newGuild.msg.base;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/11/17.
 */
public class In_NewGuildOenKeyJoin {
    public static class Request extends AbstractInnerMsg {

        private static final long serialVersionUID = -4110240452162745271L;
        private SimplePlayer simplePlayer;

        public Request(SimplePlayer simplePlayer) {
            this.simplePlayer = simplePlayer;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }
    }

    public static class Response extends AbstractInnerMsg {

        private static final long serialVersionUID = 5540372231584925294L;

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

        public Request getRequest() {
            return request;
        }

        public NewGuild getGuild() {
            return guild;
        }
    }

}
