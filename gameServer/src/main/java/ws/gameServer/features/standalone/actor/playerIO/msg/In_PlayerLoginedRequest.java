package ws.gameServer.features.standalone.actor.playerIO.msg;

import ws.common.utils.message.implement.AbstractInnerMsg;

/**
 * 玩家已登陆
 */
public class In_PlayerLoginedRequest extends AbstractInnerMsg {
    private static final long serialVersionUID = 1L;
    private final String playerId;
    private final String deviceUid;           // 登录的设备唯一标示

    public In_PlayerLoginedRequest(String playerId, String deviceUid) {
        this.playerId = playerId;
        this.deviceUid = deviceUid;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getDeviceUid() {
        return deviceUid;
    }

}
