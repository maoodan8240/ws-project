package ws.gameServer.features.standalone.extp.redPoint;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.redPoint.ctrl.RedPointCtrl;
import ws.gameServer.features.standalone.extp.redPoint.msg.Pr_CheckRedPointMsg;
import ws.gameServer.features.standalone.extp.redPoint.msg.Pr_NotifyRedPointMsg;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.RedPointProtos.Cm_RedPoint;
import ws.protos.RedPointProtos.Sm_RedPoint;
import ws.relationship.topLevelPojos.common.TopLevelHolder;
import ws.relationship.utils.ProtoUtils;


public class RedPointExtp extends AbstractPlayerExtension<RedPointCtrl> {
    public static boolean useExtension = true;

    public RedPointExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(RedPointCtrl.class, TopLevelHolder.class);
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
        if (clientMsg instanceof Cm_RedPoint) {
            Cm_RedPoint cm = (Cm_RedPoint) clientMsg;
            onCm_RedPoint(cm);
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
        if (privateMsg instanceof Pr_CheckRedPointMsg.Response) {
            onCheckRedPointMsg((Pr_CheckRedPointMsg.Response) privateMsg);
        } else if (privateMsg instanceof Pr_NotifyRedPointMsg.Request) {
            onNotifyRedPoint((Pr_NotifyRedPointMsg.Request) privateMsg);
        }
    }


    private void onCheckRedPointMsg(Pr_CheckRedPointMsg.Response privateMsg) {
        getControlerForQuery().checkRedPointMsg(privateMsg);
    }

    private void onCm_RedPoint(Cm_RedPoint cm) throws Exception {
        Sm_RedPoint.Builder b = Sm_RedPoint.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_RedPoint.Action.SYNC_VALUE:
                    b.setAction(Sm_RedPoint.Action.RESP_SYNC);
                    sync();
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_RedPoint, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
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

    private void onNotifyRedPoint(Pr_NotifyRedPointMsg.Request privateMsg) {
        getControlerForQuery().notifyRedPoint(privateMsg);
    }


}
