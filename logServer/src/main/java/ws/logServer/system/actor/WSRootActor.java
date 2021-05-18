package ws.logServer.system.actor;

import akka.actor.Props;
import ws.logServer.features.actor.saveLogs.SaveLogsManagerActor;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;

public class WSRootActor extends WsActor {

    public WSRootActor() {
        createChild();
    }

    /**
     * 创建子actor
     */
    private void createChild() {
        context().watch(context().actorOf(Props.create(SaveLogsManagerActor.class), ActorSystemPath.WS_LogServer_SaveLogsManager));
    }

    @Override
    public void onRecv(Object msg) throws Exception {

    }
}
