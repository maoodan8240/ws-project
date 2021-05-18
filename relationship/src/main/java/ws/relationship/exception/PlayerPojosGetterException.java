package ws.relationship.exception;

/**
 * 获取玩家Pojos数据异常
 */
public class PlayerPojosGetterException extends WsBaseException {
    private static final long serialVersionUID = 1L;

    public PlayerPojosGetterException(String msg, Throwable t) {
        super(msg, t);
    }

}
