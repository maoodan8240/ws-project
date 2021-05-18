package ws.gameServer.features.actor.world._msgModule.msgActions;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import ws.gameServer.features.actor.world._msgModule.Action;
import ws.gameServer.features.actor.world.ctrl.WorldCtrl;
import ws.gameServer.features.actor.world.playerIOStatus.PlayerIOStatus;
import ws.gameServer.system.ServerGlobalData;
import ws.relationship.appServers.loginServer.player.msg.In_ConnectToPlayerResponse;

public class Action_ConnectToPlayerResponse implements Action {

    @Override
    public void onRecv(Object msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        if (msg instanceof In_ConnectToPlayerResponse) {
            connectToPlayerResponse((In_ConnectToPlayerResponse) msg, worldCtrl, worldActorContext, sender);
        }
    }

    private void connectToPlayerResponse(In_ConnectToPlayerResponse msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        PlayerIOStatus playerIOStatus = worldCtrl.getPlayerIOStatus(msg.getPlayerId(), worldActorContext);
        if (msg.isRs()) {  // 登录成功后设置outerRealmId、innerRealmId信息
            int outerRealmId = msg.getRequest().getOuterRealmId();
            int innerRealmId = ServerGlobalData.getOuterToInnerRealmList(outerRealmId).getInnerRealmId();
            playerIOStatus.setOuterRealmId(outerRealmId);
            playerIOStatus.setInnerRealmId(innerRealmId);
        }
        playerIOStatus.handleMsg(msg);
    }
}
