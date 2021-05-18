package ws.relationship.daos.names;

import ws.common.mongoDB.interfaces.BaseDao;
import ws.relationship.topLevelPojos.names.Names;

public interface NamesDao extends BaseDao<Names> {

    /**
     * @param newName
     * @return
     */
    boolean inertNewName(String newName);
}
