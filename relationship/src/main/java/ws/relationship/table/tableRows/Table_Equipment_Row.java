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

public class Table_Equipment_Row extends AbstractRow {
    private static final long serialVersionUID = 1459710374264865249L;
    private static FormulaContainer formulaContainer = GlobalInjector.getInstance(FormulaContainer.class);

    /**
     * string 装备ICON
     */
    private String equipmentIcon;
    /**
     * string 觉醒后ICON
     */
    private String awakeIcon;
    /**
     * int 装备部位
     */
    private Integer equipmentPos;
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
            add(HeroAttrTypeEnum.Attack);
            add(HeroAttrTypeEnum.Defense);
            add(HeroAttrTypeEnum.HP);
            add(HeroAttrTypeEnum.AttackStep);
            add(HeroAttrTypeEnum.DefenseStep);
            add(HeroAttrTypeEnum.HPStep);
        }
    };


    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"装备ID"}
        equipmentIcon = CellParser.parseSimpleCell("EquipmentIcon", map, String.class); //string
        awakeIcon = CellParser.parseSimpleCell("AwakeIcon", map, String.class); //string
        equipmentPos = CellParser.parseSimpleCell("EquipmentPos", map, Integer.class); //int

        awake1Consume = CellParser.parseTupleListCell("Awake1Consume", map, Integer.class); //string
        awake2Consume = CellParser.parseTupleListCell("Awake2Consume", map, Integer.class); //string
        awake3Consume = CellParser.parseTupleListCell("Awake3Consume", map, Integer.class); //string
        awake4Consume = CellParser.parseTupleListCell("Awake4Consume", map, Integer.class); //string
        awake5Consume = CellParser.parseTupleListCell("Awake5Consume", map, Integer.class); //string


        parseHeroAttrs(map);
    }


    private String generateFuncName(String fieldName) {
        return FormulaContainerUtils.autoGenerateFuncName(Table_Equipment_Row.class, fieldName, getId());
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
     * 获取装备提供的属性(计算步长后),包括升级，升品，升星后的属性
     *
     * @param equipment
     * @return
     */
    public HeroAttrs getEquipmentHeroAttrs(Equipment equipment) {
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
     * @param equipStar
     * @param equipQuality
     * @return
     */
    public long _getformulaAttrValue(HeroAttrTypeEnum attrType, int equipStar, int equipQuality) {
        String funcName = generateFuncName(attrType.name());
        if (!formulaContainer.contains(funcName)) {
            return 0;
        }
        Object rs = formulaContainer.invokeFunction(funcName, new String[]{"EquipStar", "EquipQuality"}, new Object[]{equipStar, equipQuality});
        return FormulaContainerUtils.convertToLong(rs, 0l);
    }


    public String getEquipmentIcon() {
        return equipmentIcon;
    }

    public String getAwakeIcon() {
        return awakeIcon;
    }

    public EquipmentPositionEnum getEquipmentPos() {
        return EquipmentPositionEnum.valueOf(equipmentPos);
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
        String msg = String.format("装备ABCD星级消耗不支持的星级 curStarLv=%s !", curStarLv);
        throw new BusinessLogicMismatchConditionException(msg);
    }
}
