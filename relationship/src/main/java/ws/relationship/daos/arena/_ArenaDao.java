package ws.relationship.daos.arena;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.pvp.arena.Arena;

/**
 * Created by zww on 8/10/16.
 */
public class _ArenaDao extends AbstractBaseDao<Arena> implements ArenaDao {
    public _ArenaDao() {
        super(Arena.class);
    }
}
