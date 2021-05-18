package ws.relationship.daos.pve2;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.newPve.NewPve;

/**
 * Created by lee on 17-1-13.
 */
public class _NewPveDao extends AbstractBaseDao<NewPve> implements NewPveDao {
    public _NewPveDao() {
        super(NewPve.class);
    }
}
