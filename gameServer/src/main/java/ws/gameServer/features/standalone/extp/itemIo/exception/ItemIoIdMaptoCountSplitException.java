package ws.gameServer.features.standalone.extp.itemIo.exception;

import ws.relationship.base.IdMaptoCount;
import ws.relationship.exception.WsBaseException;

public class ItemIoIdMaptoCountSplitException extends WsBaseException {
    private static final long serialVersionUID = 1L;

    public ItemIoIdMaptoCountSplitException(IdMaptoCount idMaptoCount, Throwable t) {
        super("分割的物品为：" + idMaptoCount.toString(), t);
    }
}
