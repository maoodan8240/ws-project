package ws.gameServer.features.standalone.actor.newGuildCenter._module.actions;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.message.interfaces.ResultCode;
import ws.gameServer.features.standalone.actor.newGuildCenter._module.Action;
import ws.gameServer.features.standalone.actor.newGuildCenter.ctrl.NewGuildCenterCtrl;
import ws.gameServer.features.standalone.actor.newGuildCenter.msg.In_NewGuildRedBagSendSysRedBag;
import ws.gameServer.features.standalone.actor.newGuildCenter.utils.NewGuildCenterUtils;
import ws.gameServer.features.standalone.extp.newGuild.msg.redBag.In_NewGuildRedBagGetRedBagList;
import ws.gameServer.features.standalone.extp.newGuild.msg.redBag.In_NewGuildRedBagGrab;
import ws.gameServer.features.standalone.extp.newGuild.msg.redBag.In_NewGuildRedBagRank;
import ws.gameServer.features.standalone.extp.newGuild.msg.redBag.In_NewGuildRedBagSend;
import ws.gameServer.features.standalone.extp.newGuild.msg.redBag.In_NewGuildRedBagSync;
import ws.gameServer.features.standalone.extp.newGuild.utils.NewGuildCtrlUtils;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.protos.EnumsProtos.GuildBuildTypeEnum;
import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.protos.EnumsProtos.GuildSendRedBagTypeEnum;
import ws.protos.EnumsProtos.RedPointEnum;
import ws.protos.errorCode.ErrorCodeProtos.ErrorCodeEnum;
import ws.relationship.base.MagicNumbers;
import ws.relationship.base.resultCode.ResultMsg;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenter;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildPlayerRedBag;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildRedBag;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildSystemRedBag;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.CloneUtils;
import ws.relationship.utils.RandomUtils;
import ws.relationship.utils.RelationshipCommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lee on 6/8/17.
 */
