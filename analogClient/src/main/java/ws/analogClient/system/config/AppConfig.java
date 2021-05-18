package ws.analogClient.system.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import ws.relationship.exception.AppConfigInitException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppConfig {
    public static class Key {
        public static final String WS_AnalogClient_paswd = "WS-AnalogClient.paswd";
        public static final String WS_AnalogClient_game_server_role = "WS-AnalogClient.game-server-role";
        public static final String WS_AnalogClient_sdk_ip = "WS-AnalogClient.sdk-ip";
        public static final String WS_AnalogClient_use_old_acc = "WS-AnalogClient.use-old-acc";
        public static final String WS_AnalogClient_old_acc_name = "WS-AnalogClient.old-acc-name";
        public static final String WS_AnalogClient_gate_server_host = "WS-AnalogClient.gate-server.host";
        public static final String WS_AnalogClient_new_acc_name = "WS-AnalogClient.new-acc-name";
        public static final String WS_AnalogClient_gate_server_port = "WS-AnalogClient.gate-server.port";

    }



    private static Config config;

    public static void init() {
        try {
            config = ConfigFactory.load();
        } catch (Exception e) {
            throw new AppConfigInitException("AppConfig 初始化异常！", e);
        }
    }

    public static void merge(Config merge) {
        config = merge.withFallback(config);
    }

    public static void merge(Map<String, Object> map) {
        merge(ConfigFactory.parseMap(map));
    }

    public static void merge(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        merge(map);
    }

    public static List<String> getConfigLis(String key, List<String> lisDefault) {
        try {
            return config.getStringList(key);
        } catch (Exception e) {
            return lisDefault;
        }
    }

    public static List<String> getConfigLis(String key) {
        return getConfigLis(key, new ArrayList<>());
    }

    public static String getString(String key) {
        try {
            return config.getString(key);
        } catch (Exception e) {
            return null;
        }
    }

    public static int getInt(String key, int defaultValue) {
        String string = getString(key);
        try {
            return Integer.valueOf(string);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static boolean getBoolean(String key) {
        return config.getBoolean(key);
    }

    public static Config get() {
        return config;
    }

    public static void main(String[] args) {
        init();
        config.entrySet().forEach(entry -> {
            if (entry.getKey().startsWith("WS-")) {
                String key = entry.getKey();
                String keyGen = "public static final String " + key.replaceAll("\\.", "_").replaceAll("-", "_") + " = \"" + key + "\";";
                System.out.println(keyGen);
            }
        });
    }
}

