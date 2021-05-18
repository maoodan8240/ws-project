package ws.relationship.topLevelPojos.pvp.arena;

import java.io.Serializable;


public class ArenaEnemy implements Serializable {
    private static final long serialVersionUID = 4352882956634585662L;

    private String playerId;       // id
    private int rank;           // 排名
    private int victoryTimes;   // 胜利的次数
    private String declaration; // 宣言

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getVictoryTimes() {
        return victoryTimes;
    }

    public void setVictoryTimes(int victoryTimes) {
        this.victoryTimes = victoryTimes;
    }

    public String getDeclaration() {
        return declaration;
    }

    public void setDeclaration(String declaration) {
        this.declaration = declaration;
    }
}
