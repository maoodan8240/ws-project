package ws.gameServer.features.standalone.actor.newGuildCenter._module;
/**
 * Created by lee on 6/8/17.
 */

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import ws.gameServer.features.standalone.actor.newGuildCenter.ctrl.NewGuildCenterCtrl;

/**
 * 实现此接口的类必须是无状态的类
 */
@FunctionalInterface
public interface Action {
    public void onRecv(NewGuildCenterCtrl actorCtrl, Object msg, ActorContext context, ActorRef sender);
}
