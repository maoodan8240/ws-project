package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.table.table.utils.CellParser;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.formula.FormulaContainer;
import ws.common.utils.formula.FormulaContainerUtils;
import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.base.HeroAttrs;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.topLevelPojos.heros.Equipment;
import ws.relationship.utils.attrs.HeroAttrsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table_BadgeCheats_Row extends AbstractRow {
    private static final long serialVersionUID = -5559023796736320375L;
    private static FormulaContainer formulaContainer = GlobalInjector.getInstance(FormulaContainer.class);

    /**
     * string 特殊装备ICON
     */
    private String specialEquIcon;
    /**
     * string 觉醒后ICON
     */
    private String awakeIcon;
    /**
     * int 卡片ID
     */
    private Integer cardId;
    /**
     * int 特殊装备部位
     */
    private Integer specialEquPos;
    /**
     * string 攻击加成
     */
    private String attackPlus;
    /**
     * string 生命加成
     */
    private String hPPlus;
    /**
     * int 攻击加成步长
     */
    private Integer attackPlusStep;
    /**
     * int 生命加成步长
     */
    private Integer hPPlusStep;
    /**
     * string 1星消耗
     */
    private TupleListCell<Integer> awake1Consume;
    /**
     * string 2星消耗
     */
    private TupleListCell<Integer> awake2Consume;
    /**
     * string 3星消耗
     */
    private TupleListCell<Integer> awake3Consume;
    /**
     * string 4星消耗
     */
    private TupleListCell<Integer> awake4Consume;
    /**
     * string 5星消耗
     */
    private TupleListCell<Integer> awake5Consume;

    private HeroAttrs heroAttrs = new HeroAttrs();
    /**
     * 公式的属性
     */
    private static final List<HeroAttrTypeEnum> formulaAttrs = new ArrayList<HeroAttrTypeEnum>() {
        private static final long serialVersionUID = 6959701074549862171L;

        {
            add(HeroAttrTypeEnum.AttackPlus);
            add(HeroAttrTypeEnum.HPPlus);
        }
    };

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"特殊装备ID"}
        specialEquIcon = CellParser.parseSimpleCell("SpecialEquIcon", map, String.class); //string
        awakeIcon = CellParser.parseSimpleCell("AwakeIcon", map, String.class); //string
        cardId = CellParser.parseSimpleCell("CardId", map, Integer.class); //int
        specialEquPos = CellParser.parseSimpleCell("SpecialEquPos", map, Integer.class); //int
        attackPlus = CellParser.parseSimpleCell("AttackPlus", map, String.class); //string
        hPPlus = CellParser.parseSimpleCell("HPPlus", map, String.class); //string
        attackPlusStep = CellParser.parseSimpleCell("AttackPlusStep", map, Integer.class); //int
        hPPlusStep = CellParser.parseSimpleCell("HPPlusStep", map, Integer.class); //int
        awake1Consume = CellParser.parseTupleListCell("Awake1Consume", map, Integer.class); //string
        awake2Consume = CellParser.parseTupleListCell("Awake2Consume", map, Integer.class); //string
        awake3Consume = CellParser.parseTupleListCell("Awake3Consume", map, Integer.class); //string
        awake4Consume = CellParser.parseTupleListCell("Awake4Consume", map, Integer.class); //string
        awake5Consume = CellParser.parseTupleListCell("Awake5Consume", map, Integer.class); //string


        parseHeroAttrs(map);
    }


    private String generateFuncName(String fieldName) {
        return FormulaContainerUtils.autoGenerateFuncName(Table_BadgeCheats_Row.class, fieldName, getId());
    }

    private void _addToFormulaContainer(String fieldName, String funcScript) {
        formulaContainer.addFunc(generateFuncName(fieldName), funcScript);
    }


    private void parseHeroAttrs(Map<String, String> map) {
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

    /**
     * 获取特殊装备提供的属性(计算步长后)，包括升级，升品，升星后的属性
     *
     * @param equipment
     * @return
     */
    public HeroAttrs getBadgeCheatsHeroAttrs(Equipment equipment) {
        HeroAttrs heroAttrsTmp = new HeroAttrs();
        heroAttrsTmp.addAll(heroAttrs);
        formulaAttrs.forEach(attrType -> {
            long formulaValue = _getformulaAttrValue(attrType, equipment.getStarLv(), equipment.getQualityLv());
            heroAttrsTmp.add(new HeroAttr(attrType, formulaValue));
        });
        HeroAttrsUtils.calcuAndConvertAllStepAttr(heroAttrsTmp, equipment.getLv() - 1, false);
        HeroAttrsUtils.removeStepAttr(heroAttrsTmp);
        return heroAttrsTmp;
    }


    /**
     * 获取属性值
     *
     * @param attrType
     * @param specialEquStar
     * @param specialEquQuality
     * @return
     */
    private long _getformulaAttrValue(HeroAttrTypeEnum attrType, int specialEquStar, int specialEquQuality) {
        String funcName = generateFuncName(attrType.name());
        if (!formulaContainer.contains(funcName)) {
            return 0;
        }
        Object rs = formulaContainer.invokeFunction(funcName, new String[]{"SpecialEquStar", "SpecialEquQuality"}, new Object[]{specialEquStar, specialEquQuality});
        return (long) FormulaContainerUtils.convertToDouble(rs, 0l);
    }


    public EquipmentPositionEnum getSpecialEquPos() {
        return EquipmentPositionEnum.valueOf(specialEquPos);
    }

    public IdMaptoCount getAwake1Consume() {
        return IdMaptoCount.parse(awake1Consume);
    }

    public IdMaptoCount getAwake2Consume() {
        return IdMaptoCount.parse(awake2Consume);
    }

    public IdMaptoCount getAwake3Consume() {
        return IdMaptoCount.parse(awake3Consume);
    }

    public IdMaptoCount getAwake4Consume() {
        return IdMaptoCount.parse(awake4Consume);
    }

    public IdMaptoCount getAwake5Consume() {
        return IdMaptoCount.parse(awake5Consume);
    }

    public IdMaptoCount getUpStarLvConsumes(int curStarLv) {
        switch (curStarLv) {
            case MagicNumbers.STAR_NUM_0:
                return getAwake1Consume();
            case MagicNumbers.STAR_NUM_1:
                return getAwake2Consume();
            case MagicNumbers.STAR_NUM_2:
                return getAwake3Consume();
            case MagicNumbers.STAR_NUM_3:
                return getAwake4Consume();
            case MagicNumbers.STAR_NUM_4:
                return getAwake5Consume();
        }
        String msg = String.format("装备EF星级消耗不支持的星级 curStarLv=%s !", curStarLv);
        throw new BusinessLogicMismatchConditionException(msg);
    }
}
