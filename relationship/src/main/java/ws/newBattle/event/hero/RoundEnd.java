package ws.newBattle.event.hero;

import ws.newBattle.event.Event;

/**
 * 回合结束（所有人出手结束）
 */
public class RoundEnd implements Event {
    private int round;

    public RoundEnd(int round) {
        this.round = round;
    }

}
