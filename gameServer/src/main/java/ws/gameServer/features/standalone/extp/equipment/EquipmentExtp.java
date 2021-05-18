package ws.gameServer.features.standalone.extp.equipment;

import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.equipment.ctrl.EquipmentCtrl;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.EquipmentProtos.Cm_Equipment;
import ws.protos.EquipmentProtos.Cm_Equipment.Action;
import ws.protos.EquipmentProtos.Sm_Equipment;
import ws.protos.MessageHandlerProtos;
import ws.relationship.topLevelPojos.common.TopLevelHolder;
import ws.relationship.utils.ProtoUtils;

/**
 * 装备系统
 */
public class EquipmentExtp extends AbstractPlayerExtension<EquipmentCtrl> {
    public static boolean useExtension = true;

    public EquipmentExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(EquipmentCtrl.class, TopLevelHolder.class);
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
        if (clientMsg instanceof Cm_Equipment) {
            onCm_Equipment((Cm_Equipment) clientMsg);
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

    private void onCm_Equipment(Cm_Equipment cm) throws Exception {
        Sm_Equipment.Builder b = Sm_Equipment.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Action.SYNC_VALUE:
                    b.setAction(Sm_Equipment.Action.RESP_SYNC);
                    sync();
                    break;
                case Action.UP_ABCD_ONE_LV_VALUE:
                    b.setAction(Sm_Equipment.Action.RESP_UP_ABCD_ONE_LV);
                    up_ABCD_OneLv(cm);
                    break;
                case Action.UP_ABCD_ONE_KEY_LV_VALUE:
                    b.setAction(Sm_Equipment.Action.RESP_UP_ABCD_ONE_KEY_LV);
                    up_ABCD_OneKeyLv(cm);
                    break;
                case Action.UP_ABCD_FAST_LV_VALUE:
                    b.setAction(Sm_Equipment.Action.RESP_UP_ABCD_FAST_LV);
                    up_ABCD_FastLv(cm);
                    break;

                case Action.UP_EF_SIMPLE_LV_VALUE:
                    b.setAction(Sm_Equipment.Action.RESP_UP_EF_SIMPLE_LV);
                    up_EF_SimpleLv(cm);
                    break;
                case Action.UP_EF_FAST_LV_VALUE:
                    b.setAction(Sm_Equipment.Action.RESP_UP_EF_FAST_LV);
                    up_EF_FastLv(cm);
                    break;

                case Action.UP_QUALITY_VALUE:
                    b.setAction(Sm_Equipment.Action.RESP_UP_QUALITY);
                    upgradeQuality(cm);
                    break;
                case Action.UP_STAR_VALUE:
                    b.setAction(Sm_Equipment.Action.RESP_UP_STAR);
                    upgradeStar(cm);
                    break;
                case Action.REDUCE_STAR_VALUE:
                    b.setAction(Sm_Equipment.Action.RESP_REDUCE_STAR);
                    reduceStar(cm);
                    break;

            }
        } catch (Exception e) {
            MessageHandlerProtos.Response.Builder br = ProtoUtils.create_Response(Code.Sm_Equipment, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
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


    private void reduceStar(Cm_Equipment cm) {
        getControlerForQuery().reduceStar(cm.getHeroId(), cm.getEquipPos());
    }

    private void upgradeStar(Cm_Equipment cm) {
        getControlerForQuery().upgradeStar(cm.getHeroId(), cm.getEquipPos());
    }

    private void upgradeQuality(Cm_Equipment cm) {
        getControlerForQuery().upgradeQuality(cm.getHeroId(), cm.getEquipPos());
    }

    private void up_EF_FastLv(Cm_Equipment cm) {
        getControlerForQuery().up_EF_FastLv(cm.getHeroId(), cm.getEquipPos(), cm.getFastToLv());
    }

    private void up_EF_SimpleLv(Cm_Equipment cm) {
        getControlerForQuery().up_EF_SimpleLv(cm.getHeroId(), cm.getEquipPos(), ProtoUtils.parseSm_Common_IdMaptoCount(cm.getConsumeTpIds()));
    }

    private void up_ABCD_FastLv(Cm_Equipment cm) {
        getControlerForQuery().up_ABCD_FastLv(cm.getHeroId(), cm.getEquipPos(), cm.getFastToLv());
    }

    private void up_ABCD_OneKeyLv(Cm_Equipment cm) {
        getControlerForQuery().up_ABCD_OneKeyLv(cm.getHeroId(), cm.getEquipPos());
    }

    private void up_ABCD_OneLv(Cm_Equipment cm) {
        getControlerForQuery().up_ABCD_OneLv(cm.getHeroId(), cm.getEquipPos());
    }

}
