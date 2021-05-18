package ws.relationship.topLevelPojos.shop;

import ws.protos.EnumsProtos.ShopTypeEnum;
import ws.relationship.topLevelPojos.PlayerTopLevelPojo;

import java.util.HashMap;
import java.util.Map;

/**
 * 所有商店
 */
public class Shops extends PlayerTopLevelPojo {
    private static final long serialVersionUID = -2144034537951666982L;

    private Map<ShopTypeEnum, Shop> typeToShop = new HashMap<>();

    public Shops() {
    }

    public Shops(String playerId) {
        super(playerId);
    }

    public Map<ShopTypeEnum, Shop> getTypeToShop() {
        return typeToShop;
    }

    public void setTypeToShop(Map<ShopTypeEnum, Shop> typeToShop) {
        this.typeToShop = typeToShop;
    }
}
