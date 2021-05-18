package ws.sdk.features.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.sdk.system.WsRouteBuilder;

public class RealmlistRouteBuilder extends WsRouteBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RealmlistRouteBuilder.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);

    @Override
    public void _configure() throws Exception {
      
    }
}
