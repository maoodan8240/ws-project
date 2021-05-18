package ws.relationship.topLevelPojos.newGuildCenter;

import com.alibaba.fastjson.annotation.JSONField;
import ws.common.mongoDB.interfaces.TopLevelPojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lee on 16-11-30.
 */
public class NewGuildCenter implements TopLevelPojo {
    private static final long serialVersionUID = 7663715677357640890L;
    @JSONField(name = "_id")
    private String autoId;
    private String lastResetDay = "0";
    private int outRealmId;
    @JSONField(serialize = false)
    private Map<String, NewGuild> idToGuild = new HashMap<>();

    /**
     * 玩家Id对应申请的社团
     */
    private Map<String, List<String>> playerIdToGuildIds = new HashMap<>();
    /**
     * 社团的id对应该社团的玩家申请列表
     */
    private Map<String, List<NewGuildApplyInfo>> guildIdToApplyList = new HashMap<>();


    public NewGuildCenter() {
    }


    public NewGuildCenter(String autoId) {
        this.autoId = autoId;
    }

    @Override
    public String getOid() {
        return autoId;
    }

    @Override
    public void setOid(String autoId) {
        this.autoId = autoId;
    }

    public String getAutoId() {
        return autoId;
    }

    public void setAutoId(String autoId) {
        this.autoId = autoId;
    }

    public String getLastResetDay() {
        return lastResetDay;
    }

    public void setLastResetDay(String lastResetDay) {
        this.lastResetDay = lastResetDay;
    }

    public int getOutRealmId() {
        return outRealmId;
    }

    public void setOutRealmId(int outRealmId) {
        this.outRealmId = outRealmId;
    }

    public Map<String, NewGuild> getIdToGuild() {
        return idToGuild;
    }

    public void setIdToGuild(Map<String, NewGuild> idToGuild) {
        this.idToGuild = idToGuild;
    }


    public Map<String, List<String>> getPlayerIdToGuildIds() {
        return playerIdToGuildIds;
    }

    public void setPlayerIdToGuildIds(Map<String, List<String>> playerIdToGuildIds) {
        this.playerIdToGuildIds = playerIdToGuildIds;
    }

    public Map<String, List<NewGuildApplyInfo>> getGuildIdToApplyList() {
        return guildIdToApplyList;
    }

    public void setGuildIdToApplyList(Map<String, List<NewGuildApplyInfo>> guildIdToApplyList) {
        this.guildIdToApplyList = guildIdToApplyList;
    }
}
