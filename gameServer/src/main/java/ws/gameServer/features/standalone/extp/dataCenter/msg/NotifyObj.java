package ws.gameServer.features.standalone.extp.dataCenter.msg;

import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;

public class NotifyObj implements Cloneable {
    private final PrivateNotifyTypeEnum type;
    private int id;
    private long value;
    private long valueOri;

    public NotifyObj(PrivateNotifyTypeEnum type) {
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NotifyObj(PrivateNotifyTypeEnum type, int id, long value, long valueOri) {
        this.type = type;
        this.id = id;
        this.value = value;
        this.valueOri = valueOri;
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

    public void setValue(long value) {
        this.value = value;
    }

    public long getValueOri() {
        return valueOri;
    }

    public void setValueOri(long valueOri) {
        this.valueOri = valueOri;
    }

    @Override
    public NotifyObj clone() {
        return new NotifyObj(type, id, value, valueOri);
    }
}
