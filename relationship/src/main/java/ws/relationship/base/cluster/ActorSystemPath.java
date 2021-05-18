package ws.relationship.base.cluster;

public class ActorSystemPath {
    /**
     * Common
     */
    public static final String WS_Common_System = "wssystem";

    public static final String WS_Common_WSRoot = "WSRoot";
    public static final String WS_Common_Selection_WSRoot = "/user/WSRoot";

    public static final String WS_Common_ClusterListener = "clusterListener";
    public static final String WS_Common_Selection_ClusterListener = "/user/clusterListener";

    /**
     * WS-ParticularFunctionServer
     */
    public static final String WS_ThirdPartyServer_MailsCenterActor = "mailsCenterActor";
    public static final String WS_ThirdPartyServer_Selection_MailsCenterActor = WS_Common_Selection_WSRoot + "/mailsCenterActor";

    /**
     * WS_ThirdPartyServer
     */
    public static final String WS_ThirdPartyServer_PaymentCenter = "paymentCenter";
    public static final String WS_ThirdPartyServer_Selection_PaymentCenter = WS_Common_Selection_WSRoot + "/paymentCenter";

    public static final String WS_ThirdPartyServer_Payment = "payment-";
    public static final String WS_ThirdPartyServer_Selection_Payment = WS_ThirdPartyServer_Selection_PaymentCenter + "/payment-";

    public static final String WS_ThirdPartyServer_LoginCheckCenter = "loginCheckCenter";
    public static final String WS_ThirdPartyServer_Selection_LoginCheckCenter = WS_Common_Selection_WSRoot + "/loginCheckCenter";

    public static final String WS_ThirdPartyServer_LoginCheck = "loginCheck-";
    public static final String WS_ThirdPartyServer_Selection_LoginCheck = WS_ThirdPartyServer_Selection_LoginCheckCenter + "/loginCheck-";

    /**
     * WS-LoginServer
     */
    public static final String WS_LoginServer_Login = "login";
    public static final String WS_LoginServer_Selection_Login = WS_Common_Selection_WSRoot + "/login";

    /**
     * WS-GatewayServer
     */
    public static final String WS_GatewayServer_Test = "test";
    public static final String WS_GatewayServer_Selection_Test = WS_Common_Selection_WSRoot + "/test";
    public static final String WS_GatewayServer_MessageTransfer = "messageTransfer";
    public static final String WS_GatewayServer_Selection_MessageTransfer = WS_Common_Selection_WSRoot + "/messageTransfer";

    public static final String WS_GatewayServer_MessageTransferForReceive = "messageTransferForReceive-";
    public static final String WS_GatewayServer_Selection_MessageTransferForReceive = WS_GatewayServer_Selection_MessageTransfer + "/messageTransferForReceive-";

    public static final String WS_GatewayServer_MessageTransferForSend = "messageTransferForSend-";
    public static final String WS_GatewayServer_Selection_MessageTransferForSend = WS_GatewayServer_Selection_MessageTransfer + "/messageTransferForSend-";

    /**
     * WS_MongodbRedisServer
     */
    public static final String WS_MongodbRedisServer_PojoGetterManager = "pojoGetterManager";
    public static final String WS_MongodbRedisServer_Selection_PojoGetterManager = WS_Common_Selection_WSRoot + "/pojoGetterManager";
    public static final String WS_MongodbRedisServer_PojoGetter = "pojoGetter-";
    public static final String WS_MongodbRedisServer_Selection_PojoGetter = WS_MongodbRedisServer_Selection_PojoGetterManager + "/pojoGetter-";

    public static final String WS_MongodbRedisServer_PojoSaver = "pojoSaver";
    public static final String WS_MongodbRedisServer_Selection_PojoSaver = WS_Common_Selection_WSRoot + "/pojoSaver";

    public static final String WS_MongodbRedisServer_PojoRemover = "pojoRemover";
    public static final String WS_MongodbRedisServer_Selection_PojoRemover = WS_Common_Selection_WSRoot + "/pojoRemover";

    /**
     * WS-GameServer
     */
    public static final String WS_GameServer_Register = "register";
    public static final String WS_GameServer_Selection_Register = WS_Common_Selection_WSRoot + "/register";

    public static final String WS_GameServer_World = "world";
    public static final String WS_GameServer_Selection_World = WS_Common_Selection_WSRoot + "/world";

    public static final String WS_GameServer_PlayerIO = "playerIO-";
    public static final String WS_GameServer_Selection_PlayerIO = WS_GameServer_Selection_World + "/playerIO-*";

    public static final String WS_GameServer_Player = "player-";
    public static final String WS_GameServer_Selection_Player = WS_GameServer_Selection_PlayerIO + "/player-*";

    public static final String WS_GameServer_NewGuildCenter = "newGuildCenter";
    public static final String WS_GameServer_Selection_NewGuildCenter = WS_Common_Selection_WSRoot + "/newGuildCenter";

    public static final String WS_GameServer_ArenaCenter = "arenaCenter";
    public static final String WS_GameServer_Selection_ArenaCenter = WS_Common_Selection_WSRoot + "/arenaCenter";

    public static final String WS_GameServer_GuildCenter = "guildCenter";
    public static final String WS_GameServer_Selection_GuildCenter = WS_Common_Selection_WSRoot + "/guildCenter";

    public static final String[] WS_GameServer_Selection_All = {
            // WS_GameServer_Selection_Register, 没有业务需求不需要广播
            // WS_GameServer_Selection_PlayerIO, 没有业务需求不需要广播
            WS_GameServer_Selection_World,
            WS_GameServer_Selection_Player, WS_GameServer_Selection_NewGuildCenter, WS_GameServer_Selection_ArenaCenter,
            WS_GameServer_Selection_GuildCenter
    };


    /**
     * WS-ChatServer
     */
    public static final String WS_ChatServer_ChatManager = "chatManager";
    public static final String WS_ChatServer_Selection_ChatManager = WS_Common_Selection_WSRoot + "/chatManager";

    public static final String WS_ChatServer_InnerRealmChat = "innerRealmChat-";
    public static final String WS_ChatServer_Selection_InnerRealmChat = WS_ChatServer_Selection_ChatManager + "/innerRealmChat-*";

    public static final String WS_ChatServer_ChatMsgSender = "chatMsgSender-";

    /**
     * WS-LogServer
     */
    public static final String WS_LogServer_SaveLogsManager = "saveLogsManager";
    public static final String WS_LogServer_Selection_SaveLogsManager = WS_Common_Selection_WSRoot + "/saveLogsManager";
    public static final String WS_LogServer_SaveLogs = "saveLogs-";
    public static final String WS_LogServer_Selection_SaveLogs = WS_LogServer_Selection_SaveLogsManager + "/saveLogs-*";
}
