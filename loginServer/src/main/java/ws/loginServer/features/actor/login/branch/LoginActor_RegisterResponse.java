package ws.loginServer.features.actor.login.branch;

import akka.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.redis.RedisOpration;
import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.general.EnumUtils;
import ws.loginServer.features.actor.login.LoginRequestContainer;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.EnumsProtos.ErrorCodeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerLoginProtos.Sm_Login;
import ws.protos.PlayerLoginProtos.Sm_Login.Action;
import ws.relationship.appServers.loginServer.player.msg.In_Register;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.base.msg.In_MessagePassToGatewayServer;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.centerPlayer.CenterPlayerDao;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;
import ws.relationship.utils.ProtoUtils;

import java.util.Date;


public class LoginActor_RegisterResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginActor_RegisterResponse.class);
    private static final RedisOpration REDIS_OPRATION = GlobalInjector.getInstance(RedisOpration.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final CenterPlayerDao CENTER_PLAYER_DAO = DaoContainer.getDao(CenterPlayer.class);

    static {
        CENTER_PLAYER_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
    }

    public static void registerResponse(In_Register.Response response, ActorRef self) {
        ActorRef gatewaySender = response.getRequest().getGatewaySender();
        String connFlag = response.getRequest().getConnFlag();
        Response.Builder br = ProtoUtils.create_Response(Code.Sm_Login, Action.RESP_REGISTER);
        Sm_Login.Builder b = Sm_Login.newBuilder();
        b.setAction(Action.RESP_REGISTER);
        if (!response.isRs()) {
            br.setErrorCode(ErrorCodeEnum.REGIST_PLAYER_FAILED);
            br.setSmLogin(b);
            LOGGER.debug(">>> {}", br.build());
            gatewaySender.tell(new In_MessagePassToGatewayServer(connFlag, br.build(), EnumUtils.protoActionToString(Action.RESP_REGISTER)), self);
            return;
        }
        CenterPlayer centerPlayer = response.getRequest().getCenterPlayer();
        Date date = new Date(); // 注册游戏Id时间点
        centerPlayer.setGameIdDate(Integer.valueOf(WsDateUtils.dateToFormatStr(date, WsDateFormatEnum.yyyyMMdd)));
        centerPlayer.setGameIdTime(Integer.valueOf(WsDateUtils.dateToFormatStr(date, WsDateFormatEnum.HHmmss)));
        LOGGER.debug("注册游戏账号成功! outerRealmId={} centerId={} simpleId={} gameId={}", centerPlayer.getOuterRealmId(), centerPlayer.getCenterId(), centerPlayer.getSimpleId(), centerPlayer.getGameId());
        LoginRequestContainer.remove(centerPlayer);
        CENTER_PLAYER_DAO.insertIfExistThenReplace(centerPlayer);
        br.setSmLogin(b);
        br.setResult(true);
        b.setRpid(centerPlayer.getGameId()); // 真实的id
        b.setVpid(centerPlayer.getSimpleId()); // 虚拟的id
        br.setSmLogin(b);
        LOGGER.debug(">>> {}", br.build());
        gatewaySender.tell(new In_MessagePassToGatewayServer(connFlag, br.build(), EnumUtils.protoActionToString(Action.RESP_REGISTER)), self);
    }

}
