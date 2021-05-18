package ws.particularFunctionServer.features.rows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;

import java.util.Map;

public class Table_GuildTrain_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 解锁等级
     */
    private Integer openingLevel;
    /**
     * int 解锁特权等级
     */
    private Integer openingVipLevel;
    /**
     * int 开启消耗
     */
    private Integer openingConsume;
    /**
     * int 解锁前置id
     */
    private Integer preOpeningID;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"ID", columnDesc:"ID"}
        openingLevel = CellParser.parseSimpleCell("OpeningLevel", map, Integer.class); //int
        openingVipLevel = CellParser.parseSimpleCell("OpeningVipLevel", map, Integer.class); //int
        openingConsume = CellParser.parseSimpleCell("OpeningConsume", map, Integer.class); //int
        preOpeningID = CellParser.parseSimpleCell("PreOpeningID", map, Integer.class); //int

    }

    public Integer getOpeningLevel() {
        return openingLevel;
    }

    public Integer getOpeningVipLevel() {
        return openingVipLevel;
    }

    public Integer getOpeningConsume() {
        return openingConsume;
    }

    public Integer getPreOpeningID() {
        return preOpeningID;
    }
}
