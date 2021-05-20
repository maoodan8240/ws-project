package ws.gameServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.config.MongoConfig;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.redis.RedisOpration;
import ws.common.table.data.PlanningTableData;
import ws.common.utils.dataSource.txt._PlanningTableData;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.schedule.GlobalScheduler;
import ws.gameServer.features.actor.register.ExtensionIniterClassHolder;
import ws.gameServer.features.standalone.extp.dataCenter.enums.PrivateNotifyTypeEnum;
import ws.gameServer.features.standalone.extp.utils.PlayerExtensionsClassHolder;
import ws.gameServer.system.ServerGlobalData;
import ws.gameServer.system.actor.WsActorSystem;
import ws.gameServer.system.config.AppConfig;
import ws.gameServer.system.date.dayChanged.DayChanged;
import ws.gameServer.system.di.GlobalInjectorUtils;
import ws.gameServer.system.jmx.JmxJolokiaServer;
import ws.gameServer.system.jmx.JmxMBeanManager;
import ws.gameServer.system.shutdownHook.ShutdownHook;
import ws.gameServer.system.shutdownHook._ShutdownHook;
import ws.gameServer.system.table.RootTcListener;
import ws.relationship.base.LauncherUtils;
import ws.relationship.exception.LauncherInitException;
import ws.relationship.table.RootTc;
import ws.relationship.table.RowsClassHolder;
import ws.relationship.table.tableRows.Table_Exp_Row;
import ws.relationship.utils.InitCommonCreatedTargetsDB;

import java.util.*;

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

            GlobalScheduler.init();
            mongodbInit();
            redisInit();
            ServerGlobalData.init();
            _initPlanningTableData();
            LauncherUtils._redisInit_Test();
            addShutdownHook();
        } catch (Exception e) {
            throw new LauncherInitException("初始化异常！", e);
        }
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook((_ShutdownHook) GlobalInjector.getInstance(ShutdownHook.class));
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
        MongoConfig config = new MongoConfig(host, port, userName, password, dbName, minConnectionsPerHost);
        GlobalInjector.getInstance(MongoDBClient.class).init(config);
    }

    private static void _initPlanningTableData() throws Exception {
        PlanningTableData planningTableData = new _PlanningTableData(AppConfig.getString(AppConfig.Key.WS_Common_Config_planningTableData_tab_file_path));
        planningTableData.loadAllTablesData();
        RootTc.init(planningTableData, new RootTcListener());
        RootTc.loadAllTables(ServerGlobalData.getLocale());
    }

    private static void _startJmxJolokiaServer() {
        if (!"true".equals(AppConfig.getString(AppConfig.Key.WS_Common_Config_jmx_conf_jmx_server_enabled))) {
            return;
        }
        JmxJolokiaServer.start();
        JmxMBeanManager.initNew();
    }

    private static void _startActorSystem() {
        WsActorSystem.init();
    }

    /**
     * 预处理一些数据
     */
    private static void _preproccess() {
        ExtensionIniterClassHolder.getExtensionIniterClasses();
        PlayerExtensionsClassHolder.getPlayerUseExtensionClasses();
        RowsClassHolder.getAbstractRowClasses();
        Table_Exp_Row.calcuMaxHofFavorExp();
        PrivateNotifyTypeEnum.check();
        GlobalInjector.getInstance(DayChanged.class); // 初始化DayChanged
        InitCommonCreatedTargetsDB.init();
    }
}
