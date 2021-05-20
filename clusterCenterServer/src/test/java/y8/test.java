package y8;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.clusterCenterServer.system.config.AppConfig;

/**
 * Created by lee on 16-8-11.
 */
public class test {
    private static final Logger LOGGER = LoggerFactory.getLogger(test.class);
    private static final ActorSystem actorSystem = ActorSystem.create("wssystem", AppConfig.get());

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            ActorRef ref = actorSystem.actorOf(Props.create(A.class, i).withDispatcher("mProject-server-thread-pool-PinnedDispatcher"), "xxxxxxxxxx" + i);
            ref.tell("xxx", ActorRef.noSender());
        }
    }

    static class A extends UntypedActor {
        int i;

        public A(int i) {
            this.i = i;
        }

        @Override
        public void onReceive(Object message) throws Throwable {
            LOGGER.info("收到消息了...." + i);
            Thread.sleep(60 * 1000);
            LOGGER.info("处理结束了...." + i);
        }
    }
}
