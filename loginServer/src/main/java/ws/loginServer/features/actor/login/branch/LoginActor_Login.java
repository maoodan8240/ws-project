package ws.loginServer.features.actor.login.branch;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.redis.RedisOpration;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.general.EnumUtils;
import ws.loginServer.features.actor.login.LoginUtils;
import ws.loginServer.features.actor.utils.SecurityCode;
import ws.loginServer.features.actor.utils.SecurityCodeUtils;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.EnumsProtos.ErrorCodeEnum;
import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerLoginProtos.Cm_Login;
import ws.protos.PlayerLoginProtos.Sm_Login;
import ws.protos.PlayerLoginProtos.Sm_Login.Action;
import ws.relationship.appServers.loginServer.player.msg.In_ConnectToPlayerRequest;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.cluster.AkkaAddressContext;
import ws.relationship.base.msg.In_MessagePassToGatewayServer;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.centerPlayer.CenterPlayerDao;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;
import ws.relationship.utils.ChooseGameSeverUtils;
import ws.relationship.utils.ClusterMessageSender;
import ws.relationship.utils.ProtoUtils;


public class LoginActor_Login {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginActor_Login.class);
    private static final RedisOpration REDIS_OPRATION = GlobalInjector.getInstance(RedisOpration.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final CenterPlayerDao CENTER_PLAYER_DAO = DaoContainer.getDao(CenterPlayer.class);

    static {
        CENTER_PLAYER_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
    }

    public static void login(Cm_Login cm_Login, String connFlag, Sm_Login.Action action, ActorContext actorContext, ActorRef self, ActorRef sender) throws Exception {
        Response.Builder br = ProtoUtils.create_Response(Code.Sm_Login, action);
        Sm_Login.Builder b = Sm_Login.newBuilder();
        b.setAction(action);
        br.setSmLogin(b);
        SecurityCode securityCode = SecurityCodeUtils.decryptAsSecurityCode(cm_Login.getSecurityCode());
        if (securityCode == null) {
            br.setErrorCode(ErrorCodeEnum.SECURITY_CODE_CHECK_FAIL);
            br.setSmLogin(b);
            LOGGER.debug(">>> {}", br.build());
            sender.tell(new In_MessagePassToGatewayServer(connFlag, br.build(), EnumUtils.protoActionToString(Action.RESP_AUTH)), self);
            return;
        }
        String platformUid = securityCode.getPlatformUid();
        PlatformTypeEnum platformType = securityCode.getPlatformType();
        int outerRealmId = securityCode.getOuterRealmId();
        String deviceUid = "";
//        if (cm_Login.getOtherArgsList() != null && cm_Login.getOtherArgsList().size() > 0) {
//            deviceUid = cm_Login.getOtherArgsList().get(0);
//        }
        CenterPlayer centerPlayer = LoginUtils.getCenterPlayerListFirst(platformType, outerRealmId, platformUid);
        if (centerPlayer == null || StringUtils.isEmpty(centerPlayer.getGameId())) {
            br.setErrorCode(ErrorCodeEnum.PLAYER_NOT_EXISTS);
            br.setSmLogin(b);
            LOGGER.debug(">>> {}", br.build());
            sender.tell(new In_MessagePassToGatewayServer(connFlag, br.build(), EnumUtils.protoActionToString(Action.RESP_AUTH)), self);
            return;
        }
        String specifiedGameServerRole = cm_Login.getSpecifiedGameServerRole();
        String playerId = centerPlayer.getGameId();
        AkkaAddressContext addressContext = ChooseGameSeverUtils.chooseSpecificGameServer(actorContext, outerRealmId, specifiedGameServerRole);
        ClusterMessageSender.sendMsgToServer(actorContext, addressContext.getAddress(), new In_ConnectToPlayerRequest(action, sender, connFlag, playerId, outerRealmId, deviceUid, centerPlayer.clone()), ActorSystemPath.WS_GameServer_Selection_World);
    }

}
