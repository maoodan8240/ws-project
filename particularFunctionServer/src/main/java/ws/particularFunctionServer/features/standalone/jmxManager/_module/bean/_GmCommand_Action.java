package ws.particularFunctionServer.features.standalone.jmxManager._module.bean;


import com.alibaba.fastjson.JSON;
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
import ws.relationship.base.resultCode.ResultCodeEnum;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.centerPlayer.CenterPlayerDao;
import ws.relationship.enums.GmCommandFromTypeEnum;
import ws.relationship.gm.GmCommandContent;
import ws.relationship.gm.GmCommandUtils;
import ws.relationship.gm.In_GmCommand;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;
import ws.relationship.utils.ActorMsgSynchronizedExecutor;
import ws.relationship.utils.ChooseGameSeverUtils;

public class _GmCommand_Action implements Action {
    private Logger LOGGER = LoggerFactory.getLogger(_GmCommand_Action.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final CenterPlayerDao CENTER_PLAYER_DAO = DaoContainer.getDao(CenterPlayer.class);

    private static final String GM_COMMAND = "gmCommand";

    static {
        CENTER_PLAYER_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
    }

    @Override
    public String handle(String funcName, String args) {
        if (GM_COMMAND.equals(funcName)) {
            return sendCommand(args);
        }
        return "不存在funcName=[" + funcName + "]对应的解析失败.";
    }

    private String sendCommand(String args) {
        try {
            LOGGER.info("client send args={} !", args);
            GmCommandContent content = JSON.parseObject(args, GmCommandContent.class);
            if (content == null) {
                return "数据反序列化失败！";
            }
            In_GmCommand.Request gmCommand = GmCommandUtils.convertGmConentTo_In_GmCommand(content.getCommand());
            if (gmCommand == null) {
                return "命令格式有误！";
            }
            gmCommand.setFromType(GmCommandFromTypeEnum.MANAGER);
            String str = "";
            for (int simpleId : content.getSimpleIdLis()) {
                try {
                    CenterPlayer centerPlayer = CENTER_PLAYER_DAO.findCenterPlayer(simpleId);
                    if (centerPlayer == null) {
                        str += "玩家SimpleId=" + simpleId + " 不存在！\n";
                    }
                    AkkaAddressContext addressContext = ChooseGameSeverUtils.chooseSpecificGameServer(ClusterListener.getActorContext(), centerPlayer.getOuterRealmId(), null);
                    CheckPlayerOnlineMsgRequest<In_GmCommand.Request> request = new CheckPlayerOnlineMsgRequest<>(centerPlayer.getGameId(), gmCommand);
                    In_GmCommand.Response response = ActorMsgSynchronizedExecutor.sendMsgToServer(addressContext.getAddress(), ClusterListener.getActorContext(), ActorSystemPath.WS_GameServer_Selection_World, request);
                    if (response.getResultCode() != ResultCodeEnum.SUCCESS) {
                        str += "玩家SimpleId=" + simpleId + " 发送失败！\n";
                    } else {
                        str += "玩家SimpleId=" + simpleId + " 发送成功！\n";
                    }
                } catch (Exception e) {
                    str += "玩家SimpleId=" + simpleId + " 发送失败！e=" + e.getMessage() + "\n";
                    continue;
                }
            }
            return str.length() > 0 ? str : "success";
        } catch (Exception e) {
            LOGGER.error("sendCommand.", e);
            return "sendCommand！e=\n" + e.getMessage();
        }
    }
}