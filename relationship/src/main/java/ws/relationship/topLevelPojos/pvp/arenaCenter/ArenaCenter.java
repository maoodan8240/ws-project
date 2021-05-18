package ws.relationship.topLevelPojos.pvp.arenaCenter;

import com.alibaba.fastjson.annotation.JSONField;
import ws.common.mongoDB.interfaces.TopLevelPojo;

import java.util.HashMap;
import java.util.Map;

public class ArenaCenter implements TopLevelPojo {
    private static final long serialVersionUID = -2893343830908125685L;

    @JSONField(name = "_id")
    private String autoId;
    private int outerRealmId;
    private int maxRank;
    @JSONField(serialize = false)
    private Map<Integer, ArenaCenterRanker> rankToRanker = new HashMap<>(); // 排名---玩家竞技场信息


    public ArenaCenter() {
    }

    public ArenaCenter(String autoId) {
        this.autoId = autoId;
    }

    @Override
    public String getOid() {
        return autoId;
    }

    @Override
    public void setOid(String id) {
        this.autoId = id;
    }


    public String getAutoId() {
        return autoId;
    }

    public void setAutoId(String autoId) {
        this.autoId = autoId;
    }

    public int getOuterRealmId() {
        return outerRealmId;
    }

    public void setOuterRealmId(int outerRealmId) {
        this.outerRealmId = outerRealmId;
    }

    public Map<Integer, ArenaCenterRanker> getRankToRanker() {
        return rankToRanker;
    }

    public void setRankToRanker(Map<Integer, ArenaCenterRanker> rankToRanker) {
        this.rankToRanker = rankToRanker;
    }

    public int getMaxRank() {
        return maxRank;
    }

    public void setMaxRank(int maxRank) {
        this.maxRank = maxRank;
    }
}
