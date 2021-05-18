package ws.relationship.daos.formations;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.formations.Formations;

public class _FormationsDao extends AbstractBaseDao<Formations> implements FormationsDao {

    public _FormationsDao() {
        super(Formations.class);
    }
}
