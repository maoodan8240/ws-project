package ws.relationship.topLevelPojos.soulBox;

import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 7/4/17.
 */
public class SoulBox extends PlayerTopLevelPojo {
    private static final long serialVersionUID = -191025076813877603L;
    private int consume;                    //活动消耗累计 --抽中魂匣子后重置
    private List<Integer> soulBoxHeroIds = new ArrayList<>();   // 抽中的魂匣英雄ID --选择后重置

    public SoulBox() {
    }

    public SoulBox(String playerId) {
        super(playerId);
    }

    public int getConsume() {
        return consume;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }

    public List<Integer> getSoulBoxHeroIds() {
        return soulBoxHeroIds;
    }

    public void setSoulBoxHeroIds(List<Integer> soulBoxHeroIds) {
        this.soulBoxHeroIds = soulBoxHeroIds;
    }
}
