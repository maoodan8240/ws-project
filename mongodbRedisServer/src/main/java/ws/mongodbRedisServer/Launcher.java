package ws.mongodbRedisServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.config.MongoConfig;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.redis.RedisOpration;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.schedule.GlobalScheduler;
import ws.mongodbRedisServer.system.actor.WsActorSystem;
import ws.mongodbRedisServer.system.config.AppConfig;
import ws.mongodbRedisServer.system.di.GlobalInjectorUtils;
import ws.mongodbRedisServer.system.jmx.JmxJolokiaServer;
import ws.mongodbRedisServer.system.jmx.JmxMBeanManager;
import ws.relationship.base.LauncherUtils;
import ws.relationship.exception.LauncherInitException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Launcher {
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        _init();
        _startJmxJolokiaServer();
        _startActorSystem();
    }

    private static void _init() {
        try {
            AppConfig.init();
            GlobalInjectorUtils.init();
            GlobalScheduler.init();
            redisInit();
            mongodbInit();

            LauncherUtils._redisInit_Test();
            LauncherUtils._mongodbInit_Test();
        } catch (Exception e) {
            throw new LauncherInitException("初始化异常！", e);
        }
    }

    public static void redisInit() {
        RedisOpration redisOpration = GlobalInjector.getInstance(RedisOpration.class);
        List<String> masterNames = new ArrayList<>(Arrays.asList(AppConfig.getRedisMasterNames()));
        Set<String> sentinelIpAndPorts = new LinkedHashSet<>(Arrays.asList(AppConfig.getRedisSentinelIpAndPorts()));
        int maxTotal = AppConfig.getInt(AppConfig.Key.WS_Common_Config_redis_maxTotal);
        int maxIdlel = AppConfig.getInt(AppConfig.Key.WS_Common_Config_redis_maxIdlel);
        int maxWaitSeconds = AppConfig.getInt(AppConfig.Key.WS_Common_Config_redis_maxWaitSeconds);
        String pwsd = AppConfig.getString(AppConfig.Key.WS_Common_Config_redis_pwsd);
        redisOpration.init(maxTotal, maxIdlel, maxWaitSeconds, pwsd, masterNames, sentinelIpAndPorts);
    }

    public static void mongodbInit() {
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

    private static void _startActorSystem() {
        WsActorSystem.init();
    }
}
