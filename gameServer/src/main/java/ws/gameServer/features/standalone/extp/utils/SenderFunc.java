package ws.gameServer.features.standalone.extp.utils;

import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.exception.WsBaseException;
import ws.relationship.utils.ProtoUtils;

import java.lang.reflect.Method;

/**
 * Created by lee on 16-9-21.
 */
public interface SenderFunc<T extends com.google.protobuf.GeneratedMessage.Builder> {
    /**
     * @param b  需要发送的 sm Message
     * @param br Response
     */
    void send0(T b, Response.Builder br);


    /**
     * 构建最终发送的Response
     *
     * @param msgClzz        需要发送的 sm Message 的class
     * @param action         sm Message的action
     * @param senderCallBack 回调调用者
     * @return
     */
    static <X extends com.google.protobuf.GeneratedMessage, Y extends com.google.protobuf.GeneratedMessage.Builder> Response buildResponse(Class<X> msgClzz, Class<Y> builderClzz, Enum<?> action, SenderFunc<Y> senderCallBack) {
        Code code = Code.valueOf(msgClzz.getSimpleName());
        Response.Builder br = ProtoUtils.create_Response(code, action);
        try {
            Method methodNewBuilder = msgClzz.getMethod("newBuilder");
            Object BuilderObj = methodNewBuilder.invoke(null);
            Method methodSetAction = BuilderObj.getClass().getMethod("setAction", action.getClass());
            methodSetAction.invoke(BuilderObj, action);
            br.setResult(true);
            senderCallBack.send0((Y) BuilderObj, br);
            String smName = genSmName(msgClzz);
            Method methodSetSm = br.getClass().getMethod(smName, BuilderObj.getClass());
            methodSetSm.invoke(br, BuilderObj);
        } catch (Exception e) {
            throw new SenderFuncException(e);
        }
        return br.build();
    }

    /**
     * 发送经过Response包装的sm Message
     *
     * @param controler
     * @param msgClzz
     * @param action
     * @param sender
     */
    static <X extends com.google.protobuf.GeneratedMessage, Y extends com.google.protobuf.GeneratedMessage.Builder> void sendInner(PlayerExteControler<?> controler, Class<X> msgClzz, Class<Y> builderClzz, Enum<?> action, SenderFunc<Y> sender) {
        Response br = buildResponse(msgClzz, builderClzz, action, sender);
        controler.send(br);
    }

    /**
     * 获取Response的set方法
     *
     * @param clzz
     * @param <T>
     * @return
     */
    static <T extends com.google.protobuf.GeneratedMessage> String genSmName(Class<T> clzz) {
        String simpleName = clzz.getSimpleName();
        boolean isUnderline = false;
        String simpleNameNew = "";
        for (int i = 0; i < simpleName.length(); i++) {
            String cur = simpleName.substring(i, i + 1);
            if (cur.equals("_")) {
                isUnderline = true;
                continue;
            }
            if (isUnderline) {
                cur = cur.toUpperCase();
                isUnderline = false;
            }
            simpleNameNew += cur;
        }
        return "set" + simpleNameNew.substring(0, 1).toUpperCase() + simpleNameNew.substring(1);
    }

    class SenderFuncException extends WsBaseException {
        private static final long serialVersionUID = 8032781725486180760L;

        public SenderFuncException(Throwable t) {
            super("发送Response消息失败！", t);
        }
    }
}