package ws.relationship.daos.simpleGuild;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.simpleGuild.SimpleGuild;

/**
 * Created by lee on 6/12/17.
 */
public class _SimpleGuildDao extends AbstractBaseDao<SimpleGuild> implements SimpleGuildDao {
    public _SimpleGuildDao() {
        super(SimpleGuild.class);
    }
}
