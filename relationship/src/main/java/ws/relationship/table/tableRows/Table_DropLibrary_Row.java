package ws.relationship.table.tableRows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.table.RootTc;
import ws.relationship.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table_DropLibrary_Row extends AbstractRow {
    private static final Logger LOGGER = LoggerFactory.getLogger(Table_DropLibrary_Row.class);
    private static final long serialVersionUID = 1L;
    /**
     * int 掉落库ID
     */
    private Integer libraryId;
    /**
     * int 概率
     */
    private Integer dropRate;
    /**
     * int 掉落类型
     */
    private Integer dropType;
    /**
     * int 掉落ID
     */
    private Integer dropID;
    /**
     * int 最小掉落
     */
    private Integer minNum;
    /**
     * int 最大掉落
     */
    private Integer maxNum;


    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"主ID"}
        libraryId = CellParser.parseSimpleCell("LibraryId", map, Integer.class); //int
        dropRate = CellParser.parseSimpleCell("DropRate", map, Integer.class); //int
        dropType = CellParser.parseSimpleCell("DropType", map, Integer.class); //int
        dropID = CellParser.parseSimpleCell("DropID", map, Integer.class); //int
        minNum = CellParser.parseSimpleCell("MinNum", map, Integer.class); //int
        maxNum = CellParser.parseSimpleCell("MaxNum", map, Integer.class); //int
    }


    /**
     * 使用libraryId进行掉落库掉落
     *
     * @param libraryId
     * @param curPlayerLv
     * @return
     */
    public static IdMaptoCount drop(int libraryId, int curPlayerLv) {
        LOGGER.debug("准备去掉落 --> libraryId={} curPlayerLv={} ", libraryId, curPlayerLv);
        IdMaptoCount re = new IdMaptoCount();
        for (Table_DropLibrary_Row row : RootTc.get(Table_DropLibrary_Row.class).values()) {
            if (row.libraryId == libraryId) {
                re.addAll(oneDropLibraryRowDrop(row, curPlayerLv));
            }
        }
        LOGGER.debug("libraryId={} curPlayerLv={} 掉落结果={} \n\n", libraryId, curPlayerLv, re);
        return re;
    }

    /**
     * 使用多个libraryId进行掉落库掉落
     *
     * @param libraryIds
     * @param curPlayerLv
     * @return
     */
    public static IdMaptoCount drop(List<Integer> libraryIds, int curPlayerLv) {
        LOGGER.debug("List 准备去掉落 --> libraryIds={} curPlayerLv={} ", libraryIds, curPlayerLv);
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        for (Integer id : libraryIds) {
            idMaptoCount.addAll(drop(id, curPlayerLv));
        }
        LOGGER.debug("List libraryIds={} curPlayerLv={} 掉落结果={} \n\n", libraryIds, curPlayerLv, idMaptoCount);
        return idMaptoCount;
    }

    /**
     * 使用多个libraryId分别进行掉落库掉落(结果不合并,返回List)
     *
     * @param libraryIds
     * @param curPlayerLv
     * @return
     */
    public static List<IdMaptoCount> dropList(List<Integer> libraryIds, int curPlayerLv) {
        List<IdMaptoCount> idMaptoCountList = new ArrayList<>();
        LOGGER.debug("dropList 准备去掉落 --> libraryIds={} curPlayerLv={} ", libraryIds, curPlayerLv);
        for (Integer id : libraryIds) {
            idMaptoCountList.add(drop(id, curPlayerLv));
        }
        LOGGER.debug("dropList libraryIds={} curPlayerLv={} 掉落结果={} \n\n", libraryIds, curPlayerLv, idMaptoCountList);
        return idMaptoCountList;
    }

    /**
     * 掉落库的某一行进行掉落
     *
     * @param row
     * @param curPlayerLv
     * @return
     */
    private static IdMaptoCount oneDropLibraryRowDrop(Table_DropLibrary_Row row, int curPlayerLv) {
        IdMaptoCount re = new IdMaptoCount();
        IdAndCount drop = null;
        if (row.dropType == MagicNumbers.DEFAULT_ONE) { // 使用掉落组进行掉落
            LOGGER.debug("使用[组]掉落! libraryId={} ", row.libraryId);
            drop = dropGroup(row, curPlayerLv);
        } else if (row.dropType == MagicNumbers.DEFAULT_TWO) { // 直接物品掉落
            LOGGER.debug("使用[直接]掉落! libraryId={} ", row.libraryId);
            drop = dropItem(row);
        }
        if (drop != IdAndCount.NULL) {
            re.add(drop);
        }
        return re;
    }


    /**
     * 掉落组掉落
     *
     * @param row
     * @param curPlayerLv
     * @return
     */
    private static IdAndCount dropGroup(Table_DropLibrary_Row row, int curPlayerLv) {
        boolean rs = RandomUtils.isDropPartsFractionOfBase(row.dropRate, MagicNumbers.RANDOM_BASE_VALUE);
        if (rs) {
            return Table_DropGroup_Row.weightDrop(row.dropID, curPlayerLv);
        }
        return IdAndCount.NULL;
    }


    /**
     * 直接物品掉落
     *
     * @param row
     * @return
     */
    private static IdAndCount dropItem(Table_DropLibrary_Row row) {
        boolean rs = RandomUtils.isDropPartsFractionOfBase(row.dropRate, MagicNumbers.RANDOM_BASE_VALUE);
        int goodsNum;
        if (rs) {
            goodsNum = row.getDropNum();
        } else {
            // 未掉落的情况，走最小掉落
            goodsNum = row.minNum;
        }
        if (goodsNum > 0) {
            return new IdAndCount(row.dropID, goodsNum);
        }
        return IdAndCount.NULL;
    }


    /**
     * 最小掉落<=随机掉落<=最大掉落
     *
     * @return
     */
    private int getDropNum() {
        return RandomUtils.dropBetweenTowNum(minNum, maxNum);
    }

}
