package ws.gameServer.features.standalone.extp.itemIo.exception;

import ws.relationship.exception.WsBaseException;

public class ItemIoCanAddException extends WsBaseException {
    private static final long serialVersionUID = 1L;

    public ItemIoCanAddException(Throwable t) {
        super(t);
    }
}
