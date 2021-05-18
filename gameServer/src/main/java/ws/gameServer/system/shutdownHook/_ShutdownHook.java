package ws.gameServer.system.shutdownHook;

import akka.cluster.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.system.actor.WsActorSystem;
import ws.gameServer.system.config.AppConfig;

public class _ShutdownHook extends Thread implements ShutdownHook {
    private static final Logger LOGGER = LoggerFactory.getLogger(_ShutdownHook.class);

    @Override
    public void run() {
        try {
            Cluster cluster = Cluster.get(WsActorSystem.get());
            LOGGER.info("{} 真准备离开集群....", AppConfig.getServerRole());
            cluster.leave(cluster.selfAddress());
            Thread.sleep(1000l);
        } catch (Exception e) {
            LOGGER.error("退出jvm后续处理异常", e);
        }
    }
}
