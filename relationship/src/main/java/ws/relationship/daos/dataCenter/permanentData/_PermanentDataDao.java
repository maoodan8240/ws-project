package ws.relationship.daos.dataCenter.permanentData;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.dataCenter.permanentData.PermanentData;

public class _PermanentDataDao extends AbstractBaseDao<PermanentData> implements PermanentDataDao {

    public _PermanentDataDao() {
        super(PermanentData.class);
    }
}
