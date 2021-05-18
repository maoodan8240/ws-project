package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.table.table.utils.CellParser;
import ws.relationship.base.IdAndCount;
import ws.relationship.table.RootTc;

import java.util.List;
import java.util.Map;


public class Table_Consume_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * string 购买消耗
     */
    private TupleCell<Integer> pVPChallengeConsume;
    /**
     * string 刷新消耗
     */
    private TupleCell<Integer> pVPRefreshConsume;

    /**
     * string 商店刷新消耗
     */
    private TupleCell<Integer> shopRefreshConsume;
    /**
     * string 试炼她宝箱消耗
     */
    private TupleCell<Integer> towerPracticeConsume;

    /**
     * 体力购买消耗
     */
    private TupleCell<Integer> spiritShopConsume;

    /**
     * Pve重置副本消耗购买
     */
    private TupleCell<Integer> pVERefreshConsume;

    /**
     * 技能点购买消耗
     */
    private TupleCell<Integer> skillConsume;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"主ID"}
        pVPChallengeConsume = CellParser.parseTupleCell("PVPChallengeConsume", map, Integer.class); //string
        pVPRefreshConsume = CellParser.parseTupleCell("PVPRefreshConsume", map, Integer.class); //string
        shopRefreshConsume = CellParser.parseTupleCell("ShopRefreshConsume", map, Integer.class); //string
        towerPracticeConsume = CellParser.parseTupleCell("TowerPracticeConsume", map, Integer.class); //string
        spiritShopConsume = CellParser.parseTupleCell("SpiritShopConsume", map, Integer.class); //string
        pVERefreshConsume = CellParser.parseTupleCell("PVERefreshConsume", map, Integer.class); //string
        skillConsume = CellParser.parseTupleCell("SkillConsume", map, Integer.class); //string
    }

    public TupleCell<Integer> getpVPChallengeConsume() {
        return pVPChallengeConsume;
    }

    public TupleCell<Integer> getpVPRefreshConsume() {
        return pVPRefreshConsume;
    }

    public TupleCell<Integer> getShopRefreshConsume() {
        return shopRefreshConsume;
    }

    public TupleCell<Integer> getTowerPracticeConsume() {
        return towerPracticeConsume;
    }

    public TupleCell<Integer> getSpiritShopConsume() {
        return spiritShopConsume;
    }

    public TupleCell<Integer> getpVERefreshConsume() {
        return pVERefreshConsume;
    }

    public TupleCell<Integer> getSkillConsume() {
        return skillConsume;
    }

    private static Table_Consume_Row refreshConsume(int times) {
        List<Table_Consume_Row> list = RootTc.getAll(Table_Consume_Row.class);
        Table_Consume_Row last = list.get(list.size() - 1);
        if (times > list.size()) {// 如果刷新次数溢出，则取最后一个刷新的消耗
            return last;
        }
        return RootTc.get(Table_Consume_Row.class, times);
    }


    /**
     * 商店刷新消耗
     *
     * @param times
     * @return
     */
    public static IdAndCount shopRefreshConsume(int times) {
        Table_Consume_Row row = refreshConsume(times);
        return new IdAndCount(row.getShopRefreshConsume().get(TupleCell.FIRST), row.getShopRefreshConsume().get(TupleCell.SECOND));
    }

    /**
     * pvp刷新消耗
     *
     * @param times
     * @return
     */
    public static IdAndCount arneaRefreshConsume(int times) {
        Table_Consume_Row row = refreshConsume(times);
        return new IdAndCount(row.getpVPRefreshConsume().get(TupleCell.FIRST), row.getpVPRefreshConsume().get(TupleCell.SECOND));
    }

    /**
     * pvp购买挑战消耗
     *
     * @param times
     * @return
     */
    public static IdAndCount arneaBuyChallengeConsume(int times) {
        Table_Consume_Row row = refreshConsume(times);
        return new IdAndCount(row.getpVPChallengeConsume().get(TupleCell.FIRST), row.getpVPChallengeConsume().get(TupleCell.SECOND));
    }

    /**
     * 试练塔宝箱消耗
     *
     * @param times
     * @return
     */
    public static IdAndCount ultimateTestOpenBoxConSume(int times) {
        Table_Consume_Row row = refreshConsume(times);
        return new IdAndCount(row.getTowerPracticeConsume().get(TupleCell.FIRST), row.getTowerPracticeConsume().get(TupleCell.SECOND));

    }


    /**
     * 体力购买
     *
     * @param times
     * @return
     */
    public static IdAndCount energyBuyConsume(int times) {
        Table_Consume_Row row = refreshConsume(times);
        return new IdAndCount(row.getSpiritShopConsume().get(TupleCell.FIRST), row.getSpiritShopConsume().get(TupleCell.SECOND));
    }


    /**
     * 精英副本重置次数消耗
     *
     * @param times
     * @return
     */
    public static IdAndCount pveBuyResetConsume(int times) {
        Table_Consume_Row row = refreshConsume(times);
        return new IdAndCount(row.getpVERefreshConsume().get(TupleCell.FIRST), row.getpVERefreshConsume().get(TupleCell.SECOND));
    }

    /**
     * 购买技能点的消耗
     *
     * @param times
     * @return
     */
    public static IdAndCount buySkillPointConsume(int times) {
        Table_Consume_Row row = refreshConsume(times);
        return new IdAndCount(row.getSkillConsume().get(TupleCell.FIRST), row.getSkillConsume().get(TupleCell.SECOND));
    }
}



