package ws.gameServer.features.actor.world.ctrl;

import akka.actor.ActorContext;
import ws.common.utils.mc.controler.Controler;
import ws.gameServer.features.actor.world.playerIOStatus.PlayerIOStatus;
import ws.gameServer.features.actor.world.pojo.World;

public interface WorldCtrl extends Controler<World> {

    PlayerIOStatus getPlayerIOStatus(String playerId, ActorContext worldActorContext);

    void statisticsCurGameSeverStatus();
}
