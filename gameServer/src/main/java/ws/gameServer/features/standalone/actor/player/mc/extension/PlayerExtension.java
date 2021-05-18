package ws.gameServer.features.standalone.actor.player.mc.extension;

import akka.actor.ActorRef;
import com.google.protobuf.Message;
import ws.common.utils.mc.controler.Controler;
import ws.common.utils.mc.extension.Extension;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.relationship.base.msg.In_MessagePassToOtherServer;
import ws.relationship.topLevelPojos.player.Player;

/**
 * {@link Player}扩展模块
 */
public interface PlayerExtension<T extends Controler<?>> extends Extension<T> {

    /**
     * 返回所属{@link PlayerCtrl}
     *
     * @return
     */
    PlayerCtrl getOwnerCtrl();

    /**
     * new Ctrl/设置Extp的Ctrl/设置Ctrl的target
     *
     * @throws Exception
     */
    void init() throws Exception;

    /**
     * 初始化其他Extp/Ctrl的引用
     *
     * @throws Exception
     */
    void initReference() throws Exception;

    /**
     * 初始化业务逻辑
     *
     * @throws Exception
     */
    void postInit() throws Exception;

    /**
     * 销毁
     */
    void destroy();

    /**
     * 当前消息的发送者
     *
     * @param curSender
     */
    void setCurSender(ActorRef curSender);

    /**
     * <pre>
     * ==================================================================
     * 消息接收处理
     * ==================================================================
     * </pre>
     */

    /**
     * 当接收到自己的网络连接发来的消息
     *
     * @param clientMsg
     * @throws Exception
     */
    void onRecvMyNetworkMsg(Message clientMsg) throws Exception;

    /**
     * 当接收到别的网络连接发来的消息
     *
     * @param othersNetworkMsg
     * @throws Exception
     */
    void onRecvOthersNetworkMsg(In_MessagePassToOtherServer othersNetworkMsg) throws Exception;

    /**
     * 当接收到{@link InnerMsg}时
     *
     * @param innerMsg
     * @throws Exception
     */
    void onRecvInnerMsg(InnerMsg innerMsg) throws Exception;

    /**
     * 当接收到{@link PrivateMsg}时
     *
     * @param privateMsg
     * @throws Exception
     */
    void onRecvPrivateMsg(PrivateMsg privateMsg) throws Exception;
}
