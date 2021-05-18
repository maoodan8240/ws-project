package ws.logServer.features.actor.saveLogs;

import akka.actor.ActorRef;
import akka.actor.Kill;
import akka.actor.Props;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.In_LogMsgListToLogServer;
import ws.relationship.base.msg.In_LogMsgToLogServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SaveLogsManagerActor extends WsActor {
    private static final Random RD = new Random();
    private static int size = 100;

    private List<ActorRef> actorRefsForSaveLogsLis = new ArrayList<>();

    public SaveLogsManagerActor() {
        init();
    }

    private void init() {
        for (ActorRef actorRef : actorRefsForSaveLogsLis) {
            actorRef.tell(Kill.getInstance(), self());
        }
        actorRefsForSaveLogsLis.clear();
        for (int i = 0; i < size; i++) {
            ActorRef actorRef = getContext().actorOf(Props.create(SaveLogsActor.class), ActorSystemPath.WS_LogServer_SaveLogs + i);
            getContext().watch(actorRef);
            actorRefsForSaveLogsLis.add(actorRef);
        }
    }

    @Override
    public void onRecv(Object msg) throws Exception {
        if (msg instanceof In_LogMsgToLogServer || msg instanceof In_LogMsgListToLogServer) {
            randomSaveLogsActor().tell(msg, self());
        }
    }


    private ActorRef randomSaveLogsActor() {
        int idx = RD.nextInt(size);
        return actorRefsForSaveLogsLis.get(idx);
    }

}
