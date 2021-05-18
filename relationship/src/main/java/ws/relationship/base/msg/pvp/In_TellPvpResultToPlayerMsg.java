package ws.relationship.base.msg.pvp;


import ws.common.utils.message.implement.AbstractInnerMsg;

/**
 * Created by zb on 8/24/16.
 */
public class In_TellPvpResultToPlayerMsg extends AbstractInnerMsg {
    public In_TellPvpResultToPlayerMsg(String id, boolean result, int rankChenge, int level, int combat, String name) {
        this.id = id;
        this.result = result;
        this.rankChenge = rankChenge;
        this.level = level;
        this.combat = combat;
        this.name = name;
    }

    private String id;
    private int rank;
    private boolean result;
    private int rankChenge;
    private int level;
    private int combat;
    private String name;


    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getRankChenge() {
        return rankChenge;
    }

    public void setRankChenge(int rankChenge) {
        this.rankChenge = rankChenge;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCombat() {
        return combat;
    }

    public void setCombat(int combat) {
        this.combat = combat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
