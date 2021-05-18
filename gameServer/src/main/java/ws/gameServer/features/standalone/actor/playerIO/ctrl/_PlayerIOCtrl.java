package ws.gameServer.features.standalone.actor.playerIO.ctrl;

import akka.actor.ActorRef;
import ws.common.utils.mc.controler.AbstractControler;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.playerIO.ctrl._module.OfflineMsgContainer;

public class _PlayerIOCtrl extends AbstractControler<Object> implements PlayerIOCtrl {

    @Override
    public void handleOfflineMsg(String selfPlayerId, Object msg, ActorRef oriSender) {
        OfflineMsgContainer.handleOfflineMsg(selfPlayerId, msg, oriSender);
    }

    @Override
    public void sendPrivateMsg(PrivateMsg privateMsg) {
        return;
    }
}
