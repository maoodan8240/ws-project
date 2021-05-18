package ws.clusterCenterServer.system.actor;

import akka.actor.ActorSystem;
import akka.actor.Props;
import ws.clusterCenterServer.features.actor.cluster.ClusterListener;
import ws.clusterCenterServer.system.config.AppConfig;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.exception.WsActorSystemInitException;

public class WsActorSystem {
    private static final ActorSystem actorSystem = ActorSystem.create(ActorSystemPath.WS_Common_System, AppConfig.get());

    public static void init() {
        try {
            actorSystem.actorOf(Props.create(ClusterListener.class), ActorSystemPath.WS_Common_ClusterListener);
        } catch (Exception e) {
            throw new WsActorSystemInitException("ActorSystem init() 异常！", e);
        }
    }

    public static ActorSystem get() {
        return actorSystem;
    }
}
