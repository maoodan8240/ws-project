package ws.relationship.daos.missions;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.mission.Missions;

public class _MissionsDao extends AbstractBaseDao<Missions> implements MissionsDao {
    public _MissionsDao() {
        super(Missions.class);
    }
}
