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
            String msg = String.format("????????????shopType=%s????????????", shopType);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        Shop shop = getShop(shopType);
        if (tryAutoRefreshShop(shop)) {
            syncPart(shop);// ???????????????
            String msg = String.format("???????????????????????????????????????????????????????????????????????????=%s", shopType);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!shop.getIdxToCommodity().containsKey(idx)) {
            String msg = String.format("????????????shopType=%s,???????????????????????????idx=%s????????????", shopType, idx);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        ShopCommodity commodity = shop.getIdxToCommodity().get(idx);
        if (commodity.getCount() <= 0) {
            String msg = String.format("????????????shopType=%s,idx=%s?????????=%s??????????????????", shopType, idx, new IdAndCount(commodity.getIac()));
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (commodity.getCount() < count) {
            String msg = String.format("????????????shopType=%s,idx=%s??????????????????????????????=%s?????????=%s???", shopType, idx, count, commodity.getCount());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdAndCount toAdd = new IdAndCount(commodity.getIac().getId(), commodity.getIac().getCount() * count);
        Table_Item_Row itemRow = RootTc.get(Table_Item_Row.class, toAdd.getId());
        IdAndCount idAndCount = itemRow.getBuyItemPrice(commodity.getCoinType(), toAdd.getCount(), commodity.getDiscount());
        if (idAndCount.getCount() <= 0) {
            String msg = String.format("????????????Id=%s, coinType=%s ??????????????????=%s?????????", commodity.getIac().getId(), commodity.getCoinType());
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
            String msg = String.format("?????????shopType=%s?????????????????????", shopType);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (!shopRow.getCanManualRefresh()) {
            String msg = String.format("?????????shopType=%s?????????, ????????????????????????", shopType);
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
        syncPart(shop);// ???????????????
        save();
    }


    /**
     * ???????????????????????????????????????ForceRefreshNo?????????????????????????????????
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
     * ??????????????????
     */
    private void resetShopData() {
        for (Shop shop : target.getTypeToShop().values()) {
            shop.setRefreshTimes(0);
        }
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    private void checkShop() {
        for (ShopTypeEnum shopType : Table_Shop_Row.shopTypes(getPlayerCtrl().getCurLevel())) {
            tryAddShop(shopType);
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param shopType
     * @return
     */
    private Shop tryAddShop(ShopTypeEnum shopType) {
        if (!containsShop(shopType)) {
            Shop shop = createShop(shopType);
            tryAutoRefreshShop(shop);
            shop.setDisappearTime(disappearTime(shop));
            LOGGER.debug("???????????????={} id={} .", shop.getShopType(), shop.getShopType().getNumber());
            return shop;
        }
        return null;
    }


    /**
     * ????????????????????????
     */
    private void tryAutoRefreshAllShop() {
        for (Shop shop : target.getTypeToShop().values()) {
            tryAutoRefreshShop(shop);
        }
    }

    /**
     * ??????????????????
     *
     * @param shop
     */
    private boolean tryAutoRefreshShop(Shop shop) {
        long curTime = System.currentTimeMillis();
        if (curTime >= shop.getNextRefreshTime()) {
            initShopCommodity(shop);
            shop.setNextRefreshTime(nextRefreshTimePoint(shop));
            LOGGER.debug("??????={} id={} [??????]?????????.", shop.getShopType(), shop.getShopType().getNumber());
            return true;
        }
        return false;
    }

    /**
     * ??????????????????
     *
     * @param shop
     */
    private void forceRefreshShop(Shop shop) {
        initShopCommodity(shop);
        shop.setNextRefreshTime(nextRefreshTimePoint(shop));
        LOGGER.debug("??????={} id={} [??????]?????????.", shop.getShopType(), shop.getShopType().getNumber());
    }

    /**
     * ????????????
     *
     * @param shop
     */
    private void manualRefreshShop(Shop shop) {
        initShopCommodity(shop);
        shop.setRefreshTimes(shop.getRefreshTimes() + 1);
        LOGGER.debug("??????={} id={} [??????]?????????.", shop.getShopType(), shop.getShopType().getNumber());
    }


    /**
     * ??????????????????????????????
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
                String msg = String.format("???????????????????????????shopType=%s, rowId=%s", shop.getShopType(), row.getId());
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
     * ??????shop??????
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
     * ?????????????????????????????????
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
            return curTime + durationTime * 1000l * 2; // 2 ??????????????????
        }
        return ShopsCtrlUtils.calcuNextFreshTime(curTime);
    }


    /**
     * ?????????????????????????????????
     *
     * @param shop
     * @return
     */
    private long disappearTime(Shop shop) {
        if (shop.getShopType() == ShopTypeEnum.SHOP_MYSTERIOUS) { // ????????????
            int durationTime = AllServerConfig.Shops_Mysterious_DurationTime.getConfig();
            return System.currentTimeMillis() + durationTime * 1000l;
        }
        return 0;
    }


    /**
     * ????????????????????????
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
     * ??????????????????????????????
     *
     * @param row
     * @return
     */
    private int getADiscount(Table_ShopGoods_Row row) {
        if (!CellUtils.isEmptyCell(row.getDiscount())) {
            int discount = row.getDiscount().get(TupleCell.FIRST);// [MagicNumbers.LOWEST_DISCOUNT, MagicNumbers.WITHOUT_DISCOUNT]
            if (discount < MagicNumbers.LOWEST_DISCOUNT) {
                String msg = String.format("???????????????????????????(??????????????????1???) rowId=%s discount=%s", row.getId(), discount);
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
     * ????????????????????????????????????
     */
    private void tryRemoveMysteriousShop() {
        Shop shop = getShop(ShopTypeEnum.SHOP_MYSTERIOUS);
        if (shop == null) {
            return;
        }
        if (System.currentTimeMillis() >= shop.getDisappearTime()) { // ????????????????????????
            target.getTypeToShop().remove(ShopTypeEnum.SHOP_MYSTERIOUS);
        }
    }
}
