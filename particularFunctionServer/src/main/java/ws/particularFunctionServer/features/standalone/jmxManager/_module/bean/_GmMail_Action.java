package ws.particularFunctionServer.features.standalone.jmxManager._module.bean;


import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.particularFunctionServer.features.actor.cluster.ClusterListener;
import ws.particularFunctionServer.features.standalone.jmxManager.Action;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.cluster.AkkaAddressContext;
import ws.relationship.base.msg.CheckPlayerOnlineMsgRequest;
import ws.relationship.base.msg.mail.In_AddGmMail;
import ws.relationship.base.resultCode.ResultCodeEnum;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.centerPlayer.CenterPlayerDao;
import ws.relationship.gm.GmMailList;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;
import ws.relationship.topLevelPojos.mailCenter.GmMail;
import ws.relationship.utils.ActorMsgSynchronizedExecutor;
import ws.relationship.utils.ChooseGameSeverUtils;
import ws.relationship.utils.RelationshipCommonUtils;

import java.util.Date;

public class _GmMail_Action implements Action {
    private Logger LOGGER = LoggerFactory.getLogger(_GmMail_Action.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final CenterPlayerDao CENTER_PLAYER_DAO = DaoContainer.getDao(CenterPlayer.class);

    private static final String sendMailToAll = "sendMailToAll";
    private static final String sendMailToPlayers = "sendMailToPlayers";

    static {
        CENTER_PLAYER_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
    }

    @Override
    public String handle(String funcName, String args) {
        if (sendMailToAll.equals(funcName)) {
            return sendMailToAll(args);
        } else if (sendMailToPlayers.equals(funcName)) {
            return sendMailToPlayers(args);
        }
        return "不存在funcName=[" + funcName + "]对应的解析失败.";
    }

    private String sendMailToAll(String args) {
        try {
            LOGGER.info("client send args={} !", args);
            GmMail gmMail = JSON.parseObject(args, GmMail.class);
            if (gmMail == null) {
                return "数据反序列化失败！";
            }
            gmMail.setSendTime(WsDateUtils.dateToFormatStr(new Date(), WsDateFormatEnum.yyyy_MM_dd$HH_mm_ss));
            if (StringUtils.isBlank(gmMail.getExpireTime())) {
                RelationshipCommonUtils.setSendTimeAndExpiredTime(gmMail);
            }
            In_AddGmMail.Request request = new In_AddGmMail.Request("", 0, gmMail);
            In_AddGmMail.Response response = _sendMsgToMailsCenterActor(request);
            return response.getResultCode().getMessage();
        } catch (Exception e) {
            LOGGER.error("sendMailToAll失败.", e);
            return "sendMailToAll失败！e=\n" + e.getMessage();
        }
    }

    private String sendMailToPlayers(String args) {
        try {
            LOGGER.info("client send args={} !", args);
            GmMailList gmMailList = JSON.parseObject(args, GmMailList.class);
            if (gmMailList == null) {
                return "数据反序列化失败！";
            }
            String msg = ResultCodeEnum.SUCCESS.getMessage();
            RelationshipCommonUtils.setSendTimeAndExpiredTime(gmMailList.getGmMail());
            for (Integer simpleId : gmMailList.getSimpleIds()) {
                try {
                    CenterPlayer centerPlayer = CENTER_PLAYER_DAO.findCenterPlayer(simpleId);
                    AkkaAddressContext addressContext = ChooseGameSeverUtils.chooseSpecificGameServer(ClusterListener.getActorContext(), centerPlayer.getOuterRealmId(), null);
                    In_AddGmMail.Request addGmMailRequest = new In_AddGmMail.Request(centerPlayer.getGameId(), centerPlayer.getOuterRealmId(), gmMailList.getGmMail());
                    CheckPlayerOnlineMsgRequest<In_AddGmMail.Request> request = new CheckPlayerOnlineMsgRequest<>(centerPlayer.getGameId(), addGmMailRequest);
                    In_AddGmMail.Response addGmMailResponse = ActorMsgSynchronizedExecutor.sendMsgToServer(addressContext.getAddress(), ClusterListener.getActorContext(), ActorSystemPath.WS_GameServer_Selection_World, request);
                    if (addGmMailResponse.getResultCode() != ResultCodeEnum.SUCCESS) {
                        msg += simpleId + ":发送邮件失败| ";
                    }
                } catch (Exception e) {
                    LOGGER.error("simpleId={}发送邮件失败.", simpleId, e);
                    msg += simpleId + ":发送邮件失败| ";
                }
            }
            return msg;
        } catch (Exception e) {
            LOGGER.error("sendMailToPlayers失败.", e);
            return "sendMailToPlayers失败！e=\n" + e.getMessage();
        }
    }

    private <I, O> O _sendMsgToMailsCenterActor(I request) {
        InnerMsg response = ActorMsgSynchronizedExecutor.sendMsgToServer(ClusterListener.getActorContext().actorSelection(ActorSystemPath.WS_ThirdPartyServer_Selection_MailsCenterActor), request);
        return (O) response;
    }

}