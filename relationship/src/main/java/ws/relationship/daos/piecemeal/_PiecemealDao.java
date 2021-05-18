package ws.relationship.daos.piecemeal;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.piecemeal.Piecemeal;

/**
 * Created by lee on 17-4-13.
 */
public class _PiecemealDao extends AbstractBaseDao<Piecemeal> implements PiecemealDao {
    public _PiecemealDao() {
        super(Piecemeal.class);
    }
}
