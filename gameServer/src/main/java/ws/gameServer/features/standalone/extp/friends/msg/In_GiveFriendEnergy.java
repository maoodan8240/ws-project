package ws.gameServer.features.standalone.extp.friends.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.relationship.base.resultCode.ResultCodeEnum;

public class In_GiveFriendEnergy {
    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = -5279179058481675246L;
        private String giverPlayerId;
        private String beGiverPlayerId;

        public Request(String giverPlayerId, String beGiverPlayerId) {
            this.giverPlayerId = giverPlayerId;
            this.beGiverPlayerId = beGiverPlayerId;
        }

        public String getBeGiverPlayerId() {
            return beGiverPlayerId;
        }

        public String getGiverPlayerId() {
            return giverPlayerId;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = -3292138092337257686L;
        private Request request;

        public Response(Request request) {
            this.request = request;
            this.resultCode = ResultCodeEnum.SUCCESS;
        }

        public Request getRequest() {
            return request;
        }
    }
}
