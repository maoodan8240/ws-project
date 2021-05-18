package ws.relationship.daos.newGuildCenter;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenter;

/**
 * Created by lee on 16-11-30.
 */
public class _NewGuildCenterDao extends AbstractBaseDao<NewGuildCenter> implements NewGuildCenterDao {
    public _NewGuildCenterDao() {
        super(NewGuildCenter.class);
    }
}
