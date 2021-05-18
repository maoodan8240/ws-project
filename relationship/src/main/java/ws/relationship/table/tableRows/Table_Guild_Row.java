package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.ListCell;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.table.table.utils.CellParser;
import ws.relationship.table.RootTc;

import java.util.List;
import java.util.Map;

public class Table_Guild_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 公会等级
     */
    private Integer guildLevel;
    /**
     * int 公会经验
     */
    private Integer guildExp;
    /**
     * int 公会人数
     */
    private Integer guildPeople;
    /**
     * string 功能开放
     */
    private ListCell<Integer> functionOpening;
    /**
     * string 职务数量
     */
    private TupleListCell<Integer> jobCount;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        jobCount = CellParser.parseTupleListCell("JobCount", map, Integer.class); //string
        functionOpening = CellParser.parseListCell("FunctionOpening", map, Integer.class); //string
        // id column = {columnName:"id", columnDesc:"Pid"}
        guildLevel = CellParser.parseSimpleCell("GuildLevel", map, Integer.class); //int
        guildExp = CellParser.parseSimpleCell("GuildExp", map, Integer.class); //int
        guildPeople = CellParser.parseSimpleCell("GuildPeople", map, Integer.class); //int

    }

    public Integer getGuildLevel() {
        return guildLevel;
    }

    public Integer getGuildExp() {
        return guildExp;
    }

    public Integer getGuildPeople() {
        return guildPeople;
    }

    public ListCell<Integer> getFunctionOpening() {
        return functionOpening;
    }

    public TupleListCell<Integer> getJobCount() {
        return jobCount;
    }

    public static int getGuildMaxLv() {
        List<Table_Guild_Row> list = RootTc.getAll(Table_Guild_Row.class);
        return list.get(list.size() - 1).getGuildLevel();
    }
}
