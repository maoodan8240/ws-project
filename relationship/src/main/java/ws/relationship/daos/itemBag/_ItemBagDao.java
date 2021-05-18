package ws.relationship.daos.itemBag;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.itemBag.ItemBag;

public class _ItemBagDao extends AbstractBaseDao<ItemBag> implements ItemBagDao {

    public _ItemBagDao() {
        super(ItemBag.class);
    }
}
