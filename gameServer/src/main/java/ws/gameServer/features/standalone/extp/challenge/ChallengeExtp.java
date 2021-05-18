package ws.gameServer.features.standalone.extp.challenge;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.challenge.ctrl.ChallengeCtrl;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.ChallengeProtos.Cm_Challenge;
import ws.protos.ChallengeProtos.Sm_Challenge;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.topLevelPojos.challenge.Challenge;
import ws.relationship.utils.ProtoUtils;


public class ChallengeExtp extends AbstractPlayerExtension<ChallengeCtrl> {
    public static boolean useExtension = true;

    public ChallengeExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(ChallengeCtrl.class, Challenge.class);
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
        if (clientMsg instanceof Cm_Challenge) {
            Cm_Challenge cm = (Cm_Challenge) clientMsg;
            onCm_Challenge(cm);
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

    private void onCm_Challenge(Cm_Challenge cm) throws Exception {
        Sm_Challenge.Builder b = Sm_Challenge.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_Challenge.Action.SYNC_VALUE:
                    b.setAction(Sm_Challenge.Action.RESP_SYNC);
                    sync();
                    break;
                case Cm_Challenge.Action.BEGIN_VALUE:
                    b.setAction(Sm_Challenge.Action.RESP_BEGIN);
                    beginAttack(cm.getStageId());
                    break;
                case Cm_Challenge.Action.END_VALUE:
                    b.setAction(Sm_Challenge.Action.RESP_END);
                    endAttack(cm.getStageId(), cm.getFlag(), cm.getIsWin(), cm.getPercent(), cm.getScore());
                    break;
                case Cm_Challenge.Action.MOPUP_VALUE:
                    b.setAction(Sm_Challenge.Action.RESP_MOPUP);
                    mopup(cm.getStageId(), cm.getMopupTimes());
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_Challenge, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }


    private void mopup(int stageId, int mopupTimes) {
        getControlerForQuery().mopup(stageId, mopupTimes);
    }

    private void endAttack(int stageId, long flag, boolean isWin, int percent, int score) {
        getControlerForQuery().endAttack(stageId, flag, isWin, percent, score);
    }

    private void beginAttack(int stageId) {
        getControlerForQuery().beginAttack(stageId);
    }

    private void onPlayerLogined(In_PlayerLoginedRequest im_PlayerLogined) throws Exception {
        if (im_PlayerLogined.getPlayerId().equals(ownerCtrl.getTarget().getPlayerId())) {
            // 本玩家, 同步信息
            // sync();
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
