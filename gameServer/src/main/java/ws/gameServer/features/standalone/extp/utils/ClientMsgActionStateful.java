package ws.gameServer.features.standalone.extp.utils;

import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;


public interface ClientMsgActionStateful<X extends PlayerExteControler<?>, Y extends Message> {

    void onRecv(Y clientMsg, X exteControler);

    void onRecvInnerMsg(InnerMsg innerMsg, X exteControler);

    void onRecvPrivateMsg(PrivateMsg privateMsg, X exteControler);
}
