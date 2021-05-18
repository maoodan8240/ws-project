package ws.newBattle.event.hero;

import ws.newBattle.event.Event;

/**
 * 回合刚刚开始（所有人出手前）
 */
public class RoundBegin implements Event {
    private int round;

    public RoundBegin(int round) {
        this.round = round;
    }

}
