package ws.sdk.features.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.sdk.system.WsRouteBuilder;

public class PlayerLoginRecordRouteBuilder extends WsRouteBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerLoginRecordRouteBuilder.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final int ERR_CODE_PLATFORMTYPE_NOT_EXSIT = 2;

    @Override
    public void _configure() throws Exception {
       
    }
}
