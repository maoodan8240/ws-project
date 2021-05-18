package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.GuildResearchProjectTypeEnum;
import ws.relationship.table.RootTc;

import java.util.List;
import java.util.Map;

public class Table_GuildResearch_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 社团人数实验室经验
     */
    private Integer peopleLabExp;
    /**
     * int 精英人数实验室经验
     */
    private Integer elitePeopleLabExp;
    /**
     * int 社团人数实验室等级人数增加值
     */
    private Integer peopleLabAdd;
    /**
     * int 精英人数实验室等级人数增加值
     */
    private Integer elitePeopleLabAdd;
    /**
     * int 钻石红包经验
     */
    private Integer diamondRedBagExp;
    /**
     * int 金币红包经验
     */
    private Integer goldRedBagExp;
    /**
     * int 钻石红包总数量
     */
    private Integer diamondRedBagNum;
    /**
     * int 钻石红包总金额
     */
    private Integer diamondRedBagSum;
    /**
     * int 金币红包总数量
     */
    private Integer goldRedBagNum;
    /**
     * int 金币红包总金额
     */
    private Integer goldRedBagSum;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"ID"}
        peopleLabExp = CellParser.parseSimpleCell("PeopleLabExp", map, Integer.class); //int
        elitePeopleLabExp = CellParser.parseSimpleCell("ElitePeopleLabExp", map, Integer.class); //int
        peopleLabAdd = CellParser.parseSimpleCell("PeopleLabAdd", map, Integer.class); //int
        elitePeopleLabAdd = CellParser.parseSimpleCell("ElitePeopleLabAdd", map, Integer.class); //int
        diamondRedBagExp = CellParser.parseSimpleCell("DiamondRedBagExp", map, Integer.class); //int
        goldRedBagExp = CellParser.parseSimpleCell("GoldRedBagExp", map, Integer.class); //int
        diamondRedBagNum = CellParser.parseSimpleCell("DiamondRedBagNum", map, Integer.class); //int
        diamondRedBagSum = CellParser.parseSimpleCell("DiamondRedBagSum", map, Integer.class); //int
        goldRedBagNum = CellParser.parseSimpleCell("GoldRedBagNum", map, Integer.class); //int
        goldRedBagSum = CellParser.parseSimpleCell("GoldRedBagSum", map, Integer.class); //int

    }


    public Integer getPeopleLabExp() {
        return peopleLabExp;
    }

    public Integer getElitePeopleLabExp() {
        return elitePeopleLabExp;
    }

    public Integer getPeopleLabAdd() {
        return peopleLabAdd;
    }

    public Integer getElitePeopleLabAdd() {
        return elitePeopleLabAdd;
    }

    public Integer getDiamondRedBagExp() {
        return diamondRedBagExp;
    }

    public Integer getGoldRedBagExp() {
        return goldRedBagExp;
    }

    public Integer getDiamondRedBagNum() {
        return diamondRedBagNum;
    }

    public Integer getDiamondRedBagSum() {
        return diamondRedBagSum;
    }

    public Integer getGoldRedBagNum() {
        return goldRedBagNum;
    }

    public Integer getGoldRedBagSum() {
        return goldRedBagSum;
    }

    public static int getPeoleMaxLv() {
        List<Table_GuildResearch_Row> tables = RootTc.get(Table_GuildResearch_Row.class).values();
        int maxLv = 0;
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).getPeopleLabExp() != 0) {
                maxLv = tables.get(i).getId();
            }
        }
        return maxLv;
    }

    public static int getElitePeopleMaxLv() {
        List<Table_GuildResearch_Row> tables = RootTc.get(Table_GuildResearch_Row.class).values();
        int maxLv = 0;
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).getElitePeopleLabExp() != 0) {
                maxLv = tables.get(i).getId();
            }
        }
        return maxLv;
    }

    public static int getDiamondRedBagMaxLv() {
        List<Table_GuildResearch_Row> tables = RootTc.get(Table_GuildResearch_Row.class).values();
        int maxLv = 0;
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).getDiamondRedBagExp() != 0) {
                maxLv = tables.get(i).getId();
            }
        }
        return maxLv;
    }

    public static int getGoldRedBagMaxLv() {
        List<Table_GuildResearch_Row> tables = RootTc.get(Table_GuildResearch_Row.class).values();
        int maxLv = 0;
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).getGoldRedBagExp() != 0) {
                maxLv = tables.get(i).getId();
            }
        }
        return maxLv;
    }

    public static int getMaxLvByResearchProjectType(GuildResearchProjectTypeEnum researchProjectTypeEnum) {
        switch (researchProjectTypeEnum) {
            case GR_ELITE:
                return getElitePeopleMaxLv();
            case GR_MEMBER:
                return getPeoleMaxLv();
            case GR_V_MONEY:
                return getDiamondRedBagMaxLv();
            case GR_MONEY:
                return getGoldRedBagMaxLv();
        }

        return 0;
    }
}
