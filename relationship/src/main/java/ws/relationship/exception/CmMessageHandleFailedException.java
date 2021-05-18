package ws.relationship.exception;

import ws.protos.MessageHandlerProtos.Response;

/**
 * CmMessage业务逻辑处理失败
 */
public class CmMessageHandleFailedException extends WsBaseException {

    private static final long serialVersionUID = 1L;

    public CmMessageHandleFailedException(Response.Builder br, Throwable t) {
        super("[" + br.getSmMsgAction() + "] 处理失败！", t);
    }
}
