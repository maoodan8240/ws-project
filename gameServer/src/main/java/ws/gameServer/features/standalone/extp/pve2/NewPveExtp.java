package ws.gameServer.features.standalone.extp.pve2;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.pve2.ctrl.NewPveCtrl;
import ws.gameServer.features.standalone.extp.pve2.gm.NewPveExtpGmSupport;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.NewPveProtos.Cm_NewPve;
import ws.protos.NewPveProtos.Sm_NewPve;
import ws.relationship.gm.GmCommandGroupNameConstants;
import ws.relationship.gm.In_GmCommand;
import ws.relationship.topLevelPojos.newPve.NewPve;
import ws.relationship.utils.ProtoUtils;


public class NewPveExtp extends AbstractPlayerExtension<NewPveCtrl> {
    public static boolean useExtension = true;
    private NewPveExtpGmSupport pveExtpGmSupport;

    public NewPveExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(NewPveCtrl.class, NewPve.class);
        pveExtpGmSupport = new NewPveExtpGmSupport(getControlerForQuery());
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
        if (clientMsg instanceof Cm_NewPve) {
            Cm_NewPve cm = (Cm_NewPve) clientMsg;
            onCm_NewPve(cm);
        }
    }

    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg) throws Exception {
        if (innerMsg instanceof In_PlayerLoginedRequest) {
            onPlayerLogined((In_PlayerLoginedRequest) innerMsg);
        } else if (innerMsg instanceof In_DayChanged) {
            onDayChanged();
        } else if (innerMsg instanceof In_GmCommand.Request) {
            onIm_GmCommand((In_GmCommand.Request) innerMsg);
        }
    }

    private void onIm_GmCommand(In_GmCommand.Request im_GmCommand) {
        if (im_GmCommand.getGroupName().equals(GmCommandGroupNameConstants.Pve)) {
            pveExtpGmSupport.exec(im_GmCommand.getFromType(), im_GmCommand.getCommandName(), im_GmCommand.getArgs());
        }
    }

    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg) throws Exception {
    }

    private void onCm_NewPve(Cm_NewPve cm) throws Exception {
        Sm_NewPve.Builder b = Sm_NewPve.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_NewPve.Action.RESET_ATTACK_TIMES_VALUE:
                    b.setAction(Sm_NewPve.Action.RESP_RESET_ATTACK_TIMES);
                    resetStageAttackTimes(cm.getStageId());
                    break;
                case Cm_NewPve.Action.SYNC_VALUE:
                    b.setAction(Sm_NewPve.Action.RESP_SYNC);
                    sync();
                    break;
                case Cm_NewPve.Action.GET_REWARDS_VALUE:
                    b.setAction(Sm_NewPve.Action.RESP_GET_REWARDS);
                    getStarLevelRewards(cm.getChapterId(), cm.getBoxId());
                    break;
                case Cm_NewPve.Action.MOPUP_PVE_VALUE:
                    b.setAction(Sm_NewPve.Action.RESP_MOPUP_PVE);
                    mopUpPve(cm.getStageId(), cm.getMopUpTimes());
                    break;
                case Cm_NewPve.Action.END_PVE_VALUE:
                    b.setAction(Sm_NewPve.Action.RESP_END_PVE);
                    endAttack(cm.getStageId(), cm.getFlag(), cm.getIsWin(), cm.getStar());
                    break;
                case Cm_NewPve.Action.BEGIN_PVE_VALUE:
                    b.setAction(Sm_NewPve.Action.RESP_BEGIN_PVE);
                    beginAttack(cm.getStageId());
                    break;
                case Cm_NewPve.Action.GET_BOX_VALUE:
                    b.setAction(Sm_NewPve.Action.RESP_GET_BOX);
                    getBox(cm.getStageId());
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_NewPve, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }

    private void getBox(int stageId) {
        getControlerForQuery().getBox(stageId);
    }

    private void beginAttack(int stageId) {
        getControlerForQuery().beginAttackOnePve(stageId);
    }

    private void endAttack(int stageId, long flag, boolean isWin, int star) {
        getControlerForQuery().endAttackOnePve(stageId, flag, isWin, star);
    }

    private void getStarLevelRewards(int chapterId, int boxId) {
        getControlerForQuery().getStarLevelRewards(chapterId, boxId);
    }

    private void mopUpPve(int stageId, int mopUpTimes) {
        getControlerForQuery().mopUp(stageId, mopUpTimes);
    }

    private void resetStageAttackTimes(int stageId) {
        getControlerForQuery().resetStageAttackTimes(stageId);
    }


    private void onPlayerLogined(In_PlayerLoginedRequest im_PlayerLogined) throws Exception {
        if (im_PlayerLogined.getPlayerId().equals(ownerCtrl.getTarget().getPlayerId())) {
            // 本玩家, 同步信息

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
