package ws.gameServer.features.actor.world._msgModule;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import ws.gameServer.features.actor.world._msgModule.msgActions.Action_AbstractGateWayServerInnerMsg;
import ws.gameServer.features.actor.world._msgModule.msgActions.Action_CheckPlayerOnlineMsgRequest;
import ws.gameServer.features.actor.world._msgModule.msgActions.Action_ConnectToPlayerRequest;
import ws.gameServer.features.actor.world._msgModule.msgActions.Action_ConnectToPlayerResponse;
import ws.gameServer.features.actor.world._msgModule.msgActions.Action_KickPlayersToOfflineRequest;
import ws.gameServer.features.actor.world._msgModule.msgActions.Action_MultiCheckPlayerOnlineMsgResponse;
import ws.gameServer.features.actor.world._msgModule.msgActions.Action_NoticeToKillOverTimeCachePlayerActorRequest;
import ws.gameServer.features.actor.world._msgModule.msgActions.Action_OtherAction;
import ws.gameServer.features.actor.world._msgModule.msgActions.Action_PrepareToKillPlayerActorResponse;
import ws.gameServer.features.actor.world._msgModule.msgActions.Action_SyncGameServerStatus;
import ws.gameServer.features.actor.world._msgModule.msgActions.Action_TerminatedAction;
import ws.gameServer.features.actor.world.ctrl.WorldCtrl;

public enum WorldActorMsgHandleEnums {

    _Action_AbstractGateWayServerInnerMsg(new Action_AbstractGateWayServerInnerMsg()), //
    _Action_CheckPlayerOnlineMsgRequest(new Action_CheckPlayerOnlineMsgRequest()), //
    _Action_MultiCheckPlayerOnlineMsgResponse(new Action_MultiCheckPlayerOnlineMsgResponse()), //
    _Action_ConnectToPlayerRequest(new Action_ConnectToPlayerRequest()), //
    _Action_ConnectToPlayerResponse(new Action_ConnectToPlayerResponse()), //
    _Action_KickPlayersToOfflineRequest(new Action_KickPlayersToOfflineRequest()), //
    _Action_NoticeToKillOverTimeCachePlayerActorRequest(new Action_NoticeToKillOverTimeCachePlayerActorRequest()), //
    _Action_PrepareToKillPlayerActorResponse(new Action_PrepareToKillPlayerActorResponse()), //
    _Action_SyncGameServerStatus(new Action_SyncGameServerStatus()), //
    _Action_TerminatedAction(new Action_TerminatedAction()), //
    _Action_OtherAction(new Action_OtherAction()), //

    NULL(null);

    private Action action;

    WorldActorMsgHandleEnums(Action action) {
        this.action = action;
    }

    public static void onRecv(Object msg, WorldCtrl worldCtrl, ActorContext worldActorContext, ActorRef sender) throws Exception {
        for (WorldActorMsgHandleEnums msgEnum : values()) {
            if (msgEnum == NULL || msgEnum == null) {
                continue;
            }
            msgEnum.action.onRecv(msg, worldCtrl, worldActorContext, sender);
        }
    }
}
