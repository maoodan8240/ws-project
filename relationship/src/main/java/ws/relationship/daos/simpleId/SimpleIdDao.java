package ws.relationship.daos.simpleId;

import ws.common.mongoDB.interfaces.BaseDao;
import ws.relationship.enums.SimpleIdTypeEnum;
import ws.relationship.topLevelPojos.simpleId.SimpleId;

public interface SimpleIdDao extends BaseDao<SimpleId> {
    /**
     * 下一个简单Id
     *
     * @param outerRealmId
     * @param type
     * @return
     */
    int nextSimpleId(int outerRealmId, SimpleIdTypeEnum type);

    /**
     * 下一个简单Id
     *
     * @param type
     * @return
     */
    int nextSimpleId(SimpleIdTypeEnum type);
}
