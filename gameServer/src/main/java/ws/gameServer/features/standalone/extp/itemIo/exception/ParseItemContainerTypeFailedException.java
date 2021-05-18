package ws.gameServer.features.standalone.extp.itemIo.exception;

import ws.relationship.enums.item.IdItemTypeEnum;
import ws.relationship.exception.WsBaseException;

public class ParseItemContainerTypeFailedException extends WsBaseException {
    private static final long serialVersionUID = 1L;

    public ParseItemContainerTypeFailedException(String itemContainerTypeEnums, IdItemTypeEnum itemType) {
        super("在ItemContainerTypeEnums=" + itemContainerTypeEnums + "中，未找到itemType=" + itemType + "");
    }
}
