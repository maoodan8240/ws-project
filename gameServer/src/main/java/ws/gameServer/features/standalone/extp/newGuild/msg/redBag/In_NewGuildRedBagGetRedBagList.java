package ws.gameServer.features.standalone.extp.newGuild.msg.redBag;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildPlayerRedBag;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

import java.util.List;

/**
 * Created by lee on 5/31/17.
 */
public class In_NewGuildRedBagGetRedBagList {
    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = -7426038942360463553L;
        private SimplePlayer simplePlayer;

        public Request(SimplePlayer simplePlayer) {
            this.simplePlayer = simplePlayer;
        }

        public SimplePlayer getSimplePlayer() {
            return simplePlayer;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = 4937860192878245493L;
        private Request request;
        private List<NewGuildPlayerRedBag> newGuildRedBagList;

        public Response(ResultCode resultCode, Request request) {
            super(resultCode);
            this.request = request;
        }


        public Response(Request request, List<NewGuildPlayerRedBag> newGuildRedBagList) {
            this.request = request;
            this.newGuildRedBagList = newGuildRedBagList;
        }

        public Request getRequest() {
            return request;
        }

        public List<NewGuildPlayerRedBag> getNewGuildRedBagList() {
            return newGuildRedBagList;
        }
    }
}
