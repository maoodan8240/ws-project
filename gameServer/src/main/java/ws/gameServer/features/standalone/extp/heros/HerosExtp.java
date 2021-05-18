package ws.gameServer.features.standalone.extp.heros;

import akka.actor.ActorRef;
import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.gameServer.features.standalone.extp.heros.gm.HerosGmSupport;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.newBattle.NewBattleHeroContainer;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.HerosProtos.Cm_Heros;
import ws.protos.HerosProtos.Cm_Heros.Action;
import ws.protos.HerosProtos.Sm_Heros;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.base.msg.In_BroadcastEachHour;
import ws.relationship.base.msg.In_BroadcastEachHour.Request;
import ws.relationship.base.msg.heros.In_QueryBattleHeroContainerInFormation;
import ws.relationship.gm.GmCommandGroupNameConstants;
import ws.relationship.gm.In_GmCommand;
import ws.relationship.topLevelPojos.heros.Heros;
import ws.relationship.utils.ProtoUtils;

public class HerosExtp extends AbstractPlayerExtension<HerosCtrl> {
    public static boolean useExtension = true;
    private HerosGmSupport herosGmSupport;

    public HerosExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(HerosCtrl.class, Heros.class);
        herosGmSupport = new HerosGmSupport(getControlerForQuery());
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
        if (clientMsg instanceof Cm_Heros) {
            Cm_Heros cm = (Cm_Heros) clientMsg;
            onCm_Heros(cm);
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
        } else if (innerMsg instanceof In_QueryBattleHeroContainerInFormation.Request) {
            onQueryBattleHeroContainerInFormation((In_QueryBattleHeroContainerInFormation.Request) innerMsg);
        } else if (innerMsg instanceof In_GmCommand.Request) {
            onIm_GmCommand((In_GmCommand.Request) innerMsg);
        }
    }


    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg) throws Exception {
    }


    private void onCm_Heros(Cm_Heros cm) throws Exception {
        Sm_Heros.Builder b = Sm_Heros.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Action.SYNC_VALUE:
                    b.setAction(Sm_Heros.Action.RESP_SYNC);
                    sync();
                    break;
                case Action.UP_LV_VALUE:
                    b.setAction(Sm_Heros.Action.RESP_UP_LV);
                    upgradeLevel(cm);
                    break;
                case Action.UP_QUALITY_LV_VALUE:
                    b.setAction(Sm_Heros.Action.RESP_UP_QUALITY_LV);
                    upgradeQualityLevel(cm);
                    break;
                case Action.UP_STAR_LV_VALUE:
                    b.setAction(Sm_Heros.Action.RESP_UP_STAR_LV);
                    upgradeStarLevel(cm);
                    break;
                case Action.UP_SKILL_LV_VALUE:
                    b.setAction(Sm_Heros.Action.RESP_UP_SKILL_LV);
                    upgradeSkillLevel(cm);
                    break;
                case Action.UP_SOUL_LV_VALUE:
                    b.setAction(Sm_Heros.Action.RESP_UP_SOUL_LV);
                    upgradeFightingSpiritLevel(cm);
                    break;
                case Action.VIPMONEY_UP_SOUL_LV_VALUE:
                    b.setAction(Sm_Heros.Action.RESP_VIPMONEY_UP_SOUL_LV);
                    vipMoneyUpgradeFightingSpiritLevel(cm);
                    break;
                case Action.USE_UNIVERSAL_FRAG_VALUE:
                    b.setAction(Sm_Heros.Action.RESP_USE_UNIVERSAL_FRAG);
                    useUniversalFragments(cm);
                    break;
                case Action.BUY_SKILL_POINT_VALUE:
                    b.setAction(Sm_Heros.Action.RESP_BUY_SKILL_POINT);
                    buySkillPoint(cm);
                    break;
            }
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_Heros, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }


    private void onIm_GmCommand(In_GmCommand.Request im_GmCommand) {
        if (im_GmCommand.getGroupName().equals(GmCommandGroupNameConstants.Heros)) {
            herosGmSupport.exec(im_GmCommand.getFromType(), im_GmCommand.getCommandName(), im_GmCommand.getArgs());
        }
    }


    private void onPlayerLogined(In_PlayerLoginedRequest im_PlayerLogined) {
        if (im_PlayerLogined.getPlayerId().equals(ownerCtrl.getTarget().getPlayerId())) {
            // 本玩家, 同步信息
            getControlerForQuery().sync();
        }
    }

    private void onDayChanged() throws Exception {
        getControlerForQuery()._resetDataAtDayChanged();
    }

    private void sync() {
        getControlerForQuery().sync();
    }

    private void upgradeSkillLevel(Cm_Heros cm) {
        getControlerForQuery().upgradeSkillLevel(cm.getHeroId(), cm.getSkillUpgradeInfosList());
    }

    private void upgradeFightingSpiritLevel(Cm_Heros cm) {
        getControlerForQuery().upgradeWarSoulLevel(cm.getHeroId(), cm.getSoulPos(), ProtoUtils.parseSm_Common_IdMaptoCount(cm.getConsumeTpIds()));
    }

    private void vipMoneyUpgradeFightingSpiritLevel(Cm_Heros cm) {
        getControlerForQuery().vipMoneyUpgradeFightingSpiritLevel(cm.getHeroId(), cm.getSoulPos());
    }

    private void upgradeStarLevel(Cm_Heros cm) {
        getControlerForQuery().upgradeStarLevel(cm.getHeroId());
    }

    private void upgradeQualityLevel(Cm_Heros cm) {
        getControlerForQuery().upgradeQualityLevel(cm.getHeroId());
    }

    private void upgradeLevel(Cm_Heros cm) {
        getControlerForQuery().upgradeLevel(cm.getHeroId(), ProtoUtils.parseSm_Common_IdMaptoCount(cm.getConsumeTpIds()));
    }

    private void useUniversalFragments(Cm_Heros cm) {
        getControlerForQuery().useUniversalFragments(cm.getHeroId(), cm.getNum());
    }

    private void onBroadcastEachHour(Request innerMsg) {
        getControlerForQuery().onBroadcastEachHour(innerMsg.getHour());
    }

    private void onQueryBattleHeroContainerInFormation(In_QueryBattleHeroContainerInFormation.Request request) {
        NewBattleHeroContainer container = getControlerForQuery().onQueryBattleHeroContainerInFormation(request.getType());
        In_QueryBattleHeroContainerInFormation.Response response = new In_QueryBattleHeroContainerInFormation.Response(container);
        getControlerForQuery().getPlayerCtrl().getCurSendActorRef().tell(response, ActorRef.noSender());
    }

    private void buySkillPoint(Cm_Heros cm) {
        getControlerForQuery().buySkillPoint();
    }
}
