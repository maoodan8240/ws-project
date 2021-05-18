package ws.chatServer.system.schedule;

import akka.actor.ActorRef;
import ws.chatServer.system.actor.WsActorSystem;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.In_DisplayActorSelfPath;

public class Cron4jUtils {

    public static void displayActorSelfPath(String[] args) {
        WsActorSystem.get().actorSelection(ActorSystemPath.WS_Common_Selection_WSRoot + "/*").tell(new In_DisplayActorSelfPath(), ActorRef.noSender());
    }
}