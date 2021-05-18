package ws.gameServer.features.standalone.extp.arena.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.relationship.topLevelPojos.pvp.arena.ArenaRecord;

/**
 * Created by lee on 17-3-1.
 */
public class In_AddNewBattleRecords {

    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = 3559686261491970343L;
        private ArenaRecord arenaRecord;
        private String beAttackId;
        private int outerRealmId;

        public Request(ArenaRecord arenaRecord, String beAttackId, int outerRealmId) {
            this.arenaRecord = arenaRecord;
            this.beAttackId = beAttackId;
            this.outerRealmId = outerRealmId;
        }

        public ArenaRecord getArenaRecord() {
            return arenaRecord;
        }

        public String getBeAttackId() {
            return beAttackId;
        }

        public int getOuterRealmId() {
            return outerRealmId;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = 1211320823661550469L;
    }
}
