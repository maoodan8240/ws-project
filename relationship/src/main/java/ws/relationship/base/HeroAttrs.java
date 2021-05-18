package ws.relationship.base;

import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.table.table.interfaces.cell.TupleListCell;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HeroAttrs implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<HeroAttrTypeEnum, Long> attrToValue = new HashMap<>();

    public HeroAttrs() {
    }

    public HeroAttrs(HeroAttrs attrs) {
        addAll(attrs);
    }

    public Long get(HeroAttrTypeEnum attrType) {
        return attrToValue.containsKey(attrType) ? attrToValue.get(attrType) : 0L;
    }

    /**
     * 同类型相加, 而非覆盖
     *
     * @param heroAttr
     */
    public HeroAttrs add(HeroAttr heroAttr) {
        if (heroAttr == null) {
            return this;
        }
        _add(heroAttr.getAttrType(), heroAttr.getAttrValue());
        return this;
    }

    /**
     * 同类型相加, 而非覆盖
     *
     * @param
     */
    private void _add(HeroAttrTypeEnum attrType, Long toAdd) {
        attrToValue.put(attrType, toAdd + get(attrType));
    }


    public HeroAttrs replaceAll(HeroAttrs toReplace) {
        if (toReplace == null) {
            return this;
        }
        for (Entry<HeroAttrTypeEnum, Long> entry : toReplace.getRaw().entrySet()) {
            replace(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public HeroAttrs replace(HeroAttr heroAttr) {
        if (heroAttr == null) {
            return this;
        }
        replace(heroAttr.getAttrType(), heroAttr.getAttrValue());
        return this;
    }

    public HeroAttrs replace(HeroAttrTypeEnum attrType, Long replaceValue) {
        if (attrType == null) {
            return this;
        }
        attrToValue.put(attrType, replaceValue);
        return this;
    }

    /**
     * 减少
     *
     * @param heroAttr
     */
    public HeroAttrs reduce(HeroAttr heroAttr) {
        if (heroAttr == null) {
            return this;
        }
        _reduce(heroAttr.getAttrType(), heroAttr.getAttrValue());
        return this;
    }

    private void _reduce(HeroAttrTypeEnum attrType, Long toReduce) {
        attrToValue.put(attrType, get(attrType) - toReduce);
    }

    public HeroAttrs addAll(HeroAttrs toAdd) {
        if (toAdd == null) {
            return this;
        }
        for (Entry<HeroAttrTypeEnum, Long> entry : toAdd.getRaw().entrySet()) {
            _add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public HeroAttrs reduceAll(HeroAttrs toReduce) {
        if (toReduce == null) {
            return this;
        }
        for (Entry<HeroAttrTypeEnum, Long> entry : toReduce.getRaw().entrySet()) {
            _reduce(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public void remove(HeroAttrTypeEnum attrType) {
        this.attrToValue.remove(attrType);
    }

    public Map<HeroAttrTypeEnum, Long> getRaw() {
        return new HashMap<>(attrToValue);
    }

    public void clear() {
        this.attrToValue.clear();
        attrToValue = new HashMap<>();
    }

    @Override
    public String toString() {
        return getRaw().toString();
    }

    public static HeroAttrs parse(TupleListCell<Long> tupleList) {
        HeroAttrs heroAttrs = new HeroAttrs();
        for (TupleCell<Long> tuple : tupleList.getAll()) {
            heroAttrs.add(HeroAttr.parse(tuple));
        }
        return heroAttrs;
    }
}
