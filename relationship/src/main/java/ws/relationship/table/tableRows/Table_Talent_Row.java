package ws.relationship.table.tableRows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.ListCell;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.protos.EnumsProtos.SystemModuleTypeEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.base.HeroAttrs;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table_Talent_Row extends AbstractRow {
    private static final long serialVersionUID = 5769236045748498073L;
    private static final Logger LOGGER = LoggerFactory.getLogger(Table_Talent_Row.class);
    /**
     * int 天赋ID
     */
    private Integer talentId;
    /**
     * string 天赋名称
     */
    private String talentNameId;
    /**
     * string 天赋描述
     */
    private String talentExplainId;
    /**
     * string 天赋ICON
     */
    private String talentIcon;
    /**
     * int 需要战队等级
     */
    private Integer needCorpsLv;
    /**
     * int 前置天赋ID
     */
    private Integer beforeTalent;
    /**
     * int 后置天赋ID
     */
    private Integer afterId;
    /**
     * int 影响玩法类型
     */
    private Integer playType;
    /**
     * int 天赋等级
     */
    private Integer talentLv;
    /**
     * int 所在天赋树ID
     */
    private Integer talentTreeId;
    /**
     * string 天赋类型
     */
    private String talentType;
    /**
     * arrayint 天赋范围
     */
    private ListCell<Integer> talentRange;
    /**
     * arrayint 天赋范围条件
     */
    private ListCell<Integer> talentRangeCondition;
    /**
     * arrayint 属性编号
     */
    private ListCell<Integer> qualityId;
    /**
     * arrayint 属性值
     */
    private ListCell<Integer> qualityNumber;
    /**
     * int 金币消耗
     */
    private Integer goldUse;
    /**
     * int 天赋点消耗
     */
    private Integer talentPointUse;


    public Integer getTalentId() {
        return talentId;
    }

    public String getTalentNameId() {
        return talentNameId;
    }

    public String getTalentExplainId() {
        return talentExplainId;
    }

    public String getTalentIcon() {
        return talentIcon;
    }

    public Integer getNeedCorpsLv() {
        return needCorpsLv;
    }

    public Integer getBeforeTalent() {
        return beforeTalent;
    }


    public Integer getAfterId() {
        return afterId;
    }

    public SystemModuleTypeEnum getPlayType() {
        return SystemModuleTypeEnum.valueOf(playType);
    }

    public Integer getTalentLv() {
        return talentLv;
    }

    public Integer getTalentTreeId() {
        return talentTreeId;
    }

    public String getTalentType() {
        return talentType;
    }

    public List<Integer> getTalentRange() {
        return talentRange.getAll();
    }

    public List<Integer> getTalentRangeCondition() {
        return talentRangeCondition.getAll();
    }


    public Integer getGoldUse() {
        return goldUse;
    }

    public Integer getTalentPointUse() {
        return talentPointUse;
    }


    public List<Integer> getQualityId() {
        return qualityId.getAll();
    }

    public List<Integer> getQualityNumber() {
        return qualityNumber.getAll();
    }

    /**
     * 天赋的单个增加的属性
     *
     * @param idx
     * @return
     */
    public HeroAttrs getHeroAttr(int idx) {
        HeroAttrs heroAttrs = new HeroAttrs();
        List<Integer> qualityIdLis = getQualityId();
        List<Integer> qualityNumberLis = getQualityNumber();
        if (idx >= qualityIdLis.size()) {
            LOGGER.error("id={} 请求的索引={} 超过配置的属性个数! qualityIdLis={} qualityNumberLis={}", id, idx, qualityIdLis, qualityNumberLis);
            return heroAttrs;
        }
        int arrId = qualityIdLis.get(idx);
        int arrValue = qualityNumberLis.get(idx);
        heroAttrs.add(new HeroAttr(HeroAttrTypeEnum.valueOf(arrId), arrValue));
        return heroAttrs;
    }

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"天赋等级ID"}
        talentId = CellParser.parseSimpleCell("TalentId", map, Integer.class); //int
        talentNameId = CellParser.parseSimpleCell("TalentNameId", map, String.class); //int
        talentExplainId = CellParser.parseSimpleCell("TalentExplainId", map, String.class); //int
        talentIcon = CellParser.parseSimpleCell("TalentIcon", map, String.class); //string
        needCorpsLv = CellParser.parseSimpleCell("NeedCorpsLv", map, Integer.class); //int
        beforeTalent = CellParser.parseSimpleCell("BeforeTalent", map, Integer.class); //int
        afterId = CellParser.parseSimpleCell("AfterId", map, Integer.class); //int
        playType = CellParser.parseSimpleCell("PlayType", map, Integer.class); //string
        talentLv = CellParser.parseSimpleCell("TalentLv", map, Integer.class); //int
        talentTreeId = CellParser.parseSimpleCell("TalentTreeId", map, Integer.class); //int
        talentType = CellParser.parseSimpleCell("TalentType", map, String.class); //string
        talentRange = CellParser.parseListCell("TalentRange", map, Integer.class); //int
        talentRangeCondition = CellParser.parseListCell("TalentRangeCondition", map, Integer.class); //int
        qualityId = CellParser.parseListCell("QualityId", map, Integer.class); //string
        qualityNumber = CellParser.parseListCell("QualityNumber", map, Integer.class); //string
        goldUse = CellParser.parseSimpleCell("GoldUse", map, Integer.class); //int
        talentPointUse = CellParser.parseSimpleCell("TalentPointUse", map, Integer.class); //int

        check();
    }


    private void check() {
        if (getQualityId().size() != getQualityNumber().size()) {
            String msg = String.format("id=%s qualityIdLis=%s qualityNumberLis=%s 个数不相等！", id, getQualityId(), getQualityNumber());
            throw new BusinessLogicMismatchConditionException(msg);
        }
    }

    public static List<Table_Talent_Row> getTalentTree(int level) {
        List<Table_Talent_Row> tree = new ArrayList<>();
        int talentId = RootTc.get(Table_Talent_Row.class).get(level).getTalentId();
        List<Table_Talent_Row> rowList = RootTc.get(Table_Talent_Row.class).values();
        for (Table_Talent_Row row : rowList) {
            if (row.getTalentId() == talentId) {
                tree.add(row);
            }
        }
        return tree;
    }
}
