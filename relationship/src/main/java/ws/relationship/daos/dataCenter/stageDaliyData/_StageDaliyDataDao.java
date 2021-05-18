package ws.relationship.daos.dataCenter.stageDaliyData;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.dataCenter.stageDaliyData.StageDaliyData;

public class _StageDaliyDataDao extends AbstractBaseDao<StageDaliyData> implements StageDaliyDataDao {

    public _StageDaliyDataDao() {
        super(StageDaliyData.class);
    }
}
