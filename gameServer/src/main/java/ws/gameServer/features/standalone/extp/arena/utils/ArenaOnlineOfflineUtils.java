package ws.gameServer.features.standalone.extp.arena.utils;

import ws.relationship.topLevelPojos.pvp.arena.Arena;
import ws.relationship.topLevelPojos.pvp.arena.ArenaRecord;
import ws.relationship.utils.DBUtils;

/**
 * Created by zhangweiwei on 17-5-12.
 */
public class ArenaOnlineOfflineUtils {

    public static void onAddNewBattleRecords(ArenaRecord arenaRecord, String beAttackId, int outerRealmId) {
        onAddNewBattleRecords_Offline(arenaRecord, beAttackId, outerRealmId);
    }


    /**
     * 离线体加战斗记录
     *
     * @param arenaRecord
     * @param beAttackId
     * @param outerRealmId
     */
    public static void onAddNewBattleRecords_Offline(ArenaRecord arenaRecord, String beAttackId, int outerRealmId) {
        Arena arena = DBUtils.getHashPojo(beAttackId, outerRealmId, Arena.class);
        onAddNewBattleRecords_Online(arenaRecord, arena);
        DBUtils.saveHashPojo(outerRealmId, arena);
    }


    public static void onAddNewBattleRecords_Online(ArenaRecord arenaRecord, Arena arena) {
        arena.getRecords().add(arenaRecord);
    }
}
