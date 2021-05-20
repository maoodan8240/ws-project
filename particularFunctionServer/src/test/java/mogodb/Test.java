package mogodb;

import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.particularFunctionServer.Launcher;
import ws.particularFunctionServer.system.config.AppConfig;
import ws.particularFunctionServer.system.di.GlobalInjectorUtils;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.daos.names.NamesDao;
import ws.relationship.daos.names._NamesDao;

/**
 * Created by lee on 17-5-24.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        AppConfig.init();
        GlobalInjectorUtils.init();
        Launcher.mongodbInit();
        int rank = 12379;


        MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
        NamesDao testADao = new _NamesDao();
        testADao.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);

        /**
         List<ArenaCenterRanker> lis = new ArrayList<>();
         for (int i = 0; i < 5000; i++) {
         ArenaCenterRanker rankerNew1 = new ArenaCenterRanker(ObjectId.get().toString(), i, 1);
         lis.add(rankerNew1);
         }
         for (int i = 0; i < 100; i++) {
         long t1 = System.nanoTime();
         testADao.mutliReplace(lis);
         long t2 = System.nanoTime();
         System.out.println("耗时：" + ((t2 - t1) / (1000 * 1000)));
         }
         */
        testADao.inertNewName("2222");
    }
}
