package ws.relationship.exception;

public class WsActorSystemInitException extends WsBaseException {
    private static final long serialVersionUID = 1L;

    public WsActorSystemInitException(String message) {
        super(message, null);
    }

    public WsActorSystemInitException(String message, Throwable cause) {
        super(message, cause);
    }

}