public class Action_GuildRedBagMsgHandle implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(Action_GuildRedBagMsgHandle.class);

    @Override
    public void onRecv(NewGuildCenterCtrl actorCtrl, Object msg, ActorContext context, ActorRef sender) {
        if (msg instanceof In_NewGuildRedBagSync.Request) {
            onIn_NewGuildRedBagSync((In_NewGuildRedBagSync.Request) msg, actorCtrl, context, sender);
        } else if (msg instanceof In_NewGuildRedBagRank.Request) {
            onIn_NewGuildRedBagRank((In_NewGuildRedBagRank.Request) msg, actorCtrl, context, sender);
        } else if (msg instanceof In_NewGuildRedBagSend.Request) {
            onIn_NewGuildRedBagSend((In_NewGuildRedBagSend.Request) msg, actorCtrl, context, sender);
        } else if (msg instanceof In_NewGuildRedBagGrab.Request) {
            onIn_NewGuildRedBagGrab((In_NewGuildRedBagGrab.Request) msg, actorCtrl, context, sender);
        } else if (msg instanceof In_NewGuildRedBagGetRedBagList.Request) {
            onIn_NewGuildRedBagGetRedBagList((In_NewGuildRedBagGetRedBagList.Request) msg, actorCtrl, context, sender);
        } else if (msg instanceof In_NewGuildRedBagSendSysRedBag.Request) {
            onIn_NewGuildRedBagSendSysRedBag((In_NewGuildRedBagSendSysRedBag.Request) msg, actorCtrl, context, sender);
        }
    }


    private void onIn_NewGuildRedBagSendSysRedBag(In_NewGuildRedBagSendSysRedBag.Request request, NewGuildCenterCtrl actorCtrl, ActorContext context, ActorRef sender) {
        _gotoSendSysRedBag(actorCtrl, context);


    }

    private void onIn_NewGuildRedBagGetRedBagList(In_NewGuildRedBagGetRedBagList.Request request, NewGuildCenterCtrl actorCtrl, ActorContext context, ActorRef sender) {
        In_NewGuildRedBagGetRedBagList.Response response = null;
        try {
            SimplePlayer simplePlayer = request.getSimplePlayer();
            LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
            NewGuildCenter guildCenter = actorCtrl.getNewGuildCenter(simplePlayer.getOutRealmId());
            NewGuild guild = actorCtrl.getGuildById(guildCenter, simplePlayer.getGuildId());
            if (!NewGuildCenterUtils.isFunctionOpen(guild, GuildBuildTypeEnum.GB_REDBAG)) {
                String msg = String.format("社团等级没有开启这个功能:%s", GuildBuildTypeEnum.GB_REDBAG);
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_INSTITUTE_NO_OPEN_ERROR);
            }
            List<NewGuildPlayerRedBag> clonePlayerRedBagList = CloneUtils.cloneWsCloneableList(NewGuildCenterUtils.getPlayerRedBag(guild));
            response = new In_NewGuildRedBagGetRedBagList.Response(request, clonePlayerRedBagList);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildRedBagGetRedBagList.Response(resultCode, request);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildRedBagGetRedBagList.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), request);
        } finally {
            sender.tell(response, context.self());
        }

    }


    private NewGuildRedBag getRedBagByType(NewGuild guild, GuildRedBagTypeEnum redBagType, String redBagId) {
        if (redBagType == GuildRedBagTypeEnum.GRT_MONEY || redBagType == GuildRedBagTypeEnum.GRT_V_MONEY) {
            return guild.getTypeAndSysRedBag().get(redBagType);
        }
        List<NewGuildPlayerRedBag> guildPlayerRedBagList = RelationshipCommonUtils.getListByKey(guild.getTypeAndPlayerRedBag(), redBagType);
        for (NewGuildPlayerRedBag guildPlayerRedBag : guildPlayerRedBagList) {
            if (guildPlayerRedBag.getRedBagId().equals(redBagId)) {
                return guildPlayerRedBag;
            }
        }
        String msg = String.format("没有找到这个红包, id:%s ,type:%s", redBagId, redBagType);
        throw new BusinessLogicMismatchConditionException(msg);
    }


    private void _gotoSendSysRedBag(NewGuildCenterCtrl actorCtrl, ActorContext actorContext) {
        for (NewGuildCenter guildCenter : actorCtrl.getCenterOutRealmIdToGuildCenters().values()) {
            for (NewGuild guild : guildCenter.getIdToGuild().values()) {
                NewGuildCenterUtils.sendSysRedBag(guild);
                actorCtrl.saveGuildToMongoDB(guild.getOuterRealmId(), guild);
                NewGuildCtrlUtils.sendNotifyRedPointMsg(actorContext, RedPointEnum.GUILD_SYS_REDBAG, new ArrayList<>(guild.getPlayerIdToGuildPlayerInfo().values()));
            }
        }
    }


    private void onIn_NewGuildRedBagGrab(In_NewGuildRedBagGrab.Request request, NewGuildCenterCtrl actorCtrl, ActorContext context, ActorRef sender) {
        In_NewGuildRedBagGrab.Response response = null;
        try {
            SimplePlayer simplePlayer = request.getSimplePlayer();
            GuildRedBagTypeEnum redBagType = request.getRedBagTypeEnum();
            String redBagId = request.getRedBagId();
            LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
            NewGuildCenter guildCenter = actorCtrl.getNewGuildCenter(simplePlayer.getOutRealmId());
            NewGuild guild = actorCtrl.getGuildById(guildCenter, simplePlayer.getGuildId());
            if (!NewGuildCenterUtils.isFunctionOpen(guild, GuildBuildTypeEnum.GB_REDBAG)) {
                String msg = String.format("社团等级没有开启这个功能:%s", GuildBuildTypeEnum.GB_REDBAG);
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_INSTITUTE_NO_OPEN_ERROR);
            }
            NewGuildRedBag redBag = getRedBagByType(guild, redBagType, redBagId);
            if (!NewGuildCenterUtils.isShareCanGrab(redBag.getRedBagShare())) {
                String msg = "红包已经抢光了";
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_REDBAG_NO_ENOUGH_ERROR);
            }
            if (NewGuildCenterUtils.hasGrab(redBag.getPlayerNameAndShare(), simplePlayer.getPlayerName())) {
                String msg = "红包已经抢过了";
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_REDBAGL_HAS_GRAB);
            }
            _grab(simplePlayer, redBag);
            actorCtrl.saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
            response = new In_NewGuildRedBagGrab.Response(request, redBag.clone());
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildRedBagGrab.Response(resultCode, request);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildRedBagGrab.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), request);
        } finally {
            sender.tell(response, context.self());
        }
    }


    private void _grab(SimplePlayer simplePlayer, NewGuildRedBag redBag) {
        int index = RandomUtils.dropBetweenTowNum(MagicNumbers.DEFAULT_ONE, redBag.getRedBagShare().size() - MagicNumbers.DEFAULT_ONE);
        int share = redBag.getRedBagShare().remove(index);
        redBag.getPlayerNameAndShare().put(simplePlayer.getPlayerName(), share);
    }

    private void onIn_NewGuildRedBagSend(In_NewGuildRedBagSend.Request request, NewGuildCenterCtrl actorCtrl, ActorContext context, ActorRef sender) {
        In_NewGuildRedBagSend.Response response = null;
        try {
            SimplePlayer simplePlayer = request.getSimplePlayer();
            LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
            GuildRedBagTypeEnum redBagType = request.getRedBagType();
            GuildSendRedBagTypeEnum sendRedBagType = request.getSendRedBagType();
            NewGuildCenter guildCenter = actorCtrl.getNewGuildCenter(simplePlayer.getOutRealmId());
            NewGuild guild = actorCtrl.getGuildById(guildCenter, simplePlayer.getGuildId());
            if (!NewGuildCenterUtils.isFunctionOpen(guild, GuildBuildTypeEnum.GB_REDBAG)) {
                String msg = String.format("社团等级没有开启这个功能:%s", GuildBuildTypeEnum.GB_REDBAG);
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_INSTITUTE_NO_OPEN_ERROR);
            }
            NewGuildPlayerRedBag redBag = NewGuildCtrlUtils.createPlayerRedBagByType(simplePlayer.getPlayerName(), redBagType, sendRedBagType);
            List<NewGuildPlayerRedBag> guildPlayerRedBagList = RelationshipCommonUtils.getListByKey(guild.getTypeAndPlayerRedBag(), redBagType);
            guildPlayerRedBagList.add(redBag);
            actorCtrl.saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
            response = new In_NewGuildRedBagSend.Response(request, new ArrayList<>(guild.getPlayerIdToGuildPlayerInfo().values()));
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildRedBagSend.Response(resultCode, request);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildRedBagSend.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), request);
        } finally {
            sender.tell(response, context.self());
        }
    }

    private void onIn_NewGuildRedBagRank(In_NewGuildRedBagRank.Request request, NewGuildCenterCtrl actorCtrl, ActorContext context, ActorRef sender) {
        In_NewGuildRedBagRank.Response response = null;
        try {
            SimplePlayer simplePlayer = request.getSimplePlayer();
            GuildRedBagTypeEnum redBagTypeEnum = request.getRedBagTypeEnum();
            LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
            NewGuildCenter guildCenter = actorCtrl.getNewGuildCenter(simplePlayer.getOutRealmId());
            NewGuild guild = actorCtrl.getGuildById(guildCenter, simplePlayer.getGuildId());
            if (!NewGuildCenterUtils.isFunctionOpen(guild, GuildBuildTypeEnum.GB_REDBAG)) {
                String msg = String.format("社团等级没有开启这个功能:%s", GuildBuildTypeEnum.GB_REDBAG);
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_INSTITUTE_NO_OPEN_ERROR);
            }
            NewGuildRedBag redBag;
            if (redBagTypeEnum == GuildRedBagTypeEnum.GRT_MONEY || redBagTypeEnum == GuildRedBagTypeEnum.GRT_V_MONEY) {
                redBag = guild.getTypeAndSysRedBag().get(redBagTypeEnum);
            } else {
                String redBagId = request.getRedBagId();
                redBag = getRedBagByType(guild, redBagTypeEnum, redBagId);
            }
            response = new In_NewGuildRedBagRank.Response(request, CloneUtils.cloneHashMap(redBag.getPlayerNameAndShare()));
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildRedBagRank.Response(resultCode, request);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildRedBagRank.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), request);
        } finally {
            sender.tell(response, context.self());
        }
    }

    private void onIn_NewGuildRedBagSync(In_NewGuildRedBagSync.Request request, NewGuildCenterCtrl actorCtrl, ActorContext context, ActorRef sender) {
        In_NewGuildRedBagSync.Response response = null;
        try {
            SimplePlayer simplePlayer = request.getSimplePlayer();
            LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
            NewGuildCenter guildCenter = actorCtrl.getNewGuildCenter(simplePlayer.getOutRealmId());
            NewGuild guild = actorCtrl.getGuildById(guildCenter, simplePlayer.getGuildId());
            if (!NewGuildCenterUtils.isFunctionOpen(guild, GuildBuildTypeEnum.GB_REDBAG)) {
                String msg = String.format("社团等级没有开启这个功能:%s", GuildBuildTypeEnum.GB_REDBAG);
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_INSTITUTE_NO_OPEN_ERROR);
            }
            Map<GuildRedBagTypeEnum, NewGuildSystemRedBag> cloneTypeAndRedBag = CloneUtils.cloneHashMap(guild.getTypeAndSysRedBag());
            response = new In_NewGuildRedBagSync.Response(request, cloneTypeAndRedBag);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildRedBagSync.Response(resultCode, request);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildRedBagSync.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), request);
        } finally {
            sender.tell(response, context.self());
        }
    }
}
