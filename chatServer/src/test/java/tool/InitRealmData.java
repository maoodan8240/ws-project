package tool;

import org.bson.types.ObjectId;
import ws.chatServer.Launcher;
import ws.chatServer.system.config.AppConfig;
import ws.chatServer.system.di.GlobalInjectorUtils;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.schedule.GlobalScheduler;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.sdk.realm.OuterToInnerRealmListDao;
import ws.relationship.topLevelPojos.sdk.realm.OuterToInnerRealmList;

/**
 * Created by zhangweiwei on 17-5-5.
 */
public class InitRealmData {

    public static void main(String[] args) throws Exception {
        AppConfig.init();
        GlobalInjectorUtils.init();
        GlobalScheduler.init();
        Launcher.redisInit();
        Launcher.mongodbInit();

        OuterToInnerRealmList outerToInnerRealmList = new OuterToInnerRealmList();
        outerToInnerRealmList.setAuto(ObjectId.get().toString());
        outerToInnerRealmList.setOuterRealmId(1);
        outerToInnerRealmList.setInnerRealmId(1);
        outerToInnerRealmList.setGameServerRole("WS-GameServer-10.19.18.225-23000");
        // WS-GameServer-10.19.18.225-23000
        MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
        OuterToInnerRealmListDao OUTER_TO_INNER_REALMLIST_DAO = DaoContainer.getDao(OuterToInnerRealmList.class);
        OUTER_TO_INNER_REALMLIST_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
        OUTER_TO_INNER_REALMLIST_DAO.insert(outerToInnerRealmList);

    }

}

