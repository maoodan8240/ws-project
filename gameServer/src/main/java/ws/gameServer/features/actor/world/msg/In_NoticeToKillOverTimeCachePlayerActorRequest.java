package ws.gameServer.features.actor.world.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;

public class In_NoticeToKillOverTimeCachePlayerActorRequest extends AbstractInnerMsg {
    private static final long serialVersionUID = 1L;
    private int overTime; // 单位为分钟

    public In_NoticeToKillOverTimeCachePlayerActorRequest(int overTime) {
        this.overTime = overTime;
    }

    public int getOverTime() {
        return overTime;
    }
}
