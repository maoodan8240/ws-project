package ws.gameServer.features.standalone.actor.arenaCenter;

import ws.common.utils.di.GlobalInjector;
import ws.gameServer.features.standalone.actor.arenaCenter.ctrl.ArenaCenterCtrl;
import ws.gameServer.features.standalone.actor.arenaCenter.msg.In_AddPlayerToPvpCenter;
import ws.gameServer.features.standalone.actor.arenaCenter.msg.In_ExchangeRank;
import ws.gameServer.features.standalone.actor.arenaCenter.msg.In_GetRankToPvpCenter;
import ws.gameServer.features.standalone.actor.arenaCenter.msg.In_GotoSettleDaliyRankReward;
import ws.gameServer.features.standalone.actor.arenaCenter.msg.In_QueryEnemies;
import ws.gameServer.features.standalone.actor.arenaCenter.msg.In_SettleDaliyRankReward;
import ws.relationship.base.actor.WsActor;


public class ArenaCenterActor extends WsActor {
    private ArenaCenterCtrl arenaCenterCtrl;

    public ArenaCenterActor() {
        this.arenaCenterCtrl = GlobalInjector.getInstance(ArenaCenterCtrl.class);
        arenaCenterCtrl.setCurContext(getContext());
    }

    @Override
    public void onRecv(Object msg) throws Exception {
        arenaCenterCtrl.setCurSendActorRef(getSender());
        if (msg instanceof In_AddPlayerToPvpCenter.Request) {
            onIn_AddPlayerToPvpCenter((In_AddPlayerToPvpCenter.Request) msg);
        } else if (msg instanceof In_QueryEnemies.Request) {
            onIn_QueryEnemies((In_QueryEnemies.Request) msg);
        } else if (msg instanceof In_ExchangeRank.Request) {
            onIn_ExchangeRank((In_ExchangeRank.Request) msg);
        } else if (msg instanceof In_GetRankToPvpCenter.Request) {
            onIn_GetRankToPvpCenter((In_GetRankToPvpCenter.Request) msg);
        } else if (msg instanceof In_SettleDaliyRankReward.Request) {
            onIn_SettleDaliyRankReward();
        } else if (msg instanceof In_GotoSettleDaliyRankReward.Request) {
            onIn_GotoSettleDaliyRankReward();
        }

    }

    private void onIn_GotoSettleDaliyRankReward() {
        arenaCenterCtrl.gotoSettleDaliyRankReward();
    }

    private void onIn_SettleDaliyRankReward() {
        arenaCenterCtrl.shouldSettleDaliyRankReward();
    }

    private void onIn_GetRankToPvpCenter(In_GetRankToPvpCenter.Request msg) {
        this.arenaCenterCtrl.queryRank(msg.getInnerRealmId(), msg.getPlayerId());
    }

    private void onIn_ExchangeRank(In_ExchangeRank.Request msg) {
        this.arenaCenterCtrl.exchangeRank(msg.getInnerRealmId(), msg.getAttackId(), msg.getBeAttackId());
    }

    private void onIn_QueryEnemies(In_QueryEnemies.Request msg) {
        this.arenaCenterCtrl.queryRankerLis(msg.getInnerRealmId(), msg.getPlayerId(), msg.getRankList());
    }


    private void onIn_AddPlayerToPvpCenter(In_AddPlayerToPvpCenter.Request request) {
        this.arenaCenterCtrl.newPlayerToCenter(request.getOuterRealmId(), request.getInnerRealmId(), request.getPlayerId());
    }

}
