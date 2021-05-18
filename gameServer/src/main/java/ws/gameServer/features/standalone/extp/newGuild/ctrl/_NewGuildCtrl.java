package ws.gameServer.features.standalone.extp.newGuild.ctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
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
import ws.gameServer.features.standalone.extp.newGuild.utils.NewGuildCtrlProtos;
import ws.gameServer.features.standalone.extp.newGuild.utils.NewGuildCtrlUtils;
import ws.gameServer.features.standalone.extp.redPoint.msg.Pr_CheckRedPointMsg;
import ws.gameServer.features.standalone.extp.redPoint.msg.Pr_NotifyRedPointMsg;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.CommonProtos.Sm_Common_Round;
import ws.protos.EnumsProtos.ChatTypeEnum;
import ws.protos.EnumsProtos.GuildJobEnum;
import ws.protos.EnumsProtos.GuildJoinTypeEnum;
import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.protos.EnumsProtos.RedPointEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.NewGuildProtos.Sm_NewGuild;
import ws.protos.NewGuildProtos.Sm_NewGuild.Action;
import ws.protos.errorCode.ErrorCodeProtos.ErrorCodeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.chatServer.GroupChatMsg;
import ws.relationship.chatServer.SystemChatMsg;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.topLevelPojos.newGuild.NewGuildPlayer;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildApplyInfo;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenterPlayer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.BroadcastSystemMsgUtils;
import ws.relationship.utils.ProtoUtils;
import ws.relationship.utils.RelationshipCommonUtils;
import ws.relationship.utils.RelationshipCommonUtils.SortConditionValues;
import ws.relationship.utils.RelationshipCommonUtils.SortRuleEnum;
import ws.relationship.utils.SimplePojoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class _NewGuildCtrl extends AbstractPlayerExteControler<NewGuildPlayer> implements NewGuildCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_NewGuildCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
        getTarget().setGrabTimes(MagicNumbers.DEFAULT_ZERO);
        getTarget().setResearchTimes(MagicNumbers.DEFAULT_ZERO);
        Iterator<GuildRedBagTypeEnum> it = getTarget().getRedBagTypeAndCount().keySet().iterator();
        while (it.hasNext()) {
            getTarget().getRedBagTypeAndCount().put(it.next(), MagicNumbers.DEFAULT_ZERO);
        }
        _resetRedPoint();
    }


    @Override
    public void sync() {
        if (NewGuildCtrlUtils.hasGuild(target)) {
            NewGuild guild = NewGuildCtrlUtils.getGuildInfo(target, getPlayerCtrl().getSimplePlayerAfterUpdate());
            NewGuildCenterPlayer guildCenterPlayer = guild.getPlayerIdToGuildPlayerInfo().get(getPlayerCtrl().getPlayerId());
            SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_SYNC, (b, br) -> {
                b.setMemberCount(guild.getPlayerIdToGuildPlayerInfo().size());
                b.setGuildInfo(NewGuildCtrlProtos.createSm_NewGuild_GuildInfo_Whitout_Track(guild));
                b.setPlayerSelf(NewGuildCtrlProtos.createSm_NewGuild_SimplePlayerOne(getPlayerCtrl().getSimplePlayerAfterUpdate(), guildCenterPlayer));
                if (_getJob(getPlayerCtrl().getPlayerId(), guild) != GuildJobEnum.GJ_MEMBER) {
                    b.setApplysCount(_getApplys().size());
                }
            });
            return;
        }
        List<NewGuild> guildList = _getAllGuildInRealm();
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_SYNC, (b, br) -> {
            b.setAllGuildCount(guildList.size());
        });
    }

    @Override
    public String queryGuildId() {
        return target.getGuildId();
    }

    @Override
    public void create(String guildName, int guildIcon) {
        LogicCheckUtils.validateParam(String.class, guildName);
        LogicCheckUtils.validateParam(Integer.class, guildIcon);
        if (!_isLvCanCreate()) {
            String msg = "等级不足";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家已经有社团了";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        LogicCheckUtils.canRemove(itemIoCtrl, _getCreateNeedMoney());
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildCreate.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildCreate.Request(guildName, guildIcon, simplePlayer));
        NewGuild guild = response.getGuild();
        NewGuildCenterPlayer guildCenterPlayer = _getPlayerSelf(guild);
        _saveGuildPlayer(guild.getGuildId(), Sm_NewGuild.Action.RESP_CREATE);
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(Sm_NewGuild.Action.RESP_CREATE).removeItem(_getCreateNeedMoney());
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_CREATE, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
            b.setGuildInfo(NewGuildCtrlProtos.createSm_NewGuild_GuildInfo_Whitout_Track(guild));
            b.setMemberCount(guild.getPlayerIdToGuildPlayerInfo().size());
            b.setPlayerSelf(NewGuildCtrlProtos.createSm_NewGuild_SimplePlayerOne(simplePlayer, guildCenterPlayer));

        });
        save();
    }


    @Override
    public void join(String guildId) {
        LogicCheckUtils.validateParam(String.class, guildId);
        if (!_isLvCanJoin()) {
            String msg = "等级不足";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (_hasApplied(guildId)) {
            String msg = "已经申请过了";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家已经有社团了";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        In_NewGuildJoin.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildJoin.Request(guildId, getPlayerCtrl().getSimplePlayerAfterUpdate()));
        _addApplyGuildIds(guildId, response.isIn());
        NewGuildCenterPlayer guildCenterPlayer = _getPlayerSelf(response.getGuild());
        getPlayerCtrl().updateSimplePlayer();
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_JOIN, (b, br) -> {
            if (!response.isIn()) {
                b.setSimpleGuildInfoOne(NewGuildCtrlProtos.createSm_NewGuild_SimpleGuildInfo(response.getGuild(), target.getApplyGuildIds()));
            } else {
                b.setSimpleGuildInfoOne(NewGuildCtrlProtos.createSm_NewGuild_SimpleGuildInfo(response.getGuild(), target.getApplyGuildIds()));
                b.setGuildInfo(NewGuildCtrlProtos.createSm_NewGuild_GuildInfo_Whitout_Track(response.getGuild()));
                b.setPlayerSelf(NewGuildCtrlProtos.createSm_NewGuild_SimplePlayerOne(getPlayerCtrl().getSimplePlayerAfterUpdate(), guildCenterPlayer));
                b.setMemberCount(response.getGuild().getPlayerIdToGuildPlayerInfo().size());
            }
        });
        save();
    }


    @Override
    public void agree(String playerId) {
        LogicCheckUtils.validateParam(String.class, playerId);
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        SimplePlayer addSimplePlayer = _getSimplePlayerById(playerId, simplePlayer.getOutRealmId());
        In_NewGuildAgree.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildAgree.Request(target.getGuildId(), simplePlayer, addSimplePlayer));
        _saveGuildPlayer(response.getGuild().getGuildId(), Sm_NewGuild.Action.RESP_AGREE);
        NewGuild guild = response.getGuild();
        List<NewGuildApplyInfo> applyInfoList = _getApplys();
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_AGREE, (b, br) -> {
            b.setGuildInfo(NewGuildCtrlProtos.createSm_NewGuild_GuildInfo_Whitout_Track(guild));
            b.setApplysCount(applyInfoList.size());
            b.setPlayerId(playerId);
        });
        save();
    }

    @Override
    public void agreeResponse(In_NewGuildAgree.Response response) {
        LogicCheckUtils.checkResponse(response);
        _saveGuildPlayer(response.getGuild().getGuildId(), Sm_NewGuild.Action.RESP_BE_AGREE);
        NewGuild guild = response.getGuild();
        NewGuildCenterPlayer beGuildCenterPlayer = guild.getPlayerIdToGuildPlayerInfo().get(getPlayerCtrl().getPlayerId());
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_BE_AGREE, (b, br) -> {
            b.setGuildInfo(NewGuildCtrlProtos.createSm_NewGuild_GuildInfo_Whitout_Track(guild));
            b.setMemberCount(guild.getPlayerIdToGuildPlayerInfo().size());
            b.setPlayerSelf(NewGuildCtrlProtos.createSm_NewGuild_SimplePlayerOne(getPlayerCtrl().getSimplePlayerAfterUpdate(), beGuildCenterPlayer));
        });
        save();
    }


    @Override
    public void cancel(String guildId) {
        if (!_isInApply(guildId)) {
            String msg = "不再申请列表中";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        if (NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家已经有社团了";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildCancel.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildCancel.Request(guildId, simplePlayer));
        _saveGuildPlayer(guildId, Sm_NewGuild.Action.RESP_CANCEL);
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_CANCEL, (b, br) -> {
            b.setSimpleGuildInfoOne(NewGuildCtrlProtos.createSm_NewGuild_SimpleGuildInfo(response.getGuild(), target.getApplyGuildIds()));
        });

    }


    private boolean _isInApply(String guildId) {
        return target.getApplyGuildIds().contains(guildId);
    }

    @Override
    public void modify(String guildNotice) {
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildModify.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(In_NewGuildModify.Request.createModifyNoticMsg(simplePlayer, guildNotice));
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_MODIFY_NOTIFY, (b, br) -> {
            b.setGuildInfo(NewGuildCtrlProtos.createSm_NewGuild_GuildInfo_Whitout_Track(response.getGuild()));
        });
    }

    @Override
    public void modify(int guildIcon) {
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildModify.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(In_NewGuildModify.Request.createModifyIconMsg(simplePlayer, guildIcon));
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_MODIFY_ICON, (b, br) -> {
            b.setGuildInfo(NewGuildCtrlProtos.createSm_NewGuild_GuildInfo_Whitout_Track(response.getGuild()));
        });
    }

    @Override
    public void modifyName(String guildName) {
        LogicCheckUtils.validateParam(String.class, guildName);
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildModify.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(In_NewGuildModify.Request.createModifyNameMsg(simplePlayer, guildName));
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_MODIFY_NAME, (b, br) -> {
            b.setSimpleGuildInfoOne(NewGuildCtrlProtos.createSm_NewGuild_SimpleGuildInfo(response.getGuild(), target.getApplyGuildIds()));
        });
    }

    @Override
    public void oneKeyJoin(String guildId) {
        if (NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家已经有社团了";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildOenKeyJoin.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildOenKeyJoin.Request(simplePlayer));
        NewGuild guild = response.getGuild();
        NewGuildCenterPlayer guildCenterPlayer = guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId());
        _saveAgree(response.getGuild().getGuildId());
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_ONE_KEY_JOIN, (b, br) -> {
            b.setGuildInfo(NewGuildCtrlProtos.createSm_NewGuild_GuildInfo_Whitout_Track(guild));
            b.setMemberCount(response.getGuild().getPlayerIdToGuildPlayerInfo().size());
            b.setPlayerSelf(NewGuildCtrlProtos.createSm_NewGuild_SimplePlayerOne(simplePlayer, guildCenterPlayer));
        });
    }


    @Override
    public void appoint(String playerId, GuildJobEnum job) {
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        if (!_isJobCanAppoint(job)) {
            String msg = String.format("这个职务不能任命:%s", job);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        SimplePlayer appointSimplePlayer = SimplePojoUtils.querySimplePlayerById(playerId, simplePlayer.getOutRealmId());
        In_NewGuildAppoint.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildAppoint.Request(simplePlayer, appointSimplePlayer, job));
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_APPOINT, (b, br) -> {
            b.setGuildSimplePlayerOne(NewGuildCtrlProtos.createSm_NewGuild_SimplePlayerOne(appointSimplePlayer, response.getGuildCenterPlayer()));
        });
    }

    private boolean _isJobCanAppoint(GuildJobEnum jobEnum) {
        return jobEnum != GuildJobEnum.GJ_MASTER;
    }

    @Override
    public void appointResponse(In_NewGuildAppoint.Response response) {
        LogicCheckUtils.checkResponse(response);
        NewGuild guild = response.getGuild();
        SimplePlayer beAppointSimplePlayer = response.getRequest().getAppointSimplePlayer();
        NewGuildCenterPlayer guildCenterPlayer = response.getGuildCenterPlayer();
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_BE_APPOINT, (b, br) -> {
            b.setGuildSimplePlayerOne(NewGuildCtrlProtos.createSm_NewGuild_SimplePlayerOne(beAppointSimplePlayer, guildCenterPlayer));
            b.setPlayerSelf(NewGuildCtrlProtos.createSm_NewGuild_SimplePlayerOne(beAppointSimplePlayer, guildCenterPlayer));
            b.setGuildInfo(NewGuildCtrlProtos.createSm_NewGuild_GuildInfo_Whitout_Track(guild));
        });
    }

    @Override
    public void oneKeyRefuse() {
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildOneKeyRefuse.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildOneKeyRefuse.Request(simplePlayer));
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_ONE_KEY_REFUSE, (b, br) -> {

        });
    }

    @Override
    public void recruit() {
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildRecruit.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildRecruit.Request(simplePlayer));
        _broadcastSystemMsg_Recruit(response.getGuild());
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_RECRUIT, (b, br) -> {

        });
    }

    @Override
    public void giveJob(String givePlayerId) {
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        SimplePlayer giveSimplePlayer = SimplePojoUtils.querySimplePlayerById(givePlayerId, simplePlayer.getOutRealmId());

        In_NewGuildGiveJob.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildGiveJob.Request(simplePlayer, giveSimplePlayer));
        NewGuild guild = response.getGuild();
        List<NewGuildCenterPlayer> guildCenterPlayerList = new ArrayList<>();
        guildCenterPlayerList.add(response.getGuildCenterPlayer());
        guildCenterPlayerList.add(response.getBeGiveJobGuildCenterPlayer());
        Map<String, SimplePlayer> idToSimplePlayer = new HashMap<>();
        idToSimplePlayer.put(simplePlayer.getPlayerId(), simplePlayer);
        idToSimplePlayer.put(giveSimplePlayer.getPlayerId(), giveSimplePlayer);
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_GIVE_JOB, (b, br) -> {
            b.setPlayerSelf(NewGuildCtrlProtos.createSm_NewGuild_SimplePlayerOne(simplePlayer, response.getGuildCenterPlayer()));
            b.addAllGuildSimplePlayer(NewGuildCtrlProtos.createSm_NewGuild_SimplePlayer_List(idToSimplePlayer, guildCenterPlayerList));
            b.setGuildInfo(NewGuildCtrlProtos.createSm_NewGuild_GuildInfo_Whitout_Track(guild));
        });
    }

    @Override
    public void giveJobResponse(In_NewGuildGiveJob.Response response) {
        LogicCheckUtils.checkResponse(response);
        NewGuild guild = response.getGuild();
        List<NewGuildCenterPlayer> guildCenterPlayerList = new ArrayList<>();
        guildCenterPlayerList.add(response.getGuildCenterPlayer());
        guildCenterPlayerList.add(response.getBeGiveJobGuildCenterPlayer());
        Map<String, SimplePlayer> idToSimplePlayer = new HashMap<>();
        idToSimplePlayer.put(response.getRequest().getSimplePlayer().getPlayerId(), response.getRequest().getSimplePlayer());
        idToSimplePlayer.put(response.getRequest().getGiveSimplePlayer().getPlayerId(), response.getRequest().getGiveSimplePlayer());
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_BE_GIVE_JOB, (b, br) -> {
            b.setPlayerSelf(NewGuildCtrlProtos.createSm_NewGuild_SimplePlayerOne(response.getRequest().getGiveSimplePlayer(), response.getBeGiveJobGuildCenterPlayer()));
            b.addAllGuildSimplePlayer(NewGuildCtrlProtos.createSm_NewGuild_SimplePlayer_List(idToSimplePlayer, guildCenterPlayerList));
            b.setGuildInfo(NewGuildCtrlProtos.createSm_NewGuild_GuildInfo_Whitout_Track(guild));
        });
    }


    @Override
    public void mails(String content, String title) {
        LogicCheckUtils.validateParam(String.class, content);
        LogicCheckUtils.validateParam(String.class, title);
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildMails.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildMails.Request(simplePlayer, content, title));
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_MAILS, (b, br) -> {
            b.setSimpleGuildInfoOne(NewGuildCtrlProtos.createSm_NewGuild_SimpleGuildInfo(response.getGuild(), target.getApplyGuildIds()));
        });
    }


    @Override
    public void kickResponse(In_NewGuildKick.Response response) {
        LogicCheckUtils.checkResponse(response);
        _saveGuildPlayer(response.getRequest().getGuildId(), Sm_NewGuild.Action.RESP_BE_KICK);
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_BE_KICK, (b, br) -> {

        });
        save();
    }


    @Override
    public void refuse(String playerId) {
        LogicCheckUtils.validateParam(String.class, playerId);
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        SimplePlayer refuseSimplePlayer = _getSimplePlayerById(playerId, simplePlayer.getOutRealmId());
        In_NewGuildRefuse.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildRefuse.Request(target.getGuildId(), simplePlayer, refuseSimplePlayer));
        List<NewGuildApplyInfo> applyInfoList = _getApplys();
        _saveGuildPlayer(response.getRequest().getGuildId(), Sm_NewGuild.Action.RESP_REFUSE);
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_REFUSE, (b, br) -> {
            b.setPlayerId(playerId);
            b.setApplysCount(applyInfoList.size());
        });
        save();
    }


    @Override
    public void kick(String playerId) {
        LogicCheckUtils.validateParam(String.class, playerId);
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        SimplePlayer kickPlayer = _getSimplePlayerById(playerId, simplePlayer.getOutRealmId());
        In_NewGuildKick.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildKick.Request(target.getGuildId(), simplePlayer, kickPlayer));
        LogicCheckUtils.checkResponse(response);
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_KICK, (b, br) -> {
            b.setPlayerId(playerId);
        });
    }


    @Override
    public void out() {
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildOut.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildOut.Request(target.getGuildId(), simplePlayer));
        _saveGuildPlayer(response.getRequest().getGuildId(), Sm_NewGuild.Action.RESP_OUT);
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_OUT, (b, br) -> {

        });
        save();
    }

    @Override
    public void disband() {
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildDisband.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildDisband.Request(target.getGuildId(), simplePlayer));
        LogicCheckUtils.checkResponse(response);
        _saveGuildPlayer(target.getGuildId(), Sm_NewGuild.Action.RESP_DISBAND);
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_DISBAND, (b, br) -> {

        });
        save();
    }


    @Override
    public void change(GuildJoinTypeEnum joinTypeEnum, Sm_Common_Round round) {
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildChange.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildChange.Request(target.getGuildId(), simplePlayer, joinTypeEnum, round));
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_CHANGE, (b, br) -> {
            b.setGuildInfo(NewGuildCtrlProtos.createSm_NewGuild_GuildInfo_Whitout_Track(response.getGuild()));
        });
        save();
    }

    @Override
    public void search(String searchArgs) {
        LogicCheckUtils.validateParam(String.class, searchArgs);
        if (NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家已经有社团，不能搜索";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildSearch.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildSearch.Request(simplePlayer, searchArgs));
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_SEARCH_ONE, (b, br) -> {
            b.setSimpleGuildInfoOne(NewGuildCtrlProtos.createSm_NewGuild_SimpleGuildInfo(response.getGuild(), target.getApplyGuildIds()));
        });
    }


    @Override
    public void search(Sm_Common_Round round) {
        if (NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家已经有社团，不能搜索";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        List<NewGuild> guildList = _getAllGuildInRealm();
        NewGuildCtrlUtils.sortGuild(guildList);
        List<NewGuild> roundList = _getGuildByRound(round.getMin(), round.getMax(), guildList);
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_SEARCH_ALL, (b, br) -> {
            b.addAllSimpleGuildInfo(NewGuildCtrlProtos.createSm_NewGuild_SimpleGuildInfo_List(roundList, target.getApplyGuildIds()));
            b.setAllGuildCount(guildList.size());
            b.setRound(ProtoUtils.create_Sm_Common_Round(round.getMin(), roundList.size()));
        });
    }


    @Override
    public void getApplys(Sm_Common_Round round) {
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团，没有审核列表";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        List<NewGuildApplyInfo> applyInfoList = _getApplys();
        List<String> playerIds = _getPlayerIdFromApplyInfoList(applyInfoList);
        Map<String, SimplePlayer> idToSimplePlayers = SimplePojoUtils.querySimplePlayerLisByIds(playerIds, getPlayerCtrl().getSimplePlayerAfterUpdate().getOutRealmId());
        _sortApplyInfo(applyInfoList, idToSimplePlayers);
        List<NewGuildApplyInfo> roundList = _getApplyInfoByRound(round.getMin(), round.getMax(), applyInfoList);
        List<String> sortPlayerIds = _getPlayerIdFromApplyInfoList(roundList);
        Map<String, SimplePlayer> sortIdToSimplePlayers = SimplePojoUtils.querySimplePlayerLisByIds(sortPlayerIds, getPlayerCtrl().getSimplePlayerAfterUpdate().getOutRealmId());
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_GET_APPLYS, (b, br) -> {
            b.addAllApplyInfos(NewGuildCtrlProtos.createSm_NewGuild_ApplyInfo_List(roundList, sortIdToSimplePlayers));
            b.setApplysCount(applyInfoList.size());
        });
    }


    @Override
    public void getMember(Sm_Common_Round round) {
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团，没有社团成员";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        NewGuild guild = NewGuildCtrlUtils.getGuildInfo(target, getPlayerCtrl().getSimplePlayerAfterUpdate());
        List<NewGuildCenterPlayer> roundGuildCenterPlayerList = new ArrayList<>(guild.getPlayerIdToGuildPlayerInfo().values());
        sortMember(roundGuildCenterPlayerList);
        List<NewGuildCenterPlayer> roundList = NewGuildCtrlUtils.getMemberByRound(round.getMin(), round.getMax(), roundGuildCenterPlayerList);
        Map<String, SimplePlayer> idToSimplePlayers = NewGuildCtrlUtils.getStringSimplePlayerMap(roundList, getPlayerCtrl().getOuterRealmId());
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_GET_MEMBER, (b, br) -> {
            b.setMemberCount(guild.getPlayerIdToGuildPlayerInfo().size());
            b.setGuildInfo(NewGuildCtrlProtos.createSm_NewGuild_GuildInfo_Whitout_Track(guild));
            b.addAllGuildSimplePlayer(NewGuildCtrlProtos.createSm_NewGuild_SimplePlayer_List(idToSimplePlayers, roundList));
            if (_getJob(getPlayerCtrl().getPlayerId(), guild) != GuildJobEnum.GJ_MEMBER) {
                b.setApplysCount(_getApplys().size());
            }
        });

    }

    @Override
    public void getTrack() {
        if (!NewGuildCtrlUtils.hasGuild(target)) {
            String msg = "玩家没有社团，没有社团成员";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        NewGuild guild = NewGuildCtrlUtils.getGuildInfo(target, getPlayerCtrl().getSimplePlayerAfterUpdate());
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_GET_TRACK, (b, br) -> {
            b.setGuildInfo(NewGuildCtrlProtos.createSm_NewGuild_GuildInfo(guild));
        });
    }

    @Override
    public void refuseResponse(In_NewGuildRefuse.Response response) {
        LogicCheckUtils.checkResponse(response);
        _saveGuildPlayer(response.getRequest().getGuildId(), Sm_NewGuild.Action.RESP_REFUSE);
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_REFUSE, (b, br) -> {
        });
        save();
    }

    @Override
    public void disbandResponse(In_NewGuildDisband.Response response) {
        LogicCheckUtils.checkResponse(response);
        _saveGuildPlayer(target.getGuildId(), Sm_NewGuild.Action.RESP_DISBAND);
        SimplePlayer simplePlayer = getPlayerCtrl().getSimplePlayerAfterUpdate();
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_DISBAND, (b, br) -> {

        });
        save();
    }

    @Override
    public void oneKeyRefuseResponse(In_NewGuildOneKeyRefuse.Response response) {
        LogicCheckUtils.checkResponse(response);
        String guildId = response.getRequest().getSimplePlayer().getGuildId();
        _saveGuildPlayer(guildId, Sm_NewGuild.Action.RESP_REFUSE);
        SenderFunc.sendInner(this, Sm_NewGuild.class, Sm_NewGuild.Builder.class, Sm_NewGuild.Action.RESP_REFUSE, (b, br) -> {

        });
        save();
    }

    @Override
    public void checkRedPointMsg(Pr_CheckRedPointMsg.Request privateMsg) {

    }


    /*
    ============================================================以下为内部方法==============================================================
     */

    /**
     * 重置红点
     */
    private void _resetRedPoint() {
        HashMap<RedPointEnum, Boolean> redPointToShow = new HashMap<>();
        redPointToShow.put(RedPointEnum.GUILD_RESEARCH, true);
        redPointToShow.put(RedPointEnum.GUILD_TRAINER, true);
        Pr_NotifyRedPointMsg.Request request = new Pr_NotifyRedPointMsg.Request(redPointToShow);
        sendPrivateMsg(request);
    }


    private void _sortApplyInfo(List<NewGuildApplyInfo> applyInfoList, Map<String, SimplePlayer> idToSimplePlayers) {
        RelationshipCommonUtils.mutliConditionSort(applyInfoList, new SortConditionValues<NewGuildApplyInfo>() {
            @Override
            public long[] compareValues(NewGuildApplyInfo o1, NewGuildApplyInfo o2) {
                long[] v = new long[3];
                v[0] = o1.getApplyTime() - o2.getApplyTime();
                v[1] = idToSimplePlayers.get(o1.getPlayerId()).getBattleValue() - idToSimplePlayers.get(o2.getPlayerId()).getBattleValue();
                v[2] = idToSimplePlayers.get(o1.getPlayerId()).getLv() - idToSimplePlayers.get(o2.getPlayerId()).getLv();
                return v;
            }
        }, SortRuleEnum.ESC);
    }

    private NewGuildCenterPlayer _getPlayerSelf(NewGuild guild) {
        return guild.getPlayerIdToGuildPlayerInfo().get(getPlayerCtrl().getPlayerId());
    }

    private void _broadcastSystemMsg_Recruit(NewGuild guild) {
        SystemChatMsg systemChatMsg = new SystemChatMsg(MagicNumbers.SYSTEM_BROADCAST_RECRUIT,
                getPlayerCtrl().getTarget().getBase().getName(),
                String.valueOf(guild.getLimitLevel())
        );
        GroupChatMsg groupChatMsg = new GroupChatMsg(ChatTypeEnum.CHAT_SYSTEM, systemChatMsg);

        BroadcastSystemMsgUtils.broad(getPlayerCtrl().getContext(), getPlayerCtrl().getTarget().getAccount().getInnerRealmId(), groupChatMsg);
    }


    /**
     * 根据范围获取列表中的工会
     *
     * @param min
     * @param max
     * @param guildList
     * @return
     */
    private List<NewGuild> _getGuildByRound(int min, int max, List<NewGuild> guildList) {
        List<NewGuild> roundList = new ArrayList<>();
        if (guildList.size() == 0) {
            return roundList;
        }
        if (min < 0) {
            min = 0;
        }
        if (min > guildList.size() - 1) {
            min = guildList.size() - 1;
        }
        if (max == 0) {
            max = 1;
        }
        if (max >= guildList.size()) {
            max = guildList.size() - 1;
        }
        for (int i = min; i <= max; i++) {
            roundList.add(guildList.get(i));
        }
        return roundList;
    }


    private List<NewGuildApplyInfo> _getApplyInfoByRound(int min, int max, List<NewGuildApplyInfo> applyInfoList) {
        List<NewGuildApplyInfo> roundList = new ArrayList<>();
        if (applyInfoList.size() == 0) {
            return roundList;
        }
        if (min < 0) {
            min = 0;
        }
        if (min > applyInfoList.size() - 1) {
            min = applyInfoList.size() - 1;
        }
        if (max == 0) {
            max = 1;
        }
        if (max >= applyInfoList.size()) {
            max = applyInfoList.size() - 1;
        }
        for (int i = min; i <= max; i++) {
            roundList.add(applyInfoList.get(i));
        }
        return roundList;
    }


    private List<String> _getPlayerIdFromApplyInfoList(List<NewGuildApplyInfo> applyInfoList) {
        List<String> playerIdList = new ArrayList<>();
        for (NewGuildApplyInfo apply : applyInfoList) {
            playerIdList.add(apply.getPlayerId());
        }
        return playerIdList;
    }


    /**
     * 社团成员排序
     *
     * @param
     */
    private void sortMember(List<NewGuildCenterPlayer> guildCenterPlayerList) {
        RelationshipCommonUtils.mutliConditionSort(guildCenterPlayerList, new SortConditionValues<NewGuildCenterPlayer>() {
            @Override
            public long[] compareValues(NewGuildCenterPlayer o1, NewGuildCenterPlayer o2) {
                long[] v = new long[4];
                v[0] = -(o1.getJob().getNumber() - o2.getJob().getNumber()); //由于职位高的枚举数值小，所以这里取负
                v[1] = o1.getSumContribution() - o2.getSumContribution();
                v[2] = o1.getLoginTime() - o2.getLoginTime();
                v[3] = o1.getLv() - o2.getLv();
                return v;
            }
        }, SortRuleEnum.ESC);
    }


    private GuildJobEnum _getJob(String playerId, NewGuild guild) {
        Map<String, NewGuildCenterPlayer> playerIdToGuildPlayerInfo = guild.getPlayerIdToGuildPlayerInfo();
        NewGuildCenterPlayer guildCenterPlayer = playerIdToGuildPlayerInfo.get(playerId);
        if (guildCenterPlayer == null) {
            String msg = "未找到玩家社团信息";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        return guildCenterPlayer.getJob();

    }

    private List<NewGuild> _getAllGuildInRealm() {
        In_NewGuildGetInfo.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildGetInfo.Request(getPlayerCtrl().getSimplePlayerAfterUpdate()));
        List<NewGuild> guildList = response.getGuildList();
        return guildList;
    }

    private List<NewGuildApplyInfo> _getApplys() {
        In_NewGuildGetApplys.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildGetApplys.Request(target.getGuildId(), getPlayerCtrl().getSimplePlayerAfterUpdate()));
        List<NewGuildApplyInfo> applyInfos = response.getApplyList();
        return applyInfos;

    }

    private SimplePlayer _getSimplePlayerById(String playerId, int outRealmId) {
        List<String> playerIds = new ArrayList<>();
        playerIds.add(playerId);
        Map<String, SimplePlayer> idToSimplePlayer = SimplePojoUtils.querySimplePlayerLisByIds(playerIds, outRealmId);
        return idToSimplePlayer.get(playerId);
    }

    private boolean _hasApplied(String guildId) {
        return target.getApplyGuildIds().contains(guildId);
    }

    /**
     * 是否进入了社团/加入申请列表
     *
     * @param guildId
     * @param in
     */
    private void _addApplyGuildIds(String guildId, boolean in) {
        if (in) {
            target.getApplyGuildIds().clear();
            target.setGuildId(guildId);
        } else {
            target.getApplyGuildIds().add(guildId);
        }
    }


    private boolean _isLvCanJoin() {
        int needLv = AllServerConfig.Open_Guild_Lv.getConfig();
        return getPlayerCtrl().getCurLevel() >= needLv;
    }

    private void _saveGuildPlayer(String guildId, Action action) {
        switch (action) {
            case RESP_CREATE:
            case RESP_AGREE:
                _saveAgree(guildId);
                break;
            case RESP_BE_AGREE:
                _saveAgree(guildId);
                break;
            case RESP_REFUSE:
                _saveRefuse(guildId);
                break;
            case RESP_KICK:
                _saveKick();
                break;
            case RESP_BE_KICK:
                _saveKick();
                break;
            case RESP_OUT:
                _saveOut();
                break;
            case RESP_DISBAND:
                _saveDisband();
                break;
            case RESP_CANCEL:
                _saveCancel(guildId);
                break;
        }
    }

    private void _saveCancel(String guildId) {
        target.getApplyGuildIds().removeIf(listGuildId -> listGuildId.equals(guildId));
    }


    private void _saveDisband() {
        _removeGuild();
    }

    private void _saveOut() {
        _removeGuild();
        _saveExitTime();
    }

    private void _saveExitTime() {
        target.setExitTime(System.currentTimeMillis());
    }

    private void _saveKick() {
        _removeGuild();
        _saveExitTime();
    }

    private void _removeGuild() {
        target.setGuildId(null);
    }

    private void _saveAgree(String guildId) {
        target.setGuildId(guildId);
        target.getApplyGuildIds().clear();
    }

    private void _saveRefuse(String guildId) {
        target.getApplyGuildIds().removeIf(applyGuildId -> applyGuildId.equals(guildId));
    }


    private IdAndCount _getCreateNeedMoney() {
        int needMoney = AllServerConfig.Create_Guild_Vip_Money.getConfig();
        return new IdAndCount(ResourceTypeEnum.RES_VIPMONEY.getNumber(), needMoney);
    }

    private boolean _isLvCanCreate() {
        int needLv = AllServerConfig.Create_Guild_Lv.getConfig();
        return getPlayerCtrl().getCurLevel() >= needLv;
    }


}
