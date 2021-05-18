package ws.gameServer.features.standalone.extp.piecemeal.ctrl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.actor.player.mc.controler.AbstractPlayerExteControler;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.newGuild.NewGuildExtp;
import ws.gameServer.features.standalone.extp.newGuild.ctrl.NewGuildCtrl;
import ws.gameServer.features.standalone.extp.newGuild.utils.NewGuildCtrlUtils;
import ws.gameServer.features.standalone.extp.piecemeal.utils.PiecemealCtrlUtils;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.CommonProtos.Sm_Common_Rank;
import ws.protos.CommonRankProtos.Sm_CommonRank;
import ws.protos.EnumsProtos.CommonRankTypeEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.protos.PiecemealProtos.Sm_Piecemeal;
import ws.protos.PiecemealProtos.Sm_Piecemeal.Action;
import ws.protos.PlayerProtos.Sm_HeartBeat;
import ws.protos.VersionProtos;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.base.RedisRankAndScore;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Item_Row;
import ws.relationship.topLevelPojos.piecemeal.Piecemeal;
import ws.relationship.topLevelPojos.simpleGuild.SimpleGuild;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.ProtoUtils;
import ws.relationship.utils.RedisRankUtils;
import ws.relationship.utils.RelationshipCommonUtils;
import ws.relationship.utils.SimplePojoUtils;

import java.util.List;
import java.util.Map;

