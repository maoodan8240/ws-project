package ws.gameServer.features.standalone.extp.piecemeal;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.piecemeal.ctrl.PiecemealCtrl;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.CommonRankProtos.Cm_CommonRank;
import ws.protos.CommonRankProtos.Sm_CommonRank;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PiecemealProtos.Cm_Piecemeal;
import ws.protos.PiecemealProtos.Cm_Piecemeal.Action;
import ws.protos.PiecemealProtos.Sm_Piecemeal;
import ws.relationship.base.msg.In_PlayerHeartBeatingRequest;
import ws.relationship.topLevelPojos.piecemeal.Piecemeal;
import ws.relationship.utils.ProtoUtils;


/**
 * 零碎的小功能
 */
public class PiecemealExtp extends AbstractPlayerExtension<PiecemealCtrl> {
    public static boolean useExtension = true;

    public PiecemealExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(PiecemealCtrl.class, Piecemeal.class);
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
        if (clientMsg instanceof Cm_Piecemeal) {
            Cm_Piecemeal cm = (Cm_Piecemeal) clientMsg;
            onCm_Piecemeal(cm);
        } else if (clientMsg instanceof Cm_CommonRank) {
            Cm_CommonRank cm = (Cm_CommonRank) clientMsg;
            onCm_CommonRank(cm);
        }
    }

    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg) throws Exception {
        if (innerMsg instanceof In_PlayerLoginedRequest) {
            onPlayerLogined((In_PlayerLoginedRequest) innerMsg);
        } else if (innerMsg instanceof In_DayChanged) {
            onDayChanged();
        } else if (innerMsg instanceof In_PlayerHeartBeatingRequest) {
            onIn_PlayerHeartBeatingRequest();
        }
    }


    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg) throws Exception {
    }

    private void onCm_Piecemeal(Cm_Piecemeal cm) throws Exception {
        Sm_Piecemeal.Builder b = Sm_Piecemeal.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_Piecemeal.Action.SYNC_VALUE:
                    b.setAction(Sm_Piecemeal.Action.RESP_SYNC);
                    sync();
                    break;
                case Action.COMPOSITE_VALUE:
                    b.setAction(Sm_Piecemeal.Action.RESP_COMPOSITE);
                    composite(cm);
                    break;
                case Action.BUY_ITEM_VALUE:
                    b.setAction(Sm_Piecemeal.Action.RESP_BUY_ITEM);
                    buyItem(cm);
                    break;
                case Action.SET_GUIDE_VALUE:
                    b.setAction(Sm_Piecemeal.Action.RESP_SET_GUIDE);
                    setGuide(cm);
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_Piecemeal, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }

    private void setGuide(Cm_Piecemeal cm) {
        getControlerForQuery().setGuide(cm.getMaxGuideId());
    }

    private void onCm_CommonRank(Cm_CommonRank cm) throws Exception {
        Sm_CommonRank.Builder b = Sm_CommonRank.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_CommonRank.Action.QUERY_RANK_VALUE:
                    b.setAction(Sm_CommonRank.Action.RESP_QUERY_RANK);
                    queryRank(cm);
                    break;
                case Cm_CommonRank.Action.SELF_RANK_VALUE:
                    b.setAction(Sm_CommonRank.Action.RESP_SELF_RANK);
                    selfRank(cm);
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_CommonRank, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }


    private void onPlayerLogined(In_PlayerLoginedRequest im_PlayerLogined) throws Exception {
        if (im_PlayerLogined.getPlayerId().equals(ownerCtrl.getTarget().getPlayerId())) {
            // 本玩家, 同步信息
            getControlerForQuery().onPlayerLogined();
        }
    }

    private void onDayChanged() throws Exception {
        getControlerForQuery()._resetDataAtDayChanged();
        getControlerForQuery().sync();
    }

    private void sync() {
        getControlerForQuery().sync();
    }

    private void composite(Cm_Piecemeal cm) {
        getControlerForQuery().composite(cm.getItemTpId());
    }

    private void buyItem(Cm_Piecemeal cm) {
        getControlerForQuery().buyItem(cm.getItemTpId(), cm.getCount());
    }

    private void queryRank(Cm_CommonRank cm) {
        getControlerForQuery().queryRank(cm.getRankTypeEnum(), cm.getRankStart(), cm.getRankCount());
    }

    private void selfRank(Cm_CommonRank cm) {
        getControlerForQuery().selfRank(cm.getRankTypeEnum());
    }

    private void onIn_PlayerHeartBeatingRequest() {
        getControlerForQuery().onPlayerHeartBeating();
    }
}
