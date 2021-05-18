package ws.sdk.system.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import ws.common.mongoDB.implement._MongoDBClient;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.redis.RedisOpration;
import ws.common.redis._RedisOpration;
import ws.common.utils.di.GlobalInjector;
import ws.relationship.daos.example.ExampleDao;
import ws.relationship.daos.example._ExampleDao;
import ws.relationship.daos.sdk.account.AccountDao;
import ws.relationship.daos.sdk.account._AccountDao;
import ws.relationship.daos.sdk.loginRecord.PlayerLoginRecordDao;
import ws.relationship.daos.sdk.loginRecord._PlayerLoginRecordDao;
import ws.relationship.daos.sdk.realm.OuterToInnerRealmListDao;
import ws.relationship.daos.sdk.realm._OuterToInnerRealmListDao;
import ws.sdk.features.session.SessionManager;
import ws.sdk.features.session._SessionManager;

public class GlobalInjectorUtils {
    public static void init() throws Exception {
        Injector injector = Guice.createInjector(binder -> {
            binder.bind(SessionManager.class).toInstance(new _SessionManager());
            binder.bind(ExampleDao.class).to(_ExampleDao.class);
            binder.bind(AccountDao.class).to(_AccountDao.class);
            binder.bind(PlayerLoginRecordDao.class).to(_PlayerLoginRecordDao.class);
            binder.bind(OuterToInnerRealmListDao.class).to(_OuterToInnerRealmListDao.class);
            binder.bind(RedisOpration.class).to(_RedisOpration.class).in(Scopes.SINGLETON);
            binder.bind(MongoDBClient.class).to(_MongoDBClient.class).in(Scopes.SINGLETON);

        });
        GlobalInjector.setDefaultInjector(injector);
    }
}
