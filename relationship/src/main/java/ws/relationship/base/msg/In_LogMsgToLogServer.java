package ws.relationship.base.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.relationship.logServer.base.WsLog;

public class In_LogMsgToLogServer extends AbstractInnerMsg {
    private static final long serialVersionUID = -23160637341454340L;
    private WsLog wsLog;

    public In_LogMsgToLogServer(WsLog wsLog) {
        this.wsLog = wsLog;
    }

    public WsLog getWsLog() {
        return wsLog;
    }
}
