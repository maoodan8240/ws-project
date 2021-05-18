package ws.relationship.exception;

import akka.actor.ActorSelection;
import ws.relationship.base.ServerEnvEnum;
import ws.relationship.base.ServerRoleEnum;

public class SendSynchronizedMsgFailedException extends WsBaseException {
    private static final long serialVersionUID = 1L;

    public SendSynchronizedMsgFailedException(ActorSelection actorSelection, Object obj, Throwable e) {
        super("异步发送消息失败! actorSelection=" + actorSelection + ", request=" + obj + " !", e);
    }

    public SendSynchronizedMsgFailedException(ServerRoleEnum serverRoleEnum, ServerEnvEnum serverEnv, Object obj) {
        super("异步发送消息失败!  对应的服务器不存在！serverRoleEnum=" + serverRoleEnum + ", serverEnv=" + serverEnv + ", request=" + obj + " !");
    }
}
