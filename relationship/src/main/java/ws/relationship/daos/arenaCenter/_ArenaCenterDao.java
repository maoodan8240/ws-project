package ws.relationship.daos.arenaCenter;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.pvp.arenaCenter.ArenaCenter;

/**
 * Created by lee on 17-3-7.
 */
public class _ArenaCenterDao extends AbstractBaseDao<ArenaCenter> implements ArenaCenterDao {
    public _ArenaCenterDao() {
        super(ArenaCenter.class);
    }
}
