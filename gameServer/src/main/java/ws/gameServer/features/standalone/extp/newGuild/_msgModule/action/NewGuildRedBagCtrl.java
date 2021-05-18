package ws.gameServer.features.standalone.extp.newGuild._msgModule.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.newGuild.ctrl.NewGuildCtrl;
import ws.gameServer.features.standalone.extp.newGuild.msg.redBag.In_NewGuildRedBagGetRedBagList;
import ws.gameServer.features.standalone.extp.newGuild.msg.redBag.In_NewGuildRedBagGrab;
import ws.gameServer.features.standalone.extp.newGuild.msg.redBag.In_NewGuildRedBagGrab.Request;
import ws.gameServer.features.standalone.extp.newGuild.msg.redBag.In_NewGuildRedBagRank;
import ws.gameServer.features.standalone.extp.newGuild.msg.redBag.In_NewGuildRedBagSend;
import ws.gameServer.features.standalone.extp.newGuild.msg.redBag.In_NewGuildRedBagSync;
import ws.gameServer.features.standalone.extp.newGuild.utils.NewGuildCtrlProtos;
import ws.gameServer.features.standalone.extp.newGuild.utils.NewGuildCtrlUtils;
import ws.gameServer.features.standalone.extp.redPoint.msg.In_NotifyRedPointMsg;
import ws.gameServer.features.standalone.extp.redPoint.msg.Pr_CheckRedPointMsg;
import ws.gameServer.features.standalone.extp.redPoint.msg.Pr_NotifyRedPointMsg;
import ws.gameServer.features.standalone.extp.utils.ClientMsgActionStateful;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.protos.EnumsProtos.GuildSendRedBagTypeEnum;
import ws.protos.EnumsProtos.RedPointEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.NewGuildRedBagProtos.Cm_NewGuildRedBag;
import ws.protos.NewGuildRedBagProtos.Sm_NewGuildRedBag;
import ws.protos.NewGuildRedBagProtos.Sm_NewGuildRedBag.Action;
import ws.protos.NewGuildRedBagProtos.Sm_NewGuildRedBag.Builder;
import ws.protos.errorCode.ErrorCodeProtos.ErrorCodeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.tableRows.Table_GuildRedPacket_Row;
import ws.relationship.topLevelPojos.newGuild.NewGuildPlayer;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildPlayerRedBag;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildRedBag;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.ProtoUtils;
import ws.relationship.utils.RelationshipCommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by lee on 5/24/17.
 */
public class NewGuildRedBagCtrl implements ClientMsgActionStateful<NewGuildCtrl, Cm_NewGuildRedBag> {
    private static final Logger logger = LoggerFactory.getLogger(NewGuildRedBagCtrl.class);
    private ItemIoCtrl itemIoCtrl;
    private ItemIoExtp itemIoExtp;

