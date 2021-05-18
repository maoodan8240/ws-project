package ws.mongodbRedisServer.features.actor.pojoGetter;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import org.bson.types.ObjectId;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.relationship.base.MagicNumbers;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PojoGetterManagerActor extends WsActor {
    private Map<String, ActorRef> flagToRef = new HashMap<>();
    private List<String> flags = new ArrayList<>();
    private static final Random rd = new Random();

    public PojoGetterManagerActor() {
        initPojoGetterActor(MagicNumbers.AkkaPojoGetterActorCount);
    }

    @Override
    public void onRecv(Object msg) throws Exception {
        if (msg instanceof InnerMsg) {
            randomTell((InnerMsg) msg);
        } else if (msg instanceof Terminated) {
            terminated((Terminated) msg);
        }
    }

    private void initPojoGetterActor(int size) {
        while (flagToRef.size() < size) {
            String flag = ObjectId.get().toString();
            ActorRef actorRef = getContext().actorOf(Props.create(PojoGetterActor.class), ActorSystemPath
                    .WS_MongodbRedisServer_PojoGetter + flag);
            getContext().watch(actorRef);
            flagToRef.put(flag, actorRef);
            flags.add(flag);
        }
    }


    private void randomTell(InnerMsg request) {
        initPojoGetterActor(MagicNumbers.AkkaPojoGetterActorCount);
        String flag = flags.get(rd.nextInt(flags.size()));
        flagToRef.get(flag).tell(request, getSender());
    }


    private void terminated(Terminated terminated) {
        String actorName = terminated.actor().path().elements().toList().last();
        String flag = actorName.replaceFirst(ActorSystemPath.WS_MongodbRedisServer_PojoGetter, "");
        flags.remove(flag);
        flagToRef.remove(flag);
    }
}
