package ws.gameServer.features.standalone.actor.playerIO.ctrl._module.actions;

import akka.actor.ActorRef;
import ws.gameServer.features.standalone.actor.playerIO.ctrl._module.Action;
import ws.gameServer.features.standalone.extp.mails.utils.MailsCtrlOfflineUtils;
import ws.relationship.base.msg.mail.In_AddGmMail;

/**
 * Created by lee on 16-12-16.
 */
public class MailOfflineMsgHandle implements Action {
    @Override
    public void handleOfflineMsg(String selfPlayerId, Object msg, ActorRef oriSender) {
        if (msg instanceof In_AddGmMail.Request) {
            onAddGmMailRequest((In_AddGmMail.Request) msg);
        }
    }

    private void onAddGmMailRequest(In_AddGmMail.Request request) {
        MailsCtrlOfflineUtils.addMail(request.getPlayerId(), request.getOuterRealmId(), request.getMail());
    }
}
