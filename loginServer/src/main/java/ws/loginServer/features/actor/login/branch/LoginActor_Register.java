package ws.loginServer.features.actor.login.branch;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Address;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.general.EnumUtils;
import ws.loginServer.features.actor.login.LoginRequestContainer;
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
import ws.relationship.appServers.loginServer.player.msg.In_Register;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.In_MessagePassToGatewayServer;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.centerPlayer.CenterPlayerDao;
import ws.relationship.daos.simpleId.SimpleIdDao;
import ws.relationship.enums.SimpleIdTypeEnum;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;
import ws.relationship.topLevelPojos.simpleId.SimpleId;
import ws.relationship.utils.ChooseGameSeverUtils;
import ws.relationship.utils.ClusterMessageSender;
import ws.relationship.utils.NameUtils;
import ws.relationship.utils.ProtoUtils;


public class LoginActor_Register {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginActor_Register.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final CenterPlayerDao CENTER_PLAYER_DAO = DaoContainer.getDao(CenterPlayer.class);
    private static final SimpleIdDao SIMPLE_ID_DAO = DaoContainer.getDao(SimpleId.class);

    static {
        CENTER_PLAYER_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
        SIMPLE_ID_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
    }

    public static void register(Cm_Login cm_Login, String connFlag, ActorContext actorContext, ActorRef self, ActorRef sender) {
        Response.Builder br = ProtoUtils.create_Response(Code.Sm_Login, Action.RESP_REGISTER);
        Sm_Login.Builder b = Sm_Login.newBuilder();
        b.setAction(Action.RESP_REGISTER);
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
        int subPlatform = 0;
        int outerRealmId = securityCode.getOuterRealmId();
        CenterPlayer centerPlayer = LoginUtils.getCenterPlayerListFirst(platformType, outerRealmId, platformUid);
        if (centerPlayer != null && !StringUtils.isEmpty(centerPlayer.getGameId())) {
            br.setErrorCode(ErrorCodeEnum.HAS_REGIST_CENTER_PLAYER);
            br.setSmLogin(b);
            LOGGER.debug(">>> {}", br.build());
            sender.tell(new In_MessagePassToGatewayServer(connFlag, br.build(), EnumUtils.protoActionToString(Action.RESP_AUTH)), self);
            return;
        }
        if (centerPlayer != null) {
            LOGGER.debug("玩家注册过CenterId,但是未成功注册gameId,继续注册GameId...");
        } else {
            int simpleId = SIMPLE_ID_DAO.nextSimpleId(outerRealmId, SimpleIdTypeEnum.PLAYER);
            centerPlayer = new CenterPlayer(ObjectId.get().toString(), simpleId, outerRealmId, "", platformType, subPlatform, platformUid, false);
        }
        String randomName = NameUtils.random();
        if (StringUtils.isBlank(randomName)) {
            br.setErrorCode(ErrorCodeEnum.RANDOM_PLAYER_NAME_FAILED);
            br.setSmLogin(b);
            LOGGER.debug(">>> {}", br.build());
            sender.tell(new In_MessagePassToGatewayServer(connFlag, br.build(), EnumUtils.protoActionToString(Action.RESP_AUTH)), self);
            LOGGER.error("随机玩家名字失败...");
            return;
        }
        centerPlayer.updateOtherInfo(randomName, cm_Login.getPlayerIcon(), cm_Login.getSex());
        if (LoginRequestContainer.contains(centerPlayer)) { // 正在注册当中，不能重复注册
            br.setSmLogin(b);
            br.setErrorCode(ErrorCodeEnum.ING_REGIST_CENTER_PLAYER);
            LOGGER.debug(">>> {}", br.build());
            sender.tell(new In_MessagePassToGatewayServer(connFlag, br.build(), EnumUtils.protoActionToString(Action.RESP_REGISTER)), self);
            LOGGER.warn("正在注册CenterPlayer,不能重复注册！ PlatformTypeEnum={} PlatformUid={} OuterRealmId={} ", centerPlayer.getPlatformType(), centerPlayer.getPlatformUid(), centerPlayer.getOuterRealmId());
            return;
        }
        LOGGER.debug("发送GameServer注册游戏账号! outerRealmId={} centerId={} simpleId={} gameId={}", centerPlayer.getOuterRealmId(), centerPlayer.getCenterId(), centerPlayer.getSimpleId(), centerPlayer.getGameId());
        CENTER_PLAYER_DAO.insertIfExistThenReplace(centerPlayer);
        LoginRequestContainer.add(centerPlayer);
        String specifiedGameServerRole = cm_Login.getSpecifiedGameServerRole();
        Address address = ChooseGameSeverUtils.chooseSpecificGameServer(actorContext, outerRealmId, specifiedGameServerRole).getAddress();
        ClusterMessageSender.sendMsgToServer(actorContext, address, new In_Register.Request(sender, connFlag, centerPlayer.clone(), 0, 0), ActorSystemPath.WS_GameServer_Selection_Register);
    }


}
