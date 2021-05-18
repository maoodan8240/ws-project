package ws.gameServer.features.standalone.extp.itemIo.exception;

import ws.relationship.exception.WsBaseException;

public class ItemIoModifyOperationNeedCallerActionException extends WsBaseException {
    private static final long serialVersionUID = 1L;

    public ItemIoModifyOperationNeedCallerActionException() {
        super("Add Or Remove 操作时，CallerAction不能为空！");
    }
}
