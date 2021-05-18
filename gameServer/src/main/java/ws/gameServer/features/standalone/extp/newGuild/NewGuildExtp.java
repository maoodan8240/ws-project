package ws.gameServer.features.standalone.extp.newGuild;


import com.google.protobuf.Message;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.extension.AbstractPlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.extp.newGuild._msgModule.CtrlModuleContainer;
import ws.gameServer.features.standalone.extp.newGuild.ctrl.NewGuildCtrl;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildAgree;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildAppoint;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildDisband;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildGiveJob;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildKick;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildOneKeyRefuse;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildRefuse;
import ws.gameServer.features.standalone.extp.redPoint.msg.Pr_CheckRedPointMsg;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.CommonProtos.Sm_Common_Round;
import ws.protos.EnumsProtos.GuildJobEnum;
import ws.protos.EnumsProtos.GuildJoinTypeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.NewGuildProtos.Cm_NewGuild;
import ws.protos.NewGuildProtos.Sm_NewGuild;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.topLevelPojos.newGuild.NewGuildPlayer;
import ws.relationship.utils.ProtoUtils;


public class NewGuildExtp extends AbstractPlayerExtension<NewGuildCtrl> {
    public static boolean useExtension = true;
    private CtrlModuleContainer container;

    public NewGuildExtp(PlayerCtrl ownerCtrl) {
        super(ownerCtrl);
    }

    @Override
    public void _init() throws Exception {
        _init(NewGuildCtrl.class, NewGuildPlayer.class);
        container = new CtrlModuleContainer();
    }

    @Override
    public void _initReference() throws Exception {
        getControlerForQuery()._initReference();
    }

    @Override
    public void _postInit() throws Exception {
        getControlerForQuery()._initAll();
    }

