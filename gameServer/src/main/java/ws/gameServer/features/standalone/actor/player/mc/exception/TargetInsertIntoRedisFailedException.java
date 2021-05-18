package ws.gameServer.features.standalone.actor.player.mc.exception;

import ws.relationship.exception.WsBaseException;

public class TargetInsertIntoRedisFailedException extends WsBaseException {
    private static final long serialVersionUID = 1L;

    public TargetInsertIntoRedisFailedException(Class<?> clazz, String content) {
        super("存库>[" + clazz.getPackage() + "." + clazz.getName() + "]存入数据库失败！\n    content=" + content);
    }
}
