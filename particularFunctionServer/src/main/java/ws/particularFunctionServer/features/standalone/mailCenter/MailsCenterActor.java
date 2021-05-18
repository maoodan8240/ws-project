package ws.particularFunctionServer.features.standalone.mailCenter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.di.GlobalInjector;
import ws.particularFunctionServer.features.standalone.mailCenter.ctrl.MailsCenterCtrl;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.msg.mail.In_AddGmMail;
import ws.relationship.base.msg.mail.In_SyncGmMail;
import ws.relationship.topLevelPojos.mailCenter.GmMail;

import java.util.List;

public class MailsCenterActor extends WsActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailsCenterActor.class);
    private MailsCenterCtrl ctrl;

    public MailsCenterActor() {
        this.ctrl = GlobalInjector.getInstance(MailsCenterCtrl.class);
        this.ctrl.setActorContext(getContext());
    }

    @Override
    public void onRecv(Object msg) throws Exception {
        if (msg instanceof In_SyncGmMail.Request) {
            onIn_SyncGmMailRequest((In_SyncGmMail.Request) msg);
        } else if (msg instanceof In_AddGmMail.Request) {
            onIn_AddGmMailRequest((In_AddGmMail.Request) msg);
        }
    }

    private void onIn_SyncGmMailRequest(In_SyncGmMail.Request request) {
        List<GmMail> gmMailList = ctrl.syncGmMail(request);
        In_SyncGmMail.Response response = new In_SyncGmMail.Response(gmMailList, request);
        getSender().tell(response, getSelf());
    }

    private void onIn_AddGmMailRequest(In_AddGmMail.Request request) {
        ctrl.addNewGmMail((GmMail) request.getMail());
        In_AddGmMail.Response response = new In_AddGmMail.Response(request);
        getSender().tell(response, self());
    }

}
