package ws.relationship.topLevelPojos.newGuildCenter;

import com.alibaba.fastjson.annotation.JSONField;
import ws.protos.EnumsProtos.GuildJobEnum;
import ws.relationship.base.WsCloneable;
import ws.relationship.utils.CloneUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lee on 16-11-30.
 */
public class NewGuildCenterPlayer implements Serializable, WsCloneable {
    private static final long serialVersionUID = 8538206890983812122L;

    private String playerId;                                                 // 玩家id
    private int playerOuterReamlId;                                          // 玩家显示服号
    private long sumContribution;                                            // 累计贡献
    private int todayContribution;                                           // 当日贡献 --每日重置
    private GuildJobEnum job;                                                // 职位
    private long joinTime;                                                   // 加入帮派的时间
    @JSONField(serialize = false)
    private long loginTime;                                                  // 最近一次登陆时间
    @JSONField(serialize = false)
    private int lv;                                                          // 玩家等级
    private int accelerateTimes;                                             // 加速次数，每次加速给格斗家结算，每个加速次数是这个格斗家当前等级需要的经验的5% --每日重置
    private List<String> acceleratePlayerName = new ArrayList<>();                               // 被动加速的玩家列表(这个列表的size就是被动加速次数) --每日重置
    private Map<Integer, NewGuildTrainer> indexAndTrainer = new HashMap<>(); // 训练所位置对应训练桩
    private List<String> stampPlayerIds = new ArrayList<>();                 // 标记的好友


    public NewGuildCenterPlayer() {
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getPlayerOuterReamlId() {
        return playerOuterReamlId;
    }

    public void setPlayerOuterReamlId(int playerOuterReamlId) {
        this.playerOuterReamlId = playerOuterReamlId;
    }

    public long getSumContribution() {
        return sumContribution;
    }

    public void setSumContribution(long sumContribution) {
        this.sumContribution = sumContribution;
    }

    public int getTodayContribution() {
        return todayContribution;
    }

    public void setTodayContribution(int todayContribution) {
        this.todayContribution = todayContribution;
    }

    public GuildJobEnum getJob() {
        return job;
    }

    public void setJob(GuildJobEnum job) {
        this.job = job;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(long joinTime) {
        this.joinTime = joinTime;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        this.lv = lv;
    }

    public int getAccelerateTimes() {
        return accelerateTimes;
    }

    public void setAccelerateTimes(int accelerateTimes) {
        this.accelerateTimes = accelerateTimes;
    }

    public List<String> getAcceleratePlayerName() {
        return acceleratePlayerName;
    }

    public void setAcceleratePlayerName(List<String> acceleratePlayerName) {
        this.acceleratePlayerName = acceleratePlayerName;
    }

    public Map<Integer, NewGuildTrainer> getIndexAndTrainer() {
        return indexAndTrainer;
    }

    public void setIndexAndTrainer(Map<Integer, NewGuildTrainer> indexAndTrainer) {
        this.indexAndTrainer = indexAndTrainer;
    }

    public List<String> getStampPlayerIds() {
        return stampPlayerIds;
    }

    public void setStampPlayerIds(List<String> stampPlayerIds) {
        this.stampPlayerIds = stampPlayerIds;
    }

    /**
     * for clone
     *
     * @param playerId
     * @param sumContribution
     * @param todayContribution
     * @param job
     * @param joinTime
     * @param loginTime
     * @param lv
     * @param accelerateTimes
     * @param acceleratePlayerName
     * @param indexAndTrainer
     * @param stampPlayerIds
     */
    public NewGuildCenterPlayer(String playerId, int playerOuterReamlId, long sumContribution, int todayContribution, GuildJobEnum job, long joinTime, long loginTime, int lv, int accelerateTimes, List<String> acceleratePlayerName, Map<Integer, NewGuildTrainer> indexAndTrainer, List<String> stampPlayerIds) {
        this.playerId = playerId;
        this.playerOuterReamlId = playerOuterReamlId;
        this.sumContribution = sumContribution;
        this.todayContribution = todayContribution;
        this.job = job;
        this.joinTime = joinTime;
        this.loginTime = loginTime;
        this.lv = lv;
        this.accelerateTimes = accelerateTimes;
        this.acceleratePlayerName = acceleratePlayerName;
        this.indexAndTrainer = indexAndTrainer;
        this.stampPlayerIds = stampPlayerIds;
    }

    @Override
    public NewGuildCenterPlayer clone() {
        return new NewGuildCenterPlayer(playerId, playerOuterReamlId, sumContribution, todayContribution, job, joinTime, loginTime, lv, accelerateTimes, CloneUtils.cloneStringList(acceleratePlayerName), CloneUtils.
                cloneHashMap(indexAndTrainer), CloneUtils.
                cloneStringList(stampPlayerIds));
    }
}
