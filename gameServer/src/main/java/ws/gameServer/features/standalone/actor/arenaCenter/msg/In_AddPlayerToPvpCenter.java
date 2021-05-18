package ws.gameServer.features.standalone.actor.arenaCenter.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.relationship.base.resultCode.ResultCodeEnum;

public class In_AddPlayerToPvpCenter {

    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = 1L;
        private int outerRealmId;  // 显示服id
        private int innerRealmId;  // 内部服Id
        private String playerId;
        private boolean robot;

        public Request(int outerRealmId, int innerRealmId, String playerId, boolean robot) {
            this.outerRealmId = outerRealmId;
            this.innerRealmId = innerRealmId;
            this.playerId = playerId;
            this.robot = robot;
        }


        public int getInnerRealmId() {
            return innerRealmId;
        }

        public boolean isRobot() {
            return robot;
        }

        public String getPlayerId() {
            return playerId;
        }

        public int getOuterRealmId() {
            return outerRealmId;
        }
    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = 1L;
        private int newRank;

        public Response(int newRank) {
            this.resultCode = ResultCodeEnum.SUCCESS;
            this.newRank = newRank;
        }


        public int getNewRank() {
            return newRank;
        }
    }

}
