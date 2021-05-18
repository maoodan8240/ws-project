package ws.relationship.daos.heros;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.heros.Heros;

public class _HerosDao extends AbstractBaseDao<Heros> implements HerosDao {

    public _HerosDao() {
        super(Heros.class);
    }
}
