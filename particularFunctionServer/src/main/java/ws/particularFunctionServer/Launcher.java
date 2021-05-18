package ws.particularFunctionServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.config.MongoConfig;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.redis.RedisOpration;
import ws.common.table.data.PlanningTableData;
import ws.common.utils.dataSource.txt._PlanningTableData;
import ws.common.utils.di.GlobalInjector;
import ws.particularFunctionServer.system.GlobalData;
import ws.particularFunctionServer.system.actor.WsActorSystem;
import ws.particularFunctionServer.system.config.AppConfig;
import ws.particularFunctionServer.system.di.GlobalInjectorUtils;
import ws.particularFunctionServer.system.jmx.JmxJolokiaServer;
import ws.particularFunctionServer.system.jmx.JmxMBeanManager;
import ws.particularFunctionServer.system.table.RootTcListener;
import ws.particularFunctionServer.system.table.RowsClassHolder;
import ws.relationship.base.LauncherUtils;
import ws.relationship.exception.LauncherInitException;
import ws.relationship.table.RootTc;
import ws.relationship.utils.InitCommonCreatedTargetsDB;

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

        _preproccess();
    }

    private static void _init() {
        try {
            AppConfig.init();
            GlobalInjectorUtils.init();
            _initPlanningTableData();
            mongodbInit();
            _redisInit();
            LauncherUtils._redisInit_Test();
        } catch (Exception e) {
            throw new LauncherInitException("初始化异常！", e);
        }
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

    private static void _initPlanningTableData() throws Exception {
        PlanningTableData planningTableData = new _PlanningTableData(AppConfig.getString(AppConfig.Key.WS_Common_Config_planningTableData_tab_file_path));
        planningTableData.loadAllTablesData();
        RootTc.init(planningTableData, new RootTcListener());
        RootTc.loadAllTables(GlobalData.getLocale());
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

    /**
     * 保证停止服务与重新启动服务有稍许时间差
     */
    private static void waitForSeconds(int seconds, String msg) {
        for (int i = 0; i < seconds; i++) {
            try {
                Thread.sleep(1000);
                logger.info("{} {} ...", msg, (seconds - i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 预处理一些数据
     */
    private static void _preproccess() {
        Thread t = new Thread(() -> {
            waitForSeconds(20, "wait for preProccess data!");
            RowsClassHolder.getAbstractRowClasses();
            InitCommonCreatedTargetsDB.init();
        });
        t.start();
    }
}
