package ws.relationship.exception;

public class WsBaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public WsBaseException(Throwable t) {
        super(t);
    }

    public WsBaseException(String msg) {
        super(msg);
    }

    public WsBaseException(String msg, Throwable t) {
        super(msg, t);
    }
}
