package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;

import java.util.Map;

public class Table_Resource_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * string 资源名称
     */
    private String resourceName;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"Id"}
        resourceName = CellParser.parseSimpleCell("ResourceName", map, String.class); //string

    }

    public String getResourceName() {
        return resourceName;
    }
}
