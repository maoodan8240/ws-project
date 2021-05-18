package ws.relationship.utils.attrs;

import ws.protos.EnumsProtos.SystemModuleTypeEnum;
import ws.relationship.base.HeroAttrs;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_New_Card_Row;
import ws.relationship.table.tableRows.Table_New_Skill_Row;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.heros.Heros;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by zhangweiwei on 17-4-27.
 */
public class OneHeroAttrs {
    private Hero belongHero;
    private Heros belongTarget;

    private HeroAttrs heroAttrs_Base = new HeroAttrs();                                      // Base
    private HeroAttrs heroAttrs_Equip = new HeroAttrs();                                     // 装备
    private HeroAttrs heroAttrs_Skill = new HeroAttrs();                                     // 技能
    private HeroAttrs heroAttrs_Yoke = new HeroAttrs();                                      // 缘分
    private Map<HeroAttrsRange, HeroAttrs> rangeToHeroAttrs_WarSoul = new HashMap<>();       // 战魂

    public OneHeroAttrs(Hero belongHero, Heros belongTarget) {
        clearAll();
        this.belongHero = belongHero;
        this.belongTarget = belongTarget;
    }

    public void calcuAll() {
        clearAll();

        calcuBaseAttr();
        calcuEquipAttr();
        calcuSkillAttr();
        calcuYokeAttr();
        calcuWarSoulAttr();
    }

    public void calcuBaseAttr() {
        heroAttrs_Base.clear();
        Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, belongHero.getTpId());
        heroAttrs_Base.addAll(cardRow.getHeroBaseAttrs(this.belongHero));
    }

    public void calcuEquipAttr() {
        heroAttrs_Equip.clear();
        heroAttrs_Equip.addAll(HeroAttrsUtils.calcuHeroEquipAtts(this.belongHero));
    }

    public void calcuSkillAttr() {
        heroAttrs_Skill.clear();
        heroAttrs_Skill.addAll(Table_New_Skill_Row.getHeroAllSkillHeroAttrs(this.belongHero));
    }


    public void calcuYokeAttr() {
        heroAttrs_Yoke.clear();
        // -- Card
        heroAttrs_Yoke.addAll(HeroAttrsUtils.calcuAllYokeAttr_Card(belongHero, belongTarget.getIdToHero()));
        // -- EF
        heroAttrs_Yoke.addAll(HeroAttrsUtils.calcuAllYokeAttr_Equip_EF(belongHero));
    }

    public void calcuWarSoulAttr() {
        rangeToHeroAttrs_WarSoul.clear();
        rangeToHeroAttrs_WarSoul.putAll(HeroAttrsUtils.calcuWarSoulAttr(belongHero));
    }


    public void mergeAllFixAttrs(HeroAttrs heroAttrs, SystemModuleTypeEnum moduleType) {
        mergeAllAttrs(heroAttrs, moduleType, true);
    }

    public void mergeAllPrecentAttrs(HeroAttrs heroAttrs, SystemModuleTypeEnum moduleType) {
        mergeAllAttrs(heroAttrs, moduleType, false);
    }

    public void mergeAllAttrs(HeroAttrs heroAttrs, SystemModuleTypeEnum moduleType, boolean isFiex) {
        HeroAttrsUtils.mergeAttrs(heroAttrs, heroAttrs_Base, isFiex);
        HeroAttrsUtils.mergeAttrs(heroAttrs, heroAttrs_Equip, isFiex);
        HeroAttrsUtils.mergeAttrs(heroAttrs, heroAttrs_Skill, isFiex);
        HeroAttrsUtils.mergeAttrs(heroAttrs, heroAttrs_Yoke, isFiex);
        mergeWarSoulAttrs(heroAttrs, moduleType, isFiex);
    }


    public void mergeWarSoulAttrs(HeroAttrs heroAttrs, SystemModuleTypeEnum moduleType, boolean isFiex) {
        rangeToHeroAttrs_WarSoul.forEach((range, attrs) -> {
            if (range.getModuleType() == moduleType) {
                HeroAttrsUtils.mergeAttrs(heroAttrs, heroAttrs_Yoke, isFiex);
            }
        });
    }


    public Hero getBelongHero() {
        return belongHero;
    }

    private void clearAll() {
        heroAttrs_Base.clear();
        heroAttrs_Equip.clear();
        heroAttrs_Skill.clear();
        heroAttrs_Yoke.clear();
        rangeToHeroAttrs_WarSoul.clear();
    }


    public String print() {
        String pc = "";
        pc += "基础属性 : \n" + HeroAttrsUtils.printHeroAttrs(this.heroAttrs_Base);
        pc += "装备属性 : \n" + HeroAttrsUtils.printHeroAttrs(this.heroAttrs_Equip);
        pc += "技能属性 : \n" + HeroAttrsUtils.printHeroAttrs(this.heroAttrs_Skill);
        pc += "缘分属性 : \n" + HeroAttrsUtils.printHeroAttrs(this.heroAttrs_Yoke);
        pc += "战魂属性 : \n";
        for (Entry<HeroAttrsRange, HeroAttrs> kv : this.rangeToHeroAttrs_WarSoul.entrySet()) {
            String k = "[" + kv.getKey().getModuleType() + "-" + kv.getKey().getFightTarget() + "-" + kv.getKey().getConditon() + "] : ";
            pc += k + HeroAttrsUtils.printHeroAttrs(this.heroAttrs_Yoke);
        }
        return pc;
    }
}
