package ws.relationship.table.tableRows;

import org.apache.commons.lang3.StringUtils;
import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;
import ws.relationship.utils.RandomUtils;

import java.util.Map;

public class Table_NameLibrary_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    private static int minId = Integer.MAX_VALUE;
    private static int maxId = Integer.MIN_VALUE;
    /**
     * string 名字前段
     */
    private String first;
    /**
     * string 名字中段
     */
    private String center;
    /**
     * string 名字后段
     */
    private String last;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"主ID"}
        first = CellParser.parseSimpleCell("First", map, String.class); //string
        center = CellParser.parseSimpleCell("Center", map, String.class); //string
        last = CellParser.parseSimpleCell("Last", map, String.class); //string
        setMinId();
        setMaxId();
    }

    private void setMinId() {
        if (this.id < minId) {
            minId = this.id;
        }
    }

    private void setMaxId() {
        if (this.id > maxId) {
            maxId = this.id;
        }
    }

    /**
     * 随机一个名字
     *
     * @return
     */
    public static String randomName() {
        String name = randomName(NamePart.FIRST) + randomName(NamePart.CENTER) + randomName(NamePart.LAST);
        if (StringUtils.isBlank(name)) {
            throw new BusinessLogicMismatchConditionException("随机名字的结果为空！");
        }
        return name;
    }

    private static String randomName(NamePart namePart) {
        Table_NameLibrary_Row row = randomRow();
        return getNamePart(row, namePart);
    }

    private static String getNamePart(Table_NameLibrary_Row row, NamePart namePart) {
        switch (namePart) {
            case FIRST:
                return row.first;
            case CENTER:
                return row.center;
            case LAST:
                return row.last;
            default:
                return null;
        }
    }

    private static Table_NameLibrary_Row randomRow() {
        int rdx = RandomUtils.dropBetweenTowNum(minId, maxId);
        return RootTc.get(Table_NameLibrary_Row.class, rdx);
    }

    private enum NamePart {
        FIRST, CENTER, LAST
    }
}
