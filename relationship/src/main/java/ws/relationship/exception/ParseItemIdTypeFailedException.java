package ws.relationship.exception;

public class ParseItemIdTypeFailedException extends WsBaseException {
    private static final long serialVersionUID = 1L;

    public ParseItemIdTypeFailedException(String idItemTypeEnums, int itemId) {
        super("在IdItemTypeEnums=" + idItemTypeEnums + "中，未找到[itemId]=" + itemId + "所属的类型！");
    }
}
