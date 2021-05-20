package ws.chatServer.features.actor.chat.ctrl;

import ws.common.mongoDB.handler.PojoHandler;
import ws.common.utils.mc.controler.Controler;
import ws.relationship.base.msg.chat.AddChatGroupMember;
import ws.relationship.base.msg.chat.AddChatMsg;
import ws.relationship.base.msg.chat.RemoveChatGroupMember;
import ws.relationship.base.msg.chat.SendCacheMsgs;
import ws.relationship.chatServer.Cm_Chat_Msg_WithPlayerId;

/**
 * Created by lee on 17-5-4.
 */
public interface InnerRealmChatCtrl extends Controler<PojoHandler> {


    /**
     * 增加组成员
     *
     * @param request
     */
    void onAddGroupMember(AddChatGroupMember.Request request);


    /**
     * 移除组成员
     *
     * @param request
     */
    void onRemoveGroupMember(RemoveChatGroupMember.Request request);


    /**
     * 玩家登录了，增加ChatServer玩家
     *
     * @param playerId
     * @param innerRealmId
     * @param outerRealmId
     */
    void onAddChatServerPlayer(String playerId, int innerRealmId, int outerRealmId);


    /**
     * 添加聊天消息
     *
     * @param request
     */
    void onAddChatMsg(AddChatMsg.Request request);


    /**
     * 发送缓存的消息
     *
     * @param request
     */
    void onSendCacheMsgs(SendCacheMsgs.Request request);

    /**
     * 客户端发送的聊天消息
     *
     * @param msg
     */
    void onCm_Chat(Cm_Chat_Msg_WithPlayerId msg);


    /**
     * 玩家断线，移除ChatServer玩家
     *
     * @param playerId
     * @param innerRealmId
     */
    void onRemoveChatServerPlayer(String playerId, int innerRealmId);

    /**
     * 移除过期私聊消息
     */
    void onRemoveOverTimePrivateMsg();
}
