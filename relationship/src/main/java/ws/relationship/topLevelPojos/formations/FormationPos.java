package ws.relationship.topLevelPojos.formations;

import ws.protos.EnumsProtos.HeroPositionEnum;

import java.io.Serializable;

public class FormationPos implements Serializable {
    private static final long serialVersionUID = 352963544308709075L;

    private HeroPositionEnum pos;
    private int heroId = -1;

    public FormationPos() {
    }

    public FormationPos(HeroPositionEnum pos, int heroId) {
        this.pos = pos;
        this.heroId = heroId;
    }

    public FormationPos(HeroPositionEnum pos) {
        this.pos = pos;
    }

    public HeroPositionEnum getPos() {
        return pos;
    }

    public void setPos(HeroPositionEnum pos) {
        this.pos = pos;
    }

    public int getHeroId() {
        return heroId;
    }

    public void setHeroId(int heroId) {
        this.heroId = heroId;
    }
}
