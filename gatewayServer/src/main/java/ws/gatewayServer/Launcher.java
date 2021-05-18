package ws.gatewayServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.network.server.TcpServer;
import ws.common.network.server._TcpServer;
import ws.common.network.server.config.implement._ConnConfig;
import ws.common.network.server.config.implement._ServerConfig;
import ws.common.network.server.config.interfaces.ConnConfig;
import ws.common.network.server.config.interfaces.ServerConfig;
import ws.common.network.server.interfaces.CodeToMessagePrototype;
import ws.common.network.server.interfaces.NetworkListener;
import ws.common.redis.RedisOpration;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.schedule.GlobalScheduler;
import ws.gatewayServer.system.actor.WsActorSystem;
import ws.gatewayServer.system.config.AppConfig;
import ws.gatewayServer.system.di.GlobalInjectorUtils;
import ws.gatewayServer.system.jmx.JmxJolokiaServer;
import ws.gatewayServer.system.jmx.JmxMBeanManager;
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
        waitForSeconds("TcpServer");
        _startTcpServer();
    }

    private static void _init() {
        try {
            AppConfig.init();
            GlobalInjectorUtils.init();
            GlobalScheduler.init();
            _redisInit();
            LauncherUtils._redisInit_Test();
        } catch (Exception e) {
            throw new LauncherInitException("初始化异常！", e);
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

    private static void _startTcpServer() {
        ServerConfig serverConfig = new _ServerConfig(new _ConnConfig(//
                ConnConfig.ServerProtocolType.TCP, //
                AppConfig.getIp(), //
                AppConfig.getInt(AppConfig.Key.WS_HttpGatewayServer_tcp_server_port), //
                AppConfig.getInt(AppConfig.Key.WS_HttpGatewayServer_tcp_server_offlineTimeout), //
                AppConfig.getInt(AppConfig.Key.WS_HttpGatewayServer_tcp_server_disconnTimeout) //
        ));
        TcpServer tcpServer = new _TcpServer(serverConfig);
        tcpServer.getNetworkContext().setCodeToMessagePrototype(GlobalInjector.getInstance(CodeToMessagePrototype.class));
        tcpServer.getNetworkHandler().addListener(GlobalInjector.getInstance(NetworkListener.class));
        tcpServer.start();
    }

    /**
     * 保证停止服务与重新启动服务有稍许时间差
     */
    private static void waitForSeconds(String msg) {
        int seconds = 10;
        for (int i = 0; i < seconds; i++) {
            try {
                Thread.sleep(1000);
                logger.info("wait for start {}! {} ...", msg, (seconds - i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
