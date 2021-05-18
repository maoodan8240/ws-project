package ws.relationship.topLevelPojos.hof;

import java.io.Serializable;

/**
 * Created by lee on 17-2-6.
 */
public class HOF_Info implements Serializable {
    private static final long serialVersionUID = -3866031798998208687L;

    /**
     * 好感度阶段
     */
    private int favorStage = 1;
    /**
     * 好感度等级
     */
    private int favorLevel;
    /**
     * 好感度经验
     */
    private long ovfFavoExp;

    /**
     * 英雄Id
     */
    private int heroId;

    /**
     * 已经添加过的好感度经验
     */
    private long hasExp;

    public HOF_Info() {
    }

    public HOF_Info(int favorStage, int favorLevel, long ovfFavoExp, int heroId, long hasExp) {
        this.favorStage = favorStage;
        this.favorLevel = favorLevel;
        this.ovfFavoExp = ovfFavoExp;
        this.heroId = heroId;
        this.hasExp = hasExp;
    }

    public int getHeroId() {
        return heroId;
    }

    public void setHeroId(int heroId) {
        this.heroId = heroId;
    }

    public int getFavorStage() {
        return favorStage;
    }

    public void setFavorStage(int favorStage) {
        this.favorStage = favorStage;
    }

    public int getFavorLevel() {
        return favorLevel;
    }

    public void setFavorLevel(int favorLevel) {
        this.favorLevel = favorLevel;
    }

    public long getOvfFavoExp() {
        return ovfFavoExp;
    }

    public void setOvfFavoExp(long ovfFavoExp) {
        this.ovfFavoExp = ovfFavoExp;
    }

    public long getHasExp() {
        return hasExp;
    }

    public void setHasExp(long hasExp) {
        this.hasExp = hasExp;
    }

    @Override
    public HOF_Info clone() {
        return new HOF_Info(this.favorStage, this.favorLevel, this.ovfFavoExp, this.heroId, this.hasExp);
    }
}
