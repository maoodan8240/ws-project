package ws.gameServer.features.standalone.extp.redPoint.msg;

import ws.common.utils.message.implement.AbstractPrivateMsg;
import ws.protos.EnumsProtos.RedPointEnum;

import java.util.HashMap;

/**
 * Created by lee on 8/30/17.
 */
public class Pr_CheckRedPointMsg {
    public static class Request extends AbstractPrivateMsg {
        private RedPointEnum redPointEnum;

        public Request(RedPointEnum redPointEnum) {
            this.redPointEnum = redPointEnum;
        }

        public Request() {
        }

        public RedPointEnum getRedPointEnum() {
            return redPointEnum;
        }


    }

    public static class Response extends AbstractPrivateMsg {
        private Request request;
        private HashMap<RedPointEnum, Boolean> redPointToShow;

        public Response(Request request, HashMap<RedPointEnum, Boolean> redPointToShow) {
            this.request = request;
            this.redPointToShow = redPointToShow;
        }

        public Request getRequest() {
            return request;
        }

        public HashMap<RedPointEnum, Boolean> getRedPointToShow() {
            return redPointToShow;
        }

      
    }
}
