package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.table.table.utils.CellParser;
import ws.relationship.table.RootTc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table_SoulBox_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 奖品物品ID1
     */
    private Integer rewardId1;
    /**
     * int 奖品物品ID2
     */
    private Integer rewardId2;
    /**
     * int 奖品物品ID3
     */
    private Integer rewardId3;
    /**
     * int 奖品物品ID4
     */
    private Integer rewardId4;
    /**
     * int 奖品物品ID5
     */
    private Integer rewardId5;
    /**
     * int 奖品物品ID6
     */
    private Integer rewardId6;
    /**
     * int 单次购买的道具或者资源ID
     */
    private Integer buyId;
    /**
     * int 1次消耗货币数量
     */
    private Integer oneTimeConsume;
    /**
     * string 抽卡概率
     */
    private TupleListCell<Integer> pickRate;
    /**
     * int 抽卡库ID1
     */
    private Integer dropLib1;
    /**
     * int 抽卡库ID2
     */
    private Integer dropLib2;
    /**
     * int 抽卡库ID3
     */
    private Integer dropLib3;
    /**
     * int 抽卡库ID4
     */
    private Integer dropLib4;
    /**
     * int 抽卡库ID5
     */
    private Integer dropLib5;
    /**
     * int 抽卡库ID6
     */
    private Integer dropLib6;
    /**
     * int 抽卡库ID7
     */
    private Integer dropLib7;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        pickRate = CellParser.parseTupleListCell("PickRate", map, Integer.class); //string
        // id column = {columnName:"Id", columnDesc:"抽卡ID"}
        rewardId1 = CellParser.parseSimpleCell("RewardId1", map, Integer.class); //int
        rewardId2 = CellParser.parseSimpleCell("RewardId2", map, Integer.class); //int
        rewardId3 = CellParser.parseSimpleCell("RewardId3", map, Integer.class); //int
        rewardId4 = CellParser.parseSimpleCell("RewardId4", map, Integer.class); //int
        rewardId5 = CellParser.parseSimpleCell("RewardId5", map, Integer.class); //int
        rewardId6 = CellParser.parseSimpleCell("RewardId6", map, Integer.class); //int
        buyId = CellParser.parseSimpleCell("BuyId", map, Integer.class); //int
        oneTimeConsume = CellParser.parseSimpleCell("OneTimeConsume", map, Integer.class); //int
        dropLib1 = CellParser.parseSimpleCell("DropLib1", map, Integer.class); //int
        dropLib2 = CellParser.parseSimpleCell("DropLib2", map, Integer.class); //int
        dropLib3 = CellParser.parseSimpleCell("DropLib3", map, Integer.class); //int
        dropLib4 = CellParser.parseSimpleCell("DropLib4", map, Integer.class); //int
        dropLib5 = CellParser.parseSimpleCell("DropLib5", map, Integer.class); //int
        dropLib6 = CellParser.parseSimpleCell("DropLib6", map, Integer.class); //int
        dropLib7 = CellParser.parseSimpleCell("DropLib7", map, Integer.class); //int

    }

    public Integer getRewardId1() {
        return rewardId1;
    }

    public Integer getRewardId2() {
        return rewardId2;
    }

    public Integer getRewardId3() {
        return rewardId3;
    }

    public Integer getRewardId4() {
        return rewardId4;
    }

    public Integer getRewardId5() {
        return rewardId5;
    }

    public Integer getRewardId6() {
        return rewardId6;
    }

    public Integer getBuyId() {
        return buyId;
    }

    public Integer getOneTimeConsume() {
        return oneTimeConsume;
    }

    public TupleListCell<Integer> getPickRate() {
        return pickRate;
    }

    public Integer getDropLib1() {
        return dropLib1;
    }

    public Integer getDropLib2() {
        return dropLib2;
    }

    public Integer getDropLib3() {
        return dropLib3;
    }

    public Integer getDropLib4() {
        return dropLib4;
    }

    public Integer getDropLib5() {
        return dropLib5;
    }

    public Integer getDropLib6() {
        return dropLib6;
    }

    public Integer getDropLib7() {
        return dropLib7;
    }

    public static List<Integer> getDropLibraryIds(int pickId) {
        List<Integer> dropLibraryIds = new ArrayList<>();
        Table_SoulBox_Row soulBoxRow = RootTc.get(Table_SoulBox_Row.class).get(pickId);
        dropLibraryIds.add(soulBoxRow.getDropLib1());
        dropLibraryIds.add(soulBoxRow.getDropLib2());
        dropLibraryIds.add(soulBoxRow.getDropLib3());
        dropLibraryIds.add(soulBoxRow.getDropLib4());
        dropLibraryIds.add(soulBoxRow.getDropLib5());
        dropLibraryIds.add(soulBoxRow.getDropLib6());
        return dropLibraryIds;
    }

    public static int getBuyItem(int pickId) {
        Table_SoulBox_Row soulBoxRow = RootTc.get(Table_SoulBox_Row.class).get(pickId);
        return soulBoxRow.getBuyId();
    }
}
