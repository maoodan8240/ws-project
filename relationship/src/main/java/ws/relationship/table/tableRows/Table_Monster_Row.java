package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.protos.EnumsProtos.SkillPositionEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.base.HeroAttrs;
import ws.relationship.base.MagicNumbers;
import ws.relationship.topLevelPojos.heros.Hero;

import java.util.Map;

public class Table_Monster_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 卡片ID
     */
    private Integer cardId;
    /**
     * int 初始怒气
     */
    private Integer startAnger;
    /**
     * int 怒气
     */
    private Integer anger;
    /**
     * int 攻击怒气
     */
    private Integer hitAnger;
    /**
     * int 被击怒气
     */
    private Integer hitedAnger;
    /**
     * int 击杀怒气
     */
    private Integer killAnger;
    /**
     * int 攻击
     */
    private Integer attack;
    /**
     * int 防御
     */
    private Integer defense;
    /**
     * int 生命
     */
    private Integer hP;
    /**
     * int 伤害
     */
    private Integer damage;
    /**
     * int 免伤
     */
    private Integer damageOppose;
    /**
     * int 暴击
     */
    private Integer crit;
    /**
     * int 抗暴
     */
    private Integer critOppose;
    /**
     * int 暴击强度
     */
    private Integer critDamage;
    /**
     * int 破档
     */
    private Integer blockBreak;
    /**
     * int 格挡
     */
    private Integer block;
    /**
     * int 格挡强度
     */
    private Integer blockDamage;
    /**
     * int 治疗
     */
    private Integer cure;
    /**
     * int 吸血
     */
    private Integer suck;
    /**
     * int 反伤
     */
    private Integer thorns;
    /**
     * int 控制率
     */
    private Integer ctrl;
    /**
     * int 免控
     */
    private Integer freeCtrl;
    /**
     * int 是否物品怪
     */
    private Integer isObject;
    /**
     * string 形象模型阶段
     */
    private String models;
    /**
     * int 怪物是否为觉醒
     */
    private Integer isAwake;
    /**
     * int 小技能释放概率
     */
    private Integer smallSkillRate;
    /**
     * int 小技能等级
     */
    private Integer smallSkill;
    /**
     * int 怒气技能等级
     */
    private Integer angerSkill;
    /**
     * int 被动1等级
     */
    private Integer skill1;
    /**
     * int 被动2等级
     */
    private Integer skill2;
    /**
     * int 被动3等级
     */
    private Integer skill3;
    /**
     * int 被动4等级
     */
    private Integer skill4;
    /**
     * string 卡片战魂等级
     */
    private String cardWarSoul;


    private HeroAttrs heroAttrs = new HeroAttrs();

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"怪物ID"}
        cardId = CellParser.parseSimpleCell("CardId", map, Integer.class); //int
        startAnger = CellParser.parseSimpleCell("StartAnger", map, Integer.class); //int
        anger = CellParser.parseSimpleCell("Anger", map, Integer.class); //int
        hitAnger = CellParser.parseSimpleCell("HitAnger", map, Integer.class); //int
        hitedAnger = CellParser.parseSimpleCell("HitedAnger", map, Integer.class); //int
        killAnger = CellParser.parseSimpleCell("KillAnger", map, Integer.class); //int
        attack = CellParser.parseSimpleCell("Attack", map, Integer.class); //int
        defense = CellParser.parseSimpleCell("Defense", map, Integer.class); //int
        hP = CellParser.parseSimpleCell("HP", map, Integer.class); //int
        damage = CellParser.parseSimpleCell("Damage", map, Integer.class); //int
        damageOppose = CellParser.parseSimpleCell("DamageOppose", map, Integer.class); //int
        crit = CellParser.parseSimpleCell("Crit", map, Integer.class); //int
        critOppose = CellParser.parseSimpleCell("CritOppose", map, Integer.class); //int
        critDamage = CellParser.parseSimpleCell("CritDamage", map, Integer.class); //int
        blockBreak = CellParser.parseSimpleCell("BlockBreak", map, Integer.class); //int
        block = CellParser.parseSimpleCell("Block", map, Integer.class); //int
        blockDamage = CellParser.parseSimpleCell("BlockDamage", map, Integer.class); //int
        cure = CellParser.parseSimpleCell("Cure", map, Integer.class); //int
        suck = CellParser.parseSimpleCell("Suck", map, Integer.class); //int
        thorns = CellParser.parseSimpleCell("Thorns", map, Integer.class); //int
        ctrl = CellParser.parseSimpleCell("Ctrl", map, Integer.class); //int
        freeCtrl = CellParser.parseSimpleCell("FreeCtrl", map, Integer.class); //int
        isObject = CellParser.parseSimpleCell("IsObject", map, Integer.class); //int
        models = CellParser.parseSimpleCell("Models", map, String.class); //string
        isAwake = CellParser.parseSimpleCell("IsAwake", map, Integer.class); //int
        smallSkillRate = CellParser.parseSimpleCell("SmallSkillRate", map, Integer.class); //int
        smallSkill = CellParser.parseSimpleCell("SmallSkill", map, Integer.class); //int
        angerSkill = CellParser.parseSimpleCell("AngerSkill", map, Integer.class); //int
        skill1 = CellParser.parseSimpleCell("Skill1", map, Integer.class); //int
        skill2 = CellParser.parseSimpleCell("Skill2", map, Integer.class); //int
        skill3 = CellParser.parseSimpleCell("Skill3", map, Integer.class); //int
        skill4 = CellParser.parseSimpleCell("Skill4", map, Integer.class); //int
        cardWarSoul = CellParser.parseSimpleCell("CardWarSoul", map, String.class); //string
        parseHeroAttrs(map);
    }

    /**
     * 解析属性字段0519
     *
     * @param map
     */
    private void parseHeroAttrs(Map<String, String> map) {
        heroAttrs.clear();
        for (HeroAttrTypeEnum attrType : HeroAttrTypeEnum.values()) {
            String attrName = attrType.name();
            if (map.containsKey(attrName)) {
                heroAttrs.add(new HeroAttr(attrType, Long.valueOf(map.get(attrName))));
            }
        }
    }

    public HeroAttrs getHeroAttrs() {
        return getHeroAttrs(MagicNumbers.RANDOM_BASE_VALUE);
    }

    /**
     * @param multiple 以10000为基数
     * @return
     */
    public HeroAttrs getHeroAttrs(int multiple) {
        if (multiple == MagicNumbers.RANDOM_BASE_VALUE) {
            return heroAttrs;
        } else {
            HeroAttrs heroAttrNew = new HeroAttrs();
            heroAttrs.getRaw().forEach((type, value) -> {
                long newValue = (value * multiple) / MagicNumbers.RANDOM_BASE_VALUE;
                heroAttrNew.add(new HeroAttr(type, newValue));
            });
            return heroAttrNew;
        }
    }


    public Hero getHero() {
        Hero hero = new Hero(id, cardId);
        hero.setLv(MagicNumbers.DefaultValue_Hero_Level);
        // 忽略星级、品质
        setHeroSkill(hero);
        return hero;
    }

    /**
     * 设置怪物技能
     *
     * @param hero
     */
    public void setHeroSkill(Hero hero) {
        hero.getSkills().put(SkillPositionEnum.Skill_POS_1, smallSkill);
        hero.getSkills().put(SkillPositionEnum.Skill_POS_2, angerSkill);
        hero.getSkills().put(SkillPositionEnum.Skill_POS_3, skill1);
        hero.getSkills().put(SkillPositionEnum.Skill_POS_4, skill2);
        hero.getSkills().put(SkillPositionEnum.Skill_POS_5, skill3);
        hero.getSkills().put(SkillPositionEnum.Skill_POS_6, skill4);
    }
}
