package ws.relationship.table.tableRows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.implement.cell._TupleCell;
import ws.common.table.table.implement.cell._TupleListCell;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.table.table.utils.CellParser;
import ws.relationship.base.IdAndCount;
import ws.relationship.table.RootTc;
import ws.relationship.topLevelPojos.common.Iac;
import ws.relationship.utils.RandomUtils;
import ws.relationship.utils.RandomUtils.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table_DropGroup_Row extends AbstractRow {
    private static final Logger LOGGER = LoggerFactory.getLogger(Table_DropGroup_Row.class);
    private static final long serialVersionUID = 1L;
    /**
     * int 掉落组ID
     */
    private Integer groupId;
    /**
     * <pre>
     * int2 等级区间
     * [min, max] 都是闭区间
     * 形式 1: (1,2,3任意等级都出)
     *  1-1:105
     *  2-1:105
     *  3-1:105
     * 形式 2: (1任意等级都出,2>=40都出,3>=60都出)
     *  1-1:105
     *  2-40:105
     *  3-60:105
     * 形式 3: (1 [1-50]都出,2 [51-71]都出,3 [72-105]都出)
     *  1-1:50
     *  2-51:71
     *  3-72:105
     * </pre>
     */
    private TupleCell<Integer> dropLevel;
    /**
     * int 掉落权重
     */
    private Integer dropRate;
    /**
     * int 掉落物品ID
     */
    private Integer goodsId;
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
        groupId = CellParser.parseSimpleCell("GroupId", map, Integer.class); //int
        dropLevel = CellParser.parseTupleCell("DropLevel", map, Integer.class); //string
        dropRate = CellParser.parseSimpleCell("DropRate", map, Integer.class); //int
        goodsId = CellParser.parseSimpleCell("GoodsId", map, Integer.class); //int
        minNum = CellParser.parseSimpleCell("MinNum", map, Integer.class); //int
        maxNum = CellParser.parseSimpleCell("MaxNum", map, Integer.class); //int
    }

    /**
     * 最小掉落<=随机掉落<=最大掉落
     *
     * @return
     */
    private int getDropNum() {
        return RandomUtils.dropBetweenTowNum(minNum, maxNum);
    }


    /**
     * 获取同一个groupId的权重对象
     *
     * @param groupId
     * @param curPlayerLv
     * @return
     */
    private static List<RandomUtils.Entity<Iac>> getGroupIdWeightDropEntityList(int groupId, int curPlayerLv) {
        List<TupleCell<Integer>> idAndNumAndWeightLis = new ArrayList<>();
        for (Table_DropGroup_Row row : RootTc.get(Table_DropGroup_Row.class).values()) {
            if (row.groupId == groupId &&
                    (row.dropLevel.get(TupleCell.FIRST) <= curPlayerLv) &&
                    (row.dropLevel.get(TupleCell.SECOND) >= curPlayerLv)) {
                idAndNumAndWeightLis.add(new _TupleCell<>(new Integer[]{row.id, 1, row.dropRate}));
                LOGGER.debug("找到合适的组行 id={} groupId={} dropLevel={} dropRate={} ", row.id, groupId, row.dropLevel, row.dropRate);
            }
        }
        return RandomUtils.parseTupleList(new _TupleListCell(idAndNumAndWeightLis));
    }


    /**
     * 掉落组根据groupId进行一次权重掉落
     *
     * @param groupId
     * @param curPlayerLv
     * @return
     */
    public static IdAndCount weightDrop(int groupId, int curPlayerLv) {
        try {
            List<RandomUtils.Entity<Iac>> entityList = getGroupIdWeightDropEntityList(groupId, curPlayerLv);
            Entity<Iac> entity = RandomUtils.random(entityList);
            if (entity == null) {
                LOGGER.debug("未找到合适的权重组行！权重掉落为空. groupId={} curPlayerLv={}", groupId, curPlayerLv);
                return IdAndCount.NULL;
            }
            Iac iac = entity.getObject();
            Table_DropGroup_Row dropRow = RootTc.get(Table_DropGroup_Row.class, iac.getId());
            int goodsNum = dropRow.getDropNum();
            if (goodsNum > 0) {
                IdAndCount re = new IdAndCount(dropRow.goodsId, goodsNum);
                LOGGER.debug("组掉落的物品 id={} groupId={} curPlayerLv={} idAndCount={}", iac.getId(), groupId, curPlayerLv, re);
                return re;
            } else {
                return IdAndCount.NULL;
            }
        } catch (Exception e) {
            LOGGER.error("权重掉落异常，检查表是否有错！ groupId={} curPlayerLv={} ", groupId, curPlayerLv, e);
            return IdAndCount.NULL;
        }
    }
}