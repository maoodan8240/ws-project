package ws.gameServer.features.standalone.utils;

import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.common.utils.general.EnumUtils;
import ws.gameServer.features.actor.cluster.ClusterListener;
import ws.gameServer.features.standalone.extp.itemBag.utils.ItemBagUtils;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.enums.ItemOpEnum;
import ws.relationship.enums.item.IdItemTypeEnum;
import ws.relationship.logServer.base.PlayerLog;
import ws.relationship.logServer.base.WsLog;
import ws.relationship.logServer.pojos.ItemLog;
import ws.relationship.logServer.pojos.PlayerLoginLog;
import ws.relationship.logServer.pojos.PlayerLogoutLog;
import ws.relationship.logServer.pojos.PlayerLvUpLog;
import ws.relationship.logServer.pojos.PlayerPveLog;
import ws.relationship.logServer.pojos.PlayerVipLvUpLog;
import ws.relationship.logServer.pojos.ServerStatusLog;
import ws.relationship.topLevelPojos.player.Player;
import ws.relationship.utils.LogServerUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by lee on 17-6-11.
 */
public class LogHandler {


    /**
     * 新注册的玩家
     *
     * @param player
     */
    public static void playerLoginLog(Player player, String deviceUid) {
        PlayerLoginLog log = new PlayerLoginLog(deviceUid);
        setPlayerInfo(player, log);
        LogServerUtils.sendLog(ClusterListener.getActorContext(), log);
    }

    /**
     * 服务器状态
     *
     * @param innerRealmIdToServerStatusLog
     */
    public static void serverStatusLog(Map<Integer, ServerStatusLog> innerRealmIdToServerStatusLog) {
        LogServerUtils.sendLogLis(ClusterListener.getActorContext(), new ArrayList<>(innerRealmIdToServerStatusLog.values()));
    }

    /**
     * pve关卡通过
     *
     * @param player
     */
    public static void playerPveLog(Player player, int stageId) {
        PlayerPveLog log = new PlayerPveLog(stageId, player.getBase().getLevel());
        setPlayerInfo(player, log);
        LogServerUtils.sendLog(ClusterListener.getActorContext(), log);
    }

    /**
     * 玩家VIP升级
     *
     * @param player
     */
    public static void playerVipLvUpLog(Player player) {
        PlayerVipLvUpLog log = new PlayerVipLvUpLog(player.getBase().getLevel(), player.getPayment().getVipLevel());
        setPlayerInfo(player, log);
        LogServerUtils.sendLog(ClusterListener.getActorContext(), log);
    }

    /**
     * 玩家升级
     *
     * @param player
     */
    public static void playerLvUpLog(Player player) {
        PlayerLvUpLog log = new PlayerLvUpLog(player.getBase().getLevel(), player.getPayment().getVipLevel());
        setPlayerInfo(player, log);
        LogServerUtils.sendLog(ClusterListener.getActorContext(), log);
    }

    /**
     * 玩家登出
     *
     * @param player
     * @param lsinTime
     */
    public static void playerLogoutLog(Player player, long lsinTime) {
        PlayerLogoutLog log = new PlayerLogoutLog(lsinTime);
        setPlayerInfo(player, log);
        LogServerUtils.sendLog(ClusterListener.getActorContext(), log);
    }

    /**
     * 物品的变化
     *
     * @param player
     * @param action
     * @param idMaptoCount
     * @param op
     */
    public static void itemLog(Player player, Enum<?> action, IdMaptoCount idMaptoCount, ItemOpEnum op) {
        if (idMaptoCount == null) {
            return;
        }
        List<WsLog> logList = new ArrayList<>();
        idMaptoCount.getAll().forEach(idAndCount -> {
            logList.add(createItemLog(player, action, idAndCount, op));
        });
        if (logList.size() > 0) {
            LogServerUtils.sendLogLis(ClusterListener.getActorContext(), logList);
        }
    }

    private static ItemLog createItemLog(Player player, Enum<?> action, IdAndCount idAndCount, ItemOpEnum op) {
        IdItemTypeEnum type = ItemBagUtils.getIdItemType(idAndCount.getId());
        int id = idAndCount.getId();
        long count = idAndCount.getCount();
        String msgac = getActionSimpleName(action);
        ItemLog log = new ItemLog(type, id, count, op, msgac);
        setPlayerInfo(player, log);
        return log;
    }

    private static void setPlayerInfo(Player player, PlayerLog playerLog) {
        playerLog.setPid(player.getPlayerId());
        playerLog.setSid(player.getBase().getSimpleId());
        playerLog.setOrid(player.getAccount().getOuterRealmId());
        playerLog.setPlatformType(player.getAccount().getPlatformType());
        Date date1 = WsDateUtils.dateToFormatDate(player.getAccount().getCreateAt(), WsDateFormatEnum.yyyy_MM_dd$HH_mm_ss);
        playerLog.setCreateAtDate(Integer.valueOf(WsDateUtils.dateToFormatStr(date1, WsDateFormatEnum.yyyyMMdd)));
        playerLog.setCreateAtTime(Integer.valueOf(WsDateUtils.dateToFormatStr(date1, WsDateFormatEnum.HHmmss)));
    }

    public static String getActionSimpleName(Enum<?> action) {
        if (action == null) {
            return "null";
        }
        return EnumUtils.protoActionToString(action);
    }
}
