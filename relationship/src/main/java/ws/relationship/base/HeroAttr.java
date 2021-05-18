package ws.relationship.base;

import ws.common.table.table.interfaces.cell.TupleCell;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;

import java.io.Serializable;

public class HeroAttr implements Serializable {
    private static final long serialVersionUID = 1L;

    private final HeroAttrTypeEnum attrType;
    private final long attrValue;

    public HeroAttr(HeroAttrTypeEnum attrType, long attrValue) {
        this.attrType = attrType;
        this.attrValue = Math.max(attrValue, 0);
    }

    public HeroAttrTypeEnum getAttrType() {
        return attrType;
    }

    public long getAttrValue() {
        return attrValue;
    }

    @Override
    public String toString() {
        return attrType + ":" + attrValue;
    }

    public static HeroAttr parse(TupleCell<Long> tuple) {
        return new HeroAttr(HeroAttrTypeEnum.valueOf(tuple.get(TupleCell.FIRST).intValue()), tuple.get(TupleCell.SECOND));
    }
}
