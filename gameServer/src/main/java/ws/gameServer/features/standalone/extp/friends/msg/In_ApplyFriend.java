package ws.gameServer.features.standalone.extp.friends.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.relationship.base.resultCode.ResultCodeEnum;

public class In_ApplyFriend {
    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = -527674501426934752L;
        private String applyerPlayerId;    // 申请发起人
        private String beApplyerplayerId;  // 被申请人

        public Request(String applyerPlayerId, String beApplyerplayerId) {
            this.applyerPlayerId = applyerPlayerId;
            this.beApplyerplayerId = beApplyerplayerId;
        }

        public String getApplyerPlayerId() {
            return applyerPlayerId;
        }

        public String getBeApplyerplayerId() {
            return beApplyerplayerId;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = 8888396188977344448L;
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
