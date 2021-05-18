package ws.gameServer.features.standalone.actor.arenaCenter.ctrl;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import ws.common.utils.mc.controler.Controler;
import ws.relationship.topLevelPojos.common.TopLevelHolder;

import java.util.List;


public interface ArenaCenterCtrl extends Controler<TopLevelHolder> {


    /**
     * 交换排名
     *
     * @param centerOuterRealmId center的OuterRealmId  <==始终等价于==> 玩家的innerRealmId
     * @param attackerId
     * @param beAttackerId
     */
    void exchangeRank(int centerOuterRealmId, String attackerId, String beAttackerId);

    /**
     * 新增的玩家，设置排名
     *
     * @param playerOuterRealmId 玩家的OuterRealmId
     * @param centerOuterRealmId center的OuterRealmId  <==始终等价于==> 玩家的innerRealmId
     * @param playerId
     */
    void newPlayerToCenter(int playerOuterRealmId, int centerOuterRealmId, String playerId);


    /**
     * 当前消息的发送者
     *
     * @param sender
     */
    void setCurSendActorRef(ActorRef sender);

    /**
     * Actor上下文
     *
     * @param context
     */
    void setCurContext(ActorContext context);

    /**
     * 根据排名查询玩家
     *
     * @param centerOuterRealmId center的OuterRealmId  <==始终等价于==> 玩家的innerRealmId
     * @param playerId
     * @param rankList           玩家身上缓存的敌人名次列表
     */
    void queryRankerLis(int centerOuterRealmId, String playerId, List<Integer> rankList);

    /**
     * 查询当前排名
     *
     * @param centerOuterRealmId center的OuterRealmId  <==始终等价于==> 玩家的innerRealmId
     * @param playerId
     */
    void queryRank(int centerOuterRealmId, String playerId);


    /**
     * 应该去结算每日排名奖励了
     */
    void shouldSettleDaliyRankReward();

    /**
     * 结算每日排名奖励
     */
    void gotoSettleDaliyRankReward();
}
