package ws.gameServer.features.actor.world._msgModule;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import ws.gameServer.features.actor.world.ctrl.WorldCtrl;

/**
 * 实现此接口的类必须是无状态的类
 */
@FunctionalInterface
public interface Action {
    public void onRecv(Object msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception;
}
