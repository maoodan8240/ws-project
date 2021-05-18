package ws.relationship.utils;

import ws.relationship.topLevelPojos.simpleGuild.SimpleGuild;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangweiwei on 17-4-14.
 */
public class SimplePojoUtils {


    public static SimplePlayer querySimplePlayerById(String playerId, int outerRealmId) {
        return DBUtils.getHashPojo(playerId, outerRealmId, SimplePlayer.class);
    }


    public static Map<String, SimplePlayer> querySimplePlayerLisByIds(List<String> playerIds, int outerRealmId) {
        return DBUtils.getHashPojoLis(playerIds, outerRealmId, SimplePlayer.class);
    }


    public static SimpleGuild querySimpleGuild(String guildId, int outerRealmId) {
        return DBUtils.getHashPojo(guildId, outerRealmId, SimpleGuild.class);
    }

    public static Map<String, SimpleGuild> querySimpleGuildLis(List<String> guildIdLis, int outerRealmId) {
        return DBUtils.getHashPojoLis(guildIdLis, outerRealmId, SimpleGuild.class);
    }


}
