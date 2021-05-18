package ws.gameServer.features.standalone.extp.payment;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.payment.ctrl.PaymentCtrl;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PaymentProtos.Cm_Payment;
import ws.protos.PaymentProtos.Cm_Payment.Action;
import ws.protos.PaymentProtos.Sm_Payment;
import ws.relationship.topLevelPojos.payment.Payment;
import ws.relationship.utils.ProtoUtils;


public class PaymentExtp extends AbstractPlayerExtension<PaymentCtrl> {
    public static boolean useExtension = true;

    public PaymentExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(PaymentCtrl.class, Payment.class);
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
        if (clientMsg instanceof Cm_Payment) {
            Cm_Payment cm = (Cm_Payment) clientMsg;
            onCm_Payment(cm);
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

    private void onCm_Payment(Cm_Payment cm) throws Exception {
        Sm_Payment.Builder b = Sm_Payment.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_Payment.Action.SYNC_VALUE:
                    b.setAction(Sm_Payment.Action.RESP_SYNC);
                    sync();
                    break;
                case Action.SYNC_ORDER_VALUE:
                    b.setAction(Sm_Payment.Action.RESP_SYNC_ORDER);
                    syncOrder(cm);
                    break;
                case Action.CREATE_VALUE:
                    b.setAction(Sm_Payment.Action.RESP_CREATE);
                    create(cm);
                    break;
                case Action.BUY_VIP_GIFT_VALUE:
                    b.setAction(Sm_Payment.Action.RESP_BUY_VIP_GIFT);
                    buyVipGift(cm);
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_Payment, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }

    private void buyVipGift(Cm_Payment cm) {
        getControlerForQuery().buyVipGift(cm.getVipLv());
    }

    private void create(Cm_Payment cm) {
        getControlerForQuery().create(cm.getGoodId(), cm.getArgs());
    }

    private void syncOrder(Cm_Payment cm) {
        getControlerForQuery().syncOrder(cm.getOrder());
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
