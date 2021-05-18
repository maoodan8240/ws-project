package ws.relationship.topLevelPojos.heros;

import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.protos.EnumsProtos.SkillPositionEnum;
import ws.protos.EnumsProtos.WarSoulPositionEnum;
import ws.relationship.base.MagicNumbers;
import ws.relationship.topLevelPojos.common.LevelUpObj;
import ws.relationship.utils.CloneUtils;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class Hero implements Serializable {
    private static final long serialVersionUID = -5202522662617799798L;

    private int id;
    private int tpId;

    private int lv = MagicNumbers.DefaultValue_Hero_Level; // 武将等级
    private long ovfExp;    // 武将溢出经验值

    private int qualityLv; // 品级 -- 引用至ColorDetailTypeEnum的值
    private int starLv;    // 星级

    private LinkedHashMap<SkillPositionEnum, Integer> skills = new LinkedHashMap<>();       // 技能类型--等级
    private LinkedHashMap<WarSoulPositionEnum, LevelUpObj> souls = new LinkedHashMap<>();   // 战魂位置--等级
    private LinkedHashMap<EquipmentPositionEnum, Equipment> equips = new LinkedHashMap<>(); // 装备位置--装备

    public Hero() {
    }

    public Hero(int id, int tpId) {
        this.id = id;
        this.tpId = tpId;
    }

    public Hero(int id, int tpId, int lv, long ovfExp, int qualityLv, int starLv, LinkedHashMap<SkillPositionEnum, Integer> skills, LinkedHashMap<WarSoulPositionEnum, LevelUpObj> souls, LinkedHashMap<EquipmentPositionEnum, Equipment> equips) {
        this.id = id;
        this.tpId = tpId;
        this.lv = lv;
        this.ovfExp = ovfExp;
        this.qualityLv = qualityLv;
        this.starLv = starLv;
        this.skills = skills;
        this.souls = souls;
        this.equips = equips;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTpId() {
        return tpId;
    }

    public void setTpId(int tpId) {
        this.tpId = tpId;
    }

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        this.lv = lv;
    }

    public long getOvfExp() {
        return ovfExp;
    }

    public void setOvfExp(long ovfExp) {
        this.ovfExp = ovfExp;
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

    public LinkedHashMap<SkillPositionEnum, Integer> getSkills() {
        return skills;
    }

    public void setSkills(LinkedHashMap<SkillPositionEnum, Integer> skills) {
        this.skills = skills;
    }

    public LinkedHashMap<WarSoulPositionEnum, LevelUpObj> getSouls() {
        return souls;
    }

    public void setSouls(LinkedHashMap<WarSoulPositionEnum, LevelUpObj> souls) {
        this.souls = souls;
    }

    public LinkedHashMap<EquipmentPositionEnum, Equipment> getEquips() {
        return equips;
    }

    public void setEquips(LinkedHashMap<EquipmentPositionEnum, Equipment> equips) {
        this.equips = equips;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Hero clone() {
        return new Hero(id, tpId, lv, ovfExp, qualityLv, starLv,
                (LinkedHashMap<SkillPositionEnum, Integer>) skills.clone(), CloneUtils.cloneWsCloneableLinkedHashMap(souls), CloneUtils.cloneWsCloneableLinkedHashMap(equips));
    }
}
