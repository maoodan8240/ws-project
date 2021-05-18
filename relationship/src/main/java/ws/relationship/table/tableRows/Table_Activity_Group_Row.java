package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.ListCell;
import ws.common.table.table.utils.CellParser;
import ws.common.utils.general.TrueParser;
import ws.protos.EnumsProtos.ActivityBigTypeEnum;

import java.util.List;
import java.util.Map;

public class Table_Activity_Group_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * int 活动类型
     */
    private Integer type;
    /**
     * 是否走通用逻辑
     */
    private Integer goCommonLogic;
    /**
     * int 子活动Id
     */
    private ListCell<Integer> subId;
    /**
     * string 活动标签标题
     */
    private String tagTitle;
    /**
     * string 活动内容标题
     */
    private String contentTitle;
    /**
     * string 活动内容的描述
     */
    private String contentDesc;
    /**
     * string 活动标签图标
     */
    private String tagIcon;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"活动组别"}
        type = CellParser.parseSimpleCell("Type", map, Integer.class); //int
        goCommonLogic = CellParser.parseSimpleCell("GoCommonLogic", map, Integer.class); //int
        subId = CellParser.parseListCell("SubId", map, Integer.class); //string
        tagTitle = CellParser.parseSimpleCell("TagTitle", map, String.class); //string
        contentTitle = CellParser.parseSimpleCell("ContentTitle", map, String.class); //string
        contentDesc = CellParser.parseSimpleCell("ContentDesc", map, String.class); //string
        tagIcon = CellParser.parseSimpleCell("TagIcon", map, String.class); //string
    }

    public ActivityBigTypeEnum getType() {
        return ActivityBigTypeEnum.valueOf(type);
    }

    public boolean getGoCommonLogic() {
        return TrueParser.isTrue(goCommonLogic);
    }

    public List<Integer> getSubId() {
        return subId.getAll();
    }

    public String getTagTitle() {
        return tagTitle;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public String getContentDesc() {
        return contentDesc;
    }

    public String getTagIcon() {
        return tagIcon;
    }
}
