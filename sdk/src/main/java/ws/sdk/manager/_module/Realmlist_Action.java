package ws.sdk.manager._module;

import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.sdk.manager.Action;

public class Realmlist_Action implements Action {
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final String MODIFY = "modify";
    private static final String ADD = "add";
    private static final String QUERY = "query";

    @Override
    public String handle(String funcName, String args) {
        
        return null;
    }

}
