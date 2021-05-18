package ws.relationship.topLevelPojos.simplePlayer;

import ws.protos.EnumsProtos.HeroPositionEnum;
import ws.relationship.topLevelPojos.heros.Hero;

import java.io.Serializable;

public class SimplePlayerMfHero implements Serializable {
    private static final long serialVersionUID = -6304538106622693120L;

    private HeroPositionEnum pos; // 武将站位
    private int heroTpId;         // 武将模板ID
    private int lv;               // 武将等级
    private int qualityLv;        // 武将品级
    private int starLv;           // 武将星级


    public SimplePlayerMfHero() {
    }

    public SimplePlayerMfHero(HeroPositionEnum pos, Hero hero) {
        this.pos = pos;
        this.heroTpId = hero.getTpId();
        this.lv = hero.getLv();
        this.qualityLv = hero.getQualityLv();
        this.starLv = hero.getStarLv();
    }

    public SimplePlayerMfHero(HeroPositionEnum pos, int heroTpId, int lv, int qualityLv, int starLv) {
        this.pos = pos;
        this.heroTpId = heroTpId;
        this.lv = lv;
        this.qualityLv = qualityLv;
        this.starLv = starLv;
    }

    public HeroPositionEnum getPos() {
        return pos;
    }

    public void setPos(HeroPositionEnum pos) {
        this.pos = pos;
    }

    public int getHeroTpId() {
        return heroTpId;
    }

    public void setHeroTpId(int heroTpId) {
        this.heroTpId = heroTpId;
    }

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        this.lv = lv;
    }

    public int getQualityLv() {
        return qualityLv;
    }

    public void setQualityLv(int qualityLv) {
        this.qualityLv = qualityLv;
    }

    public int getStarLv() {
        return starLv;
    }

    public void setStarLv(int starLv) {
        this.starLv = starLv;
    }
}
