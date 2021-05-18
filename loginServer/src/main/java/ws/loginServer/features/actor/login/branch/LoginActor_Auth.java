package ws.loginServer.features.actor.login.branch;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.general.EnumUtils;
import ws.loginServer.features.actor.login.LoginUtils;
import ws.loginServer.features.actor.login.ThirdPartySystemUtils;
import ws.loginServer.features.actor.login.ThirdPartySystemUtils.ThirdPartyAccountInfo;
import ws.loginServer.features.actor.utils.SecurityCode;
import ws.loginServer.features.actor.utils.SecurityCodeUtils;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.EnumsProtos.ErrorCodeEnum;
import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerLoginProtos.Cm_Login;
import ws.protos.PlayerLoginProtos.Sm_Login;
import ws.protos.PlayerLoginProtos.Sm_Login.Action;
import ws.relationship.appServers.thirdPartyServer.In_LoginToPlatformRequest;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.base.msg.In_MessagePassToGatewayServer;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.centerPlayer.CenterPlayerDao;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;
import ws.relationship.utils.ProtoUtils;

public class LoginActor_Auth {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginActor_Auth.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final CenterPlayerDao CENTER_PLAYER_DAO = DaoContainer.getDao(CenterPlayer.class);

    static {
        CENTER_PLAYER_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
    }


    public static void auth(Cm_Login cm_Login, String connFlag, ActorContext actorContext, ActorRef self, ActorRef sender) {
        Response.Builder br = ProtoUtils.create_Response(Code.Sm_Login, Action.RESP_AUTH);
        Sm_Login.Builder b = Sm_Login.newBuilder();
        b.setAction(Action.RESP_AUTH);
        PlatformTypeEnum platformType = cm_Login.getPlatformType();
        String token = cm_Login.getToken();
        int outerRealmId = cm_Login.getOuterRealmId();
        String uid = cm_Login.getUid();
        String args = cm_Login.getArgs();
        // sdk验证
        String platformUid = _sdkAuthAndGetPlatformUid(token, platformType, uid, args, actorContext);
        if (StringUtils.isEmpty(platformUid)) {
            br.setErrorCode(ErrorCodeEnum.SDK_AUTH_FAIL);
            br.setSmLogin(b);
            LOGGER.debug(">>> {}", br.build());
            sender.tell(new In_MessagePassToGatewayServer(connFlag, br.build(), EnumUtils.protoActionToString(Action.RESP_AUTH)), self);
            return;
        }
        // 构造SecurityCode
        b.setSecurityCode(_buildSecurityCode(platformType, platformUid, outerRealmId));
        // 获取注册过的CenterPlayer
        CenterPlayer centerPlayer = LoginUtils.getCenterPlayerListFirst(platformType, outerRealmId, platformUid);
        if (centerPlayer != null && !StringUtils.isEmpty(centerPlayer.getGameId())) {// 已经注册
            LOGGER.debug("SDK验证成功,已经注册过了，直接可以登录");
            br.setSmLogin(b);
            br.setResult(true);
            b.setRpid(centerPlayer.getGameId()); // 真实的id
            b.setVpid(centerPlayer.getSimpleId()); // 虚拟的id
            br.setSmLogin(b);
            LOGGER.debug(">>> {}", br.build());
            sender.tell(new In_MessagePassToGatewayServer(connFlag, br.build(), EnumUtils.protoActionToString(Sm_Login.Action.RESP_AUTH)), self);
            return;
        } else {// 未注册
            LOGGER.debug("SDK验证成功,尚未注册");
            br.setErrorCode(ErrorCodeEnum.UNREGISTERED);
            br.setSmLogin(b);
            LOGGER.debug(">>> {}", br.build());
            sender.tell(new In_MessagePassToGatewayServer(connFlag, br.build(), EnumUtils.protoActionToString(Action.RESP_AUTH)), self);
            return;
        }
    }


    private static String _sdkAuthAndGetPlatformUid(String token, PlatformTypeEnum platformType, //
                                                    String uid, String args, ActorContext actorContext) {
        In_LoginToPlatformRequest loginToPlatformRequest = new In_LoginToPlatformRequest(platformType, uid, token, args);
        ThirdPartyAccountInfo accountInfo = ThirdPartySystemUtils.query(actorContext, loginToPlatformRequest);
        if (accountInfo == null) {
            LOGGER.info("SDK中查询的[accountInfo]为空！platform={} uid={} token={} args ={}", platformType, uid, token, args);
            return null;
        }
        return accountInfo.getPlatformUid();
    }


    private static String _buildSecurityCode(PlatformTypeEnum platformType, String platformUid, int outerRealmId) {
        SecurityCode securityCode = new SecurityCode(platformType, platformUid, outerRealmId);
        return SecurityCodeUtils.encryptAsBase64String(securityCode);
    }
}
