package ws.gameServer.features.standalone.extp.mails;


import akka.actor.ActorRef;
import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.mails.ctrl.MailsCtrl;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MailProtos.Cm_Mail;
import ws.protos.MailProtos.Cm_Mail.Action;
import ws.protos.MailProtos.Sm_Mail;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.base.msg.mail.In_AddGmMail;
import ws.relationship.base.msg.mail.In_AddGmMail.Request;
import ws.relationship.base.msg.mail.In_HasNewGmMail;
import ws.relationship.topLevelPojos.mails.Mails;
import ws.relationship.utils.ProtoUtils;


public class MailsExtp extends AbstractPlayerExtension<MailsCtrl> {
    public static boolean useExtension = true;

    public MailsExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(MailsCtrl.class, Mails.class);
    }

    @Override
    public void _initReference() throws Exception {
        getControlerForQuery()._initReference();
    }

    @Override
    public void _postInit() throws Exception {
        getControlerForQuery()._initAll();
    }

    @Override
    public void onRecvMyNetworkMsg(Message clientMsg) throws Exception {
        if (clientMsg instanceof Cm_Mail) {
            Cm_Mail cm = (Cm_Mail) clientMsg;
            onCm_Mail(cm);
        }
    }

    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg) throws Exception {
        if (innerMsg instanceof In_PlayerLoginedRequest) {
            onPlayerLogined((In_PlayerLoginedRequest) innerMsg);
        } else if (innerMsg instanceof In_DayChanged) {
            onDayChanged();
        } else if (innerMsg instanceof Request) {
            onIn_AddGmMailRequest((Request) innerMsg);
        } else if (innerMsg instanceof In_HasNewGmMail.Request) {
            onIn_HasNewGmMailRequest((In_HasNewGmMail.Request) innerMsg);
        }
    }


    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg) throws Exception {
    }

    private void onCm_Mail(Cm_Mail cm) throws Exception {
        Sm_Mail.Builder b = Sm_Mail.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_Mail.Action.SYNC_VALUE:
                    b.setAction(Sm_Mail.Action.RESP_SYNC);
                    sync();
                    break;
                case Cm_Mail.Action.READ_VALUE:
                    b.setAction(Sm_Mail.Action.RESP_READ);
                    read(cm.getMailId());
                    break;
                case Cm_Mail.Action.GET_ATTACHMENTS_VALUE:
                    b.setAction(Sm_Mail.Action.RESP_GET_ATTACHMENTS);
                    getAttachments(cm.getMailId());
                    break;
                case Action.GET_All_ATTACHMENTS_VALUE:
                    b.setAction(Sm_Mail.Action.RESP_GET_All_ATTACHMENTS);
                    getAllAttachments();
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_Mail, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }

    private void getAttachments(String mailId) {
        getControlerForQuery().getAttachments(mailId);
    }

    private void read(String mailId) {
        getControlerForQuery().read(mailId);
    }

    private void onPlayerLogined(In_PlayerLoginedRequest im_PlayerLogined) throws Exception {
        if (im_PlayerLogined.getPlayerId().equals(ownerCtrl.getTarget().getPlayerId())) {
            // 本玩家, 同步信息
            sync();
        }
    }

    private void onDayChanged() throws Exception {
        getControlerForQuery()._resetDataAtDayChanged();
        getControlerForQuery().sync();
    }


    private void onIn_AddGmMailRequest(In_AddGmMail.Request request) {
        getControlerForQuery().addMail(request.getMail());
        getOwnerCtrl().getCurSendActorRef().tell(new In_AddGmMail.Response(request), ActorRef.noSender());
    }

    private void getAllAttachments() {
        getControlerForQuery().getAllAttachments();
    }


    private void sync() {
        getControlerForQuery().sync();
    }

    private void onIn_HasNewGmMailRequest(In_HasNewGmMail.Request request) {
        getControlerForQuery().toGetNewGmMail();
    }
}
