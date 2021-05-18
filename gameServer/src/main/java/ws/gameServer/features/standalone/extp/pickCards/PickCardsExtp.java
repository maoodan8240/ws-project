package ws.gameServer.features.standalone.extp.pickCards;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.pickCards.ctrl.PickCardsCtrl;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PickCardsProtos.Cm_PickCards;
import ws.protos.PickCardsProtos.Cm_PickCards.Action;
import ws.protos.PickCardsProtos.Sm_PickCards;
import ws.relationship.topLevelPojos.pickCards.PickCards;
import ws.relationship.utils.ProtoUtils;


public class PickCardsExtp extends AbstractPlayerExtension<PickCardsCtrl> {
    public static boolean useExtension = true;

    public PickCardsExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(PickCardsCtrl.class, PickCards.class);
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
        if (clientMsg instanceof Cm_PickCards) {
            Cm_PickCards cm = (Cm_PickCards) clientMsg;
            onCm_PickCards(cm);
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

    private void onCm_PickCards(Cm_PickCards cm) throws Exception {
        Sm_PickCards.Builder b = Sm_PickCards.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Action.SYNC_VALUE:
                    b.setAction(Sm_PickCards.Action.RESP_SYNC);
                    sync();
                    break;
                case Action.FREE_PICK_VALUE:
                    b.setAction(Sm_PickCards.Action.RESP_FREE_PICK);
                    freePick(cm);
                    break;
                case Action.PICK_VALUE:
                    b.setAction(Sm_PickCards.Action.RESP_PICK);
                    pick(cm);
                    break;
                case Action.TEN_PICK_VALUE:
                    b.setAction(Sm_PickCards.Action.RESP_TEN_PICK);
                    tenPick(cm);
                    break;
                case Action.HUNDRED_PICK_VALUE:
                    b.setAction(Sm_PickCards.Action.RESP_HUNDRED_PICK);
                    hundredPick(cm);
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_PickCards, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }


    private void onPlayerLogined(In_PlayerLoginedRequest im_PlayerLogined) throws Exception {
        if (im_PlayerLogined.getPlayerId().equals(ownerCtrl.getTarget().getPlayerId())) {
        }
    }

    private void onDayChanged() throws Exception {
        getControlerForQuery()._resetDataAtDayChanged();
        getControlerForQuery().sync();
    }

    private void sync() {
        getControlerForQuery().sync();
    }


    private void freePick(Cm_PickCards cm) {
        getControlerForQuery().freePick(cm.getPickId());
    }

    private void pick(Cm_PickCards cm) {
        getControlerForQuery().pick(cm.getPickId());
    }

    private void tenPick(Cm_PickCards cm) {
        getControlerForQuery().tenPick(cm.getPickId());
    }

    private void hundredPick(Cm_PickCards cm) {
        getControlerForQuery().hundredPick(cm.getPickId());
    }


}
