package ws.gameServer.system.di;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import ws.common.mongoDB.implement._MongoDBClient;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.redis.RedisOpration;
import ws.common.redis._RedisOpration;
import ws.common.utils.dataSource.mysql.DataSourceManager;
import ws.common.utils.dataSource.mysql._DataSourceManager;
import ws.common.utils.formula.FormulaContainer;
import ws.common.utils.formula._FormulaContainer;
import ws.gameServer.system.date.dayChanged.DayChanged;
import ws.gameServer.system.date.dayChanged._DayChanged;
import ws.gameServer.system.shutdownHook.ShutdownHook;
import ws.gameServer.system.shutdownHook._ShutdownHook;

/**
 * 绑定所有Bean
 */
public class BindAllBeans {
    public static void bind(Binder binder) {
        binder.bind(RedisOpration.class).to(_RedisOpration.class).in(Scopes.SINGLETON);
        binder.bind(DayChanged.class).to(_DayChanged.class).in(Scopes.SINGLETON);
        binder.bind(DataSourceManager.class).to(_DataSourceManager.class).in(Scopes.SINGLETON);
        binder.bind(MongoDBClient.class).to(_MongoDBClient.class).in(Scopes.SINGLETON);
        binder.bind(FormulaContainer.class).to(_FormulaContainer.class).in(Scopes.SINGLETON);
        binder.bind(ShutdownHook.class).to(_ShutdownHook.class).in(Scopes.SINGLETON);
    }
}
