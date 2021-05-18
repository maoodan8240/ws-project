package ws.analogClient;

import ws.relationship.exception.LauncherInitException;
import ws.analogClient.features.FlowExecutor;
import ws.analogClient.system.config.AppConfig;
import ws.analogClient.system.di.GlobalInjectorUtils;

import java.security.Provider;
import java.security.Security;

public class Launcher {

    static {
        System.out.println(System.getProperty("java.version"));
        for (Provider provider : Security.getProviders()) {
            System.out.println(provider);
        }
    }

    public static void main(String[] args) throws Exception {
        _init();
        FlowExecutor.run();
        Thread.sleep(Long.MAX_VALUE);
    }

    private static void _init() {
        try {
            AppConfig.init();
            GlobalInjectorUtils.init();
        } catch (Exception e) {
            throw new LauncherInitException("初始化异常！", e);
        }
    }
}