public class _PiecemealCtrl extends AbstractPlayerExteControler<Piecemeal> implements PiecemealCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_PiecemealCtrl.class);
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private NewGuildCtrl newGuildCtrl;

    @Override
    public void _initReference() throws Exception {
        itemIoExtp = getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        newGuildCtrl = getPlayerCtrl().getExtension(NewGuildExtp.class).getControlerForQuery();
    }

    @Override
    public void _resetDataAtDayChanged() throws Exception {
    }


    @Override
    public void onPlayerLogined() {
        onPlayerHeartBeating();
        syncGuide();
    }

    @Override
    public void sync() {
        onPlayerHeartBeating();
    }


    @Override
    public void onPlayerHeartBeating() {
        SenderFunc.sendInner(this, Sm_HeartBeat.class, Sm_HeartBeat.Builder.class, Sm_HeartBeat.Action.RESP_SYNC, (b, br) -> {
            b.setAction(Sm_HeartBeat.Action.RESP_SYNC);
            b.setServerProtoVersion(VersionProtos.Version.newBuilder().getCode());
            b.setServerTime(System.currentTimeMillis());
        });
    }


    @Override
    public void composite(int itemTpId) {
        LogicCheckUtils.validateParam(Integer.class, itemTpId);
        LogicCheckUtils.checkTableHasTpId(Table_Item_Row.class, itemTpId);
        Table_Item_Row itemRow = RootTc.get(Table_Item_Row.class, itemTpId);
        if (!itemRow.getCanComposite()) {
            String msg = String.format("该itemTpId=%s ！不可用来合成.", itemTpId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        LogicCheckUtils.validateParam(Integer.class, itemRow.getCompositeNumToTarget_NeedNum());
        IdAndCount consume = new IdAndCount(itemTpId, itemRow.getCompositeNumToTarget_NeedNum());
        LogicCheckUtils.canRemove(itemIoCtrl, consume);
        IdAndCount add = new IdAndCount(itemRow.getCompositeNumToTarget_TargetId(), 1);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_COMPOSITE).removeItem(new IdMaptoCount().add(consume));
        IdMaptoCount refresh_2 = itemIoExtp.getControlerForUpdate(Action.RESP_COMPOSITE).addItem(new IdMaptoCount().add(add));
        SenderFunc.sendInner(this, Sm_Piecemeal.class, Sm_Piecemeal.Builder.class, Sm_Piecemeal.Action.RESP_COMPOSITE, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).addAll(refresh_2), br);
            b.setAfterCompositeTpId(itemRow.getCompositeNumToTarget_TargetId());
        });
    }

    @Override
    public void buyItem(int itemTpId, int count) {
        LogicCheckUtils.validateParam(Integer.class, itemTpId);
        LogicCheckUtils.validateParam(Integer.class, count);
        LogicCheckUtils.checkTableHasTpId(Table_Item_Row.class, itemTpId);
        Table_Item_Row itemRow = RootTc.get(Table_Item_Row.class, itemTpId);
        LogicCheckUtils.validateParam(Integer.class, itemRow.getItemJiamond());
        IdAndCount consume = new IdAndCount(ResourceTypeEnum.RES_VIPMONEY_VALUE, itemRow.getItemJiamond() * count);
        LogicCheckUtils.canRemove(itemIoCtrl, consume);
        IdAndCount add = new IdAndCount(itemTpId, count);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Action.RESP_BUY_ITEM).removeItem(new IdMaptoCount().add(consume));
        IdMaptoCount refresh_2 = itemIoExtp.getControlerForUpdate(Action.RESP_BUY_ITEM).addItem(new IdMaptoCount().add(add));
        SenderFunc.sendInner(this, Sm_Piecemeal.class, Sm_Piecemeal.Builder.class, Sm_Piecemeal.Action.RESP_BUY_ITEM, (b, br) -> {
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1).addAll(refresh_2), br);
        });
    }


    @Override
    public void queryRank(CommonRankTypeEnum rankTypeEnum, int rankStart, int rankCount) {
        String playerId = getPlayerCtrl().getPlayerId();
        int innerRealmId = getPlayerCtrl().getInnerRealmId();
        LogicCheckUtils.validateParam(CommonRankTypeEnum.class, rankTypeEnum);
        LogicCheckUtils.validateParam(Integer.class, rankStart);
        LogicCheckUtils.validateParam(Integer.class, rankCount);
        rankCount = Math.min(rankCount, MagicNumbers.ONE_TIME_QUERY_RANK_COUNT);

        List<RedisRankAndScore> rankAndScoreList = RedisRankUtils.getRankRangeWithScores(innerRealmId, rankTypeEnum, rankStart, rankCount);
        List<String> memberIds = PiecemealCtrlUtils.memberIds(rankAndScoreList);

        switch (rankTypeEnum) {
            case RK_GUILDBATTLEVALUE:
                querySimpleGuildRank(rankTypeEnum, innerRealmId, rankAndScoreList, memberIds);
                break;
            default:
                querySimplePlayerRank(rankTypeEnum, playerId, innerRealmId, rankAndScoreList, memberIds);
        }
    }

    @Override
    public void selfRank(CommonRankTypeEnum rankTypeEnum) {
        String playerId = getPlayerCtrl().getPlayerId();
        int innerRealmId = getPlayerCtrl().getInnerRealmId();
        LogicCheckUtils.validateParam(CommonRankTypeEnum.class, rankTypeEnum);
        int selfRank = RedisRankUtils.getRank(playerId, innerRealmId, rankTypeEnum);
        long selfScore = RedisRankUtils.getScore(playerId, innerRealmId, rankTypeEnum);
        Sm_Common_Rank selfRankInfo = ProtoUtils.create_Sm_Common_Rank(getPlayerCtrl().getSimplePlayerAfterUpdate(), selfRank, selfScore);
        SenderFunc.sendInner(this, Sm_CommonRank.class, Sm_CommonRank.Builder.class, Sm_CommonRank.Action.RESP_SELF_RANK, (b, br) -> {
            b.setSelfRankInfo(selfRankInfo);
        });
    }


    @Override
    public void syncGuide() {
        SenderFunc.sendInner(this, Sm_Piecemeal.class, Sm_Piecemeal.Builder.class, Action.RESP_SYNC_GUIDE, (b, br) -> {
            b.setMaxGuideId(target.getMaxGuideId());
        });
    }

    @Override
    public void setGuide(int maxGuideId) {
        target.setMaxGuideId(Math.max(maxGuideId, target.getMaxGuideId()));
        SenderFunc.sendInner(this, Sm_Piecemeal.class, Sm_Piecemeal.Builder.class, Action.RESP_SET_GUIDE, (b, br) -> {
            b.setMaxGuideId(target.getMaxGuideId());
        });
        save();
    }

    /**
     * 通用排行榜，适用于展示SimplePlayer信息
     *
     * @param rankTypeEnum
     * @param playerId
     * @param innerRealmId
     * @param rankAndScoreList
     * @param memberIds
     */
    private void querySimplePlayerRank(CommonRankTypeEnum rankTypeEnum, String playerId, int innerRealmId, List<RedisRankAndScore> rankAndScoreList, List<String> memberIds) {
        int selfRank = RedisRankUtils.getRank(playerId, innerRealmId, rankTypeEnum);
        long selfScore = RedisRankUtils.getScore(playerId, innerRealmId, rankTypeEnum);
        Sm_Common_Rank selfRankInfo = ProtoUtils.create_Sm_Common_Rank(getPlayerCtrl().getSimplePlayerAfterUpdate(), selfRank, selfScore);
        Map<String, SimplePlayer> playerIdToSimplePlayer = SimplePojoUtils.querySimplePlayerLisByIds(memberIds, getPlayerCtrl().getOuterRealmId());
        RelationshipCommonUtils.removeNullElements(playerIdToSimplePlayer);
        SenderFunc.sendInner(this, Sm_CommonRank.class, Sm_CommonRank.Builder.class, Sm_CommonRank.Action.RESP_QUERY_RANK, (b, br) -> {
            b.setRankInfo(ProtoUtils.create_Sm_Common_Rank_List(rankTypeEnum, playerIdToSimplePlayer, rankAndScoreList));
            b.setSelfRankInfo(selfRankInfo);
        });
    }

    /**
     * 社团战力排行
     *
     * @param rankTypeEnum
     * @param innerRealmId
     * @param rankAndScoreList
     * @param memberIds
     */
    private void querySimpleGuildRank(CommonRankTypeEnum rankTypeEnum, int innerRealmId, List<RedisRankAndScore> rankAndScoreList, List<String> memberIds) {
        Sm_Common_Rank selfRankInfo = null;
        if (!StringUtils.isBlank(newGuildCtrl.queryGuildId())) {
            int selfRank = RedisRankUtils.getRank(newGuildCtrl.queryGuildId(), innerRealmId, rankTypeEnum);
            long selfScore = RedisRankUtils.getScore(newGuildCtrl.queryGuildId(), innerRealmId, rankTypeEnum);
            SimpleGuild selfSimpleGuild = NewGuildCtrlUtils.getSimpleGuild(newGuildCtrl.queryGuildId(), innerRealmId);
            selfRankInfo = ProtoUtils.create_Sm_Common_Rank(selfSimpleGuild, selfRank, selfScore);
        }
        final Sm_Common_Rank selfRankInfo1 = selfRankInfo;
        Map<String, SimpleGuild> idToSimpleGuild = NewGuildCtrlUtils.getSimpleGuildLis(memberIds, innerRealmId);
        RelationshipCommonUtils.removeNullElements(idToSimpleGuild);
        SenderFunc.sendInner(this, Sm_CommonRank.class, Sm_CommonRank.Builder.class, Sm_CommonRank.Action.RESP_QUERY_RANK, (b, br) -> {
            b.setRankInfo(ProtoUtils.create_Sm_Common_Rank_List_ForGuild(rankTypeEnum, idToSimpleGuild, rankAndScoreList));
            if (selfRankInfo1 != null) {
                b.setSelfRankInfo(selfRankInfo1);
            }
        });
    }
}
