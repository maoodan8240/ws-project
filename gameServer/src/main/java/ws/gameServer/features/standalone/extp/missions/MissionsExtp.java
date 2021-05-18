package ws.gameServer.features.standalone.extp.missions;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_NotifyMsg;
import ws.gameServer.features.standalone.extp.dataCenter.msg.Pr_UpdateRegNotifyMsg;
import ws.gameServer.features.standalone.extp.missions.ctrl.MissionsCtrl;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.MissionsProtos.Cm_Missions;
import ws.protos.MissionsProtos.Cm_Missions.Action;
import ws.protos.MissionsProtos.Sm_Missions;
import ws.relationship.base.msg.In_BroadcastEachHour;
import ws.relationship.base.msg.In_BroadcastEachHour.Request;
import ws.relationship.topLevelPojos.mission.Missions;
import ws.relationship.utils.ProtoUtils;


public class MissionsExtp extends AbstractPlayerExtension<MissionsCtrl> {
    public static boolean useExtension = true;

    public MissionsExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(MissionsCtrl.class, Missions.class);
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
        if (clientMsg instanceof Cm_Missions) {
            Cm_Missions cm = (Cm_Missions) clientMsg;
            onCm_Example(cm);
        }
    }

    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg) throws Exception {
        if (innerMsg instanceof In_PlayerLoginedRequest) {
            onPlayerLogined((In_PlayerLoginedRequest) innerMsg);
        } else if (innerMsg instanceof In_DayChanged) {
            onDayChanged();
        } else if (innerMsg instanceof In_BroadcastEachHour.Request) {
            onBroadcastEachHour((In_BroadcastEachHour.Request) innerMsg);
        }
    }


    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg) throws Exception {
        if (privateMsg instanceof Pr_NotifyMsg) {
            onNotifyMsg((Pr_NotifyMsg) privateMsg);
        } else if (privateMsg instanceof Pr_UpdateRegNotifyMsg) {
            onUpdateRegNotifyMsg((Pr_UpdateRegNotifyMsg) privateMsg);
        }
    }


    private void onCm_Example(Cm_Missions cm) throws Exception {
        Sm_Missions.Builder b = Sm_Missions.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Action.SYNC_MISSION_VALUE:
                    b.setAction(Sm_Missions.Action.SYNC_MISSION);
                    syncMission(cm);
                    break;
                case Action.SYNC_ACHIEVE_VALUE:
                    b.setAction(Sm_Missions.Action.SYNC_ACHIEVE);
                    syncAchieve(cm);
                    break;
                case Action.GET_REWARDS_VALUE:
                    b.setAction(Sm_Missions.Action.RESP_GET_REWARDS);
                    getRewards(cm);
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_Missions, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }

    private void syncMission(Cm_Missions cm) {
        getControlerForQuery().syncMission();
    }

    private void syncAchieve(Cm_Missions cm) {
        getControlerForQuery().syncAchieve();
    }

    private void onPlayerLogined(In_PlayerLoginedRequest im_PlayerLogined) throws Exception {
        if (im_PlayerLogined.getPlayerId().equals(ownerCtrl.getTarget().getPlayerId())) {
            // 本玩家, 同步信息
            getControlerForQuery().syncMission();
        }
    }

    private void onDayChanged() throws Exception {

    }


    private void getRewards(Cm_Missions cm) {
        getControlerForQuery().getRewards(cm.getMissionType(), cm.getMid());
    }

    private void onUpdateRegNotifyMsg(Pr_UpdateRegNotifyMsg updateRegNotifyMsg) {
        getControlerForQuery().dealUpdateRegNotifyMsg(updateRegNotifyMsg);
    }

    private void onNotifyMsg(Pr_NotifyMsg notifyMsg) {
        getControlerForQuery().dealNotifyMsg(notifyMsg);
    }


    private void onBroadcastEachHour(Request innerMsg) {
        getControlerForQuery().onBroadcastEachHour(innerMsg.getHour());
    }

}
