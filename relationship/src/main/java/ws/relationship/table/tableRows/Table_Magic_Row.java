package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.ColorDetailTypeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;

import java.util.Map;

public class Table_Magic_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 法宝名称
     */
    private Integer magicNameId;
    /**
     * int 法宝描述
     */
    private Integer magicExplainId;
    /**
     * string 法宝ICON
     */
    private String magicIcon;
    /**
     * int 卡片ID
     */
    private Integer cardId;
    /**
     * int 法宝觉醒显示
     */
    private Integer awakenShow;
    /**
     * int 法宝品质
     */
    private Integer magicQuality;
    /**
     * int 是否为法宝主将
     */
    private Integer ismagic;
    /**
     * int 前置法宝
     */
    private Integer beforeMagic;
    /**
     * int 后置法宝
     */
    private Integer afterMagic;
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
     * int 攻击步长
     */
    private Integer attackStep;
    /**
     * int 防御步长
     */
    private Integer defenseStep;
    /**
     * int 生命步长
     */
    private Integer hPStep;
    /**
     * int 普攻ID
     */
    private Integer commonId;
    /**
     * int 技能ID
     */
    private Integer skill;
    /**
     * int 大招ID
     */
    private Integer criId;
    /**
     * int 法宝资源编号
     */
    private Integer magicResources;
    /**
     * int 材料1ID
     */
    private Integer material1Id;
    /**
     * int 材料1数量
     */
    private Integer material1Num;
    /**
     * int 材料2ID
     */
    private Integer material2Id;
    /**
     * int 材料2数量
     */
    private Integer material2Num;
    /**
     * int 材料3ID
     */
    private Integer material3Id;
    /**
     * int 材料3数量
     */
    private Integer material3Num;
    /**
     * string 法宝觉醒属性编号
     */
    private String magicAwakenProperty;
    /**
     * string 法宝觉醒属性值
     */
    private String magicAwakenEffect;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"法宝ID"}
        magicNameId = CellParser.parseSimpleCell("MagicNameId", map, Integer.class); //int
        magicExplainId = CellParser.parseSimpleCell("MagicExplainId", map, Integer.class); //int
        magicIcon = CellParser.parseSimpleCell("MagicIcon", map, String.class); //string
        awakenShow = CellParser.parseSimpleCell("AwakenShow", map, Integer.class); //int
        cardId = CellParser.parseSimpleCell("CardId", map, Integer.class); //int
        magicQuality = CellParser.parseSimpleCell("MagicQuality", map, Integer.class); //int
        ismagic = CellParser.parseSimpleCell("Ismagic", map, Integer.class); //int
        beforeMagic = CellParser.parseSimpleCell("BeforeMagic", map, Integer.class); //int
        afterMagic = CellParser.parseSimpleCell("AfterMagic", map, Integer.class); //int
        attack = CellParser.parseSimpleCell("Attack", map, Integer.class); //int
        defense = CellParser.parseSimpleCell("Defense", map, Integer.class); //int
        hP = CellParser.parseSimpleCell("HP", map, Integer.class); //int
        attackStep = CellParser.parseSimpleCell("AttackStep", map, Integer.class); //int
        defenseStep = CellParser.parseSimpleCell("DefenseStep", map, Integer.class); //int
        hPStep = CellParser.parseSimpleCell("HPStep", map, Integer.class); //int
        commonId = CellParser.parseSimpleCell("CommonId", map, Integer.class); //int
        skill = CellParser.parseSimpleCell("Skill", map, Integer.class); //int
        criId = CellParser.parseSimpleCell("CriId", map, Integer.class); //int
        magicResources = CellParser.parseSimpleCell("MagicResources", map, Integer.class); //int
        material1Id = CellParser.parseSimpleCell("Material1Id", map, Integer.class); //int
        material1Num = CellParser.parseSimpleCell("Material1Num", map, Integer.class); //int
        material2Id = CellParser.parseSimpleCell("Material2Id", map, Integer.class); //int
        material2Num = CellParser.parseSimpleCell("Material2Num", map, Integer.class); //int
        material3Id = CellParser.parseSimpleCell("Material3Id", map, Integer.class); //int
        material3Num = CellParser.parseSimpleCell("Material3Num", map, Integer.class); //int
        magicAwakenProperty = CellParser.parseSimpleCell("MagicAwakenProperty", map, String.class); //string
        magicAwakenEffect = CellParser.parseSimpleCell("MagicAwakenEffect", map, String.class); //string
    }

    public Integer getMagicNameId() {
        return magicNameId;
    }

    public Integer getMagicExplainId() {
        return magicExplainId;
    }

    public String getMagicIcon() {
        return magicIcon;
    }

    public Integer getAwakenShow() {
        return awakenShow;
    }

    public Integer getCardId() {
        return cardId;
    }

    public ColorDetailTypeEnum getMagicQuality() {
        return ColorDetailTypeEnum.valueOf(magicQuality);
    }

    public Integer getIsmagic() {
        return ismagic;
    }

    public Integer getBeforeMagic() {
        return beforeMagic;
    }

    public Integer getAfterMagic() {
        return afterMagic;
    }

    public Integer getAttack() {
        return attack;
    }

    public Integer getDefense() {
        return defense;
    }

    public Integer gethP() {
        return hP;
    }

    public Integer getAttackStep() {
        return attackStep;
    }

    public Integer getDefenseStep() {
        return defenseStep;
    }

    public Integer gethPStep() {
        return hPStep;
    }

    public Integer getCommonId() {
        return commonId;
    }

    public Integer getSkill() {
        return skill;
    }

    public Integer getCriId() {
        return criId;
    }

    public Integer getMagicResources() {
        return magicResources;
    }

    public Integer getMaterial1Id() {
        return material1Id;
    }

    public Integer getMaterial1Num() {
        return material1Num;
    }

    public Integer getMaterial2Id() {
        return material2Id;
    }

    public Integer getMaterial2Num() {
        return material2Num;
    }

    public Integer getMaterial3Id() {
        return material3Id;
    }

    public Integer getMaterial3Num() {
        return material3Num;
    }

    public String getMagicAwakenProperty() {
        return magicAwakenProperty;
    }

    public String getMagicAwakenEffect() {
        return magicAwakenEffect;
    }


    /**
     * 法宝觉醒到下一个觉醒等级的消耗材料
     *
     * @return
     */
    public IdMaptoCount getAwakeAllConsumes() {
        IdMaptoCount re = new IdMaptoCount();
        if (getMaterial1Id() > 0 && getMaterial1Num() > 0) {
            re.add(new IdAndCount(getMaterial1Id(), getMaterial1Num()));
        }
        if (getMaterial2Id() > 0 && getMaterial2Num() > 0) {
            re.add(new IdAndCount(getMaterial2Id(), getMaterial2Num()));
        }
        if (getMaterial3Id() > 0 && getMaterial3Num() > 0) {
            re.add(new IdAndCount(getMaterial3Id(), getMaterial3Num()));
        }
        return re;
    }

}
