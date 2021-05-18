package ws.relationship.daos.hof;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.hof.HOF;

/**
 * Created by lee on 17-2-6.
 */
public class _HOFDao extends AbstractBaseDao<HOF> implements HOFDao {
    public _HOFDao() {
        super(HOF.class);
    }
}
