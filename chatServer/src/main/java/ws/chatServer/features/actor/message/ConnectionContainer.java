package ws.chatServer.features.actor.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.network.server.interfaces.Connection;
import ws.relationship.base.OneToOneConcurrentMapWithAttachment;
import ws.relationship.base.OneToOneConcurrentMapWithoutAttachment;
import ws.relationship.base.PlayerRealmPair;

public class ConnectionContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionContainer.class);
    private static final OneToOneConcurrentMapWithAttachment<String, Connection, PlayerRealmPair> FLAG_TO_CONNECTION = new OneToOneConcurrentMapWithAttachment<>();
    private static final OneToOneConcurrentMapWithoutAttachment<String, String> GAMEID_TO_FLAG = new OneToOneConcurrentMapWithoutAttachment<>();


    /**
     * flag 和 connection 必须一一对应
     *
     * @param flag
     * @param connection
     */
    public static void put(String flag, Connection connection) {
        LOGGER.debug("添加新的连接对应关系 flag={}  <--> connection={} .", flag, connection);
        FLAG_TO_CONNECTION.removeKV(flag, connection);
        FLAG_TO_CONNECTION.put(flag, connection);
    }

    /**
     * 覆盖GameId的数据
     *
     * @param gameId
     * @param flagNew
     */
    public static void put(String flagNew, String gameId, int innerRealmId, int outerRealmId) {
        LOGGER.debug("添加新的GameId={} flagNew={} .", gameId, flagNew);
        if (containsGameId_GTF(gameId)) {
            String flagOld = getFlagByGameId_GTF(gameId);
            if (!flagNew.equals(flagOld)) {
                Connection connectionOld = FLAG_TO_CONNECTION.removeByK(flagOld);
                LOGGER.debug("flagOld={} flagNew={} 不一样, 新的连接->新的登录, 移除connectionOld={} .", flagOld, flagNew, connectionOld);
                if (connectionOld != null) {
                    connectionOld.close();
                }
            } else {
                LOGGER.debug("flagOld={} flagNew={} 相同, 老的连接->新的登录 .", flagOld, flagNew);
            }
        }
        GAMEID_TO_FLAG.removeKV(gameId, flagNew);
        GAMEID_TO_FLAG.put(gameId, flagNew);
        FLAG_TO_CONNECTION.addAttachment(flagNew, new PlayerRealmPair(gameId, outerRealmId, innerRealmId));
    }

    public static PlayerRealmPair remove(Connection connectionOld) {
        LOGGER.debug("connectionOld={} 断开了,移除相关信息.", connectionOld);
        if (containsConnection_FTC(connectionOld)) {
            String flagOld = getFlagByConnection_FTC(connectionOld);
            PlayerRealmPair realmPairOld = getRealmPairByFlag_FTC(flagOld);
            FLAG_TO_CONNECTION.removeByV(connectionOld);
            if (realmPairOld == null) { // 没有登录成功
                LOGGER.debug("remove connectionOld={} <---> flagOld={}, 不存在gameIdOld，connectionOld上面玩家没有成功登录 ", connectionOld, flagOld);
                return null;
            }
            String gameIdOld = realmPairOld.getPlayerId();
            String flagNew = getFlagByGameId_GTF(gameIdOld);
            LOGGER.debug("remove connectionOld={} <---> flagOld={} <---> gameIdOld={}<---> flagNew={} ", connectionOld, flagOld, gameIdOld, flagNew);
            if (flagOld.equals(flagNew)) {  // 同一个GameId相同的Connection
                LOGGER.debug("移除的Connection={} 对应的玩家不在线了.", connectionOld);
                return realmPairOld.clone();
            }
        }
        return null;
    }

    // ============================== FLAG_TO_CONNECTION start ================================
    public static boolean containsFlag_FTC(String flag) {
        return FLAG_TO_CONNECTION.containsK(flag);
    }

    public static boolean containsConnection_FTC(Connection connection) {
        return FLAG_TO_CONNECTION.containsV(connection);
    }


    public static String getFlagByConnection_FTC(Connection connection) {
        return FLAG_TO_CONNECTION.getKByV(connection);
    }

    public static Connection getConnectionByFlag_FTC(String flag) {
        return FLAG_TO_CONNECTION.getVByK(flag);
    }

    public static PlayerRealmPair getRealmPairByFlag_FTC(String flag) {
        return FLAG_TO_CONNECTION.getAttachmentByK(flag);
    }
    // ============================== FLAG_TO_CONNECTION end ================================

    //

    // ============================== GAMEID_TO_FLAG start ================================

    public static boolean containsGameId_GTF(String gameId) {
        return GAMEID_TO_FLAG.containsK(gameId);
    }

    public static boolean containsFlag_GTF(String flag) {
        return GAMEID_TO_FLAG.containsV(flag);
    }


    public static String getGameIdByFlag_GTF(String flag) {
        return GAMEID_TO_FLAG.getKByV(flag);
    }

    public static String getFlagByGameId_GTF(String gameId) {
        return GAMEID_TO_FLAG.getVByK(gameId);
    }
    // ============================== GAMEID_TO_FLAG end ================================

    //

    public static PlayerRealmPair getRealmPairByGameId(String gameId) {
        if (containsGameId_GTF(gameId)) {
            String flag = getFlagByGameId_GTF(gameId);
            if (containsFlag_FTC(flag)) {
                return getRealmPairByFlag_FTC(flag);
            }
            LOGGER.debug("查询PlayerRealmPair时，FLAG_TO_CONNECTION 对应的关系不存在, gameId={} flag={} .", gameId, flag);
            return null;
        }
        LOGGER.debug("查询PlayerRealmPair时，GAMEID_TO_FLAG 对应的关系不存在, gameId={} .", gameId);
        return null;
    }
}
