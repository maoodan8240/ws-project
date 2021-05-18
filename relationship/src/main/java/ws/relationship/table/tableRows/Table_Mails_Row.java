package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.common.table.table.utils.CellParser;
import ws.common.table.table.utils.CellUtils;
import ws.common.utils.general.TrueParser;
import ws.relationship.base.IdAndCount;
import ws.relationship.topLevelPojos.common.Iac;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table_Mails_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * string 模版邮件的标题
     */
    private String title;
    /**
     * string 模版邮件的内容
     */
    private String content;
    /**
     * arrayint2 附件
     */
    private TupleListCell<Integer> attachments;
    /**
     * string 发送者
     */
    private String senderName;
    /**
     * int 存储时间
     */
    private Integer storeTime;
    /**
     * int 阅读后是否删除
     */
    private Integer isReadedDelete;

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"模版ID"}
        title = CellParser.parseSimpleCell("Title", map, String.class); //string
        content = CellParser.parseSimpleCell("Content", map, String.class); //string
        attachments = CellParser.parseTupleListCell("attachments", map, Integer.class); //arrayint2
        senderName = CellParser.parseSimpleCell("SenderName", map, String.class); //string
        storeTime = CellParser.parseSimpleCell("StoreTime", map, Integer.class); //int
        isReadedDelete = CellParser.parseSimpleCell("isReadedDelete", map, Integer.class); //int
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<Iac> getAttachments() {
        if (CellUtils.isEmptyCell(attachments)) {
            return new ArrayList<>();
        }
        return IdAndCount.parseIacList(attachments);
    }

    public String getSenderName() {
        return senderName;
    }

    public Integer getStoreTime() {
        return storeTime;
    }

    public boolean getIsReadedDelete() {
        return TrueParser.isTrue(isReadedDelete);
    }
}
