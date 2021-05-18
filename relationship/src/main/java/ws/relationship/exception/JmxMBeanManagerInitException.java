package ws.relationship.exception;

public class JmxMBeanManagerInitException extends WsBaseException {
    private static final long serialVersionUID = 1L;

    public JmxMBeanManagerInitException(String message) {
        super(message, null);
    }

    public JmxMBeanManagerInitException(String message, Throwable cause) {
        super(message, cause);
    }

}
