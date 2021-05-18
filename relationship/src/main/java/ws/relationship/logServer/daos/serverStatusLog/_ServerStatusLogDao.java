package ws.relationship.logServer.daos.serverStatusLog;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.logServer.pojos.ServerStatusLog;

/**
 * Created by zww on 8/10/16.
 */
public class _ServerStatusLogDao extends AbstractBaseDao<ServerStatusLog> implements ServerStatusLogDao {
    public _ServerStatusLogDao() {
        super(ServerStatusLog.class);
    }
}
