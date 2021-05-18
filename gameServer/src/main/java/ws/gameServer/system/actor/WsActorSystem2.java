package ws.gameServer.system.actor;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import ws.gameServer.features.actor.cluster.ClusterListener;
import ws.gameServer.system.config.AppConfig;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.exception.WsActorSystemInitException;

public class WsActorSystem2 {
    private static ActorSystem actorSystem = ActorSystem.create(ActorSystemPath.WS_Common_System, AppConfig.get());

    public static void init() {
        try {
            init2();
            restartActorSystem();
        } catch (Exception e) {
            throw new WsActorSystemInitException("ActorSystem init() 异常！", e);
        }
    }

    public static void init2() {
        actorSystem.actorOf(Props.create(ClusterListener.class), ActorSystemPath.WS_Common_ClusterListener);
        actorSystem.actorOf(Props.create(WSRootActor.class), ActorSystemPath.WS_Common_WSRoot);
    }


    public static ActorSystem get() {
        return actorSystem;
    }


    public static void restartActorSystem() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("暂停60秒停止 actorSystem.....");
                    Thread.sleep(60 * 1000l);

                    Cluster cluster = Cluster.get(actorSystem);
                    cluster.leave(cluster.selfAddress());
                    actorSystem.terminate();
                    System.out.println("暂停60秒重启动 actorSystem.....");
                    Thread.sleep(60 * 1000l);
                    actorSystem = ActorSystem.create(ActorSystemPath.WS_Common_System, AppConfig.get());
                    init2();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
