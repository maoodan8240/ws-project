package ws.gameServer.features.standalone.actor.newGuildCenter;

import akka.actor.ActorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.gameServer.features.standalone.actor.newGuildCenter._module.GuildTechnologyMsgHandleEnum;
import ws.gameServer.features.standalone.actor.newGuildCenter.ctrl.NewGuildCenterCtrl;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildAgree;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildAppoint;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildCancel;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildChange;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildCreate;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildDisband;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildGetApplys;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildGetInfo;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildGiveJob;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildJoin;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildKick;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildMails;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildModify;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildOenKeyJoin;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildOneKeyRefuse;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildOut;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildRecruit;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildRefuse;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildSearch;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.CheckPlayerOnlineMsgRequest;
import ws.relationship.base.resultCode.ResultMsg;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildApplyInfo;
import ws.relationship.utils.ClusterMessageSender;

import java.util.List;

/**
 * Created by lee on 16-11-30.
 */
public class NewGuildCenterActor extends WsActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewGuildCenterActor.class);
    private NewGuildCenterCtrl ctrl;


    public NewGuildCenterActor() {
        this.ctrl = GlobalInjector.getInstance(NewGuildCenterCtrl.class);
    }

    @Override
    public void onRecv(Object msg) throws Exception {
        if (msg instanceof InnerMsg) {
            GuildTechnologyMsgHandleEnum.onRecv(ctrl, msg, getContext(), getSender());
            if (msg instanceof In_DayChanged) {
                onDayChanged();
            } else if (msg instanceof In_NewGuildCreate.Request) {
                onIn_NewGuildCreate((In_NewGuildCreate.Request) msg);
            } else if (msg instanceof In_NewGuildJoin.Request) {
                onIn_NewGuildJoin((In_NewGuildJoin.Request) msg);
            } else if (msg instanceof In_NewGuildAgree.Request) {
                onIn_NewGuildAgree((In_NewGuildAgree.Request) msg);
            } else if (msg instanceof In_NewGuildRefuse.Request) {
                onIn_NewGuildRefuse((In_NewGuildRefuse.Request) msg);
            } else if (msg instanceof In_NewGuildKick.Request) {
                onIn_NewGuildKick((In_NewGuildKick.Request) msg);
            } else if (msg instanceof In_NewGuildOut.Request) {
                onIn_NewGuildOut((In_NewGuildOut.Request) msg);
            } else if (msg instanceof In_NewGuildDisband.Request) {
                onIn_NewGuildDisband((In_NewGuildDisband.Request) msg);
            } else if (msg instanceof In_NewGuildChange.Request) {
                onIn_NewGuildChange((In_NewGuildChange.Request) msg);
            } else if (msg instanceof In_NewGuildSearch.Request) {
                onIn_NewGuildSearch((In_NewGuildSearch.Request) msg);
            } else if (msg instanceof In_NewGuildGetApplys.Request) {
                onIn_NewGuildGetApplys((In_NewGuildGetApplys.Request) msg);
            } else if (msg instanceof In_NewGuildGetInfo.Request) {
                onIn_NewGuildGetInfo((In_NewGuildGetInfo.Request) msg);
            } else if (msg instanceof In_NewGuildCancel.Request) {
                onIn_NewGuildCancel((In_NewGuildCancel.Request) msg);
            } else if (msg instanceof In_NewGuildModify.Request) {
                onIn_NewGuildModify((In_NewGuildModify.Request) msg);
            } else if (msg instanceof In_NewGuildOenKeyJoin.Request) {
                onIn_NewGuildOenKeyJoin((In_NewGuildOenKeyJoin.Request) msg);
            } else if (msg instanceof In_NewGuildAppoint.Request) {
                onIn_NewGuildAppoint((In_NewGuildAppoint.Request) msg);
            } else if (msg instanceof In_NewGuildGiveJob.Request) {
                onIn_NewGuildGiveJob((In_NewGuildGiveJob.Request) msg);
            } else if (msg instanceof In_NewGuildMails.Request) {
                onIn_NewGuildMails((In_NewGuildMails.Request) msg);
            } else if (msg instanceof In_NewGuildOneKeyRefuse.Request) {
                onIn_NewGuildOneKeyRefuse((In_NewGuildOneKeyRefuse.Request) msg);
            } else if (msg instanceof In_NewGuildRecruit.Request) {
                onIn_NewGuildRecruit((In_NewGuildRecruit.Request) msg);
            }
        }

    }

    private void onDayChanged() {
        ctrl.onDayChanged();
    }


    private void onIn_NewGuildRecruit(In_NewGuildRecruit.Request msg) {
        In_NewGuildRecruit.Response response = null;
        try {
            NewGuild guild = ctrl.recruit(msg.getSimplePlayer());
            response = new In_NewGuildRecruit.Response(msg, guild);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildRecruit.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildRecruit.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }

    private void onIn_NewGuildOneKeyRefuse(In_NewGuildOneKeyRefuse.Request msg) {
        In_NewGuildOneKeyRefuse.Response response = null;
        try {
            List<String> refusePlayerIds = ctrl.oneKeyRefuse(msg.getSimplePlayer());
            response = new In_NewGuildOneKeyRefuse.Response(msg);
            _notifyRefusePlayers(refusePlayerIds, msg);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildOneKeyRefuse.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildOneKeyRefuse.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }


    private void onIn_NewGuildMails(In_NewGuildMails.Request msg) {
        In_NewGuildMails.Response response = null;
        try {
            NewGuild guild = ctrl.mails(msg.getSimplePlayer(), msg.getContent(), msg.getTitle());
            response = new In_NewGuildMails.Response(msg, guild);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildMails.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildMails.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }


    private void onIn_NewGuildGiveJob(In_NewGuildGiveJob.Request msg) {
        In_NewGuildGiveJob.Response response = null;
        try {
            response = ctrl.giveJob(msg);
            CheckPlayerOnlineMsgRequest<In_NewGuildGiveJob.Response> request = new CheckPlayerOnlineMsgRequest<>(msg.getGiveSimplePlayer().getPlayerId(), response);
            _sendCheckPlayerOnlineMsg(request, getContext());
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildGiveJob.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildGiveJob.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }

    private void onIn_NewGuildAppoint(In_NewGuildAppoint.Request msg) {
        In_NewGuildAppoint.Response response = null;
        try {
            response = ctrl.appoint(msg);
            CheckPlayerOnlineMsgRequest<In_NewGuildAppoint.Response> request = new CheckPlayerOnlineMsgRequest<>(msg.getAppointSimplePlayer().getPlayerId(), response);
            _sendCheckPlayerOnlineMsg(request, getContext());
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildAppoint.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildAppoint.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }


    private void onIn_NewGuildOenKeyJoin(In_NewGuildOenKeyJoin.Request msg) {
        In_NewGuildOenKeyJoin.Response response = null;
        try {
            NewGuild guild = ctrl.oneKeyJoin(msg.getSimplePlayer());
            response = new In_NewGuildOenKeyJoin.Response(msg, guild);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildOenKeyJoin.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildOenKeyJoin.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }

    private void onIn_NewGuildModify(In_NewGuildModify.Request msg) {
        In_NewGuildModify.Response response = null;
        try {
            NewGuild guild = ctrl.modify(msg.getSimplePlayer(), msg.getGuildNotic(), msg.getGuildIcon(), msg.getGuildName());
            response = new In_NewGuildModify.Response(msg, guild);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildModify.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildModify.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }

    private void onIn_NewGuildCancel(In_NewGuildCancel.Request msg) {
        In_NewGuildCancel.Response response = null;
        try {
            NewGuild guild = ctrl.cancel(msg.getGuildId(), msg.getSimplePlayer());
            response = new In_NewGuildCancel.Response(msg, guild);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildCancel.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildCancel.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }


    private void onIn_NewGuildGetInfo(In_NewGuildGetInfo.Request msg) {
        In_NewGuildGetInfo.Response response = null;
        try {
            List<NewGuild> guildList = ctrl.getGuildInfo(msg.getGuildId(), msg.getSimplePlayer());
            response = new In_NewGuildGetInfo.Response(msg, guildList);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildGetInfo.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildGetInfo.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }

    private void onIn_NewGuildGetApplys(In_NewGuildGetApplys.Request msg) {
        In_NewGuildGetApplys.Response response = null;
        try {
            List<NewGuildApplyInfo> applyInfoList = this.ctrl.getApplys(msg);
            response = new In_NewGuildGetApplys.Response(msg, applyInfoList);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildGetApplys.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildGetApplys.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }

    private void onIn_NewGuildSearch(In_NewGuildSearch.Request msg) {
        In_NewGuildSearch.Response response = null;
        try {
            NewGuild guild = this.ctrl.search(msg);
            response = new In_NewGuildSearch.Response(guild);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildSearch.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildSearch.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }

    private void onIn_NewGuildChange(In_NewGuildChange.Request msg) {
        In_NewGuildChange.Response response = null;
        try {
            NewGuild guild = ctrl.change(msg.getGuildId(), msg.getSimplePlayer(), msg.getJoinTypeEnum(), msg.getRound());
            response = new In_NewGuildChange.Response(msg, guild);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildChange.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildChange.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }


    private void onIn_NewGuildDisband(In_NewGuildDisband.Request msg) {
        In_NewGuildDisband.Response response = null;
        try {
            ctrl.disband(msg.getGuildId(), msg.getSimplePlayer());
            response = new In_NewGuildDisband.Response(msg);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildDisband.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildDisband.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }

    private void _notifyRefusePlayers(List<String> refusePlayerIds, In_NewGuildOneKeyRefuse.Request msg) {
        for (String playerId : refusePlayerIds) {
            CheckPlayerOnlineMsgRequest<In_NewGuildOneKeyRefuse.Response> request = new CheckPlayerOnlineMsgRequest<>(playerId, new In_NewGuildOneKeyRefuse.Response(msg, playerId));
            _sendCheckPlayerOnlineMsg(request, getContext());
        }
    }

    private void onIn_NewGuildOut(In_NewGuildOut.Request msg) {
        In_NewGuildOut.Response response = null;
        try {
            ctrl.out(msg.getGuildId(), msg.getSimplePlayer());
            response = new In_NewGuildOut.Response(msg);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildOut.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildOut.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }

    private void onIn_NewGuildKick(In_NewGuildKick.Request msg) {
        In_NewGuildKick.Response response = null;
        try {
            ctrl.kick(msg.getGuildId(), msg.getSimplePlayer(), msg.getKickSimplePlayer());
            response = new In_NewGuildKick.Response(msg);
            CheckPlayerOnlineMsgRequest<In_NewGuildKick.Response> request = new CheckPlayerOnlineMsgRequest<>(msg.getKickSimplePlayer().getPlayerId(), response);
            _sendCheckPlayerOnlineMsg(request, getContext());
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildKick.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildKick.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }

    private void onIn_NewGuildRefuse(In_NewGuildRefuse.Request msg) {
        In_NewGuildRefuse.Response response = null;
        try {
            ctrl.refuse(msg.getGuildId(), msg.getSimplePlayer(), msg.getRefuseSimplePlayer());
            response = new In_NewGuildRefuse.Response(msg);
            CheckPlayerOnlineMsgRequest<In_NewGuildRefuse.Response> request = new CheckPlayerOnlineMsgRequest<>(msg.getRefuseSimplePlayer().getPlayerId(), response);
            _sendCheckPlayerOnlineMsg(request, getContext());
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildRefuse.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildRefuse.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }


    private void onIn_NewGuildAgree(In_NewGuildAgree.Request msg) {
        In_NewGuildAgree.Response response = null;
        try {
            NewGuild guild = ctrl.agree(msg.getGuildId(), msg.getSimplePlayer(), msg.getAddSimplePlayer());
            response = new In_NewGuildAgree.Response(msg, guild);
            CheckPlayerOnlineMsgRequest<In_NewGuildAgree.Response> request = new CheckPlayerOnlineMsgRequest<>(msg.getAddSimplePlayer().getPlayerId(), response);
            _sendCheckPlayerOnlineMsg(request, getContext());
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildAgree.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildAgree.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }

    private void onIn_NewGuildJoin(In_NewGuildJoin.Request msg) {
        In_NewGuildJoin.Response response = null;
        try {
            response = ctrl.join(msg);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildJoin.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildJoin.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }

    private void onIn_NewGuildCreate(In_NewGuildCreate.Request msg) {
        In_NewGuildCreate.Response response = null;
        this.ctrl.setCurSendActorRef(getSender());
        this.ctrl.setCurContext(getContext());
        try {
            NewGuild guild = ctrl.create(msg.getGuildName(), msg.getGuildIcon(), msg.getSimplePlayer());
            response = new In_NewGuildCreate.Response(msg, guild);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildCreate.Response(resultCode, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildCreate.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), msg);
        } finally {
            getSender().tell(response, getSelf());
        }
    }

    private void _sendCheckPlayerOnlineMsg(InnerMsg request, ActorContext actorContext) {
        ClusterMessageSender.sendMsgToPath(actorContext, request, ActorSystemPath.WS_GameServer_Selection_World);
    }

}
