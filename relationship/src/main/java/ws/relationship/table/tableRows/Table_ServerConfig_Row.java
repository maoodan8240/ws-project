package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.common.table.table.utils.CellUtils;

import java.util.Map;

public class Table_ServerConfig_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * string 备注
     */
    private String desc;
    /**
     * string 数据类型
     */
    private String type;
    /**
     * Object 配置
     */
    private Object config;
    /**
     * string 简称
     */
    private String simpleName;


    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"下标"}
        desc = CellParser.parseSimpleCell("Desc", map, String.class); //string
        type = CellParser.parseSimpleCell("Type", map, String.class); //string
        config = CellParser.parseSimpleCell("Config", map, String.class); //string
        simpleName = CellParser.parseSimpleCell("SimpleName", map, String.class); //string
        String rawConfigString = CellParser.parseSimpleCell("Config", map, String.class); // string
        try {
            config = CellUtils.parse(type, rawConfigString);
        } catch (Exception e) {
            throw new CellParseFailedException(CellParser.CellType.SIMPLE, String.class, "Config", rawConfigString, e);
        }
    }

    public String getDesc() {
        return desc;
    }

    public String getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfig() {
        return (T) config;
    }

    public String getSimpleName() {
        return simpleName;
    }
}
