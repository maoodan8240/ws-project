package ws.gameServer.features.standalone.extp.friends.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.relationship.base.resultCode.ResultCodeEnum;

public class In_AgreeApply {
    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = -3395730843519536542L;
        private String operateAgreerPlayerId;   // 操作同意行为的玩家
        private String beAgreerPlayerId;        // 被同意的玩家

        public Request(String operateAgreerPlayerId, String beAgreerPlayerId) {
            this.operateAgreerPlayerId = operateAgreerPlayerId;
            this.beAgreerPlayerId = beAgreerPlayerId;
        }

        public String getOperateAgreerPlayerId() {
            return operateAgreerPlayerId;
        }

        public String getBeAgreerPlayerId() {
            return beAgreerPlayerId;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = 2659446543466012683L;
        private Request request;

        public Response(Request request) {
            this.request = request;
            this.resultCode = ResultCodeEnum.SUCCESS;
        }

        public Response(Request request, ResultCode code) {
            this.request = request;
            this.resultCode = code;
        }

        public Request getRequest() {
            return request;
        }
    }
}
