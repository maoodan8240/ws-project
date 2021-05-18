package ws.mongodbRedisServer.system.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import ws.mongodbRedisServer.features.actor.pojoGetter.PojoGetterManagerActor;
import ws.mongodbRedisServer.features.actor.pojoRemover.PojoRemoverActor;
import ws.mongodbRedisServer.features.actor.pojoSaver.PojoSaverActor;
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
        ActorRef ref1 = context().actorOf(Props.create(PojoGetterManagerActor.class), ActorSystemPath.WS_MongodbRedisServer_PojoGetterManager);
        ActorRef ref2 = context().actorOf(Props.create(PojoRemoverActor.class), ActorSystemPath.WS_MongodbRedisServer_PojoRemover);
        ActorRef ref3 = context().actorOf(Props.create(PojoSaverActor.class), ActorSystemPath.WS_MongodbRedisServer_PojoSaver);

        context().watch(ref1);
        context().watch(ref2);
        context().watch(ref3);
    }

    @Override
    public void onRecv(Object msg) throws Exception {

    }
}
