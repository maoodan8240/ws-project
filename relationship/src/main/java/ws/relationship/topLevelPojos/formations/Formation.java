package ws.relationship.topLevelPojos.formations;

import ws.protos.EnumsProtos.FormationTypeEnum;
import ws.protos.EnumsProtos.HeroPositionEnum;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * <pre>
 * 武将站位
 * HERO_POSITION_FOUR HERO_POSITION_ONE
 * HERO_POSITION_FIVE HERO_POSITION_TWO
 * HERO_POSITION_SIX HERO_POSITION_THREE
 * </pre>
 */
public class Formation implements Serializable {
    private static final long serialVersionUID = -7524225727612468399L;

    private FormationTypeEnum type;
    private LinkedHashMap<HeroPositionEnum, FormationPos> posToFormationPos = new LinkedHashMap<>(); // 位置-武将


    public Formation() {
    }

    public Formation(FormationTypeEnum type) {
        this.type = type;
    }

    public FormationTypeEnum getType() {
        return type;
    }

    public void setType(FormationTypeEnum type) {
        this.type = type;
    }

    public LinkedHashMap<HeroPositionEnum, FormationPos> getPosToFormationPos() {
        return posToFormationPos;
    }

    public void setPosToFormationPos(LinkedHashMap<HeroPositionEnum, FormationPos> posToFormationPos) {
        this.posToFormationPos = posToFormationPos;
    }
}
