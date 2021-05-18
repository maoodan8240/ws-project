package ws.relationship.daos.ultimateTest;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.ultimateTest.UltimateTest;

/**
 * Created by lee on 17-3-27.
 */
public class _UltimateTestDao extends AbstractBaseDao<UltimateTest> implements UltimateTestDao {
    public _UltimateTestDao() {
        super(UltimateTest.class);
    }
}
