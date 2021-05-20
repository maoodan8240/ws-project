package ws.newBattle;

/**
 * 剩余血量大于50%
 * 7回合
 * 死亡人数不超过3
 * Created by lee on 16-8-24.
 */
public class NewBattleConfig {
    private int runRound;// 总共进行几回合
    private int waves;   // 几波怪
    private boolean keepStatusBetweenWaves; // 波之间攻击方是否保存状态


    //=========================================================================================
    //=========================================================================================


    public NewBattleConfig(int runRound, int waves) {
        this.runRound = runRound;
        this.waves = waves;
    }

    public NewBattleConfig(int runRound, int waves, boolean keepStatusBetweenWaves) {
        this.runRound = runRound;
        this.waves = waves;
        this.keepStatusBetweenWaves = keepStatusBetweenWaves;
    }

    public int getWaves() {
        return waves;
    }

    public int getRunRound() {
        return runRound;
    }

    public boolean isKeepStatusBetweenWaves() {
        return keepStatusBetweenWaves;
    }
}
