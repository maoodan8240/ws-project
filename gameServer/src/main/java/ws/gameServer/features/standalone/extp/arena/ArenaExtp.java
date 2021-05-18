package ws.gameServer.features.standalone.extp.arena;

import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.extp.arena.ctrl.ArenaCtrl;
import ws.gameServer.features.standalone.extp.arena.msg.In_AddNewBattleRecords;
import ws.protos.CodesProtos;
import ws.protos.MessageHandlerProtos;
import ws.protos.PvpProtos.Cm_Pvp;
import ws.protos.PvpProtos.Sm_Pvp;
import ws.protos.PvpProtos.Sm_Pvp.Action;
import ws.relationship.base.msg.pvp.In_TellPvpResultToPlayerMsg;
import ws.relationship.topLevelPojos.pvp.arena.Arena;
import ws.relationship.utils.ProtoUtils;

public class ArenaExtp extends AbstractPlayerExtension<ArenaCtrl> {
    public static boolean useExtension = true;

    public ArenaExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(ArenaCtrl.class, Arena.class);
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
        if (clientMsg instanceof Cm_Pvp) {
            onCm_Pvp((Cm_Pvp) clientMsg);
        }
    }

    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg) throws Exception {
        if (innerMsg instanceof In_TellPvpResultToPlayerMsg) {

        } else if (innerMsg instanceof In_AddNewBattleRecords.Request) {
            onAddNewBattleRecords((In_AddNewBattleRecords.Request) innerMsg);
        }
    }


    private void onCm_Pvp(Cm_Pvp cm) throws Exception {
        Sm_Pvp.Builder b = Sm_Pvp.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_Pvp.Action.SYNC_VALUE:
                    b.setAction(Sm_Pvp.Action.RESP_SYNC);
                    sync();
                    break;
                case Cm_Pvp.Action.BUY_TIMES_VALUE:
                    b.setAction(Action.RESP_BUY_TIMES);
                    buyAttackTimes();
                    break;
                case Cm_Pvp.Action.REFRESH_VALUE:
                    b.setAction(Action.RESP_REFRESH);
                    refresh();
                    break;
                case Cm_Pvp.Action.CHALLENGE_VALUE:
                    b.setAction(Action.RESP_CHALLENGE);
                    challenge(cm.getRank(), cm.getPlayerId());
                    break;
                case Cm_Pvp.Action.CLEAR_CD_VALUE:
                    b.setAction(Action.RESP_CLEAR_CD);
                    clearCd();
                    break;
                case Cm_Pvp.Action.GET_INTEGRAL_REWARDS_VALUE:
                    b.setAction(Action.RESP_GET_INTEGRAL_REWARDS);
                    getIntegralRewards(cm.getIntegralRewards());
                    break;
                case Cm_Pvp.Action.GET_RANK_REWARDS_VALUE:
                    b.setAction(Action.RESP_GET_RANK_REWARDS);
                    getRankRewards(cm.getRankRewards());
                    break;
                case Cm_Pvp.Action.MODIFY_DECLARATION_VALUE:
                    b.setAction(Action.RESP_MODIFY_DECLARATION);
                    modifyDeclaration(cm.getDeclaration());
                    break;
                case Cm_Pvp.Action.MODIFY_PVP_ICON_VALUE:
                    b.setAction(Action.RESP_MODIFY_PVP_ICON);
                    modifyPvpIcon(cm.getPvpIcon());
                    break;
                case Cm_Pvp.Action.MAX_WORSHIP_VALUE:
                    b.setAction(Action.RESP_MAX_WORSHIP);
                    maxWorship();
                    break;
                case Cm_Pvp.Action.MAX_GET_INTEGRAL_REWARDS_VALUE:
                    b.setAction(Action.RESP_MAX_GET_INTEGRAL_REWARDS);
                    maxGetIntegralRewards();
                    break;
                case Cm_Pvp.Action.WORSHIP_VALUE:
                    b.setAction(Action.RESP_WORSHIP);
                    worship(cm.getPlayerId());
                    break;
                case Cm_Pvp.Action.QUERY_RANK_VALUE:
                    b.setAction(Action.RESP_QUERY_RANK);
                    queryRank();
                    break;
                case Cm_Pvp.Action.GET_BATTLE_RECORDS_VALUE:
                    b.setAction(Action.RESP_GET_BATTLE_RECORDS);
                    getBattleRecords();
                    break;
                case Cm_Pvp.Action.DISPLAY_BATTLE_RECORDS_VALUE:
                    b.setAction(Action.RESP_DISPLAY_BATTLE_RECORDS);
                    displayBattleRecords(cm.getRecordId());
                    break;
            }
        } catch (Exception e) {
            MessageHandlerProtos.Response.Builder br = ProtoUtils.create_Response(CodesProtos.ProtoCodes.Code.Sm_ItemBag, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }
    }

    private void worship(String playerId) {
        getControlerForQuery().worship(playerId);
    }

    private void maxGetIntegralRewards() {
        getControlerForQuery().maxGetIntegralRewards();
    }

    private void maxWorship() {
        getControlerForQuery().maxWorship();
    }

    private void modifyDeclaration(String declaration) {
        getControlerForQuery().modifyDeclaration(declaration);
    }

    private void modifyPvpIcon(int pvpIcon) {
        getControlerForQuery().modifyPvpIcon(pvpIcon);
    }

    private void getRankRewards(int rankRewards) {
        getControlerForQuery().getRankRewards(rankRewards);
    }

    private void getIntegralRewards(int integralRewards) {
        getControlerForQuery().getIntegralRewards(integralRewards);
    }

    private void clearCd() {
        getControlerForQuery().clearCd();
    }

    private void challenge(int rank, String playerId) {
        getControlerForQuery().challenge(rank, playerId);
    }

    private void buyAttackTimes() {
        getControlerForQuery().buyAttackTimes();
    }

    private void sync() {
        getControlerForQuery().sync();
    }

    private void refresh() {
        getControlerForQuery().refresh();
    }

    private void queryRank() {
        getControlerForQuery().queryRank();
    }

    private void getBattleRecords() {
        getControlerForQuery().getBattleRecords();
    }

    private void displayBattleRecords(String recordId) {
        getControlerForQuery().displayBattleRecords(recordId);
    }

    private void onAddNewBattleRecords(In_AddNewBattleRecords.Request request) {
        getControlerForQuery().onAddNewBattleRecords(request.getArenaRecord());
    }
}
