package ws.relationship.topLevelPojos.shop;

import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.MagicNumbers;
import ws.relationship.topLevelPojos.common.Iac;

import java.io.Serializable;

/**
 * 单个商品
 */
public class ShopCommodity implements Serializable {
    private static final long serialVersionUID = 893411264335715385L;

    private int idx;                                         // 索引
    private ResourceTypeEnum coinType;                       // 购买的货币类型
    private Iac iac;                                         // 商品id-物品数量(该数量只能一次性购买)
    private int discount = MagicNumbers.WITHOUT_DISCOUNT;    // 折扣力度
    private int count;                                       // 可以购买的次数 <=0表示已经被购买完

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public Iac getIac() {
        return iac;
    }

    public void setIac(Iac iac) {
        this.iac = iac;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ResourceTypeEnum getCoinType() {
        return coinType;
    }

    public void setCoinType(ResourceTypeEnum coinType) {
        this.coinType = coinType;
    }
}
