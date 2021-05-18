package ws.gameServer.features.standalone.extp.friends.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.relationship.base.resultCode.ResultCodeEnum;

public class In_DelFriend {
    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = -8509774825359211069L;
        private String operateDelPlayerId;   // 操作删除行为的玩家
        private String beDelPlayerId;        // 被删除的玩家

        public Request(String operateDelPlayerId, String beDelPlayerId) {
            this.operateDelPlayerId = operateDelPlayerId;
            this.beDelPlayerId = beDelPlayerId;
        }

        public String getOperateDelPlayerId() {
            return operateDelPlayerId;
        }

        public String getBeDelPlayerId() {
            return beDelPlayerId;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = 603944278808348139L;
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
