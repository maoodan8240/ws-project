package ws.relationship.exception;

public class ParseItemTemplateIdTypeFailedException extends WsBaseException {
    private static final long serialVersionUID = 1L;

    public ParseItemTemplateIdTypeFailedException(String idItemTypeEnums, int itemTemplateId) {
        super("在IdItemTypeEnums=" + idItemTypeEnums + "中，未找到[itemTemplateId]=" + itemTemplateId + "所属的类型！");
    }
}
