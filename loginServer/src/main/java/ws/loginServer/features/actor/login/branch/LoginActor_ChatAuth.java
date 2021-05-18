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
import ws.protos.ChatProtos.Cm_ChatServerLogin;
import ws.protos.ChatProtos.Sm_ChatServerLogin;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.EnumsProtos.ErrorCodeEnum;
import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.appServers.thirdPartyServer.In_LoginToPlatformRequest;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.base.msg.In_MessagePassToGatewayServer;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.centerPlayer.CenterPlayerDao;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;
import ws.relationship.utils.ProtoUtils;

public class LoginActor_ChatAuth {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginActor_ChatAuth.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final CenterPlayerDao CENTER_PLAYER_DAO = DaoContainer.getDao(CenterPlayer.class);

    static {
        CENTER_PLAYER_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
    }


    public static void auth(Cm_ChatServerLogin cm_chatServerLogin, String connFlag, ActorContext actorContext, ActorRef self, ActorRef sender) {
        Response.Builder br = ProtoUtils.create_Response(Code.Sm_ChatServerLogin, Sm_ChatServerLogin.Action.RESP_AUTH);
        Sm_ChatServerLogin.Builder b = Sm_ChatServerLogin.newBuilder();
        b.setAction(Sm_ChatServerLogin.Action.RESP_AUTH);
        SecurityCode securityCode = SecurityCodeUtils.decryptAsSecurityCode(cm_chatServerLogin.getSecurityCode());
        if (securityCode == null) {
            LOGGER.error("ChatServer 验证失败！securityCode验证失败！");
            br.setErrorCode(ErrorCodeEnum.SECURITY_CODE_CHECK_FAIL);
            send(br, b, connFlag, self, sender);
            return;
        }
        String platformUid = securityCode.getPlatformUid();
        PlatformTypeEnum platformType = securityCode.getPlatformType();
        int outerRealmId = securityCode.getOuterRealmId();
        // 获取注册过的CenterPlayer
        CenterPlayer centerPlayer = LoginUtils.getCenterPlayerListFirst(platformType, outerRealmId, platformUid);
        if (centerPlayer == null) {
            LOGGER.error("ChatServer 验证失败！CenterPlayer为空！或者GameId为空");
            br.setErrorCode(ErrorCodeEnum.CENTERPLAYER_IS_NULL);
            send(br, b, connFlag, self, sender);
            return;
        }
        if (StringUtils.isEmpty(centerPlayer.getGameId())) {
            LOGGER.error("ChatServer 验证失败！GameId为空！ CenterId={}", centerPlayer.getOid());
            br.setErrorCode(ErrorCodeEnum.GAMEID_OF_CENTERPLAYER_IS_NULL);
            send(br, b, connFlag, self, sender);
            return;
        }
        br.setResult(true);
        b.setRpid(centerPlayer.getGameId());
        b.setOuterRealmId(centerPlayer.getOuterRealmId());
        b.setInnerRealmId(centerPlayer.getInnerRealmId());
        send(br, b, connFlag, self, sender);
    }


    private static void send(Response.Builder br, Sm_ChatServerLogin.Builder b, String connFlag, ActorRef self, ActorRef sender) {
        br.setSmChatServerLogin(b);
        sender.tell(new In_MessagePassToGatewayServer(connFlag, br.build(), EnumUtils.protoActionToString(Sm_ChatServerLogin.Action.RESP_AUTH)), self);
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
}
