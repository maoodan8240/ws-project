package ws.gameServer.features.standalone.extp.dataCenter.msg;

import ws.common.utils.message.implement.AbstractPrivateMsg;
import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;

public class Pr_UpdateRegNotifyMsg extends AbstractPrivateMsg {
    private final PrivateNotifyTypeEnum type;

    public Pr_UpdateRegNotifyMsg(PrivateNotifyTypeEnum type) {
        this.type = type;
    }

    public PrivateNotifyTypeEnum getType() {
        return type;
    }
}