    @Override
    public void onRecvMyNetworkMsg(Message clientMsg) throws Exception {
        if (clientMsg instanceof Cm_NewGuild) {
            Cm_NewGuild cm = (Cm_NewGuild) clientMsg;
            onCm_NewGuild(cm);
        } else {
            container.onRecv(clientMsg, getControlerForQuery());
        }
    }


    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg) throws Exception {
        if (innerMsg instanceof In_PlayerLoginedRequest) {
            onPlayerLogined((In_PlayerLoginedRequest) innerMsg);
        } else if (innerMsg instanceof In_DayChanged) {
            onDayChanged();
        } else if (innerMsg instanceof In_NewGuildAgree.Response) {
            onIn_NewGuildAgree((In_NewGuildAgree.Response) innerMsg);
        } else if (innerMsg instanceof In_NewGuildKick.Response) {
            onIn_NewGuildKick((In_NewGuildKick.Response) innerMsg);
        } else if (innerMsg instanceof In_NewGuildAppoint.Response) {
            onIn_NewGuildAppoint((In_NewGuildAppoint.Response) innerMsg);
        } else if (innerMsg instanceof In_NewGuildGiveJob.Response) {
            onIn_NewGuildGiveJob((In_NewGuildGiveJob.Response) innerMsg);
        } else if (innerMsg instanceof In_NewGuildOneKeyRefuse.Response) {
            onIn_NewGuildOneKeyRefuse((In_NewGuildOneKeyRefuse.Response) innerMsg);
        } else if (innerMsg instanceof In_NewGuildRefuse.Response) {
            onIn_NewGuildRefuse((In_NewGuildRefuse.Response) innerMsg);
        } else if (innerMsg instanceof In_NewGuildDisband.Response) {
            onIn_NewGuildDisband((In_NewGuildDisband.Response) innerMsg);
        } else {
            container.onRecvInnerMsg(innerMsg, getControlerForQuery());
        }
    }

    private void onIn_NewGuildDisband(In_NewGuildDisband.Response response) {
        getControlerForQuery().disbandResponse(response);
    }

    private void onIn_NewGuildRefuse(In_NewGuildRefuse.Response response) {
        getControlerForQuery().refuseResponse(response);
    }

    private void onIn_NewGuildOneKeyRefuse(In_NewGuildOneKeyRefuse.Response response) {
        getControlerForQuery().oneKeyRefuseResponse(response);
    }

    private void onIn_NewGuildGiveJob(In_NewGuildGiveJob.Response response) {
        getControlerForQuery().giveJobResponse(response);
    }

    private void onIn_NewGuildAppoint(In_NewGuildAppoint.Response response) {
        getControlerForQuery().appointResponse(response);
    }


    private void onIn_NewGuildKick(In_NewGuildKick.Response response) {
        getControlerForQuery().kickResponse(response);
    }

    private void onIn_NewGuildAgree(In_NewGuildAgree.Response response) {
        getControlerForQuery().agreeResponse(response);
    }


    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg) throws Exception {
        if (privateMsg instanceof Pr_CheckRedPointMsg.Request) {
            onCheckRedPointMsg((Pr_CheckRedPointMsg.Request) privateMsg);
        }
        container.onRecvPrivateMsg(privateMsg, getControlerForQuery());
    }

    private void onCheckRedPointMsg(Pr_CheckRedPointMsg.Request privateMsg) {
        getControlerForQuery().checkRedPointMsg(privateMsg);
    }


    /**
     * 社团基础功能协议
     *
     * @param cm
     * @throws Exception
     */
    private void onCm_NewGuild(Cm_NewGuild cm) throws Exception {
        Sm_NewGuild.Builder b = Sm_NewGuild.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_NewGuild.Action.SYNC_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_SYNC);
                    sync();
                    break;
                case Cm_NewGuild.Action.CREATE_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_CREATE);
                    create(cm.getGuildName(), cm.getGuildIcon());
                    break;
                case Cm_NewGuild.Action.DISBAND_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_DISBAND);
                    disband();
                    break;
                case Cm_NewGuild.Action.JOIN_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_JOIN);
                    join(cm.getGuildId());
                    break;
                case Cm_NewGuild.Action.AGREE_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_AGREE);
                    agree(cm.getPlayerId());
                    break;
                case Cm_NewGuild.Action.REFUSE_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_REFUSE);
                    refuse(cm.getPlayerId());
                    break;
                case Cm_NewGuild.Action.KICK_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_KICK);
                    kick(cm.getPlayerId());
                    break;
                case Cm_NewGuild.Action.OUT_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_OUT);
                    out();
                    break;
                case Cm_NewGuild.Action.CHANGE_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_CHANGE);
                    change(cm.getJoinType(), cm.getRound());
                    break;
                case Cm_NewGuild.Action.APPOINT_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_APPOINT);
                    appoint(cm.getPlayerId(), cm.getJob());
                    break;
                case Cm_NewGuild.Action.SEARCH_ONE_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_SEARCH_ONE);
                    search(cm.getSearchArgs());
                    break;
                case Cm_NewGuild.Action.GET_APPLYS_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_SEARCH_ALL);
                    getApplys(cm.getRound());
                    break;
                case Cm_NewGuild.Action.SEARCH_ALL_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_SEARCH_ALL);
                    search(cm.getRound());
                    break;
                case Cm_NewGuild.Action.GET_MEMBER_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_GET_MEMBER);
                    getMember(cm.getRound());
                    break;
                case Cm_NewGuild.Action.CANCEL_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_CANCEL);
                    cancel(cm.getGuildId());
                    break;
                case Cm_NewGuild.Action.MODIFY_NOTIFY_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_MODIFY_NOTIFY);
                    modify(cm.getGuildNotice());
                    break;
                case Cm_NewGuild.Action.MODIFY_ICON_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_MODIFY_ICON);
                    modify(cm.getGuildIcon());
                    break;
                case Cm_NewGuild.Action.ONE_KEY_JOIN_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_ONE_KEY_JOIN);
                    oneKeyJoin(cm.getGuildId());
                    break;
                case Cm_NewGuild.Action.MAILS_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_MAILS);
                    mails(cm.getContent(), cm.getTitle());
                    break;
                case Cm_NewGuild.Action.GIVE_JOB_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_GIVE_JOB);
                    giveJob(cm.getPlayerId());
                    break;
                case Cm_NewGuild.Action.MODIFY_NAME_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_MODIFY_NAME);
                    modifyName(cm.getGuildName());
                    break;
                case Cm_NewGuild.Action.ONE_KEY_REFUSE_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_ONE_KEY_REFUSE);
                    oneKeyRefuse();
                    break;
                case Cm_NewGuild.Action.RECRUIT_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_RECRUIT);
                    recruit();
                    break;
                case Cm_NewGuild.Action.GET_TRACK_VALUE:
                    b.setAction(Sm_NewGuild.Action.RESP_GET_TRACK);
                    getTrack();
                    break;
            }
        } catch (BusinessLogicMismatchConditionException e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_NewGuild, b.getAction(), e.getErrorCodeEnum());
            getControlerForQuery().send(br.build());
            throw e;
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_NewGuild, b.getAction());
            getControlerForQuery().send(br.build());
            throw e;
        }

    }

    private void recruit() {
        getControlerForQuery().recruit();
    }

    private void oneKeyRefuse() {
        getControlerForQuery().oneKeyRefuse();
    }

    private void modifyName(String guildName) {
        getControlerForQuery().modifyName(guildName);
    }

    private void giveJob(String playerId) {
        getControlerForQuery().giveJob(playerId);
    }

    private void mails(String content, String title) {
        getControlerForQuery().mails(content, title);
    }

    private void appoint(String playerId, GuildJobEnum job) {
        getControlerForQuery().appoint(playerId, job);
    }

    private void oneKeyJoin(String guildId) {
        getControlerForQuery().oneKeyJoin(guildId);
    }

    private void modify(int guildIcon) {
        getControlerForQuery().modify(guildIcon);
    }

    private void modify(String guildNotice) {
        getControlerForQuery().modify(guildNotice);
    }

    private void cancel(String guildId) {
        getControlerForQuery().cancel(guildId);
    }

    private void getMember(Sm_Common_Round round) {
        getControlerForQuery().getMember(round);
    }

    private void getApplys(Sm_Common_Round round) {
        getControlerForQuery().getApplys(round);
    }

    private void search(String searchArgs) {
        getControlerForQuery().search(searchArgs);
    }

    private void search(Sm_Common_Round round) {
        getControlerForQuery().search(round);
    }

    private void change(GuildJoinTypeEnum joinTypeEnum, Sm_Common_Round round) {
        getControlerForQuery().change(joinTypeEnum, round);
    }

    private void disband() {
        getControlerForQuery().disband();
    }

    private void out() {
        getControlerForQuery().out();
    }

    private void kick(String playerId) {
        getControlerForQuery().kick(playerId);
    }

    private void refuse(String playerId) {
        getControlerForQuery().refuse(playerId);
    }

    private void agree(String playerId) {
        getControlerForQuery().agree(playerId);
    }

    private void join(String guildId) {
        getControlerForQuery().join(guildId);
    }

    private void create(String guildName, int guildIcon) {
        getControlerForQuery().create(guildName, guildIcon);
    }

    private void onPlayerLogined(In_PlayerLoginedRequest im_PlayerLogined) throws Exception {
        if (im_PlayerLogined.getPlayerId().equals(ownerCtrl.getTarget().getPlayerId())) {
            // 本玩家, 同步信息
            // sync();
        }
    }

    private void onDayChanged() throws Exception {
        getControlerForQuery()._resetDataAtDayChanged();
        getControlerForQuery().sync();
    }

    private void sync() {
        getControlerForQuery().sync();
    }


    public void getTrack() {
        getControlerForQuery().getTrack();
    }
}
