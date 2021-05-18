package ws.relationship.daos.example;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.example.Example;

public class _ExampleDao extends AbstractBaseDao<Example> implements ExampleDao {

    public _ExampleDao() {
        super(Example.class);
    }
}
