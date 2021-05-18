package ws.gameServer.features.standalone.extp.energyRole;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.player.msg.Pr_PlayerLvChanged;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.energyRole.ctrl.EnergyRoleCtrl;
import ws.gameServer.features.standalone.extp.resourcePoint.msg.Pr_EnergyValueChanged;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.EnergyRoleProtos.Cm_EnergyRole;
import ws.protos.EnergyRoleProtos.Cm_EnergyRole.Action;
import ws.protos.EnergyRoleProtos.Sm_EnergyRole;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.topLevelPojos.energyRole.EnergyRole;
import ws.relationship.utils.ProtoUtils;


/**
 * 体力规则
 */
public class EnergyRoleExtp extends AbstractPlayerExtension<EnergyRoleCtrl> {
    public static boolean useExtension = true;

    public EnergyRoleExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(EnergyRoleCtrl.class, EnergyRole.class);
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
        if (clientMsg instanceof Cm_EnergyRole) {
            Cm_EnergyRole cm = (Cm_EnergyRole) clientMsg;
            onCm_EnergyRole(cm);
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
        if (privateMsg instanceof Pr_EnergyValueChanged) {

        } else if (privateMsg instanceof Pr_PlayerLvChanged) {
            getControlerForQuery().setMaxEnergyValueWhenPlayerLvUp();
        }
    }

    private void onEnergyValueChanged(Pr_EnergyValueChanged changed) {
        getControlerForQuery().onEnergyValueChanged(changed.getBefore(), changed.getNow());
    }

    private void onCm_EnergyRole(Cm_EnergyRole cm) throws Exception {
        Sm_EnergyRole.Builder b = Sm_EnergyRole.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_EnergyRole.Action.SYNC_VALUE:
                    b.setAction(Sm_EnergyRole.Action.RESP_SYNC);
                    sync();
                    break;
                case Cm_EnergyRole.Action.BUY_ENERGY_VALUE:
                    b.setAction(Sm_EnergyRole.Action.RESP_BUY_ENERGY);
                    buyEnergy(cm);
                    break;
                case Action.SETTLE_ENERGY_VALUE:
                    b.setAction(Sm_EnergyRole.Action.RESP_SETTLE_ENERGY);
                    settleEnergy(cm);
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_EnergyRole, b.getAction());
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

    private void buyEnergy(Cm_EnergyRole cm) {
        getControlerForQuery().buyEnergy();
    }

    private void settleEnergy(Cm_EnergyRole cm) {
        getControlerForQuery().settleAutoIncrease();
    }

}
