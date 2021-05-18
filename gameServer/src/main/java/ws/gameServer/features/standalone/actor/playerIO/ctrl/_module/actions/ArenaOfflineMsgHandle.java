package ws.gameServer.features.standalone.actor.playerIO.ctrl._module.actions;

import akka.actor.ActorRef;
import ws.gameServer.features.standalone.actor.playerIO.ctrl._module.Action;
import ws.gameServer.features.standalone.extp.arena.msg.In_AddNewBattleRecords;
import ws.gameServer.features.standalone.extp.arena.utils.ArenaOnlineOfflineUtils;

/**
 * Created by lee on 5/23/17.
 */
public class ArenaOfflineMsgHandle implements Action {
    @Override
    public void handleOfflineMsg(String selfPlayerId, Object msg, ActorRef oriSender) {
        if (msg instanceof In_AddNewBattleRecords.Request) { // todo Response 怪怪的
            onAddNewBattleRecords((In_AddNewBattleRecords.Request) msg, oriSender);
        }
    }

    private void onAddNewBattleRecords(In_AddNewBattleRecords.Request request, ActorRef oriSender) {
        ArenaOnlineOfflineUtils.onAddNewBattleRecords(request.getArenaRecord(), request.getBeAttackId(), request.getOuterRealmId());
    }

}
