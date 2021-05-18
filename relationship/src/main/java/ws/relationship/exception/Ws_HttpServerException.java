package ws.relationship.exception;

/**
 * Created by lee on 17-2-23.
 */
public class Ws_HttpServerException extends WsBaseException {
    private static final long serialVersionUID = 1L;

    public Ws_HttpServerException(String message) {
        super(message, null);
    }

    public Ws_HttpServerException(String message, Throwable t) {
        super(message, t);
    }

}
