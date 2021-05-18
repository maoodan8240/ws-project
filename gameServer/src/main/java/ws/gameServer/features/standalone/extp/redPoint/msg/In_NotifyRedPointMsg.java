package ws.gameServer.features.standalone.extp.redPoint.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.protos.EnumsProtos.RedPointEnum;

import java.util.HashMap;

/**
 * Created by lee on 9/1/17.
 */
public class In_NotifyRedPointMsg {
    public static class Request extends AbstractInnerMsg {
        private HashMap<RedPointEnum, Boolean> redPointToShow;

        public Request(HashMap<RedPointEnum, Boolean> redPointToShow) {
            this.redPointToShow = redPointToShow;
        }

        public HashMap<RedPointEnum, Boolean> getRedPointToShow() {
            return redPointToShow;
        }
    }
}
