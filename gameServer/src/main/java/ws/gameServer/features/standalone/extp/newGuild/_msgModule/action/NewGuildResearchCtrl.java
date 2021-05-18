package ws.gameServer.features.standalone.extp.newGuild._msgModule.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.newGuild.ctrl.NewGuildCtrl;
import ws.gameServer.features.standalone.extp.newGuild.msg.institute.In_NewGuildResearch;
import ws.gameServer.features.standalone.extp.newGuild.utils.NewGuildCtrlProtos;
import ws.gameServer.features.standalone.extp.newGuild.utils.NewGuildCtrlUtils;
import ws.gameServer.features.standalone.extp.redPoint.msg.Pr_CheckRedPointMsg;
import ws.gameServer.features.standalone.extp.redPoint.msg.Pr_CheckRedPointMsg.Request;
import ws.gameServer.features.standalone.extp.utils.ClientMsgActionStateful;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.EnumsProtos.GuildInstituteTypeEnum;
import ws.protos.EnumsProtos.GuildResearchProjectTypeEnum;
import ws.protos.EnumsProtos.GuildResearchTypeEnum;
import ws.protos.EnumsProtos.RedPointEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.NewGuildResearchProtos.Cm_NewGuildResearch;
import ws.protos.NewGuildResearchProtos.Sm_NewGuildResearch;
import ws.protos.errorCode.ErrorCodeProtos.ErrorCodeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.topLevelPojos.newGuild.NewGuildPlayer;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.ProtoUtils;

import java.util.HashMap;

public class NewGuildResearchCtrl implements ClientMsgActionStateful<NewGuildCtrl, Cm_NewGuildResearch> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewGuildResearchCtrl.class);
    private ItemIoCtrl itemIoCtrl;
    private ItemIoExtp itemIoExtp;


    @Override
    public void onRecv(Cm_NewGuildResearch cm, NewGuildCtrl exteControler) {
        itemIoExtp = exteControler.getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        Sm_NewGuildResearch.Builder b = Sm_NewGuildResearch.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_NewGuildResearch.Action.SYNC_RESEARCH_VALUE:
                    b.setAction(Sm_NewGuildResearch.Action.RESP_SYNC_RESEARCH);
                    syncResearch(exteControler);
                    break;
                case Cm_NewGuildResearch.Action.RESEARCH_VALUE:
                    b.setAction(Sm_NewGuildResearch.Action.RESP_RESEARCH);
                    research(cm.getInstituteType(), cm.getResearchProjectType(), cm.getResearchType(), exteControler);
                    break;
            }
        } catch (BusinessLogicMismatchConditionException e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_NewGuildResearch, b.getAction(), e.getErrorCodeEnum());
            exteControler.send(br.build());
            throw e;
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_NewGuildResearch, b.getAction());
            exteControler.send(br.build());
            throw e;
        }

    }

    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg, NewGuildCtrl exteControler) {

    }

    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg, NewGuildCtrl exteControler) {
        if (privateMsg instanceof Pr_CheckRedPointMsg.Request) {
            onCheckRedPointMsg((Pr_CheckRedPointMsg.Request) privateMsg, exteControler);
        }
    }

    private void onCheckRedPointMsg(Request privateMsg, NewGuildCtrl exteControler) {
        if (privateMsg.getRedPointEnum() == null || privateMsg.getRedPointEnum() == RedPointEnum.GUILD_RESEARCH) {
            HashMap<RedPointEnum, Boolean> redPointToShow = _checkRedPoint(exteControler);
            Pr_CheckRedPointMsg.Response response = new Pr_CheckRedPointMsg.Response(privateMsg, redPointToShow);
            exteControler.sendPrivateMsg(response);
        }
    }


    private HashMap<RedPointEnum, Boolean> _checkRedPoint(NewGuildCtrl exteControler) {
        HashMap<RedPointEnum, Boolean> redPointToShow = new HashMap<>();
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            redPointToShow.put(RedPointEnum.GUILD_RESEARCH, false);
            return redPointToShow;
        }
        // TODO 需要策划给 具体什么时候出红点的逻辑，目前逻辑是捐献次数是满的时候就出现红点
        redPointToShow.put(RedPointEnum.GUILD_RESEARCH, exteControler.getTarget().getResearchTimes() == MagicNumbers.DEFAULT_ZERO);
        return redPointToShow;

    }

    private void syncResearch(NewGuildCtrl exteControler) {
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = _getSimplePlayer(exteControler);
        NewGuild guild = NewGuildCtrlUtils.getGuildInfo(exteControler.getTarget(), simplePlayer);
        SenderFunc.sendInner(exteControler, Sm_NewGuildResearch.class, Sm_NewGuildResearch.Builder.class, Sm_NewGuildResearch.Action.RESP_SYNC_RESEARCH, (b, br) -> {
            b.setTodayExp(guild.getTodayExp());
            b.setResearchTimes(guildPlayer.getResearchTimes());
            b.addAllInstitutes(NewGuildCtrlProtos.createSm_NewGuildResearch_Institute_List(guild.getTypeAndInstitute()));
        });
    }


    private void research(GuildInstituteTypeEnum instituteType, GuildResearchProjectTypeEnum researchProjectType, GuildResearchTypeEnum researchType, NewGuildCtrl exteControler) {
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        if (!_isTimesCanResearch(exteControler.getTarget())) {
            String msg = String.format("捐献次数已经用光了:%s", exteControler.getTarget().getResearchTimes());
            throw new BusinessLogicMismatchConditionException(msg);
        }
        IdAndCount consume = NewGuildCtrlUtils.getConsumeByResearchType(researchType);
        LogicCheckUtils.canRemove(itemIoCtrl, consume);
        SimplePlayer simplePlayer = _getSimplePlayer(exteControler);
        In_NewGuildResearch.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildResearch.Request(simplePlayer, instituteType, researchProjectType, researchType));
        NewGuild guild = response.getGuild();
        guildPlayer.setResearchTimes(guildPlayer.getResearchTimes() + MagicNumbers.DEFAULT_ONE);
        IdAndCount reward = NewGuildCtrlUtils.getRewardByResearchType(researchType);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Sm_NewGuildResearch.Action.RESP_RESEARCH).addItem(reward);
        IdMaptoCount refresh_2 = itemIoExtp.getControlerForUpdate(Sm_NewGuildResearch.Action.RESP_RESEARCH).removeItem(consume);
        SenderFunc.sendInner(exteControler, Sm_NewGuildResearch.class, Sm_NewGuildResearch.Builder.class, Sm_NewGuildResearch.Action.RESP_RESEARCH, (b, br) -> {
            if (instituteType == GuildInstituteTypeEnum.GI_LV) {
                b.setTodayExp(guild.getTodayExp());
            }
            b.setResearchTimes(guildPlayer.getResearchTimes());
            b.setInstituteType(instituteType);
            b.setProject(NewGuildCtrlProtos.createSm_NewGuildResearch_Institute_ProjectByResearchType(guild.getTypeAndInstitute().get(instituteType).getResearchTypeAndProject(), researchProjectType));
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).addAll(refresh_2), br);
        });
    }


    private SimplePlayer _getSimplePlayer(NewGuildCtrl exteControler) {
        return exteControler.getPlayerCtrl().getSimplePlayerAfterUpdate();
    }


    private boolean _isTimesCanResearch(NewGuildPlayer guildPlayer) {
        int maxTimes = AllServerConfig.Guild_Max_Research_Times.getConfig();
        return maxTimes > guildPlayer.getResearchTimes();
    }
}
