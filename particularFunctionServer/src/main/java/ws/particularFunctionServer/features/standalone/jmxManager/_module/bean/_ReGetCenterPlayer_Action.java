package ws.particularFunctionServer.features.standalone.jmxManager._module.bean;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.particularFunctionServer.features.actor.cluster.ClusterListener;
import ws.particularFunctionServer.features.standalone.jmxManager.Action;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.cluster.AkkaAddressContext;
import ws.relationship.base.msg.CheckPlayerOnlineMsgRequest;
import ws.relationship.base.msg.player.In_ReGetCenterPlayer;
import ws.relationship.base.resultCode.ResultCodeEnum;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.centerPlayer.CenterPlayerDao;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;
import ws.relationship.utils.ActorMsgSynchronizedExecutor;
import ws.relationship.utils.ChooseGameSeverUtils;

public class _ReGetCenterPlayer_Action implements Action {
    private Logger LOGGER = LoggerFactory.getLogger(_ReGetCenterPlayer_Action.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final CenterPlayerDao CENTER_PLAYER_DAO = DaoContainer.getDao(CenterPlayer.class);

    private static final String GM_COMMAND = "get";

    static {
        CENTER_PLAYER_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
    }

    @Override
    public String handle(String funcName, String args) {
        if (GM_COMMAND.equals(funcName)) {
            return send(args);
        }
        return "不存在funcName=[" + funcName + "]对应的解析失败.";
    }

    private String send(String args) {
        try {
            LOGGER.info("client send args={} !", args);
            if (StringUtils.isBlank(args)) {
                return "simpleId不能为空！";
            }
            String simpleId = args;
            try {
                CenterPlayer centerPlayer = CENTER_PLAYER_DAO.findCenterPlayer(Integer.valueOf(simpleId));
                if (centerPlayer == null) {
                    return "玩家SimpleId=" + simpleId + " 不存在！\n";
                }
                AkkaAddressContext addressContext = ChooseGameSeverUtils.chooseSpecificGameServer(ClusterListener.getActorContext(), centerPlayer.getOuterRealmId(), null);
                CheckPlayerOnlineMsgRequest<In_ReGetCenterPlayer.Request> request = new CheckPlayerOnlineMsgRequest<>(centerPlayer.getGameId(), new In_ReGetCenterPlayer.Request(simpleId));
                In_ReGetCenterPlayer.Response response = ActorMsgSynchronizedExecutor.sendMsgToServer(addressContext.getAddress(), ClusterListener.getActorContext(), ActorSystemPath.WS_GameServer_Selection_World, request);
                if (response.getResultCode() != ResultCodeEnum.SUCCESS) {
                    return "玩家SimpleId=" + simpleId + " 发送失败！\n";
                } else {
                    return "玩家SimpleId=" + simpleId + " 发送成功！\n";
                }
            } catch (Exception e) {
                return "玩家SimpleId=" + simpleId + " 发送失败！e=" + e.getMessage() + "\n";
            }
        } catch (Exception e) {
            LOGGER.error("sendCommand.", e);
            return "sendCommand！e=\n" + e.getMessage();
        }
    }
}