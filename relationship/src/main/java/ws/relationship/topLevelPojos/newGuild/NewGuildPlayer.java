package ws.relationship.topLevelPojos.newGuild;

import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lee on 16-11-30.
 */
public class NewGuildPlayer extends PlayerTopLevelPojo {
    private static final long serialVersionUID = 1970742354403781622L;

    private String guildId;//帮会id
    private long exitTime; //退出帮会时间
    private List<String> applyGuildIds = new ArrayList<>(); // 已经发出申请的帮会
    private int researchTimes; //捐献次数
    private int trainerCount;    //社团训练所位置开启个数
    private Map<GuildRedBagTypeEnum, Integer> redBagTypeAndCount = new HashMap<>();     //社团发红包数量 --每日重置
    private int grabTimes; // 抢红包次数 --每日重置


    public NewGuildPlayer() {
    }

    public int getGrabTimes() {
        return grabTimes;
    }

    public void setGrabTimes(int grabTimes) {
        this.grabTimes = grabTimes;
    }

    public Map<GuildRedBagTypeEnum, Integer> getRedBagTypeAndCount() {
        return redBagTypeAndCount;
    }

    public void setRedBagTypeAndCount(Map<GuildRedBagTypeEnum, Integer> redBagTypeAndCount) {
        this.redBagTypeAndCount = redBagTypeAndCount;
    }

    public int getTrainerCount() {
        return trainerCount;
    }

    public void setTrainerCount(int trainerCount) {
        this.trainerCount = trainerCount;
    }

    public NewGuildPlayer(String playerId) {
        super(playerId);
    }

    public int getResearchTimes() {
        return researchTimes;
    }

    public void setResearchTimes(int researchTimes) {
        this.researchTimes = researchTimes;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public long getExitTime() {
        return exitTime;
    }

    public void setExitTime(long exitTime) {
        this.exitTime = exitTime;
    }

    public List<String> getApplyGuildIds() {
        return applyGuildIds;
    }

    public void setApplyGuildIds(List<String> applyGuildIds) {
        this.applyGuildIds = applyGuildIds;
    }
}
