package ws.gameServer.features.standalone.extp.formations;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.formations.ctrl.FormationsCtrl;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.FormationsProtos.Cm_Formations;
import ws.protos.FormationsProtos.Cm_Formations.Action;
import ws.protos.FormationsProtos.Sm_Formations;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.topLevelPojos.formations.Formations;
import ws.relationship.utils.ProtoUtils;


public class FormationsExtp extends AbstractPlayerExtension<FormationsCtrl> {
    public static boolean useExtension = true;

    public FormationsExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(FormationsCtrl.class, Formations.class);
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
        if (clientMsg instanceof Cm_Formations) {
            Cm_Formations cm = (Cm_Formations) clientMsg;
            onCm_Formation(cm);
        }
    }

    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg) throws Exception {
        if (innerMsg instanceof In_PlayerLoginedRequest) {
            onPlayerLogined((In_PlayerLoginedRequest) innerMsg);
        } else if (innerMsg instanceof In_DayChanged) {
            onDayChanged();
        }
    }

    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg) throws Exception {
    }

    private void onCm_Formation(Cm_Formations cm) throws Exception {
        Sm_Formations.Builder b = Sm_Formations.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Action.SYNC_VALUE:
                    b.setAction(Sm_Formations.Action.RESP_SYNC);
                    sync();
                    break;
                case Action.DEPLOYMENT_VALUE:
                    b.setAction(Sm_Formations.Action.RESP_DEPLOYMENT);
                    deployment(cm);
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_Formations, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }

    private void deployment(Cm_Formations cm) {
        getControlerForQuery().deployment(cm.getFormation());
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

    private void sync() {
        getControlerForQuery().sync();
    }

}
