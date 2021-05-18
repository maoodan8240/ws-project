package ws.analogClient.features.utils;

import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.network.client.tcp.TcpClient;
import ws.common.network.client.tcp._TcpClient;
import ws.common.network.server.config.implement._ConnConfig;
import ws.common.network.server.config.implement._ServerConfig;
import ws.common.network.server.config.interfaces.ConnConfig;
import ws.common.network.server.config.interfaces.ServerConfig;
import ws.common.network.server.handler.tcp.MessageSendHolder;
import ws.common.network.server.interfaces.CodeToMessagePrototype;
import ws.common.network.server.interfaces.Connection;
import ws.common.network.server.interfaces.NetworkListener;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.general.EnumUtils;
import ws.protos.MessageHandlerProtos.Response;

import java.util.ArrayList;

public class ChatClientUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatClientUtils.class);
    private static final CodeToMessagePrototype PROTOTYPE = GlobalInjector.getInstance(CodeToMessagePrototype.class);
    private static final NetworkListener LISTENER = GlobalInjector.getInstance(NetworkListener.class);
    private static TcpClient tcpClient = null;

    static {
        ServerConfig serverConfig = new _ServerConfig(new _ConnConfig(ConnConfig.ServerProtocolType.TCP, "192.168.0.56", 20602));
        tcpClient = new _TcpClient(serverConfig);
        tcpClient.getNetworkContext().setCodeToMessagePrototype(PROTOTYPE);
        tcpClient.getNetworkHandler().addListener(LISTENER);
        tcpClient.start();
    }

    public static Connection getConn() {
        return tcpClient.getConnection();
    }

    public static Response send(Message message, Enum<?> expectAction) {
        long timeStart = System.nanoTime();
        if (!PROTOTYPE.contains(message.getClass())) {
            LOGGER.warn("不能发送消息，CodeToMessagePrototype不包含Class={}", message.getClass());
            return null;
        }
        Connection conn = getConn();
        MessageWaiter.Waiter waiter = MessageWaiter.create(conn, message, expectAction);
        synchronized (waiter) {
            conn.send(new MessageSendHolder(message, EnumUtils.protoActionToString(expectAction), new ArrayList<>()));
            try {
                waiter.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Response response = MessageWaiter.getReturnMsg(waiter.getFlag());
            long timeEnd = System.nanoTime();
            double cost = ((timeEnd - timeStart) * 1.0) / 1000000;
            LOGGER.debug("{} -> 此次发送耗时为{}毫秒", EnumUtils.protoActionToString(expectAction), cost);
            return response == null ? Response.newBuilder().build() : response;
        }
    }

    public static void check(Response response) {
        if (!response.getResult()) {
            throw new RuntimeException("服务器返回失败！！！");
        }
    }
}
