package ws.gameServer.features.standalone.extp.shops.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.table.table.utils.CellUtils;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.resourcePoint.ResourcePointExtp;
import ws.gameServer.features.standalone.extp.resourcePoint.ctrl.ResourcePointCtrl;
import ws.gameServer.features.standalone.extp.shops.utils.ShopsCtrlProtos;
import ws.gameServer.features.standalone.extp.shops.utils.ShopsCtrlUtils;
import ws.gameServer.features.standalone.extp.utils.CommonUtils;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.EnumsProtos.ShopTypeEnum;
import ws.protos.ShopsProtos.Sm_Shops;
import ws.protos.ShopsProtos.Sm_Shops.Action;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Consume_Row;
import ws.relationship.table.tableRows.Table_DropLibrary_Row;
import ws.relationship.table.tableRows.Table_Item_Row;
import ws.relationship.table.tableRows.Table_ShopGoods_Row;
import ws.relationship.table.tableRows.Table_Shop_Row;
import ws.relationship.topLevelPojos.shop.Shop;
import ws.relationship.topLevelPojos.shop.ShopCommodity;
import ws.relationship.topLevelPojos.shop.Shops;
import ws.relationship.utils.RandomUtils;

import java.util.List;


public class _ShopsCtrl extends AbstractPlayerExteControler<Shops> implements ShopsCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_ShopsCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private ResourcePointExtp resourcePointExtp;
    private ResourcePointCtrl resourcePointCtrl;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();

        resourcePointExtp = getPlayerCtrl().getExtension(ResourcePointExtp.class);
        resourcePointCtrl = resourcePointExtp.getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
        resetShopData();
    }

    @Override
    public void sync() {
        checkShop();
        tryForceRefeshAllShop();
        tryRemoveMysteriousShop();
        tryAutoRefreshAllShop();
        SenderFunc.sendInner(this, Sm_Shops.class, Sm_Shops.Builder.class, Action.RESP_SYNC, (b, br) -> {
            b.addAllShops(ShopsCtrlProtos.create_Sm_Shops_OneShop_Lis(target.getTypeToShop()));
        });
        save();
    }


    private void syncPart(Shop shop) {
        SenderFunc.sendInner(this, Sm_Shops.class, Sm_Shops.Builder.class, Action.RESP_SYNC_PART, (b, br) -> {
            b.addShops(ShopsCtrlProtos.create_Sm_Shops_OneShop(shop));
        });
    }


    @Override
    public void buy(ShopTypeEnum shopType, int idx, int count) {
        tryRemoveMysteriousShop();
        LogicCheckUtils.validateParam(ShopTypeEnum.class, shopType);
        LogicCheckUtils.validateParam(Integer.class, idx);
        LogicCheckUtils.validateParam(Integer.class, count);
        if (!containsShop(shopType)) {
            String msg = String.format("商店类型shopType=%s不存在！", shopType);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Shop shop = getShop(shopType);
        if (tryAutoRefreshShop(shop)) {
            syncPart(shop);// 刷新客户端
            String msg = String.format("到了刷新时间点了，本次购买失败，重新购买！商店类型=%s", shopType);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!shop.getIdxToCommodity().containsKey(idx)) {
            String msg = String.format("商店类型shopType=%s,物品中不存在位置为idx=%s的商品！", shopType, idx);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        ShopCommodity commodity = shop.getIdxToCommodity().get(idx);
        if (commodity.getCount() <= 0) {
            String msg = String.format("商店类型shopType=%s,idx=%s的商品=%s已经卖光了！", shopType, idx, new IdAndCount(commodity.getIac()));
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (commodity.getCount() < count) {
            String msg = String.format("商店类型shopType=%s,idx=%s的商品数量不足，需求=%s，剩余=%s！", shopType, idx, count, commodity.getCount());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdAndCount toAdd = new IdAndCount(commodity.getIac().getId(), commodity.getIac().getCount() * count);
        Table_Item_Row itemRow = RootTc.get(Table_Item_Row.class, toAdd.getId());
        IdAndCount idAndCount = itemRow.getBuyItemPrice(commodity.getCoinType(), toAdd.getCount(), commodity.getDiscount());
        if (idAndCount.getCount() <= 0) {
            String msg = String.format("购买商店Id=%s, coinType=%s 所需货币数量=%s异常！", commodity.getIac().getId(), commodity.getCoinType());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        LogicCheckUtils.canRemove(itemIoCtrl, idAndCount);
        commodity.setCount(commodity.getCount() - count);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_BUY).removeItem(idAndCount);
        IdMaptoCount refresh_2 = itemIoExtp.getControlerForUpdate(Action.RESP_BUY).addItem(toAdd);
        SenderFunc.sendInner(this, Sm_Shops.class, Sm_Shops.Builder.class, Action.RESP_BUY, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).addAll(refresh_2), br);
        });
        save();
    }

    @Override
    public void refresh(ShopTypeEnum shopType) {
        LogicCheckUtils.validateParam(ShopTypeEnum.class, shopType);
        Table_Shop_Row shopRow = RootTc.get(Table_Shop_Row.class, shopType.getNumber());
        if (!containsShop(shopType)) {
            String msg = String.format("类型为shopType=%s的商店不存在！", shopType);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!shopRow.getCanManualRefresh()) {
            String msg = String.format("类型为shopType=%s的商店, 不支持手动刷新！", shopType);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Shop shop = getShop(shopType);
        int nextRefreshTimes = shop.getRefreshTimes() + 1;
        IdAndCount idAndCount = Table_Consume_Row.shopRefreshConsume(nextRefreshTimes);
        LogicCheckUtils.canRemove(itemIoCtrl, idAndCount);
        manualRefreshShop(shop);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_REFRESH).removeItem(idAndCount);
        SenderFunc.sendInner(this, Sm_Shops.class, Sm_Shops.Builder.class, Action.RESP_REFRESH, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1), br);
            b.addShops(ShopsCtrlProtos.create_Sm_Shops_OneShop(shop));
        });
        save();
    }

    @Override
    public void triggerMysteriousShop() {
        if (containsShop(ShopTypeEnum.SHOP_MYSTERIOUS)) {
            return;
        }
        Shop shop = tryAddShop(ShopTypeEnum.SHOP_MYSTERIOUS);
        if (shop == null) {
            return;
        }
        syncPart(shop);// 刷新客户端
        save();
    }


    /**
     * 尝试强制刷新所有商店，如果ForceRefreshNo字段改变了，则强制刷新
     */
    private void tryForceRefeshAllShop() {
        for (Shop shop : target.getTypeToShop().values()) {
            Table_Shop_Row shopRow = RootTc.get(Table_Shop_Row.class, shop.getShopType().getNumber());
            if (shop.getForceRefreshNo() != shopRow.getForceRefreshNo()) {
                forceRefreshShop(shop);
            }
        }
    }

    /**
     * 重置商店数据
     */
    private void resetShopData() {
        for (Shop shop : target.getTypeToShop().values()) {
            shop.setRefreshTimes(0);
        }
    }

    /**
     * 检查是否有新增的商店
     *
     * @return
     */
    private void checkShop() {
        for (ShopTypeEnum shopType : Table_Shop_Row.shopTypes(getPlayerCtrl().getCurLevel())) {
            tryAddShop(shopType);
        }
    }

    /**
     * 尝试添加一个类型的商店
     *
     * @param shopType
     * @return
     */
    private Shop tryAddShop(ShopTypeEnum shopType) {
        if (!containsShop(shopType)) {
            Shop shop = createShop(shopType);
            tryAutoRefreshShop(shop);
            shop.setDisappearTime(disappearTime(shop));
            LOGGER.debug("新增了商店={} id={} .", shop.getShopType(), shop.getShopType().getNumber());
            return shop;
        }
        return null;
    }


    /**
     * 自动刷新所有商店
     */
    private void tryAutoRefreshAllShop() {
        for (Shop shop : target.getTypeToShop().values()) {
            tryAutoRefreshShop(shop);
        }
    }

    /**
     * 自动尝试刷新
     *
     * @param shop
     */
    private boolean tryAutoRefreshShop(Shop shop) {
        long curTime = System.currentTimeMillis();
        if (curTime >= shop.getNextRefreshTime()) {
            initShopCommodity(shop);
            shop.setNextRefreshTime(nextRefreshTimePoint(shop));
            LOGGER.debug("商店={} id={} [自动]刷新了.", shop.getShopType(), shop.getShopType().getNumber());
            return true;
        }
        return false;
    }

    /**
     * 强制刷新商店
     *
     * @param shop
     */
    private void forceRefreshShop(Shop shop) {
        initShopCommodity(shop);
        shop.setNextRefreshTime(nextRefreshTimePoint(shop));
        LOGGER.debug("商店={} id={} [强制]刷新了.", shop.getShopType(), shop.getShopType().getNumber());
    }

    /**
     * 手动刷新
     *
     * @param shop
     */
    private void manualRefreshShop(Shop shop) {
        initShopCommodity(shop);
        shop.setRefreshTimes(shop.getRefreshTimes() + 1);
        LOGGER.debug("商店={} id={} [手动]刷新了.", shop.getShopType(), shop.getShopType().getNumber());
    }


    /**
     * 初始化商店的所有商品
     *
     * @param shop
     */
    private void initShopCommodity(Shop shop) {
        shop.getIdxToCommodity().clear();
        Table_Shop_Row shopRow = RootTc.get(Table_Shop_Row.class, shop.getShopType().getNumber());
        List<Table_ShopGoods_Row> rows = Table_ShopGoods_Row.curLevelRows(shop.getShopType(), getPlayerCtrl().getCurLevel());
        for (Table_ShopGoods_Row row : rows) {
            try {
                ShopCommodity commodity = createShopCommodity(row);
                shop.getIdxToCommodity().put(commodity.getIdx(), commodity);
            } catch (Exception e) {
                String msg = String.format("构建商品单元失败！shopType=%s, rowId=%s", shop.getShopType(), row.getId());
                throw new BusinessLogicMismatchConditionException(msg, e);
            }
        }
        shop.setForceRefreshNo(shopRow.getForceRefreshNo());
    }


    private boolean containsShop(ShopTypeEnum shopType) {
        return target.getTypeToShop().containsKey(shopType);
    }

    private Shop getShop(ShopTypeEnum shopType) {
        return target.getTypeToShop().get(shopType);
    }

    /**
     * 创建shop对象
     *
     * @param shopType
     * @return
     */
    private Shop createShop(ShopTypeEnum shopType) {
        Shop shop = new Shop(shopType);
        target.getTypeToShop().put(shop.getShopType(), shop);
        return shop;
    }


    /**
     * 当前时间的下一个刷新点
     *
     * @param shop
     * @return
     */
    private long nextRefreshTimePoint(Shop shop) {
        long curTime = System.currentTimeMillis();
        if (shop.getShopType() == ShopTypeEnum.SHOP_AWAKE_FRAGMENT) {
            int interval = AllServerConfig.Shops_AwakeFragmentRefeshInterval.getConfig();
            return curTime + interval * 1000l;
        } else if (shop.getShopType() == ShopTypeEnum.SHOP_MYSTERIOUS) {
            int durationTime = AllServerConfig.Shops_Mysterious_DurationTime.getConfig();
            return curTime + durationTime * 1000l * 2; // 2 倍的持续时间
        }
        return ShopsCtrlUtils.calcuNextFreshTime(curTime);
    }


    /**
     * 持续时间后消失的时间点
     *
     * @param shop
     * @return
     */
    private long disappearTime(Shop shop) {
        if (shop.getShopType() == ShopTypeEnum.SHOP_MYSTERIOUS) { // 神秘商店
            int durationTime = AllServerConfig.Shops_Mysterious_DurationTime.getConfig();
            return System.currentTimeMillis() + durationTime * 1000l;
        }
        return 0;
    }


    /**
     * 构建一个商店商品
     *
     * @param row
     * @return
     */
    private ShopCommodity createShopCommodity(Table_ShopGoods_Row row) {
        IdMaptoCount idMaptoCount = Table_DropLibrary_Row.drop(row.getGoodsLibrary(), getPlayerCtrl().getCurLevel());
        IdAndCount idAndCount = CommonUtils.getFirstIdAndCount(idMaptoCount);
        ShopCommodity commodity = new ShopCommodity();
        commodity.setIac(idAndCount.getIac());
        commodity.setIdx(row.getGoodsPos());
        commodity.setDiscount(getADiscount(row));
        commodity.setCount(row.getCanBuyTimes());
        commodity.setCoinType(row.getCoinType());
        return commodity;
    }


    /**
     * 获取折扣，有概率打折
     *
     * @param row
     * @return
     */
    private int getADiscount(Table_ShopGoods_Row row) {
        if (!CellUtils.isEmptyCell(row.getDiscount())) {
            int discount = row.getDiscount().get(TupleCell.FIRST);// [MagicNumbers.LOWEST_DISCOUNT, MagicNumbers.WITHOUT_DISCOUNT]
            if (discount < MagicNumbers.LOWEST_DISCOUNT) {
                String msg = String.format("获取折扣信息异常！(折扣不能小于1折) rowId=%s discount=%s", row.getId(), discount);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            discount = Math.min(discount, MagicNumbers.WITHOUT_DISCOUNT);
            int prob = row.getDiscount().get(TupleCell.SECOND);
            boolean rs = RandomUtils.isDropPartsFractionOfBase(prob, MagicNumbers.RANDOM_BASE_VALUE);
            if (rs) {
                return discount;
            }
        }
        return MagicNumbers.WITHOUT_DISCOUNT;
    }


    /**
     * 尝试移除到时间的神秘商店
     */
    private void tryRemoveMysteriousShop() {
        Shop shop = getShop(ShopTypeEnum.SHOP_MYSTERIOUS);
        if (shop == null) {
            return;
        }
        if (System.currentTimeMillis() >= shop.getDisappearTime()) { // 达到消失的时间了
            target.getTypeToShop().remove(ShopTypeEnum.SHOP_MYSTERIOUS);
        }
    }
}
