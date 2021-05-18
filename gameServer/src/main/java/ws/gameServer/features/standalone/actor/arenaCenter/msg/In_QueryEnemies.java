package ws.gameServer.features.standalone.actor.arenaCenter.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.relationship.base.resultCode.ResultCodeEnum;
import ws.relationship.topLevelPojos.pvp.arenaCenter.ArenaCenterRanker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lee on 17-2-28.
 */
public class In_QueryEnemies {
    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = -4025919773131038468L;
        private String playerId; // 查询者的Id
        private int innerRealmId;
        private List<Integer> rankList;

        public Request(String playerId, int innerRealmId, List<Integer> rankList) {
            this.innerRealmId = innerRealmId;
            this.playerId = playerId;
            this.rankList = rankList;
        }

        public int getInnerRealmId() {
            return innerRealmId;
        }

        public String getPlayerId() {
            return playerId;
        }

        public List<Integer> getRankList() {
            return rankList;
        }


    }

    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = 222729993697551927L;
        private Map<Integer, ArenaCenterRanker> rankToRanker = new HashMap<>();
        private Map<Integer, ArenaCenterRanker> topTenRankers = new HashMap<>();
        private List<Integer> rankListFinal; // 最终返回的名次列表 - 跟请求的可能不一样


        public Response(Map<Integer, ArenaCenterRanker> rankToRanker, List<Integer> rankListFinal) {
            this.resultCode = ResultCodeEnum.SUCCESS;
            this.rankToRanker = rankToRanker;
            this.rankListFinal = rankListFinal;
        }

        public List<Integer> getRankListFinal() {
            return rankListFinal;
        }

        public Map<Integer, ArenaCenterRanker> getTopTenRankers() {
            return topTenRankers;
        }

        public Map<Integer, ArenaCenterRanker> getRankToRanker() {
            return rankToRanker;
        }
    }
}
