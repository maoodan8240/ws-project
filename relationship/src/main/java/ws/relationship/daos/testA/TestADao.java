package ws.relationship.daos.testA;

import ws.common.mongoDB.interfaces.BaseDao;
import ws.relationship.topLevelPojos.testA.TestA;

public interface TestADao extends BaseDao<TestA> {


    void test();

    void test1();

    void test2();
}
