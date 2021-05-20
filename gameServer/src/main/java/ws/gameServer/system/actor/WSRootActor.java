package ws.gameServer.system.actor;

import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.actor.register.RegisterActor;
import ws.gameServer.features.actor.world.WorldActor;
import ws.gameServer.features.standalone.actor.arenaCenter.ArenaCenterActor;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;

public class WSRootActor extends WsActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(WSRootActor.class);

    public WSRootActor() {
        createChild();
    }

    /**
     * 创建子actor
     */
    private void createChild() {
        try {
            context().watch(context().actorOf(Props.create(RegisterActor.class), ActorSystemPath.WS_GameServer_Register));
            context().watch(context().actorOf(Props.create(WorldActor.class), ActorSystemPath.WS_GameServer_World));
            context().watch(context().actorOf(Props.create(ArenaCenterActor.class), ActorSystemPath.WS_GameServer_ArenaCenter));
        } catch (Exception e) {
            LOGGER.error("", e);
            System.exit(-1);
        }
    }

    @Override
    public void onRecv(Object msg) throws Exception {

    }
}
