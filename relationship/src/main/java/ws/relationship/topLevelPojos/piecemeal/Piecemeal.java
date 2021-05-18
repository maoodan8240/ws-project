package ws.relationship.topLevelPojos.piecemeal;

import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

/**
 * Created by zhangweiwei on 17-4-12.
 */
public class Piecemeal extends PlayerTopLevelPojo {
    private static final long serialVersionUID = -692998520146291101L;
    private int maxGuideId; // 最大新手引导值

    public Piecemeal() {
    }

    public Piecemeal(String playerId) {
        super(playerId);
    }

    public int getMaxGuideId() {
        return maxGuideId;
    }

    public void setMaxGuideId(int maxGuideId) {
        this.maxGuideId = maxGuideId;
    }
}
