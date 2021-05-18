import org.apache.commons.lang3.time.DateUtils;
import org.bson.types.ObjectId;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.gm.Launcher;
import ws.gm.system.config.AppConfig;
import ws.gm.system.di.GlobalInjectorUtils;
import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.daos.centerPlayer.CenterPlayerDao;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;

/**
 * Created by zhangweiwei on 17-7-12.
 */
public class T {
    public static void main(String[] args) throws Exception {
        AppConfig.init();
        GlobalInjectorUtils.init();
        Launcher._mongodbInit();
        MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
        // PlayerLvUpLogDao dao = GlobalInjector.getInstance(PlayerLvUpLogDao.class);
        //
        // dao.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Logs);
        // dao.findLvByDate("20170720", "20170722", "AWO", 1);
        CenterPlayerDao dao = GlobalInjector.getInstance(CenterPlayerDao.class);
        dao.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
        CenterPlayer centerPlayer = new CenterPlayer(ObjectId.get().toString(), 1112223, 1, ObjectId.get().toString(), PlatformTypeEnum.AWO, 1, "ccc", false);
        centerPlayer.setLastLockTime(System.currentTimeMillis() + 50 * DateUtils.MILLIS_PER_MINUTE);
        centerPlayer.setPlayerName("weiweiwei");
        dao.insert(centerPlayer);
        dao.queryAllLockPlayerList();
        System.out.println(dao.findAll().size());
    }
}
