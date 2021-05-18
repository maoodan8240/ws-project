package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.ListCell;
import ws.common.table.table.utils.CellParser;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.formula.FormulaContainer;
import ws.common.utils.formula.FormulaContainerUtils;
import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.protos.EnumsProtos.HeroAttrPosTypeEnum;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.protos.EnumsProtos.SexEnum;
import ws.protos.EnumsProtos.SkillPositionEnum;
import ws.protos.EnumsProtos.WarSoulPositionEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.base.HeroAttrs;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.utils.attrs.HeroAttrsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table_New_Card_Row extends AbstractRow {
    private static final long serialVersionUID = -6875138645815758540L;
    private static FormulaContainer formulaContainer = GlobalInjector.getInstance(FormulaContainer.class);


    /**
     * int 卡片类型
     */
    private Integer cardType;
    /**
     * int 卡片初始星级
     */
    private Integer cardStar;
    /**
     * string 卡片品质等级
     */
    private String cardQualityLevel;
    /**
     * int 卡片性别
     */
    private Integer cardSexy;
    /**
     * int 卡片资质
     */
    private Integer cardAptitude;

    /**
     * int 普攻
     */
    private Integer skillAtt;
    /**
     * int 小技能
     */
    private Integer smallSkill;
    /**
     * int 怒气技能
     */
    private Integer angerSkill;
    /**
     * int 被动1
     */
    private Integer skill1;
    /**
     * int 被动2
     */
    private Integer skill2;
    /**
     * int 被动3
     */
    private Integer skill3;
    /**
     * int 被动4
     */
    private Integer skill4;
    /**
     * int 技能觉醒
     */
    private Integer skillSwake;
    /**
     * int 技能觉醒影响技能类型
     */
    private Integer skillSwakeType;
    /**
     * arrayint 卡片缘分
     */
    private ListCell<Integer> cardYoke;
    /**
     * int 卡片战魂
     */
    private ListCell<Integer> cardWarSoul;
    /**
     * int 卡片碎片ID
     */
    private Integer cardPiece;
    /**
     * int 需求碎片数量
     */
    private Integer pieceNumber;

    /**
     * int 正卡转化碎片数量
     */
    private Integer convertNumber;

    /**
     * int 攻击比例
     */
    private Integer attackRate;
    /**
     * int 防御比例
     */
    private Integer defenseRate;
    /**
     * int 生命比例
     */
    private Integer hPRate;
    /**
     * int 武器ID
     */
    private Integer weaponId;
    /**
     * int 衣服ID
     */
    private Integer clothesId;
    /**
     * int 裤子ID
     */
    private Integer trousersId;
    /**
     * int 鞋子ID
     */
    private Integer shoesId;
    /**
     * int 徽章ID
     */
    private Integer badgeId;
    /**
     * int 秘籍ID
     */
    private Integer cheatsId;


    private HeroAttrs heroAttrs = new HeroAttrs();

    /**
     * 公式的属性
     */
    private static final List<HeroAttrTypeEnum> formulaAttrs = new ArrayList<HeroAttrTypeEnum>() {
        private static final long serialVersionUID = 6959701074549862171L;

        {
            add(HeroAttrTypeEnum.AttackStep);
            add(HeroAttrTypeEnum.DefenseStep);
            add(HeroAttrTypeEnum.HPStep);
        }
    };


    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"卡片ID"}
        cardType = CellParser.parseSimpleCell("CardType", map, Integer.class); //int
        cardStar = CellParser.parseSimpleCell("CardStar", map, Integer.class); //int
        cardQualityLevel = CellParser.parseSimpleCell("CardQualityLevel", map, String.class); //string
        cardSexy = CellParser.parseSimpleCell("CardSexy", map, Integer.class); //int
        cardAptitude = CellParser.parseSimpleCell("CardAptitude", map, Integer.class); //int
        skillAtt = CellParser.parseSimpleCell("SkillAtt", map, Integer.class); //int
        smallSkill = CellParser.parseSimpleCell("SmallSkill", map, Integer.class); //int
        angerSkill = CellParser.parseSimpleCell("AngerSkill", map, Integer.class); //int
        skill1 = CellParser.parseSimpleCell("Skill1", map, Integer.class); //int
        skill2 = CellParser.parseSimpleCell("Skill2", map, Integer.class); //int
        skill3 = CellParser.parseSimpleCell("Skill3", map, Integer.class); //int
        skill4 = CellParser.parseSimpleCell("Skill4", map, Integer.class); //int
        skillSwake = CellParser.parseSimpleCell("SkillSwake", map, Integer.class); //int
        skillSwakeType = CellParser.parseSimpleCell("SkillSwakeType", map, Integer.class); //int
        cardYoke = CellParser.parseListCell("CardYoke", map, Integer.class); //string
        cardWarSoul = CellParser.parseListCell("CardWarSoul", map, Integer.class); //string
        cardPiece = CellParser.parseSimpleCell("CardPiece", map, Integer.class); //int
        pieceNumber = CellParser.parseSimpleCell("PieceNumber", map, Integer.class); //int
        convertNumber = CellParser.parseSimpleCell("ConvertNumber", map, Integer.class); //int
        attackRate = CellParser.parseSimpleCell("AttackRate", map, Integer.class); //int
        defenseRate = CellParser.parseSimpleCell("DefenseRate", map, Integer.class); //int
        hPRate = CellParser.parseSimpleCell("HPRate", map, Integer.class); //int
        weaponId = CellParser.parseSimpleCell("WeaponId", map, Integer.class); //int
        clothesId = CellParser.parseSimpleCell("ClothesId", map, Integer.class); //int
        trousersId = CellParser.parseSimpleCell("TrousersId", map, Integer.class); //int
        shoesId = CellParser.parseSimpleCell("ShoesId", map, Integer.class); //int
        badgeId = CellParser.parseSimpleCell("BadgeId", map, Integer.class); //int
        cheatsId = CellParser.parseSimpleCell("CheatsId", map, Integer.class); //int
        parseHeroAttrs(map);
    }


    private String generateFuncName(String fieldName) {
        return FormulaContainerUtils.autoGenerateFuncName(Table_New_Card_Row.class, fieldName, getId());
    }

    private void _addToFormulaContainer(String fieldName, String funcScript) {
        formulaContainer.addFunc(generateFuncName(fieldName), funcScript);
    }


    /**
     * 获取武将Card中Base属性,包括Card升品，升星带来的属性
     *
     * @param hero
     * @return
     */
    public HeroAttrs getHeroBaseAttrs(Hero hero) {
        HeroAttrs heroAttrsTmp = new HeroAttrs();
        heroAttrsTmp.addAll(heroAttrs);
        formulaAttrs.forEach(attrType -> {
            long formulaValue = _getformulaAttrValue(attrType, heroAttrsTmp.get(HeroAttrTypeEnum.Attack), heroAttrsTmp.get(HeroAttrTypeEnum.Defense), heroAttrsTmp.get(HeroAttrTypeEnum.HP));
            heroAttrsTmp.add(new HeroAttr(attrType, formulaValue));
        });
        HeroAttrsUtils.calcuAndConvertAllStepAttr(heroAttrsTmp, hero.getLv() - 1, true);
        HeroAttrsUtils.removeStepAttr(heroAttrsTmp);
        heroAttrsTmp.replaceAll(HeroAttrsUtils.calcuHeroRateAttr(hero, heroAttrsTmp));
        return heroAttrsTmp;
    }

    /**
     * 攻防血步长公式计算
     *
     * @param attrType
     * @param defenseValue
     * @param hpValue
     * @return
     */
    private long _getformulaAttrValue(HeroAttrTypeEnum attrType, long attackValue, long defenseValue, long hpValue) {
        String funcName = generateFuncName(attrType.name());
        if (!formulaContainer.contains(funcName)) {
            return 0;
        }
        Object rs = formulaContainer.invokeFunction(generateFuncName(attrType.name()), new String[]{"Attack", "Defense", "HP"}, new Object[]{attackValue, defenseValue, hpValue});
        return FormulaContainerUtils.convertToLong(rs, 0l);
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
                if (formulaAttrs.contains(attrType)) {
                    _addToFormulaContainer(attrName, map.get(attrName));
                } else {
                    heroAttrs.add(new HeroAttr(attrType, Long.valueOf(map.get(attrName))));
                }
            }
        }
    }


    public HeroAttrPosTypeEnum getCardType() {
        return HeroAttrPosTypeEnum.valueOf(cardType);
    }

    public Integer getCardStar() {
        return cardStar;
    }

    public String getCardQualityLevel() {
        return cardQualityLevel;
    }

    public SexEnum getCardSexy() {
        return SexEnum.valueOf(cardSexy);
    }

    public Integer getCardAptitude() {
        return cardAptitude;
    }

    public Integer getSmallSkill() {
        return smallSkill;
    }

    public Integer getAngerSkill() {
        return angerSkill;
    }

    public Integer getSkill1() {
        return skill1;
    }

    public Integer getSkill2() {
        return skill2;
    }

    public Integer getSkill3() {
        return skill3;
    }

    public Integer getSkill4() {
        return skill4;
    }

    public Integer getSkillSwake() {
        return skillSwake;
    }

    public SkillPositionEnum getSkillSwakeType() {
        return SkillPositionEnum.valueOf(skillSwakeType);
    }

    public List<Integer> getCardYoke() {
        return cardYoke.getAll();
    }

    public List<Integer> getCardWarSoul() {
        return cardWarSoul.getAll();
    }

    public Integer getCardPiece() {
        return cardPiece;
    }

    public Integer getPieceNumber() {
        return pieceNumber;
    }

    public Integer getConvertNumber() {
        return convertNumber;
    }

    public Integer getAttackRate() {
        return attackRate;
    }

    public Integer getDefenseRate() {
        return defenseRate;
    }

    public Integer gethPRate() {
        return hPRate;
    }

    public Integer getWeaponId() {
        return weaponId;
    }

    public Integer getClothesId() {
        return clothesId;
    }

    public Integer getTrousersId() {
        return trousersId;
    }

    public Integer getShoesId() {
        return shoesId;
    }

    public Integer getBadgeId() {
        return badgeId;
    }

    public Integer getCheatsId() {
        return cheatsId;
    }

    public Integer getSkillAtt() {
        return skillAtt;
    }

    public HeroAttrs getHeroAttrs() {
        return new HeroAttrs(heroAttrs);
    }


    /**
     * 根据技能类型获取技能Id
     *
     * @param skillPos
     * @param awake    是否已经觉醒
     * @return
     */
    public int getSkillId(SkillPositionEnum skillPos, boolean awake) {
        if (awake) {
            if (skillPos == this.getSkillSwakeType()) {
                return this.getSkillSwake();
            }
        }
        switch (skillPos) {
            case Skill_POS_1:
                return this.getSmallSkill();
            case Skill_POS_2:
                return this.getAngerSkill();
            case Skill_POS_3:
                return this.getSkill1();
            case Skill_POS_4:
                return this.getSkill2();
            case Skill_POS_5:
                return this.getSkill3();
            case Skill_POS_6:
                return this.getSkill4();
        }
        String msg = String.format("skillType=%s，不支持的技能类型！", skillPos);
        throw new BusinessLogicMismatchConditionException(msg);
    }


    /**
     * 获取装备Id
     *
     * @param equipPos
     * @return
     */
    public int getEquipmentTpId(EquipmentPositionEnum equipPos) {
        switch (equipPos) {
            case POS_A:
                return weaponId;
            case POS_B:
                return clothesId;
            case POS_C:
                return trousersId;
            case POS_D:
                return shoesId;
            case POS_E:
                return badgeId;
            case POS_F:
                return cheatsId;
        }
        String msg = String.format("equipPos=%s，不支持的装备位置！", equipPos);
        throw new BusinessLogicMismatchConditionException(msg);
    }


    /**
     * 获取战魂Id
     *
     * @param pos
     * @return
     */
    public int getWarSoulId(WarSoulPositionEnum pos) {
        switch (pos) {
            case POS_Soul_A:
                return cardWarSoul.get(MagicNumbers.DEFAULT_ZERO);
            case POS_Soul_B:
                return cardWarSoul.get(MagicNumbers.DEFAULT_ONE);
            case POS_Soul_C:
                return cardWarSoul.get(MagicNumbers.DEFAULT_TWO);
            case POS_Soul_D:
                return cardWarSoul.get(MagicNumbers.DEFAULT_THREE);
        }
        String msg = String.format("pos=%s，不支持的战魂位置！", pos);
        throw new BusinessLogicMismatchConditionException(msg);
    }
}
