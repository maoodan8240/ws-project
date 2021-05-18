package ws.gameServer.features.standalone.extp.itemBag.exception;

import ws.relationship.exception.WsBaseException;

public class ParseItemTemplateIdTypeFailedException extends WsBaseException {
    private static final long serialVersionUID = 1L;

    public ParseItemTemplateIdTypeFailedException(String IdItemTypeEnums, int itemTemplateId) {
        super("在IdItemTypeEnums=" + IdItemTypeEnums + "中，未找到itemTemplateId=" + itemTemplateId + "所属的类型！");
    }
}
