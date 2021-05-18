package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.CardAptitudeTypeEnum;
import ws.protos.EnumsProtos.ColorDetailTypeEnum;
import ws.protos.EnumsProtos.HeroAttrPosTypeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;

import java.util.List;
import java.util.Map;

public class Table_CardUpgradeQuality_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 卡片类型
     */
    private Integer cardType;
    /**
     * int 卡片资质类型
     */
    private Integer cardAptitudeType;
    /**
     * int 卡片等级
     */
    private Integer cardLevel;
    /**
     * int 卡片品质
     */
    private Integer cardQuality;
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
     * int 材料4ID
     */
    private Integer material4Id;
    /**
     * int 材料4数量
     */
    private Integer material4Num;
    /**
     * int 材料5ID
     */
    private Integer material5Id;
    /**
     * int 材料5数量
     */
    private Integer material5Num;


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


    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"基础ID"}
        cardType = CellParser.parseSimpleCell("CardType", map, Integer.class); //int
        cardAptitudeType = CellParser.parseSimpleCell("CardAptitudeType", map, Integer.class); //int
        cardQuality = CellParser.parseSimpleCell("CardQuality", map, Integer.class); //int
        cardLevel = CellParser.parseSimpleCell("CardLevel", map, Integer.class); //int
        material1Id = CellParser.parseSimpleCell("Material1Id", map, Integer.class); //int
        material1Num = CellParser.parseSimpleCell("Material1Num", map, Integer.class); //int
        material2Id = CellParser.parseSimpleCell("Material2Id", map, Integer.class); //int
        material2Num = CellParser.parseSimpleCell("Material2Num", map, Integer.class); //int
        material3Id = CellParser.parseSimpleCell("Material3Id", map, Integer.class); //int
        material3Num = CellParser.parseSimpleCell("Material3Num", map, Integer.class); //int
        material4Id = CellParser.parseSimpleCell("Material4Id", map, Integer.class); //int
        material4Num = CellParser.parseSimpleCell("Material4Num", map, Integer.class); //int
        material5Id = CellParser.parseSimpleCell("Material5Id", map, Integer.class); //int
        material5Num = CellParser.parseSimpleCell("Material5Num", map, Integer.class); //int

        attackRate = CellParser.parseSimpleCell("AttackRate", map, Integer.class); //int
        defenseRate = CellParser.parseSimpleCell("DefenseRate", map, Integer.class); //int
        hPRate = CellParser.parseSimpleCell("HPRate", map, Integer.class); //int
    }


    public HeroAttrPosTypeEnum getCardType() {
        return HeroAttrPosTypeEnum.valueOf(cardType);
    }

    public CardAptitudeTypeEnum getCardAptitudeType() {
        return CardAptitudeTypeEnum.valueOf(cardAptitudeType);
    }

    public ColorDetailTypeEnum getCardQuality() {
        return ColorDetailTypeEnum.valueOf(cardQuality);
    }

    public IdAndCount getMaterial1() {
        return new IdAndCount(material1Id, material1Num);
    }


    public IdAndCount getMaterial2() {
        return new IdAndCount(material2Id, material2Num);
    }


    public IdAndCount getMaterial3() {
        return new IdAndCount(material3Id, material3Num);
    }


    public IdAndCount getMaterial4() {
        return new IdAndCount(material4Id, material4Num);
    }

    public IdAndCount getMaterial5() {
        return new IdAndCount(material5Id, material5Num);
    }

    public Integer getCardLevel() {
        return cardLevel;
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

    /**
     * 获取卡片升品需要的武将等级
     *
     * @param heroTpId
     * @param nextQualityLevel
     * @return
     */
    public static int getUpgradeNeedHeroLv(int heroTpId, ColorDetailTypeEnum nextQualityLevel) {
        Table_CardUpgradeQuality_Row row = getCardUpgradeQualityRow(heroTpId, nextQualityLevel);
        return row.getCardLevel();
    }


    /**
     * 根据品级查找适合的行数据
     *
     * @param heroTpId
     * @param qualityLevel
     * @return
     */
    public static Table_CardUpgradeQuality_Row getCardUpgradeQualityRow(int heroTpId, ColorDetailTypeEnum qualityLevel) {
        Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, heroTpId);
        int aptitude = cardRow.getCardAptitude();
        List<Table_CardUpgradeQuality_Row> rows = RootTc.getAll(Table_CardUpgradeQuality_Row.class);
        for (Table_CardUpgradeQuality_Row row : rows) {
            if (row.getCardType() == cardRow.getCardType() && row.getCardAptitudeType() == getHeroAptitudeType(aptitude) && row.getCardQuality() == qualityLevel) {
                return row;
            }
        }
        String msg = String.format("heroTpId=%s, cardType=%s, aptitude=%s qualityLevel=%s 适合的行数据！", heroTpId, cardRow.getCardType(), aptitude, qualityLevel);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 获取武将升品消耗
     *
     * @param heroTpId
     * @param nextQualityLevel 当前的下一个品级
     * @return
     */
    public static IdMaptoCount getUpgradeConsumes(int heroTpId, ColorDetailTypeEnum nextQualityLevel) {
        IdMaptoCount ret = new IdMaptoCount();
        Table_CardUpgradeQuality_Row row = getCardUpgradeQualityRow(heroTpId, nextQualityLevel);
        ret.add(row.getMaterial1()).add(row.getMaterial2()).add(row.getMaterial3()).add(row.getMaterial4()).add(row.getMaterial5());
        return ret;
    }


    /**
     * 获取卡片资质类型  1代表低于14资质  2代表14资质
     *
     * @param aptitude
     * @return
     */
    public static CardAptitudeTypeEnum getHeroAptitudeType(int aptitude) {
        if (aptitude < MagicNumbers.APTITUDE_14) {
            return CardAptitudeTypeEnum.LOWER_THAN_14;
        } else if (aptitude == MagicNumbers.APTITUDE_14) {
            return CardAptitudeTypeEnum.EQUAL_14;
        }
        String msg = String.format("aptitude=%s，未匹配上卡牌资质类型！", aptitude);
        throw new BusinessLogicMismatchConditionException(msg);
    }
}
