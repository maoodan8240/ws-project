package ws.thirdPartyServer;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.redis.RedisOpration;
import ws.common.utils.di.GlobalInjector;
import ws.relationship.base.LauncherUtils;
import ws.relationship.exception.LauncherInitException;
import ws.thirdPartyServer.features.utils.AssignedClassHolder;
import ws.thirdPartyServer.system.actor.WsActorSystem;
import ws.thirdPartyServer.system.config.AppConfig;
import ws.thirdPartyServer.system.di.GlobalInjectorUtils;
import ws.thirdPartyServer.system.jmx.JmxJolokiaServer;
import ws.thirdPartyServer.system.jmx.JmxMBeanManager;

import java.io.IOException;
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

    /**
     * 预处理一些数据
     *
     * @throws IOException
     */
    private static void _preproccess() {
        try {
            AssignedClassHolder.getLoginCheckPlatformClasses();
            CloseableHttpClient httpClient = HttpClients.createDefault();
            httpClient.close();
        } catch (Exception e) {
            throw new RuntimeException("预处理异常！", e);
        }
    }
}
