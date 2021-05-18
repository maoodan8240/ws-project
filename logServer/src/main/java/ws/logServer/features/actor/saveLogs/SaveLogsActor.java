package ws.logServer.features.actor.saveLogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.BaseDao;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.mongoDB.interfaces.TopLevelPojo;
import ws.common.utils.di.GlobalInjector;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.msg.In_LogMsgListToLogServer;
import ws.relationship.base.msg.In_LogMsgToLogServer;
import ws.relationship.logServer.base.WsLog;
import ws.relationship.logServer.daos.LogDaoContainer;
import ws.relationship.logServer.pojos.ItemLog;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SaveLogsActor extends WsActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveLogsActor.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final Map<String, BaseDao<TopLevelPojo>> collectionNameToDaoCache = new ConcurrentHashMap<>();


    @Override
    public void onRecv(Object msg) throws Exception {
        if (msg instanceof In_LogMsgToLogServer) {
            onLogMsgToLogServer(((In_LogMsgToLogServer) msg).getWsLog());
        } else if (msg instanceof In_LogMsgListToLogServer) {
            onLogMsgListToLogServer(((In_LogMsgListToLogServer) msg).getWsLogLis());
        }
    }

    private void onLogMsgListToLogServer(List<WsLog> wsLogLis) {
        //todo 可以使用批量插入，待优化
        wsLogLis.forEach(wsLog -> {
            onLogMsgToLogServer(wsLog);
        });
    }

    private void onLogMsgToLogServer(WsLog wsLog) {
        getDao(wsLog).insertIfExistThenReplace(wsLog);
    }


    public static BaseDao<TopLevelPojo> getDao(WsLog wsLog) {
        String collectionName = getCollectionName(wsLog);
        if (!collectionNameToDaoCache.containsKey(collectionName)) {
            newAndCacheDao(wsLog);
        }
        return collectionNameToDaoCache.get(collectionName);
    }

    private static void newAndCacheDao(WsLog wsLog) {
        if (wsLog instanceof ItemLog) {
            ItemLog itemLog = (ItemLog) wsLog;
            String collectionName = itemLog.getType().name();
            BaseDao<TopLevelPojo> itemLogDao = LogDaoContainer.getDao(ItemLog.class);
            itemLogDao.init(MONGO_DB_CLIENT, collectionName);
//            collectionNameToDaoCache.put(itemLogDao.getMongoCollectionName(), itemLogDao);
        } else {
            BaseDao<TopLevelPojo> dao = LogDaoContainer.getDao(wsLog.getClass());
            dao.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Logs);
//            collectionNameToDaoCache.put(dao.getMongoCollectionName(), dao);
        }
    }

    private static String getCollectionName(WsLog wsLog) {
        if (wsLog instanceof ItemLog) {
            ItemLog itemLog = (ItemLog) wsLog;
            return itemLog.getType().name();
        } else {
            return wsLog.getClass().getSimpleName();
        }
    }
}
