package ws.gameServer.features.actor.world.ctrl;

import akka.actor.ActorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.mc.controler.AbstractControler;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.actor.world.playerIOStatus.IOStatus;
import ws.gameServer.features.actor.world.playerIOStatus.PlayerIOStatus;
import ws.gameServer.features.actor.world.playerIOStatus.Status;
import ws.gameServer.features.actor.world.pojo.World;
import ws.gameServer.features.standalone.utils.LogHandler;
import ws.relationship.logServer.pojos.ServerStatusLog;
import ws.relationship.utils.RelationshipCommonUtils;

import java.util.HashMap;
import java.util.Map;

public class _WorldCtrl extends AbstractControler<World> implements WorldCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_WorldCtrl.class);


    @Override
    public PlayerIOStatus getPlayerIOStatus(String playerId, ActorContext worldActorContext) {
        if (!containsPlayerId(playerId)) {
            putPlayerIO(new PlayerIOStatus(this, worldActorContext, playerId));
        }
        return target.getPlayerIdToIOStatus().get(playerId);
    }

    private boolean containsPlayerId(String playerId) {
        return target.getPlayerIdToIOStatus().containsKey(playerId);
    }

    private void putPlayerIO(PlayerIOStatus playerIO) {
        target.getPlayerIdToIOStatus().put(playerIO.getPlayerId(), playerIO);
    }

    @Override
    public void statisticsCurGameSeverStatus() {
        Map<Integer, ServerStatusLog> innerRealmIdToServerStatusLog = new HashMap<>();
        for (PlayerIOStatus playerIOStatus : target.getPlayerIdToIOStatus().values()) {
            if (playerIOStatus.getIoStatus() == IOStatus.Inend) {
                if (playerIOStatus.getStatus() == Status.Online) {
                    ServerStatusLog statusLog = RelationshipCommonUtils.getValueByKeyFromMap(innerRealmIdToServerStatusLog, playerIOStatus.getInnerRealmId(), new ServerStatusLog(playerIOStatus.getInnerRealmId()));
                    statusLog.setOnline(statusLog.getOnline() + 1);
                    continue;
                } else if (playerIOStatus.getStatus() == Status.Offline) {
                    ServerStatusLog statusLog = RelationshipCommonUtils.getValueByKeyFromMap(innerRealmIdToServerStatusLog, playerIOStatus.getInnerRealmId(), new ServerStatusLog(playerIOStatus.getInnerRealmId()));
                    statusLog.setOffline(statusLog.getOffline() + 1);
                    continue;
                }
            } else {
                LOGGER.debug("玩家状态: {} {} {} {}", playerIOStatus.getPlayerId(), playerIOStatus.getStatus(), playerIOStatus.getIoStatus(), playerIOStatus.getPlayerIOActorRef());
            }
            ServerStatusLog statusLog = RelationshipCommonUtils.getValueByKeyFromMap(innerRealmIdToServerStatusLog, playerIOStatus.getInnerRealmId(), new ServerStatusLog(playerIOStatus.getInnerRealmId()));
            statusLog.setOther(statusLog.getOther() + 1);
        }
        if (innerRealmIdToServerStatusLog.size() > 0) {
            LogHandler.serverStatusLog(innerRealmIdToServerStatusLog);
        }
    }


    @Override
    public void sendPrivateMsg(PrivateMsg privateMsg) {
        
    }

}
