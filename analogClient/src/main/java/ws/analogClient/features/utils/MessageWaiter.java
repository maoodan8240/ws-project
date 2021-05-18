package ws.analogClient.features.utils;

import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.network.server.interfaces.CodeToMessagePrototype;
import ws.common.network.server.interfaces.Connection;
import ws.common.utils.cooldown.implement.AutoClearCdList;
import ws.common.utils.cooldown.interfaces.CdList;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.general.EnumUtils;
import ws.protos.MessageHandlerProtos.Response;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageWaiter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageWaiter.class);
    private static final CodeToMessagePrototype PROTOTYPE = GlobalInjector.getInstance(CodeToMessagePrototype.class);
    private static final AutoClearCdList CLEAR_CD_LIST = new AutoClearCdList();
    private static final long OVER_TIME = 15 * 1000;

    static {
        CLEAR_CD_LIST.clearAll();
        CLEAR_CD_LIST.setCallbackOnExpire(new CdCallBack());
    }

    static class Waiter {
        private String flag;
        private int expectCode;
        private String expectAction;

        public Waiter(String flag, int expectCode, String expectAction) {
            this.flag = flag;
            this.expectCode = expectCode;
            this.expectAction = expectAction;
        }

        public String getFlag() {
            return flag;
        }

        public int getExpectCode() {
            return expectCode;
        }

        public String getExpectAction() {
            return expectAction;
        }
    }

    private static Map<String, Waiter> flagToWaiter = new ConcurrentHashMap<>();
    private static Map<String, Message> flagToReturnMsg = new ConcurrentHashMap<>();

    public static Waiter create(Connection connection, Message msgSend, Enum<?> expectAction) {
        String flag = genFlag(EnumUtils.protoActionToString(expectAction), connection);
        int code = PROTOTYPE.queryCode(msgSend.getClass());
        Waiter waiter = new Waiter(flag, code + 1, EnumUtils.protoActionToString(expectAction));
        flagToWaiter.put(waiter.getFlag(), waiter);
        CLEAR_CD_LIST.add(flag, new Date(System.currentTimeMillis() + OVER_TIME));
        LOGGER.debug("发送消息 > flag={} msgSend={}", flag, msgSend);
        return waiter;
    }

    public static void addReturnMsg(Connection connection, Message returnMsg) {
        if (returnMsg instanceof Response) {
            Response response = (Response) returnMsg;
            int code = response.getMsgCode().getNumber();
            if (!PROTOTYPE.contains(code)) {
                return;
            }
            String flag = genFlag(response.getSmMsgAction(), connection);
            if (flagToWaiter.containsKey(flag)) {
                Waiter waiter = flagToWaiter.get(flag);
                if (waiter.getExpectCode() == code && waiter.getExpectAction().equals(response.getSmMsgAction())) {
                    CLEAR_CD_LIST.clear(flag);
                    flagToReturnMsg.put(flag, returnMsg);
                    flagToWaiter.remove(flag);
                    synchronized (waiter) {
                        waiter.notifyAll();
                    }
                }
            } else {
                MessageReceiveUtils.onRecv(response, response.getMsgCode());
            }
        }
    }

    public static String genFlag(String smMsgAction, Connection connection) {
        return smMsgAction + "-" + connection.hashCode();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getReturnMsg(String flag) {
        return (T) flagToReturnMsg.remove(flag);
    }

    static class CdCallBack implements CdList.CallbackOnExpire {

        @Override
        public void run(String type) {
            String flag = type;
            if (flagToWaiter.containsKey(flag)) {
                LOGGER.warn("type={} 请求超时了！", type);
                Waiter waiter = flagToWaiter.remove(flag);
                flagToReturnMsg.remove(flag);
                if (waiter != null) {
                    synchronized (waiter) {
                        waiter.notifyAll();
                    }
                }
            }
        }
    }
}
