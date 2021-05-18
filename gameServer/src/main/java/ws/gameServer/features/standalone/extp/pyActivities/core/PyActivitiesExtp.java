package ws.gameServer.features.standalone.extp.pyActivities.core;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_UpdateRegNotifyMsg;
import ws.gameServer.features.standalone.extp.pyActivities.core.ctrl.PyActivitiesCtrl;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PyActivitiesProtos.Cm_PyActivities;
import ws.protos.PyActivitiesProtos.Cm_PyActivities.Action;
import ws.protos.PyActivitiesProtos.Sm_PyActivities;
import ws.relationship.topLevelPojos.pyActivities.PyActivities;
import ws.relationship.utils.ProtoUtils;


public class PyActivitiesExtp extends AbstractPlayerExtension<PyActivitiesCtrl> {
    public static boolean useExtension = true;

    public PyActivitiesExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(PyActivitiesCtrl.class, PyActivities.class);
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
        if (clientMsg instanceof Cm_PyActivities) {
            Cm_PyActivities cm = (Cm_PyActivities) clientMsg;
            onCm_PyActivities(cm);
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
        if (privateMsg instanceof Pr_UpdateRegNotifyMsg) {
            onUpdateRegNotifyMsg((Pr_UpdateRegNotifyMsg) privateMsg);
        }
    }

    private void onCm_PyActivities(Cm_PyActivities cm) throws Exception {
        Sm_PyActivities.Builder b = Sm_PyActivities.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_PyActivities.Action.SYNC_VALUE:
                    b.setAction(Sm_PyActivities.Action.RESP_SYNC);
                    sync();
                    break;
                case Action.GET_REWARDS_VALUE:
                    b.setAction(Sm_PyActivities.Action.RESP_GET_REWARDS);
                    getRewards(cm);
                    break;
                case Action.EXCHANGE_VALUE:
                    b.setAction(Sm_PyActivities.Action.RESP_EXCHANGE);
                    exchange(cm);
                    break;
                case Action.BUY_FUND_VALUE:
                    b.setAction(Sm_PyActivities.Action.RESP_BUY_FUND);
                    buyFund(cm);
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_PyActivities, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }


    private void onUpdateRegNotifyMsg(Pr_UpdateRegNotifyMsg updateRegNotifyMsg) {
        getControlerForQuery().onUpdateRegNotifyMsg(updateRegNotifyMsg.getType());
    }


    private void onPlayerLogined(In_PlayerLoginedRequest im_PlayerLogined) throws Exception {
        if (im_PlayerLogined.getPlayerId().equals(ownerCtrl.getTarget().getPlayerId())) {
            // 本玩家, 同步信息
            getControlerForQuery().onPlayerLogined();
        }
    }

    private void onDayChanged() throws Exception {
        getControlerForQuery()._resetDataAtDayChanged();
        getControlerForQuery().onPlayerLogined();
    }

    private void sync() {
        getControlerForQuery().sync();
    }

    private void getRewards(Cm_PyActivities cm) {
        getControlerForQuery().getRewards(cm.getRealmAcId(), cm.getSubAcId());
    }

    private void buyFund(Cm_PyActivities cm) {
        getControlerForQuery().buyFund();
    }

    private void exchange(Cm_PyActivities cm) {
    }
}
