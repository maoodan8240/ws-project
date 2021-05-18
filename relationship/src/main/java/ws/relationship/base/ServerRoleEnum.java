package ws.relationship.base;

import org.apache.commons.lang3.StringUtils;

public enum ServerRoleEnum {
    WS_ClusterCenterServer("WS-ClusterCenterServer"), //
    WS_GatewayServer("WS-GatewayServer"), //
    WS_LoginServer("WS-LoginServer"), //
    WS_ThirdPartyServer("WS-ThirdPartyServer"), //
    WS_GameServer("WS-GameServer"), //
    WS_ParticularFunctionServer("WS-ParticularFunctionServer"), //
    WS_MongodbRedisServer("WS-MongodbRedisServer"), //
    WS_ChatServer("WS-ChatServer"), //
    WS_LogServer("WS-LogServer"), //
    NULL("");

    private String roleName;

    ServerRoleEnum(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public boolean isDevServer() {
        return roleName.indexOf("Server-DEV-") > 0;
    }

    public static ServerRoleEnum parse(String roleName) {
        if (StringUtils.isBlank(roleName)) {
            return NULL;
        }
        for (ServerRoleEnum serverRole : values()) {
            if (serverRole == NULL) {
                continue;
            }
            if (roleName.startsWith(serverRole.roleName)) {
                return serverRole;
            }
        }
        return NULL;
    }
}
