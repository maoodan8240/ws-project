package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;

import java.util.Map;

public class Table_InnerServer_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * string 服务器角色
     */
    private String serverRole;
    /**
     * string 网关地址
     */
    private String gateURL;
    /**
     * int 开服日期
     */
    private Integer openDate;
    /**
     * int 内部服Id
     */
    private Integer innerRealmId;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"下标"}
        serverRole = CellParser.parseSimpleCell("ServerRole", map, String.class); //string
        gateURL = CellParser.parseSimpleCell("GateURL", map, String.class); //string
        openDate = CellParser.parseSimpleCell("OpenDate", map, Integer.class); //int
        innerRealmId = CellParser.parseSimpleCell("InnerRealmId", map, Integer.class); //int

    }

    public String getServerRole() {
        return serverRole;
    }

    public String getGateURL() {
        return gateURL;
    }

    public Integer getOpenDate() {
        return openDate;
    }

    public Integer getInnerRealmId() {
        return innerRealmId;
    }
}
