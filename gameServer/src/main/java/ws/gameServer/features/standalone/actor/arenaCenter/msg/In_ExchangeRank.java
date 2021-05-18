package ws.gameServer.features.standalone.actor.arenaCenter.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.relationship.base.resultCode.ResultCodeEnum;

/**
 * Created by lee on 17-3-1.
 */
public class In_ExchangeRank {
    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = 4470649131244496055L;
        private int innerRealmId;
        private String attackId;
        private String beAttackId;

        public Request(int innerRealmId, String attackId, String beAttackId) {
            this.innerRealmId = innerRealmId;
            this.attackId = attackId;
            this.beAttackId = beAttackId;
        }

        public int getInnerRealmId() {
            return innerRealmId;
        }

        public String getAttackId() {
            return attackId;
        }

        public String getBeAttackId() {
            return beAttackId;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = 7632333685454959079L;
        private int attackNewRank;
        private int beAttackNewRank;


        public Response(int attackNewRank, int beAttackNewRank) {
            this.attackNewRank = attackNewRank;
            this.beAttackNewRank = beAttackNewRank;
            this.resultCode = ResultCodeEnum.SUCCESS;
        }

        public int getBeAttackNewRank() {
            return beAttackNewRank;
        }

        public int getAttackNewRank() {
            return attackNewRank;
        }
    }
}
