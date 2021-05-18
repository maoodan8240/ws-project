package ws.gameServer.features.actor.register.utils.create.ext;

import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.relationship.topLevelPojos.itemBag.ItemBag;

public class _100_ItemBagExtIniter implements ExtensionIniter {
    @Override
    public void init(ExtCommonData commonData) throws Exception {
        ItemBag itemBag = new ItemBag(commonData.getPlayer().getPlayerId());

        commonData.addPojo(itemBag);
    }
}
