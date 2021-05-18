package ws.gameServer.features.actor.world.playerIOStatus.actions;

import ws.gameServer.features.actor.world.playerIOStatus.PlayerIOStatus;

public enum PlayerIOMsgHandleEnums {

    Action_CheckPlayerOnlineMsgRequest(new Action_CheckPlayerOnlineMsgRequest()), //
    Action_ConnectionStatus(new Action_ConnectionStatus()), //
    Action_ConnectToPlayer(new Action_ConnectToPlayer()), //
    Action_HandlePlayerNetworkMsg(new Action_HandlePlayerNetworkMsg()), //
    Action_MultiCheckPlayerOnlineMsgResponse(new Action_MultiCheckPlayerOnlineMsgResponse()), //

    NULL(null);

    private Action action;

    private PlayerIOMsgHandleEnums(Action action) {
        this.action = action;
    }

    public static void onRecv(Object msg, PlayerIOStatus playerIO) throws Exception {
        for (PlayerIOMsgHandleEnums msgEnum : values()) {
            if (msgEnum == NULL || msgEnum == null) {
                continue;
            }
            msgEnum.action.onRecv(msg, playerIO);
        }
    }
}
