package ws.sdk.manager;

/**
 * 实现此接口的类必须是无状态的类
 */
@FunctionalInterface
public interface Action {
    String handle(String funcName, String args);
}
