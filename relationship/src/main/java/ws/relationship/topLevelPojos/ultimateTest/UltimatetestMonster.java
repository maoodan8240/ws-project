package ws.relationship.topLevelPojos.ultimateTest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * Created by lee on 17-3-30.
 */
public class UltimatetestMonster implements Serializable {
    private static final long serialVersionUID = -5801073161715551009L;
    private int monsterId;
    private int level;
    private int monsterStar;
    private int monsterGrade;


    public UltimatetestMonster() {
    }

    public UltimatetestMonster(int monsterId, int level, int monsterStar, int monsterGrade) {
        this.monsterId = monsterId;
        this.level = level;
        this.monsterStar = monsterStar;
        this.monsterGrade = monsterGrade;
    }

    public void setMonsterId(int monsterId) {
        this.monsterId = monsterId;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setMonsterStar(int monsterStar) {
        this.monsterStar = monsterStar;
    }

    public void setMonsterGrade(int monsterGrade) {
        this.monsterGrade = monsterGrade;
    }

    public int getMonsterStar() {
        return monsterStar;
    }

    public int getMonsterGrade() {
        return monsterGrade;
    }

    public int getMonsterId() {
        return monsterId;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
