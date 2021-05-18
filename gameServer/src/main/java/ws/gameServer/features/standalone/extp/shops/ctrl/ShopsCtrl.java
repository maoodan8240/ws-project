package ws.gameServer.features.standalone.extp.shops.ctrl;

import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.protos.EnumsProtos.ShopTypeEnum;
import ws.relationship.topLevelPojos.shop.Shops;


public interface ShopsCtrl extends PlayerExteControler<Shops> {

    /**
     * 购买商店物品
     *
     * @param shopType
     * @param idx
     * @param count
     */
    void buy(ShopTypeEnum shopType, int idx, int count);

    /**
     * 刷新商店
     *
     * @param shopType
     */
    void refresh(ShopTypeEnum shopType);


    /**
     * 触发神秘商店
     */
    void triggerMysteriousShop();
}
