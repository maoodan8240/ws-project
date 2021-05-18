package ws.gameServer.features.standalone.extp.redPoint.msg;

import ws.common.utils.message.implement.AbstractPrivateMsg;
import ws.protos.EnumsProtos.RedPointEnum;

import java.util.HashMap;

/**
 * Created by lee on 9/4/17.
 */
public class Pr_NotifyRedPointMsg {
    public static class Request extends AbstractPrivateMsg {
        private HashMap<RedPointEnum, Boolean> redPointToShow;

        public Request(HashMap<RedPointEnum, Boolean> redPointToShow) {
            this.redPointToShow = redPointToShow;
        }

        public HashMap<RedPointEnum, Boolean> getRedPointToShow() {
            return redPointToShow;
        }
    }
}
