package ws.relationship.topLevelPojos.newGuildCenter;

import ws.relationship.base.WsCloneable;

import java.io.Serializable;

/**
 * Created by lee on 5/18/17.
 */
public class NewGuildTrainer implements Serializable, WsCloneable<NewGuildTrainer> {
    private static final long serialVersionUID = -3127066035197526897L;

    private int index;                          // 训练桩位置
    private int heroId;                         // 格斗家id
    private long lastSettle;                    // 最后一次结算经验的时间


    public NewGuildTrainer() {
    }


    public NewGuildTrainer(int index) {
        this.index = index;
    }

    /**
     * for clone
     *
     * @param index
     * @param heroId
     * @param lastSettle
     */
    public NewGuildTrainer(int index, int heroId, long lastSettle) {
        this.index = index;
        this.heroId = heroId;
        this.lastSettle = lastSettle;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getHeroId() {
        return heroId;
    }

    public void setHeroId(int heroId) {
        this.heroId = heroId;
    }

    public long getLastSettle() {
        return lastSettle;
    }

    public void setLastSettle(long lastSettle) {
        this.lastSettle = lastSettle;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewGuildTrainer clone() {
        return new NewGuildTrainer(index, heroId, lastSettle);
    }

}
