package ws.particularFunctionServer.test;

import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.particularFunctionServer.Launcher;
import ws.particularFunctionServer.system.config.AppConfig;
import ws.particularFunctionServer.system.di.GlobalInjectorUtils;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.simpleId.SimpleIdDao;
import ws.relationship.enums.SimpleIdTypeEnum;
import ws.relationship.topLevelPojos.simpleId.SimpleId;

/**
 * Created by zhangweiwei on 17-4-5.
 */
public class Test2 {
    public static void main(String[] args) throws Exception {
        AppConfig.init();
        GlobalInjectorUtils.init();
        Launcher.mongodbInit();

        MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
        SimpleIdDao CENTER_PLAYER_DAO = DaoContainer.getDao(SimpleId.class);
        CENTER_PLAYER_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
        int simpleId = CENTER_PLAYER_DAO.nextSimpleId(1, SimpleIdTypeEnum.PLAYER);
        System.out.println(simpleId);
    }
}
