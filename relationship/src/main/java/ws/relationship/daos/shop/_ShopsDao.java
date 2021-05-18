package ws.relationship.daos.shop;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.shop.Shops;

public class _ShopsDao extends AbstractBaseDao<Shops> implements ShopsDao {
    public _ShopsDao() {
        super(Shops.class);
    }
}
