package ws.relationship.exception;

public class LauncherInitException extends WsBaseException {
    private static final long serialVersionUID = 1L;

    public LauncherInitException(String message) {
        super(message, null);
    }

    public LauncherInitException(String message, Throwable cause) {
        super(message, cause);
    }

}
