package ws.gameServer.features.standalone.extp.pyActivities.soulBox;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.pyActivities.soulBox.ctrl.SoulBoxCtrl;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PyActivitySoulBoxProtos.Cm_PyActivity_SoulBox;
import ws.protos.PyActivitySoulBoxProtos.Sm_PyActivity_SoulBox;
import ws.relationship.base.MagicNumbers;
import ws.relationship.topLevelPojos.soulBox.SoulBox;
import ws.relationship.utils.ProtoUtils;


public class SoulBoxExtp extends AbstractPlayerExtension<SoulBoxCtrl> {
    public static boolean useExtension = true;

    public SoulBoxExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(SoulBoxCtrl.class, SoulBox.class);
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
        if (clientMsg instanceof Cm_PyActivity_SoulBox) {
            Cm_PyActivity_SoulBox cm = (Cm_PyActivity_SoulBox) clientMsg;
            onCm_PyActivity_SoulBox(cm);
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

    private void onCm_PyActivity_SoulBox(Cm_PyActivity_SoulBox cm) throws Exception {
        Sm_PyActivity_SoulBox.Builder b = Sm_PyActivity_SoulBox.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_PyActivity_SoulBox.Action.SYNC_VALUE:
                    b.setAction(Sm_PyActivity_SoulBox.Action.RESP_SYNC);
                    sync();
                    break;
                case Cm_PyActivity_SoulBox.Action.PICK_VALUE:
                    b.setAction(Sm_PyActivity_SoulBox.Action.RESP_PICK);
                    pick(MagicNumbers.SOULBOX_ONE_PICK_CARD, cm.getPickId());
                    break;
                case Cm_PyActivity_SoulBox.Action.PICK_FIVE_VALUE:
                    b.setAction(Sm_PyActivity_SoulBox.Action.RESP_PICK_FIVE);
                    pick(MagicNumbers.SOULBOX_FIVE_PICK_CARD, cm.getPickId());
                    break;
                case Cm_PyActivity_SoulBox.Action.SELECT_VALUE:
                    b.setAction(Sm_PyActivity_SoulBox.Action.RESP_SELECT);
                    select(cm.getHeroId());
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_PyActivity_SoulBox, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }

    private void select(int heroId) {
        getControlerForQuery().select(heroId);
    }

    private void pick(int pickTimes, int pickId) {
        getControlerForQuery().pick(pickTimes, pickId);
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
