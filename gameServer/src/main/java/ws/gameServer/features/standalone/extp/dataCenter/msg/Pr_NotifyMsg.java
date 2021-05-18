package ws.gameServer.features.standalone.extp.dataCenter.msg;


import ws.common.utils.message.implement.AbstractPrivateMsg;
import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;

public class Pr_NotifyMsg extends AbstractPrivateMsg {
    private final PrivateNotifyTypeEnum type;
    private final int id;
    private final long value;

    public Pr_NotifyMsg(PrivateNotifyTypeEnum type, long value) {
        this(type, -1, value);
    }

    public Pr_NotifyMsg(PrivateNotifyTypeEnum type, int id, long value) {
        this.type = type;
        this.id = id;
        this.value = value;
    }

    public PrivateNotifyTypeEnum getType() {
        return type;
    }

    public long getValue() {
        return value;
    }

    public int getId() {
        return id;
    }

    public int getTypeCode() {
        return type.getCode();
    }
}
