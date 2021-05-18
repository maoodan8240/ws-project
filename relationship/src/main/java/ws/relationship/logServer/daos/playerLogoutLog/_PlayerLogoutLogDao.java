package ws.relationship.logServer.daos.playerLogoutLog;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.logServer.pojos.PlayerLogoutLog;

/**
 * Created by zww on 8/10/16.
 */
public class _PlayerLogoutLogDao extends AbstractBaseDao<PlayerLogoutLog> implements PlayerLogoutLogDao {
    public _PlayerLogoutLogDao() {
        super(PlayerLogoutLog.class);
    }
}
