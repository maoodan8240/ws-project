package ws.gameServer.features.standalone.extp.newGuild.msg.base;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 5/23/17.
 */
public class In_NewGuildOneKeyRefuse {
    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = 3539740881755383824L;
        private SimplePlayer simplePlayer;

        public Request(SimplePlayer simplePlayer) {
            this.simplePlayer = simplePlayer;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = 7657612554931950449L;
        private Request request;
        private String BePlayerId;

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }

        public Response(Request request) {
            this.request = request;
        }

        public Response(Request request, String BePlayerId) {
            this.request = request;
            this.BePlayerId = BePlayerId;
        }

        public Request getRequest() {
            return request;
        }

        public String getBePlayerId() {
            return BePlayerId;
        }
    }
}
