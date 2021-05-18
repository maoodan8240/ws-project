package ws.sdk;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import ws.common.mongoDB.config.MongoConfig;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.redis.RedisOpration;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.schedule.GlobalScheduler;
import ws.relationship.base.LauncherUtils;
import ws.sdk.features.route.AuthRouteBuilder;
import ws.sdk.features.route.LoginRouteBuilder;
import ws.sdk.features.route.RegisterRouteBuilder;
import ws.sdk.system.config.AppConfig;
import ws.sdk.system.di.GlobalInjectorUtils;
import ws.sdk.system.jmx.JmxJolokiaServer;
import ws.sdk.system.jmx.JmxMBeanManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Launcher {
    public static void main(String[] args) throws Exception {
        _init();
        _startJmxJolokiaServer();
        _startCamel();
    }

    private static void _init() {
        try {
            AppConfig.init();
            GlobalInjectorUtils.init();
            GlobalScheduler.init();
            _redisInit();
            _mongodbInit();

            LauncherUtils._redisInit_Test();
            LauncherUtils._mongodbInit_Test();
        } catch (Exception e) {
            throw new RuntimeException("初始化异常！", e);
        }
    }

    private static void _redisInit() {
        RedisOpration redisOpration = GlobalInjector.getInstance(RedisOpration.class);
        List<String> masterNames = new ArrayList<>(Arrays.asList(AppConfig.getRedisMasterNames()));
        Set<String> sentinelIpAndPorts = new LinkedHashSet<>(Arrays.asList(AppConfig.getRedisSentinelIpAndPorts()));
        int maxTotal = AppConfig.getInt(AppConfig.Key.WS_Common_Config_redis_maxTotal);
        int maxIdlel = AppConfig.getInt(AppConfig.Key.WS_Common_Config_redis_maxIdlel);
        int maxWaitSeconds = AppConfig.getInt(AppConfig.Key.WS_Common_Config_redis_maxWaitSeconds);
        String pwsd = AppConfig.getString(AppConfig.Key.WS_Common_Config_redis_pwsd);
        redisOpration.init(maxTotal, maxIdlel, maxWaitSeconds, pwsd, masterNames, sentinelIpAndPorts);
    }

    private static void _mongodbInit() {
        String host = AppConfig.getString(AppConfig.Key.WS_Common_Config_mongodb_host);
        int port = AppConfig.getInt(AppConfig.Key.WS_Common_Config_mongodb_port);
        String userName = AppConfig.getString(AppConfig.Key.WS_Common_Config_mongodb_userName);
        String password = AppConfig.getString(AppConfig.Key.WS_Common_Config_mongodb_password);
        String dbName = AppConfig.getString(AppConfig.Key.WS_Common_Config_mongodb_dbName);
        int connectionsPerHost = AppConfig.getInt(AppConfig.Key.WS_Common_Config_mongodb_connectionsPerHost);
        int minConnectionsPerHost = AppConfig.getInt(AppConfig.Key.WS_Common_Config_mongodb_minConnectionsPerHost);
        MongoConfig config = new MongoConfig(host, port, userName, password, dbName, connectionsPerHost);
        GlobalInjector.getInstance(MongoDBClient.class).init(config);
    }

    private static void _startJmxJolokiaServer() {
        if (!"true".equals(AppConfig.getString(AppConfig.Key.WS_Common_Config_jmx_conf_jmx_server_enabled))) {
            return;
        }
        JmxJolokiaServer.start();
        JmxMBeanManager.init();
    }

    private static void _startCamel() throws Exception {
        CamelContext context = new DefaultCamelContext();
        _addAllRoutes(context);
        context.start();
    }

    private static void _addAllRoutes(CamelContext context) throws Exception {
        context.addRoutes(new AuthRouteBuilder());
        // context.addRoutes(new ListAllAccountRouteBuilder());
        context.addRoutes(new LoginRouteBuilder());
        context.addRoutes(new RegisterRouteBuilder());
        // context.addRoutes(new UniqueOuterAccountRouteBuilder());
        // context.addRoutes(new RealmlistRouteBuilder());
        // context.addRoutes(new PlayerLoginRecordRouteBuilder());
        // context.addRoutes(new TestRouteBuilder());
    }
}
