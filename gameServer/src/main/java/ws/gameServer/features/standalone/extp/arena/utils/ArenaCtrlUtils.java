package ws.gameServer.features.standalone.extp.arena.utils;

import akka.actor.ActorSelection;
import ws.gameServer.features.actor.cluster.ClusterListener;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.extp.heros.utils.HerosCtrlOnlineOfflineUtils;
import ws.newBattle.NewBattleCreater;
import ws.newBattle.NewBattleHeroContainer;
import ws.protos.BattleProtos.Sm_Battle_BackData;
import ws.protos.EnumsProtos.FormationTypeEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.CheckPlayerOnlineMsgRequest;
import ws.relationship.base.msg.heros.In_QueryBattleHeroContainerInFormation;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_RankDailyReward_Row;
import ws.relationship.table.tableRows.Table_Robot_Row;
import ws.relationship.utils.ActorMsgSynchronizedExecutor;

import java.util.List;

/**
 * Created by zb on 8/9/16.
 */
public class ArenaCtrlUtils {


    public static IdMaptoCount getAreanPointReward(List<Table_RankDailyReward_Row> rows) {
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        for (Table_RankDailyReward_Row row : rows) {
            idMaptoCount.addAll(row.getReward());
        }
        return idMaptoCount;
    }

    public static IdAndCount getWorshipReward(int playerCount) {
        int count = (int) AllServerConfig.Pvp_Worship_Rewards.getConfig() * playerCount;
        return new IdAndCount(ResourceTypeEnum.RES_MONEY.getNumber(), count);
    }


    /**
     * 打机器人
     *
     * @param playerCtrl
     * @param robotId
     * @return
     */
    public static Sm_Battle_BackData runArenaBattle_Robot(PlayerCtrl playerCtrl, int robotId) {
        Table_Robot_Row robotRow = RootTc.get(Table_Robot_Row.class, robotId);
        NewBattleHeroContainer selfHeroContainer = HerosCtrlOnlineOfflineUtils.createFormationBattleHeroContainer_Online(FormationTypeEnum.F_MAIN, playerCtrl);
        return NewBattleCreater.runNewBattle(selfHeroContainer, robotRow.getRotbotHeroContainer());
    }


    /**
     * 打真实玩家
     *
     * @param playerCtrl
     * @param outerRealmId
     * @param playerId
     * @return
     */
    public static Sm_Battle_BackData runArenaBattle_Player(PlayerCtrl playerCtrl, int outerRealmId, String playerId) {
        In_QueryBattleHeroContainerInFormation.Request heroContainerRequest = new In_QueryBattleHeroContainerInFormation.Request(FormationTypeEnum.F_MAIN, outerRealmId, playerId);
        CheckPlayerOnlineMsgRequest<In_QueryBattleHeroContainerInFormation.Request> request = new CheckPlayerOnlineMsgRequest<>(playerId, heroContainerRequest);
        ActorSelection actorSelection = ClusterListener.getActorContext().actorSelection(ActorSystemPath.WS_GameServer_Selection_World);
        In_QueryBattleHeroContainerInFormation.Response response = ActorMsgSynchronizedExecutor.sendMsgToServer(actorSelection, request);
        NewBattleHeroContainer selfHeroContainer = HerosCtrlOnlineOfflineUtils.createFormationBattleHeroContainer_Online(FormationTypeEnum.F_MAIN, playerCtrl);
        return NewBattleCreater.runNewBattle(selfHeroContainer, response.getContainer());
    }
}
