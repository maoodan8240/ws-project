package ws.thirdPartyServer.system.actor;

import akka.actor.ActorSystem;
import akka.actor.Props;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.exception.WsActorSystemInitException;
import ws.thirdPartyServer.features.actor.cluster.ClusterListener;
import ws.thirdPartyServer.system.config.AppConfig;

public class WsActorSystem {
    private static final ActorSystem actorSystem = ActorSystem.create(ActorSystemPath.WS_Common_System, AppConfig.get());

    public static void init() {
        try {
            actorSystem.actorOf(Props.create(ClusterListener.class), ActorSystemPath.WS_Common_ClusterListener);
            actorSystem.actorOf(Props.create(WSRootActor.class), ActorSystemPath.WS_Common_WSRoot);
        } catch (Exception e) {
            throw new WsActorSystemInitException("ActorSystem init() 异常！", e);
        }
    }

    public static ActorSystem get() {
        return actorSystem;
    }
}
