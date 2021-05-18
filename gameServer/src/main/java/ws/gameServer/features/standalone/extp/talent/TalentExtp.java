package ws.gameServer.features.standalone.extp.talent;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.talent.ctrl.TalentCtrl;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.TalentProtos.Cm_Talent;
import ws.protos.TalentProtos.Sm_Talent;
import ws.relationship.topLevelPojos.talent.Talent;
import ws.relationship.utils.ProtoUtils;


public class TalentExtp extends AbstractPlayerExtension<TalentCtrl> {
    public static boolean useExtension = true;

    public TalentExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(TalentCtrl.class, Talent.class);
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
        if (clientMsg instanceof Cm_Talent) {
            Cm_Talent cm = (Cm_Talent) clientMsg;
            onCm_Talent(cm);
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

    private void onCm_Talent(Cm_Talent cm) throws Exception {
        Sm_Talent.Builder b = Sm_Talent.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_Talent.Action.SYNC_VALUE:
                    b.setAction(Sm_Talent.Action.RESP_SYNC);
                    sync();
                    break;
                case Cm_Talent.Action.UP_LEVEL_VALUE:
                    b.setAction(Sm_Talent.Action.RESP_UP_LEVEL);
                    upLevel(cm.getTalentLevelId());
                    break;
                case Cm_Talent.Action.RESET_VALUE:
                    b.setAction(Sm_Talent.Action.RESP_RESET);
                    rest();
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_Talent, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }

    private void rest() {
        getControlerForQuery().reset();
    }

    private void upLevel(int talentLevelId) {
        getControlerForQuery().upLevel(talentLevelId);
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