    @Override
    public void onRecv(Cm_NewGuildRedBag cm, NewGuildCtrl exteControler) {
        itemIoExtp = exteControler.getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        Sm_NewGuildRedBag.Builder b = Sm_NewGuildRedBag.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_NewGuildRedBag.Action.SYNC_VALUE:
                    b.setAction(Sm_NewGuildRedBag.Action.RESP_SYNC);
                    redBagSync(exteControler);
                    break;
                case Cm_NewGuildRedBag.Action.GRAB_VALUE:
                    b.setAction(Sm_NewGuildRedBag.Action.RESP_GRAB);
                    grab(cm.getRedBagType(), cm.getRedBagId(), exteControler);
                    break;
                case Cm_NewGuildRedBag.Action.SEND_VALUE:
                    b.setAction(Sm_NewGuildRedBag.Action.RESP_SEND);
                    send(cm.getRedBagType(), cm.getSendRedBagType(), exteControler);
                    break;
                case Cm_NewGuildRedBag.Action.GRAB_RANK_VALUE:
                    b.setAction(Sm_NewGuildRedBag.Action.RESP_GRAB_RANK);
                    rank(cm.getRedBagType(), cm.getRedBagId(), exteControler);
                    break;
                case Cm_NewGuildRedBag.Action.GRAB_ALL_RANK_VALUE:
                    b.setAction(Sm_NewGuildRedBag.Action.RESP_GRAB_ALL_RANK);
                    grabAllRank(exteControler);
                    break;
                case Cm_NewGuildRedBag.Action.SEND_ALL_RANK_VALUE:
                    b.setAction(Sm_NewGuildRedBag.Action.RESP_SEND_ALL_RANK);
                    sendAllRank(exteControler);
                    break;
                case Cm_NewGuildRedBag.Action.GET_REDBAG_LIST_VALUE:
                    b.setAction(Sm_NewGuildRedBag.Action.RESP_GET_REDBAG_LIST);
                    getRedBagList(exteControler);
                    break;

            }
        } catch (BusinessLogicMismatchConditionException e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_NewGuildRedBag, b.getAction());
            exteControler.send(br.build());
            throw e;
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_NewGuildRedBag, b.getAction());
            exteControler.send(br.build());
            throw e;
        }

    }


    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg, NewGuildCtrl exteControler) {
        if (innerMsg instanceof In_NotifyRedPointMsg.Request) {
            onIn_NotifyRedPointMsg((In_NotifyRedPointMsg.Request) innerMsg, exteControler);
        }
    }


    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg, NewGuildCtrl exteControler) {
        if (privateMsg instanceof Pr_CheckRedPointMsg.Request) {
            onCheckRedPointMsg((Pr_CheckRedPointMsg.Request) privateMsg, exteControler);
        }
    }


    private void onCheckRedPointMsg(Pr_CheckRedPointMsg.Request privateMsg, NewGuildCtrl exteControler) {
        if (privateMsg.getRedPointEnum() == null || privateMsg.getRedPointEnum() == RedPointEnum.GUILD_SYS_REDBAG || privateMsg.getRedPointEnum() == RedPointEnum.GUILD_PLAYER_REDBAG) {
            HashMap<RedPointEnum, Boolean> redPointToShow = _checkRedPoint(exteControler);
            Pr_CheckRedPointMsg.Response response = new Pr_CheckRedPointMsg.Response(privateMsg, redPointToShow);
            exteControler.sendPrivateMsg(response);
        }
    }

    private void onIn_NotifyRedPointMsg(In_NotifyRedPointMsg.Request innerMsg, NewGuildCtrl exteControler) {
        for (Entry<RedPointEnum, Boolean> entry : innerMsg.getRedPointToShow().entrySet()) {
            if (entry.getKey() == null || entry.getKey() == RedPointEnum.GUILD_SYS_REDBAG || entry.getKey() == RedPointEnum.GUILD_PLAYER_REDBAG) {
                Pr_NotifyRedPointMsg.Request request = new Pr_NotifyRedPointMsg.Request(innerMsg.getRedPointToShow());
                exteControler.sendPrivateMsg(request);
            }
        }
    }

    private HashMap<RedPointEnum, Boolean> _checkRedPoint(NewGuildCtrl exteControler) {
        HashMap<RedPointEnum, Boolean> redPointToShow = new HashMap<>();
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            redPointToShow.put(RedPointEnum.GUILD_PLAYER_REDBAG, false);
            redPointToShow.put(RedPointEnum.GUILD_SYS_REDBAG, false);
            return redPointToShow;
        }
        SimplePlayer simplePlayer = exteControler.getPlayerCtrl().getSimplePlayerAfterUpdate();
        // 检查社团系统红包
        In_NewGuildRedBagSync.Response sysRedBagResponse = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildRedBagSync.Request(simplePlayer));
        boolean sysFlag = _canGrab(simplePlayer, new ArrayList<>(sysRedBagResponse.getTypeAndNewGuildRedBag().values()));
        redPointToShow.put(RedPointEnum.GUILD_SYS_REDBAG, sysFlag);
        // 检查玩家红包
        if (_isTimesCanGrab(exteControler)) {
            In_NewGuildRedBagGetRedBagList.Response playerRedBagResponse = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildRedBagGetRedBagList.Request(simplePlayer));
            boolean playerFlag = _canGrab(simplePlayer, playerRedBagResponse.getNewGuildRedBagList());
            redPointToShow.put(RedPointEnum.GUILD_PLAYER_REDBAG, playerFlag);
        }
        return redPointToShow;
    }

    private <X extends NewGuildRedBag> boolean _canGrab(SimplePlayer simplePlayer, List<X> redBagList) {
        for (NewGuildRedBag redBag : redBagList) {
            if (!redBag.getPlayerNameAndShare().containsKey(simplePlayer.getPlayerName()) && redBag.getRedBagShare().size() != MagicNumbers.DEFAULT_ZERO) {
                return true;
            }
        }
        return false;
    }

    private void getRedBagList(NewGuildCtrl exteControler) {
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = exteControler.getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildRedBagGetRedBagList.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildRedBagGetRedBagList.Request(simplePlayer));
        List<NewGuildPlayerRedBag> redBagList = response.getNewGuildRedBagList();
        SenderFunc.sendInner(exteControler, Sm_NewGuildRedBag.class, Sm_NewGuildRedBag.Builder.class, Sm_NewGuildRedBag.Action.RESP_GET_REDBAG_LIST, (b, br) -> {
            b.addAllRedBagInfos(NewGuildCtrlProtos.createSm_NewGuildRedBag_Info_List(redBagList, exteControler.getPlayerCtrl().getPlayerId(), exteControler.getPlayerCtrl().getOuterRealmId()));
            b.setGrabTimes(exteControler.getTarget().getGrabTimes());
        });
    }


    private void redBagSync(NewGuildCtrl exteControler) {
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = exteControler.getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildRedBagSync.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildRedBagSync.Request(simplePlayer));
        int sendCount = _getSendCount(exteControler.getTarget().getRedBagTypeAndCount());
        SenderFunc.sendInner(exteControler, Sm_NewGuildRedBag.class, Sm_NewGuildRedBag.Builder.class, Sm_NewGuildRedBag.Action.RESP_SYNC, (b, br) -> {
            b.setSendCount(sendCount);
            b.addAllRedBagInfos(NewGuildCtrlProtos.createSm_NewGuildRedBag_Info_List(response.getTypeAndNewGuildRedBag(), simplePlayer));
        });

    }

    private void grabAllRank(NewGuildCtrl exteControler) {

    }

    private void sendAllRank(NewGuildCtrl exteControler) {
    }

    private void rank(GuildRedBagTypeEnum redBagType, String redBagId, NewGuildCtrl exteControler) {
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = exteControler.getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildRedBagRank.Request request = _createIn_NewGuildRedBagRankRequest(redBagType, redBagId, simplePlayer);
        In_NewGuildRedBagRank.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(request);
        IdAndCount idAndCount = _getShareCount(redBagType, response.getPlayerNameAndShare().get(simplePlayer.getPlayerName()));
        Map<String, Integer> sortRank = RelationshipCommonUtils.sortMapByValueESC(response.getPlayerNameAndShare());
        SenderFunc.sendInner(exteControler, Sm_NewGuildRedBag.class, Sm_NewGuildRedBag.Builder.class, Sm_NewGuildRedBag.Action.RESP_GRAB_RANK, (b, br) -> {
            b.addAllRank(NewGuildCtrlProtos.createSm_NewGuildRedBag_Rank_List(sortRank));
            b.setIdAndCount(ProtoUtils.create_Sm_Common_IdAndCount(idAndCount));
        });
    }


    private void send(GuildRedBagTypeEnum redBagType, GuildSendRedBagTypeEnum sendRedBagType, NewGuildCtrl exteControler) {
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        if (redBagType == GuildRedBagTypeEnum.GRT_MONEY || redBagType == GuildRedBagTypeEnum.GRT_V_MONEY) {
            String msg = "玩家不能发系统红包";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_REDBAGL_NOT_BE_PRT);
        }
        if (!_isSendTimeEnought(exteControler.getTarget().getRedBagTypeAndCount())) {
            String msg = "发红包次数用完了";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_REDBAG_COUNT_NO_ENOUGH_ERROR);
        }
        if (!_isLvCanSend(exteControler, redBagType, sendRedBagType)) {
            String msg = String.format("vipLv不足，无法发送这种红包, vipLv:%s,发送红包类型:%s", exteControler.getPlayerCtrl().getCurVipLevel(), sendRedBagType);
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_REDBAG_COUNT_NO_ENOUGH_ERROR);
        }
        LogicCheckUtils.canRemove(itemIoCtrl, Table_GuildRedPacket_Row.getConsumeByRedBagType(sendRedBagType, redBagType));
        SimplePlayer simplePlayer = exteControler.getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildRedBagSend.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildRedBagSend.Request(simplePlayer, redBagType, sendRedBagType));
        IdAndCount idAndCount = Table_GuildRedPacket_Row.getConsumeByRedBagType(sendRedBagType, redBagType);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Sm_NewGuildRedBag.Action.RESP_SEND).removeItem(idAndCount);
        _addSendRedBagTimes(redBagType, exteControler);
        int sendCount = _getSendCount(exteControler.getTarget().getRedBagTypeAndCount());
        SenderFunc.sendInner(exteControler, Sm_NewGuildRedBag.class, Sm_NewGuildRedBag.Builder.class, Sm_NewGuildRedBag.Action.RESP_SEND, (b, br) -> {
            b.setSendCount(sendCount);
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1), br);
        });
        // 通知红点模块
        NewGuildCtrlUtils.sendNotifyRedPointMsg(exteControler.getPlayerCtrl().getContext(), RedPointEnum.GUILD_PLAYER_REDBAG, response.getGuildCenterPlayerList());
    }


    private void grab(GuildRedBagTypeEnum redBagType, String redBagId, NewGuildCtrl exteControler) {
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        if (!_isTimesCanGrab(exteControler)) {
            String msg = "每日抢红包次数已经用完了";
            throw new BusinessLogicMismatchConditionException(msg);
        }
        SimplePlayer simplePlayer = exteControler.getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildRedBagGrab.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new Request(simplePlayer, redBagType, redBagId));
        if (NewGuildCtrlUtils.isPlayerRedBag(redBagType)) {
            _saveGrabTimes(exteControler);
        }
        IdAndCount idAndCount = _getShareByRedBag(response.getRedBag(), simplePlayer.getPlayerName());
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_GRAB).addItem(idAndCount);
        SenderFunc.sendInner(exteControler, Sm_NewGuildRedBag.class, Builder.class, Action.RESP_GRAB, (b, br) -> {
            b.setRedBagInfo(NewGuildCtrlProtos.createSm_NewGuildRedBag_Info(response.getRedBag(), simplePlayer.getPlayerId(), simplePlayer.getOutRealmId()));
            b.setGrabTimes(exteControler.getTarget().getGrabTimes());
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1), br);
            b.setIdAndCount(ProtoUtils.create_Sm_Common_IdAndCount(idAndCount));
        });
    }





    /*
    ============================================================以下为内部方法==============================================================
     */


    /**
     * 创建获取抢红包排行的请求消息
     *
     * @param redBagType
     * @param redBagId
     * @param simplePlayer
     * @return
     */
    private In_NewGuildRedBagRank.Request _createIn_NewGuildRedBagRankRequest(GuildRedBagTypeEnum redBagType, String redBagId, SimplePlayer simplePlayer) {
        if (NewGuildCtrlUtils.isPlayerRedBag(redBagType)) {
            return new In_NewGuildRedBagRank.Request(simplePlayer, redBagId, redBagType);
        } else {
            return new In_NewGuildRedBagRank.Request(simplePlayer, redBagType);
        }
    }


    /**
     * 获取抢到的资源
     *
     * @param redBag
     * @param playerName
     * @return
     */
    private IdAndCount _getShareByRedBag(NewGuildRedBag redBag, String playerName) {
        int share = redBag.getPlayerNameAndShare().get(playerName);
        return _getShareCount(redBag.getRedBagTypeEnum(), share);
    }


    private IdAndCount _getShareCount(GuildRedBagTypeEnum redBagType, int share) {
        switch (redBagType) {
            case GRT_MONEY:
            case PRT_MONEY:
                return new IdAndCount(ResourceTypeEnum.RES_MONEY_VALUE, share);
            case GRT_V_MONEY:
            case PRT_V_MONEY:
                return new IdAndCount(ResourceTypeEnum.RES_VIPMONEY_VALUE, share);
        }
        String msg = String.format("红包类型错误:%s", redBagType);
        throw new BusinessLogicMismatchConditionException(msg);
    }


    /**
     * 保存抢红包的次数
     *
     * @param exteControler
     */
    private void _saveGrabTimes(NewGuildCtrl exteControler) {
        exteControler.getTarget().setGrabTimes(exteControler.getTarget().getGrabTimes() + MagicNumbers.DEFAULT_ONE);
    }

    /**
     * 增加发红包的次数
     *
     * @param redBagType
     * @param exteControler
     */
    private void _addSendRedBagTimes(GuildRedBagTypeEnum redBagType, NewGuildCtrl exteControler) {
        int times = exteControler.getTarget().getRedBagTypeAndCount().get(redBagType);
        exteControler.getTarget().getRedBagTypeAndCount().put(redBagType, times + MagicNumbers.DEFAULT_ONE);
    }

    /**
     * 等级是否可以抢红包
     *
     * @param exteControler
     * @param redBagType
     * @param sendRedBagType
     * @return
     */
    private boolean _isLvCanSend(NewGuildCtrl exteControler, GuildRedBagTypeEnum redBagType, GuildSendRedBagTypeEnum sendRedBagType) {
        int vipLv = exteControler.getPlayerCtrl().getTarget().getPayment().getVipLevel();
        int limitLv = Table_GuildRedPacket_Row.getLimitLvByRedBagType(sendRedBagType, redBagType);
        return vipLv >= limitLv;
    }

    /**
     * 是否还有发红包次数
     *
     * @param redBagTypeAndCount
     * @return
     */
    private boolean _isSendTimeEnought(Map<GuildRedBagTypeEnum, Integer> redBagTypeAndCount) {
        int count = AllServerConfig.Guild_Player_Send_RedBag_Count.getConfig();
        int hasSendCount = 0;
        for (Entry<GuildRedBagTypeEnum, Integer> entry : redBagTypeAndCount.entrySet()) {
            hasSendCount += entry.getValue();
        }
        return hasSendCount < count;
    }

    /**
     * 获取已经发红包的次数
     *
     * @param redBagTypeAndCount
     * @return
     */
    private int _getSendCount(Map<GuildRedBagTypeEnum, Integer> redBagTypeAndCount) {
        int count = AllServerConfig.Guild_Player_Send_RedBag_Count.getConfig();
        int hasSendCount = 0;
        for (Entry<GuildRedBagTypeEnum, Integer> entry : redBagTypeAndCount.entrySet()) {
            hasSendCount += entry.getValue();
        }
        return count - hasSendCount;
    }

    /**
     * 是否还可以有抢红包的次数
     *
     * @param exteControler
     * @return
     */
    private boolean _isTimesCanGrab(NewGuildCtrl exteControler) {
        int maxTimes = AllServerConfig.Guild_Grab_RedBag_Day_Times.getConfig();
        return exteControler.getTarget().getGrabTimes() < maxTimes;
    }


}
