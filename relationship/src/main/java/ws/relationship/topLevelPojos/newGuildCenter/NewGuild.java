package ws.relationship.topLevelPojos.newGuildCenter;

import com.alibaba.fastjson.annotation.JSONField;
import ws.common.mongoDB.interfaces.TopLevelPojo;
import ws.protos.EnumsProtos.GuildInstituteTypeEnum;
import ws.protos.EnumsProtos.GuildJoinTypeEnum;
import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.relationship.base.WsCloneable;
import ws.relationship.utils.CloneUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lee on 16-11-30.
 */
public class NewGuild implements TopLevelPojo, WsCloneable {
    private static final long serialVersionUID = -2312740042562922221L;

    @JSONField(name = "_id")
    private String guildId;//社团ID

    private int outerRealmId;//服号

    private int simpleId; //社团simpleId

    private String guildName; //社团名字

    private String masterPlayerId;  //帮主playerId

    private int masterOuterRealmId; //帮主显示服号

    private int todayExp; // 今日经验(此经验不能超过社团人数*250(不包含额外增加的人数，对应社团等级表中社团等级对应的对大社团人数)) --每日重置

    private String guildNotice;//社团公告

    private int guildIcon;//社团图标ID

    private long createTime; //社团创建时间

    private long battleValue; // 社团战斗力(社团成员所有人的最强战力总和)

    private Map<String, NewGuildCenterPlayer> playerIdToGuildPlayerInfo = new HashMap<>();//成员

    private List<NewGuildTrack> guildTracks = new ArrayList<>();//社团动态

    private int limitLevel; //限制加入等级

    private GuildJoinTypeEnum joinTypeEnum; // 限制加入方式

    private long giveJobTime;//最后一次传职的时间戳

    private Map<GuildInstituteTypeEnum, NewGuildInstitute> typeAndInstitute = new HashMap<>(); //研究所类型对应研究所

    private int mailsCount; //邮件使用数量  --每日重置

    private Map<GuildRedBagTypeEnum, NewGuildSystemRedBag> typeAndSysRedBag = new HashMap<>(); // 系统红包:红包类型对应红包详细信息
    private Map<GuildRedBagTypeEnum, List<NewGuildPlayerRedBag>> typeAndPlayerRedBag = new HashMap<>();// 玩家红包:红包类型对应红包详细信息
    private int rank;

    public NewGuild() {
    }

    /**
     * for clone
     *
     * @param guildId
     * @param outerRealmId
     * @param simpleId
     * @param guildName
     * @param masterPlayerId
     * @param todayExp
     * @param guildNotice
     * @param guildIcon
     * @param createTime
     * @param battleValue
     * @param playerIdToGuildPlayerInfo
     * @param guildTracks
     * @param limitLevel
     * @param joinTypeEnum
     * @param giveJobTime
     * @param typeAndInstitute
     * @param mailsCount
     * @param typeAndSysRedBag
     * @param typeAndPlayerRedBag
     * @param rank
     */
    public NewGuild(String guildId, int outerRealmId, int simpleId, String guildName, String masterPlayerId, int masterOuterRealmId, int todayExp, String guildNotice, int guildIcon, long createTime, long battleValue, Map<String, NewGuildCenterPlayer> playerIdToGuildPlayerInfo, List<NewGuildTrack> guildTracks, int limitLevel, GuildJoinTypeEnum joinTypeEnum, long giveJobTime, Map<GuildInstituteTypeEnum, NewGuildInstitute> typeAndInstitute, int mailsCount, Map<GuildRedBagTypeEnum, NewGuildSystemRedBag> typeAndSysRedBag, Map<GuildRedBagTypeEnum, List<NewGuildPlayerRedBag>> typeAndPlayerRedBag, int rank) {
        this.guildId = guildId;
        this.outerRealmId = outerRealmId;
        this.simpleId = simpleId;
        this.guildName = guildName;
        this.masterPlayerId = masterPlayerId;
        this.masterOuterRealmId = masterOuterRealmId;
        this.todayExp = todayExp;
        this.guildNotice = guildNotice;
        this.guildIcon = guildIcon;
        this.createTime = createTime;
        this.battleValue = battleValue;
        this.playerIdToGuildPlayerInfo = playerIdToGuildPlayerInfo;
        this.guildTracks = guildTracks;
        this.limitLevel = limitLevel;
        this.joinTypeEnum = joinTypeEnum;
        this.giveJobTime = giveJobTime;
        this.typeAndInstitute = typeAndInstitute;
        this.mailsCount = mailsCount;
        this.typeAndSysRedBag = typeAndSysRedBag;
        this.typeAndPlayerRedBag = typeAndPlayerRedBag;
        this.rank = rank;
    }


