package ws.relationship.topLevelPojos.ultimateTest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import ws.protos.EnumsProtos.HardTypeEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lee on 17-3-31.
 */
public class UltimatetestEnemy implements Serializable {
    private static final long serialVersionUID = -2482371718902766049L;
    private String playerName;
    private List<UltimatetestMonster> monsterList = new ArrayList<>();
    private HardTypeEnum hardTypeEnum;


    public UltimatetestEnemy() {
    }

    public UltimatetestEnemy(String playerName, List<UltimatetestMonster> monsterList, HardTypeEnum hardTypeEnum) {
        this.playerName = playerName;
        this.monsterList = monsterList;
        this.hardTypeEnum = hardTypeEnum;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public List<UltimatetestMonster> getMonsterList() {
        return monsterList;
    }

    public void setMonsterList(List<UltimatetestMonster> monsterList) {
        this.monsterList = monsterList;
    }

    public HardTypeEnum getHardTypeEnum() {
        return hardTypeEnum;
    }

    public void setHardTypeEnum(HardTypeEnum hardTypeEnum) {
        this.hardTypeEnum = hardTypeEnum;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
