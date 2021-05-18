package ws.gameServer.features.actor.world.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.cooldown.implement.AutoClearCdList;
import ws.common.utils.cooldown.interfaces.CdList;
import ws.gameServer.features.actor.world.ctrl.WorldCtrl;
import ws.gameServer.features.actor.world.playerIOStatus.IOStatus;
import ws.gameServer.features.actor.world.playerIOStatus.PlayerIOStatus;
import ws.relationship.base.MagicNumbers;

import java.util.Date;

public class IntermediateStateHandle {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntermediateStateHandle.class);
    private final AutoClearCdList CD_LIST = new AutoClearCdList();
    private CdCallback CD_CALLBACK = null;
    private final WorldCtrl worldCtrl;

    public IntermediateStateHandle(WorldCtrl worldCtrl) {
        this.worldCtrl = worldCtrl;
    }

    public void run() {
        CD_CALLBACK = new CdCallback();
        CD_LIST.setCallbackOnExpire(CD_CALLBACK);
        clearAndAdd();
    }

    private void clearAndAdd() {
        CD_LIST.clearAll();
        CD_LIST.add("HandleIntermediateState_" + System.currentTimeMillis(), new Date(System.currentTimeMillis() + MagicNumbers.PLAYERIO_INTERMEDIATE_STATUS_INTERVAL_TIME));
    }

    private class CdCallback implements CdList.CallbackOnExpire {

        @Override
        public void run(String s) {
            try {
                for (PlayerIOStatus ctrl : worldCtrl.getTarget().getPlayerIdToIOStatus().values()) {
                    if (IOStatus.isIntermediateStatus(ctrl.getIoStatus())) {
                        if ((System.currentTimeMillis() - ctrl.getLastIntermediateStateTime()) > MagicNumbers.PLAYERIO_INTERMEDIATE_STATUS_INTERVAL_TIME) {
                            LOGGER.debug("清除中间状态超时的玩家PlayerId={}！", ctrl.getPlayerId());
                            ctrl.killendPlayerIOActor();
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("清除玩家操作的中间状态失败！", e);
            } finally {
                clearAndAdd();
            }
        }
    }
}
