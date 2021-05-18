package ws.particularFunctionServer.system.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import ws.common.utils.general.ParseRoute;
import ws.relationship.base.ServerEnvEnum;
import ws.relationship.base.ServerRoleEnum;
import ws.relationship.exception.AppConfigInitException;
import ws.relationship.exception.AppConfigServerRoleNotRightException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppConfig {
    public static class Key {
        public static final String Akka_Remote_Netty_Tcp_Hostname = "akka.remote.netty.tcp.hostname";
        public static final String Akka_Remote_Netty_Tcp_Port = "akka.remote.netty.tcp.port";
        public static final String Akka_Cluster_Roles = "akka.cluster.roles";
        public static final String Akka_Cluster_Join_Point = "akka.cluster.join-point";

        public static final String WS_Common_Config_lang = "WS-Common-Config.lang";
        public static final String WS_Common_Config_country = "WS-Common-Config.country";

        public static final String WS_Common_Config_use_net_ip_first = "WS-Common-Config.use-net-ip-first";

        public static final String WS_Common_Config_jmx_conf_jmx_server_enabled = "WS-Common-Config.jmx-conf.jmx-server-enabled";
        public static final String WS_Common_Config_jmx_conf_jmx_server_host = "WS-Common-Config.jmx-conf.jmx-server-host";
        public static final String WS_Common_Config_jmx_conf_jmx_server_port = "WS-Common-Config.jmx-conf.jmx-server-port";

        public static final String WS_Common_Config_planningTableData_tab_file_path = "WS-Common-Config.planningTableData.tab-file-path";
        public static final String WS_Common_Config_planningTableData_db_name = "WS-Common-Config.planningTableData.db.name";
        public static final String WS_Common_Config_planningTableData_db_uname = "WS-Common-Config.planningTableData.db.uname";
        public static final String WS_Common_Config_planningTableData_db_upswd = "WS-Common-Config.planningTableData.db.upswd";
        public static final String WS_Common_Config_planningTableData_db_uri = "WS-Common-Config.planningTableData.db.uri";

        public static final String WS_Common_Config_redis_masterNames = "WS-Common-Config.redis.masterNames";
        public static final String WS_Common_Config_redis_maxIdlel = "WS-Common-Config.redis.maxIdlel";
        public static final String WS_Common_Config_redis_maxTotal = "WS-Common-Config.redis.maxTotal";
        public static final String WS_Common_Config_redis_maxWaitSeconds = "WS-Common-Config.redis.maxWaitSeconds";
        public static final String WS_Common_Config_redis_pwsd = "WS-Common-Config.redis.pwsd";
        public static final String WS_Common_Config_redis_sentinelIpAndPorts = "WS-Common-Config.redis.sentinelIpAndPorts";

        public static final String WS_Common_Config_mongodb_minConnectionsPerHost = "WS-Common-Config.mongodb.minConnectionsPerHost";
        public static final String WS_Common_Config_mongodb_connectionsPerHost = "WS-Common-Config.mongodb.connectionsPerHost";
        public static final String WS_Common_Config_mongodb_dbName = "WS-Common-Config.mongodb.dbName";
        public static final String WS_Common_Config_mongodb_host = "WS-Common-Config.mongodb.host";
        public static final String WS_Common_Config_mongodb_password = "WS-Common-Config.mongodb.password";
        public static final String WS_Common_Config_mongodb_port = "WS-Common-Config.mongodb.port";
        public static final String WS_Common_Config_mongodb_userName = "WS-Common-Config.mongodb.userName";
    }

    private static Config config;

    public static void init() {
        try {
            config = ConfigFactory.load();
            List<String> roles = getConfigLis(AppConfig.Key.Akka_Cluster_Roles);
            if (roles.size() != 2) {
                throw new AppConfigServerRoleNotRightException(roles);
            }
            List<String> rolesLis = new ArrayList<>();
            rolesLis.add(roles.get(0) + "-" + getIp() + "-" + config.getInt(AppConfig.Key.Akka_Remote_Netty_Tcp_Port)); // akka_cluster_roles 接上 -ip-host
            rolesLis.addAll(roles.subList(1, roles.size()));
            merge(AppConfig.Key.Akka_Cluster_Roles, rolesLis);
            if (config.getString(AppConfig.Key.Akka_Remote_Netty_Tcp_Hostname).trim().length() == 0) {
                merge(AppConfig.Key.Akka_Remote_Netty_Tcp_Hostname, getIp());
            }
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

    public static String getServerRole() {
        return getConfigLis(AppConfig.Key.Akka_Cluster_Roles).get(0);
    }

    public static ServerRoleEnum getServerRoleEnum() {
        return ServerRoleEnum.parse(getServerRole());
    }

    public static String getServerEnv() {
        return getConfigLis(AppConfig.Key.Akka_Cluster_Roles).get(1);
    }

    public static ServerEnvEnum getServerEnvEnum() {
        return ServerEnvEnum.parse(getServerEnv());
    }

    public static Config get() {
        return config;
    }

    public static String[] getRedisSentinelIpAndPorts() {
        return getString(AppConfig.Key.WS_Common_Config_redis_sentinelIpAndPorts).replaceAll(" ", "").split(",");
    }

    public static String[] getRedisMasterNames() {
        return getString(AppConfig.Key.WS_Common_Config_redis_masterNames).replaceAll(" ", "").split(",");
    }

    public static String getIp() {
        boolean useNetIpFirst = getBoolean(Key.WS_Common_Config_use_net_ip_first);
        String localIp = ParseRoute.getInstance().getLocalIPAddress();
        String netIp = ParseRoute.getInstance().getNetIp();
        if (useNetIpFirst) {
            if (!StringUtils.isEmpty(netIp)) {
                return netIp;
            }
        }
        if (!StringUtils.isEmpty(localIp)) {
            return localIp;
        }
        return "127.0.0.1";
    }

    public static void main(String[] args) {
        init();
        System.out.println(getServerRole());
        config.entrySet().forEach(entry -> {
            if (entry.getKey().startsWith("Y6-")) {
                String key = entry.getKey();
                String keyGen = "public static final String " + key.replaceAll("\\.", "_").replaceAll("-", "_") + " = \"" + key + "\";";
                System.out.println(keyGen);
            }
        });
        System.out.println(Arrays.toString(getRedisSentinelIpAndPorts()));
        System.out.println(Arrays.toString(getRedisMasterNames()));
    }
}
