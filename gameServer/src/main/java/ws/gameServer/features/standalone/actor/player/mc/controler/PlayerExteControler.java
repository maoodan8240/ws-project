package ws.gameServer.features.standalone.actor.player.mc.controler;

import ws.common.utils.mc.controler.Controler;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

public interface PlayerExteControler<T extends PlayerTopLevelPojo> extends Controler<T> {
    /**
     * 初始化业务逻辑
     * 调用 _initBeforeChanged --> _resetDataAtDayChanged0 --> _initAfterChanged
     */
    void _initAll() throws Exception;

    /**
     * 初始化其他Extp/Ctrl的引用
     *
     * @throws Exception
     */
    void _initReference() throws Exception;

    /**
     * 初始化业务逻辑部分一（在_resetDataAtDayChanged之前被调用）
     *
     * @throws Exception
     */
    void _initBeforeChanged() throws Exception;

    /**
     * 设置切日相关的数据
     *
     * @throws Exception
     */
    void _resetDataAtDayChanged() throws Exception;

    /**
     * 初始化业务逻辑部分二（在_resetDataAtDayChanged之后被调用）
     *
     * @throws Exception
     */
    void _initAfterChanged() throws Exception;

    /**
     * 同步信息
     */
    void sync();

    /**
     * 同步部分物品信息，同时刷新到客户端
     *
     * @param idMaptoCount
     */
    void refreshItem(IdMaptoCount idMaptoCount);

    /**
     * 同步部分物品信息，不刷新到客户端
     *
     * @return
     */
    void refreshItemAddToResponse(IdMaptoCount idMaptoCount, Response.Builder br);

    /**
     * 获得{@link PlayerCtrl}
     *
     * @return
     */
    PlayerCtrl getPlayerCtrl();

    /**
     * 设置{@link PlayerCtrl}
     *
     * @param playerCtrl
     */
    void setPlayerCtrl(PlayerCtrl playerCtrl);

    /**
     * 保存
     */
    void save();

    /**
     * 向客户端回消息
     *
     * @param response
     */
    void send(Response response);
}
