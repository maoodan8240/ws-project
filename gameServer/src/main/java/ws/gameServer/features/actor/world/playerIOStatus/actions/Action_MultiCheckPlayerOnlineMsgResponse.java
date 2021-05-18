package ws.gameServer.features.actor.world.playerIOStatus.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.actor.world.playerIOStatus.IOStatus;
import ws.gameServer.features.actor.world.playerIOStatus.PlayerIOStatus;
import ws.relationship.base.msg.MultiCheckPlayerOnlineMsg;

public class Action_MultiCheckPlayerOnlineMsgResponse implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(Action_MultiCheckPlayerOnlineMsgResponse.class);

    @Override
    public void onRecv(Object msg, PlayerIOStatus playerIO) throws Exception {
        if (msg instanceof MultiCheckPlayerOnlineMsg.Response) {
            onMultiCheckPlayerOnlineMsgResponse((MultiCheckPlayerOnlineMsg.Response) msg, playerIO);
        }
    }

    private void onMultiCheckPlayerOnlineMsgResponse(MultiCheckPlayerOnlineMsg.Response response, PlayerIOStatus playerIOStatus) throws Exception {
        String playerId = response.getRequest().getCheckPlayerId();
        IOStatus ioStatus = playerIOStatus.getIoStatus();
        if (ioStatus == IOStatus.OfflineOperating) {
            if (playerIOStatus.getWaitingSendCheckOnlineMsgs().size() > 0) {
                playerIOStatus.sendCheckPlayerOnlineMsg();
                LOGGER.debug("playerId={} ioStatus={} MultiCheckPlayerOnlineMsgResponse后发现，还有未处理的离线消息，继续发送> 玩家玩家数据【不在】内存中，给玩家发送离线消息！", playerId, ioStatus);
            } else {
                LOGGER.debug("离线消息发送完毕！");
                playerIOStatus.killingPlayerIOActor();
            }
        } else {
            LOGGER.warn("异常！playerId={} ioStatus={} CheckPlayerOnlineMsgResponse后的状态【必须是OfflineOperating】，因为OfflineOperating状态必须MultiCheckPlayerOnlineMsgResponse来清除！", playerId, ioStatus);
        }
    }

}
