package ws.analogClient.system.network;

import ws.analogClient.features.utils.MessageWaiter;
import ws.common.network.server.handler.tcp.MessageReceiveHolder;
import ws.common.network.server.interfaces.Connection;
import ws.common.network.server.interfaces.NetworkListener;

public class BroadcastNetworkMessageListener implements NetworkListener {

    @Override
    public void onReceive(MessageReceiveHolder receiveHolder) {
        MessageWaiter.addReturnMsg(receiveHolder.getConnection(), receiveHolder.getMessage());
        System.out.println("onReceive  >>> ");
    }

    @Override
    public void onOffline(Connection connection) {
        System.out.println("onOffline  >>> ");
    }

    @Override
    public void onDisconnected(Connection connection) {
        System.out.println("onDisconnected  >>> ");
    }

    @Override
    public void onHeartBeating(Connection connection) {
        System.out.println("onHeartBeating  >>> ");
    }
}
