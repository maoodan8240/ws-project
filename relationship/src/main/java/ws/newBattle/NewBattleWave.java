package ws.newBattle;


import ws.protos.EnumsProtos.BattleSideEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangweiwei on 16-8-24.
 */
public class NewBattleWave {
    protected int curWaveNum; // 当前第几波
    protected int curRoundNum; // 当前回合
    protected Map<BattleSideEnum, NewBattleSide> sideMap = new HashMap<>();

    public int getCurWaveNum() {
        return curWaveNum;
    }

    public void setCurWaveNum(int curWaveNum) {
        this.curWaveNum = curWaveNum;
    }

    public int getCurRoundNum() {
        return curRoundNum;
    }

    public void setCurRoundNum(int curRoundNum) {
        this.curRoundNum = curRoundNum;
    }

    public Map<BattleSideEnum, NewBattleSide> getSideMap() {
        return sideMap;
    }

    public void setSideMap(Map<BattleSideEnum, NewBattleSide> sideMap) {
        this.sideMap = sideMap;
    }
}