    public int getMasterOuterRealmId() {
        return masterOuterRealmId;
    }

    public void setMasterOuterRealmId(int masterOuterRealmId) {
        this.masterOuterRealmId = masterOuterRealmId;
    }

    public Map<GuildRedBagTypeEnum, NewGuildSystemRedBag> getTypeAndSysRedBag() {
        return typeAndSysRedBag;
    }

    public void setTypeAndSysRedBag(Map<GuildRedBagTypeEnum, NewGuildSystemRedBag> typeAndSysRedBag) {
        this.typeAndSysRedBag = typeAndSysRedBag;
    }

    public Map<GuildRedBagTypeEnum, List<NewGuildPlayerRedBag>> getTypeAndPlayerRedBag() {
        return typeAndPlayerRedBag;
    }

    public void setTypeAndPlayerRedBag(Map<GuildRedBagTypeEnum, List<NewGuildPlayerRedBag>> typeAndPlayerRedBag) {
        this.typeAndPlayerRedBag = typeAndPlayerRedBag;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getMailsCount() {
        return mailsCount;
    }

    public void setMailsCount(int mailsCount) {
        this.mailsCount = mailsCount;
    }

    public Map<GuildInstituteTypeEnum, NewGuildInstitute> getTypeAndInstitute() {
        return typeAndInstitute;
    }

    public void setTypeAndInstitute(Map<GuildInstituteTypeEnum, NewGuildInstitute> typeAndInstitute) {
        this.typeAndInstitute = typeAndInstitute;
    }

    public long getGiveJobTime() {
        return giveJobTime;
    }

    public void setGiveJobTime(long giveJobTime) {
        this.giveJobTime = giveJobTime;
    }

    public int getTodayExp() {
        return todayExp;
    }

    public void setTodayExp(int todayExp) {
        this.todayExp = todayExp;
    }

    public GuildJoinTypeEnum getJoinTypeEnum() {
        return joinTypeEnum;
    }

    public void setJoinTypeEnum(GuildJoinTypeEnum joinTypeEnum) {
        this.joinTypeEnum = joinTypeEnum;
    }

    public int getLimitLevel() {
        return limitLevel;
    }

    public void setLimitLevel(int limitLevel) {
        this.limitLevel = limitLevel;
    }

    public int getSimpleId() {
        return simpleId;
    }

    public void setSimpleId(int simpleId) {
        this.simpleId = simpleId;
    }

    public int getOuterRealmId() {
        return outerRealmId;
    }

    public void setOuterRealmId(int outerRealmId) {
        this.outerRealmId = outerRealmId;
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

    public String getMasterPlayerId() {
        return masterPlayerId;
    }

    public void setMasterPlayerId(String masterPlayerId) {
        this.masterPlayerId = masterPlayerId;
    }

    public String getGuildNotice() {
        return guildNotice;
    }

    public void setGuildNotice(String guildNotice) {
        this.guildNotice = guildNotice;
    }

    public int getGuildIcon() {
        return guildIcon;
    }

    public void setGuildIcon(int guildIcon) {
        this.guildIcon = guildIcon;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Map<String, NewGuildCenterPlayer> getPlayerIdToGuildPlayerInfo() {
        return playerIdToGuildPlayerInfo;
    }

    public void setPlayerIdToGuildPlayerInfo(Map<String, NewGuildCenterPlayer> playerIdToGuildPlayerInfo) {
        this.playerIdToGuildPlayerInfo = playerIdToGuildPlayerInfo;
    }

    public List<NewGuildTrack> getGuildTracks() {
        return guildTracks;
    }

    public void setGuildTracks(List<NewGuildTrack> guildTracks) {
        this.guildTracks = guildTracks;
    }

    public long getBattleValue() {
        return battleValue;
    }

    public void setBattleValue(long battleValue) {
        this.battleValue = battleValue;
    }

    @Override
    public String getOid() {
        return guildId;
    }

    @Override
    public void setOid(String oid) {
        this.guildId = oid;
    }

    @Override
    public NewGuild clone() {

        return new NewGuild(this.guildId, this.outerRealmId, this.simpleId, this.guildName, this.masterPlayerId, this.masterOuterRealmId, this.todayExp, this.guildNotice, this.guildIcon, this.createTime, this.battleValue, this.playerIdToGuildPlayerInfo, this.guildTracks, this.limitLevel, this.joinTypeEnum, this.giveJobTime, CloneUtils.cloneHashMap(typeAndInstitute), this.mailsCount, CloneUtils.cloneHashMap(typeAndSysRedBag), CloneUtils.cloneKListVHashMap(typeAndPlayerRedBag), this.rank);
    }
}
