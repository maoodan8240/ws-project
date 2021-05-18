package ws.relationship.topLevelPojos.shop;

import ws.protos.EnumsProtos.ShopTypeEnum;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 单个商店
 */
public class Shop implements Serializable {
    private static final long serialVersionUID = 3557539904721788797L;

    private ShopTypeEnum shopType;                                         // 当前商店类型
    private Map<Integer, ShopCommodity> idxToCommodity = new HashMap<>();  // 商品列表
    private int forceRefreshNo;                                            // 强制刷新标识
    private int refreshTimes;                                              // 手动刷新的次数
    private long nextRefreshTime;                                          // 下次刷新时间点
    private long disappearTime;                                            // 消失的时间点

    public Shop() {
    }

    public Shop(ShopTypeEnum shopType) {
        this.shopType = shopType;
    }

    public ShopTypeEnum getShopType() {
        return shopType;
    }

    public void setShopType(ShopTypeEnum shopType) {
        this.shopType = shopType;
    }

    public Map<Integer, ShopCommodity> getIdxToCommodity() {
        return idxToCommodity;
    }

    public void setIdxToCommodity(Map<Integer, ShopCommodity> idxToCommodity) {
        this.idxToCommodity = idxToCommodity;
    }

    public int getRefreshTimes() {
        return refreshTimes;
    }

    public void setRefreshTimes(int refreshTimes) {
        this.refreshTimes = refreshTimes;
    }

    public long getNextRefreshTime() {
        return nextRefreshTime;
    }

    public void setNextRefreshTime(long nextRefreshTime) {
        this.nextRefreshTime = nextRefreshTime;
    }

    public long getDisappearTime() {
        return disappearTime;
    }

    public void setDisappearTime(long disappearTime) {
        this.disappearTime = disappearTime;
    }

    public int getForceRefreshNo() {
        return forceRefreshNo;
    }

    public void setForceRefreshNo(int forceRefreshNo) {
        this.forceRefreshNo = forceRefreshNo;
    }
}
