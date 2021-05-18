package ws.gameServer.features.actor.world.playerIOStatus.actions;

import ws.gameServer.features.actor.world.playerIOStatus.PlayerIOStatus;

/**
 * 实现此接口的类必须是无状态的类
 */
@FunctionalInterface
public interface Action {
    public void onRecv(Object msg, PlayerIOStatus playerIO) throws Exception;
}
