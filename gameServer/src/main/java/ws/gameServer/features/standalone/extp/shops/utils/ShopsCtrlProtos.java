package ws.gameServer.features.standalone.extp.shops.utils;

import ws.protos.EnumsProtos.ShopTypeEnum;
import ws.protos.ShopsProtos.Sm_Shops_Commodity;
import ws.protos.ShopsProtos.Sm_Shops_OneShop;
import ws.relationship.topLevelPojos.shop.Shop;
import ws.relationship.topLevelPojos.shop.ShopCommodity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShopsCtrlProtos {

    public static List<Sm_Shops_OneShop> create_Sm_Shops_OneShop_Lis(Map<ShopTypeEnum, Shop> typeToShop) {
        List<Sm_Shops_OneShop> bs = new ArrayList<>();
        for (Shop shop : typeToShop.values()) {
            Sm_Shops_OneShop.Builder b = Sm_Shops_OneShop.newBuilder();
            b.clear();
            bs.add(create_Sm_Shops_OneShop(b, shop));
        }
        return bs;
    }

    public static Sm_Shops_OneShop create_Sm_Shops_OneShop(Sm_Shops_OneShop.Builder b, Shop shop) {
        b.setShopType(shop.getShopType());
        b.setRefreshTimes(shop.getRefreshTimes());
        b.setNextRefreshTime(shop.getNextRefreshTime());
        b.setDisappearTime(shop.getDisappearTime());
        b.addAllCommodityList(create_Sm_Shops_Commodity_Lis(shop.getIdxToCommodity()));
        return b.build();
    }

    public static Sm_Shops_OneShop create_Sm_Shops_OneShop(Shop shop) {
        return create_Sm_Shops_OneShop(Sm_Shops_OneShop.newBuilder(), shop);
    }

    public static List<Sm_Shops_Commodity> create_Sm_Shops_Commodity_Lis(Map<Integer, ShopCommodity> idxToCommodity) {
        List<Sm_Shops_Commodity> bs = new ArrayList<>();
        for (ShopCommodity commodity : idxToCommodity.values()) {
            Sm_Shops_Commodity.Builder b = Sm_Shops_Commodity.newBuilder();
            b.clear();
            bs.add(create_Sm_Shops_Commodity(b, commodity));
        }
        return bs;
    }

    public static Sm_Shops_Commodity create_Sm_Shops_Commodity(Sm_Shops_Commodity.Builder b, ShopCommodity commodity) {
        b.setIdx(commodity.getIdx());
        b.setId(commodity.getIac().getId());
        b.setNum(commodity.getIac().getCount());
        b.setDiscount(commodity.getDiscount());
        b.setCount(commodity.getCount());
        b.setCoinType(commodity.getCoinType());
        return b.build();
    }
}
