package ws.relationship.utils;

import akka.actor.ActorContext;
import ws.relationship.base.ServerRoleEnum;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.In_LogMsgListToLogServer;
import ws.relationship.base.msg.In_LogMsgToLogServer;
import ws.relationship.logServer.base.WsLog;

import java.util.List;

/**
 * Created by lee on 17-5-12.
 */
public class LogServerUtils {

    public static void sendLog(ActorContext actorContext, WsLog wsLog) {
        In_LogMsgToLogServer addLog = new In_LogMsgToLogServer(wsLog);
        ClusterMessageSender.sendMsgToServer(actorContext, ServerRoleEnum.WS_LogServer, addLog, ActorSystemPath.WS_LogServer_Selection_SaveLogsManager);
    }

    public static void sendLogLis(ActorContext actorContext, List<WsLog> wsLogLis) {
        In_LogMsgListToLogServer addLogLis = new In_LogMsgListToLogServer(wsLogLis);
        ClusterMessageSender.sendMsgToServer(actorContext, ServerRoleEnum.WS_LogServer, addLogLis, ActorSystemPath.WS_LogServer_Selection_SaveLogsManager);
    }
}
