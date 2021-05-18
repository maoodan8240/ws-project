package ws.gameServer.features.standalone.extp.piecemeal.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.protos.EnumsProtos.CommonRankTypeEnum;
import ws.relationship.topLevelPojos.piecemeal.Piecemeal;

public interface PiecemealCtrl extends PlayerExteControler<Piecemeal> {


    /**
     * 玩家登录
     */
    void onPlayerLogined();

    /**
     * 接收到心跳信息
     */
    void onPlayerHeartBeating();

    /**
     * 合成
     *
     * @param itemTpId
     */
    void composite(int itemTpId);

    /**
     * 购买道具
     *
     * @param itemTpId
     * @param count
     */
    void buyItem(int itemTpId, int count);

    /**
     * 查询通用排行榜
     *
     * @param rankTypeEnum
     * @param rankStart
     * @param rankCount
     */
    void queryRank(CommonRankTypeEnum rankTypeEnum, int rankStart, int rankCount);

    /**
     * 查询自己的排名
     *
     * @param rankTypeEnum
     */
    void selfRank(CommonRankTypeEnum rankTypeEnum);


    /**
     * 同步新手引导
     */
    void syncGuide();

    /**
     * 设置新手引导最大值
     *
     * @param maxGuideId
     */
    void setGuide(int maxGuideId);
}