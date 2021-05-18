package ws.gameServer.features.standalone.actor.arenaCenter.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.relationship.base.resultCode.ResultCodeEnum;

/**
 * Created by lee on 17-3-15.
 */
public class In_GetRankToPvpCenter {
    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = -4995029203662703128L;
        private String playerId;
        private int innerRealmId;

        public Request(int innerRealmId, String playerId) {
            this.playerId = playerId;
            this.innerRealmId = innerRealmId;
        }

        public String getPlayerId() {
            return playerId;
        }

        public int getInnerRealmId() {
            return innerRealmId;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = -4484252257218591768L;
        private int rank;

        public Response(int rank) {
            this.resultCode = ResultCodeEnum.SUCCESS;
            this.rank = rank;
        }

        public int getRank() {
            return rank;
        }
    }
}
