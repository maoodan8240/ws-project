package ws.relationship.daos.pyActivities;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.pyActivities.PyActivities;

public class _PyActivitiesDao extends AbstractBaseDao<PyActivities> implements PyActivitiesDao {

    public _PyActivitiesDao() {
        super(PyActivities.class);
    }
}
