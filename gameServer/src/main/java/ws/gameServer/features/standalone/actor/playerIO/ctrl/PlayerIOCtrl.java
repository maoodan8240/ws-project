package ws.gameServer.features.standalone.actor.playerIO.ctrl;

import akka.actor.ActorRef;
import ws.common.utils.mc.controler.Controler;

public interface PlayerIOCtrl extends Controler<Object> {

    void handleOfflineMsg(String selfPlayerId, Object msg, ActorRef oriSender);

}
