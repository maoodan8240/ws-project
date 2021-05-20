package ws.relationship.topLevelPojos.heros;

import ws.relationship.base.MagicNumbers;
import ws.relationship.base.WsCloneable;

import java.io.Serializable;

/**
 * Created by lee on 17-3-10.
 */
public class Equipment implements Serializable, WsCloneable<Equipment> {
    private static final long serialVersionUID = -8816764932821233496L;

    private int lv; // 装备等级
    private int qualityLv; // 装备品级
    private int starLv; // 装备星级/觉醒等级
    private long ovfExp; // 装备等级溢出经验值


    public Equipment() {
        this.lv = MagicNumbers.DefaultValue_Equipment_Level;
        this.qualityLv = MagicNumbers.DefaultValue_Equipment_Quality_Level;
        this.starLv = MagicNumbers.DefaultValue_Equipment_Star_Level;
        this.ovfExp = 0;
    }

    public Equipment(int lv, int qualityLv, int starLv, long ovfExp) {
        this.lv = lv;
        this.qualityLv = qualityLv;
        this.starLv = starLv;
        this.ovfExp = ovfExp;
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

    public long getOvfExp() {
        return ovfExp;
    }

    public void setOvfExp(long ovfExp) {
        this.ovfExp = ovfExp;
    }

    @Override
    public Equipment clone() {
        return new Equipment(lv, qualityLv, starLv, ovfExp);
    }
}
