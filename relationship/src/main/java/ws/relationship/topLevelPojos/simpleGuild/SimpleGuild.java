package ws.relationship.topLevelPojos.simpleGuild;

import com.alibaba.fastjson.annotation.JSONField;
import ws.common.mongoDB.interfaces.TopLevelPojo;

/**
 * Created by lee on 5/26/17.
 */
public class SimpleGuild implements TopLevelPojo {
    private static final long serialVersionUID = -1252237280260364628L;

    @JSONField(name = "_id")
    private String guildId;
    private String masterName;
    private String guildName;
    private String guildNotic;
    private int guildSimpleId;
    private int guildLv;
    private int rank;
    private int memberCount;
    private int icon;
    private long guildBattleValue;
    private int outRealmId;


    @Override
    public String getOid() {
        return getGuildId();
    }

    @Override
    public void setOid(String guildId) {
        this.guildId = guildId;
    }

    public int getOutRealmId() {
        return outRealmId;
    }

    public void setOutRealmId(int outRealmId) {
        this.outRealmId = outRealmId;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public String getGuildName() {
        return guildName;
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getGuildNotic() {
        return guildNotic;
    }

    public void setGuildNotic(String guildNotic) {
        this.guildNotic = guildNotic;
    }

    public int getGuildSimpleId() {
        return guildSimpleId;
    }

    public void setGuildSimpleId(int guildSimpleId) {
        this.guildSimpleId = guildSimpleId;
    }

    public int getGuildLv() {
        return guildLv;
    }

    public void setGuildLv(int guildLv) {
        this.guildLv = guildLv;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public long getGuildBattleValue() {
        return guildBattleValue;
    }

    public void setGuildBattleValue(long guildBattleValue) {
        this.guildBattleValue = guildBattleValue;
    }
}
