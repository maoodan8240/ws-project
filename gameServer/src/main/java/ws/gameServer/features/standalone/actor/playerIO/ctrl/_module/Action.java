package ws.gameServer.features.standalone.actor.playerIO.ctrl._module;

import akka.actor.ActorRef;

/**
 * 实现此接口的类必须是无状态的类
 */
@FunctionalInterface
public interface Action {
    public void handleOfflineMsg(String selfPlayerId, Object msg, ActorRef oriSender);
}
