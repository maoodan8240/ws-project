package ws.relationship.base.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.relationship.logServer.base.WsLog;

import java.util.ArrayList;
import java.util.List;

public class In_LogMsgListToLogServer extends AbstractInnerMsg {
    private static final long serialVersionUID = -23160637341454340L;
    private List<WsLog> wsLogLis = new ArrayList<>();

    public In_LogMsgListToLogServer(List<WsLog> wsLogLis) {
        this.wsLogLis = wsLogLis;
    }

    public List<WsLog> getWsLogLis() {
        return wsLogLis;
    }
}
