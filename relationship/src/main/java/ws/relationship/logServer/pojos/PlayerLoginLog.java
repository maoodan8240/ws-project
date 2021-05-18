package ws.relationship.logServer.pojos;

import org.bson.types.ObjectId;
import ws.relationship.logServer.base.PlayerLog;

/**
 * Created by zhangweiwei on 17-7-11.
 */
public class PlayerLoginLog extends PlayerLog {
    private static final long serialVersionUID = 2282353612799755541L;
    private String deviceUid;           // 登录的设备唯一标示


    public PlayerLoginLog() {
    }

    public PlayerLoginLog(String deviceUid) {
        super(ObjectId.get().toString());
        this.deviceUid = deviceUid;
    }

    public String getDeviceUid() {
        return deviceUid;
    }

    public void setDeviceUid(String deviceUid) {
        this.deviceUid = deviceUid;
    }
}
