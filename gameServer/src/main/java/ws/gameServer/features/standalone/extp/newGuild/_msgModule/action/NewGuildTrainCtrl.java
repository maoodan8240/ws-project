package ws.gameServer.features.standalone.extp.newGuild._msgModule.action;

import akka.actor.ActorRef;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.extp.heros.HerosExtp;
import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.gameServer.features.standalone.extp.itemIo.ItemIoExtp;
import ws.gameServer.features.standalone.extp.itemIo.ctrl.ItemIoCtrl;
import ws.gameServer.features.standalone.extp.newGuild.ctrl.NewGuildCtrl;
import ws.gameServer.features.standalone.extp.newGuild.msg.train.In_NewGuildTrainAccelerate;
import ws.gameServer.features.standalone.extp.newGuild.msg.train.In_NewGuildTrainBeAccelerate;
import ws.gameServer.features.standalone.extp.newGuild.msg.train.In_NewGuildTrainGetMember;
import ws.gameServer.features.standalone.extp.newGuild.msg.train.In_NewGuildTrainGetTrainerInfo;
import ws.gameServer.features.standalone.extp.newGuild.msg.train.In_NewGuildTrainReplace;
import ws.gameServer.features.standalone.extp.newGuild.msg.train.In_NewGuildTrainStamp;
import ws.gameServer.features.standalone.extp.newGuild.msg.train.In_NewGuildTrainUnlock;
import ws.gameServer.features.standalone.extp.newGuild.utils.NewGuildCtrlProtos;
import ws.gameServer.features.standalone.extp.newGuild.utils.NewGuildCtrlUtils;
import ws.gameServer.features.standalone.extp.redPoint.msg.Pr_CheckRedPointMsg;
import ws.gameServer.features.standalone.extp.utils.ClientMsgActionStateful;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.SenderFunc;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.CommonProtos.Sm_Common_Round;
import ws.protos.EnumsProtos.RedPointEnum;
import ws.protos.HerosProtos.Sm_Heros;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.NewGuildTrainProtos.Cm_NewGuildTrain;
import ws.protos.NewGuildTrainProtos.Sm_NewGuildTrain;
import ws.protos.NewGuildTrainProtos.Sm_NewGuildTrain_Trainer;
import ws.protos.errorCode.ErrorCodeProtos.ErrorCodeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.CheckPlayerOnlineMsgRequest;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.heros.Heros;
import ws.relationship.topLevelPojos.newGuild.NewGuildPlayer;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenterPlayer;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildTrainer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.ActorMsgSynchronizedExecutor;
import ws.relationship.utils.DBUtils;
import ws.relationship.utils.ProtoUtils;
import ws.relationship.utils.RandomUtils;
import ws.relationship.utils.SimplePojoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lee on 5/18/17.
 */
public class NewGuildTrainCtrl implements ClientMsgActionStateful<NewGuildCtrl, Cm_NewGuildTrain> {
    private ItemIoExtp itemIoExtp;
    private ItemIoCtrl itemIoCtrl;
    private HerosCtrl herosCtrl;


    @Override
    public void onRecv(Cm_NewGuildTrain cm, NewGuildCtrl exteControler) {
        itemIoExtp = exteControler.getPlayerCtrl().getExtension(ItemIoExtp.class);
        itemIoCtrl = itemIoExtp.getControlerForQuery();
        herosCtrl = exteControler.getPlayerCtrl().getExtension(HerosExtp.class).getControlerForQuery();
        Sm_NewGuildTrain.Builder b = Sm_NewGuildTrain.newBuilder();
        try {
            switch (cm.getAction().getNumber()) {
                case Cm_NewGuildTrain.Action.SYNC_VALUE:
                    b.setAction(Sm_NewGuildTrain.Action.RESP_SYNC);
                    sync(exteControler);
                    break;
                case Cm_NewGuildTrain.Action.REPLACE_VALUE:
                    b.setAction(Sm_NewGuildTrain.Action.RESP_REPLACE);
                    replace(cm.getHeroId(), cm.getIndex(), exteControler);
                    break;
                case Cm_NewGuildTrain.Action.UNLOCK_VALUE:
                    b.setAction(Sm_NewGuildTrain.Action.RESP_UNLOCK);
                    unlock(cm.getIndex(), exteControler);
                    break;
                case Cm_NewGuildTrain.Action.STAMP_VALUE:
                    b.setAction(Sm_NewGuildTrain.Action.RESP_STAMP);
                    stamp(cm.getPlayerId(), exteControler);
                    break;
                case Cm_NewGuildTrain.Action.ACCELERATE_VALUE:
                    b.setAction(Sm_NewGuildTrain.Action.RESP_ACCELERATE);
                    accelerate(cm.getPlayerId(), cm.getIndex(), exteControler);
                    break;
                case Cm_NewGuildTrain.Action.GET_MEMBER_VALUE:
                    b.setAction(Sm_NewGuildTrain.Action.RESP_GET_MEMBER);
                    getMember(cm.getRound(), exteControler);
                    break;
                case Cm_NewGuildTrain.Action.GET_TRAINER_INFO_VALUE:
                    b.setAction(Sm_NewGuildTrain.Action.RESP_GET_TRAINER_INFO);
                    getTrainerInfo(cm.getPlayerId(), exteControler);
                    break;
                case Cm_NewGuildTrain.Action.SETTLE_VALUE:
                    b.setAction(Sm_NewGuildTrain.Action.RESP_SETTLE);
                    settle(cm.getIndex(), exteControler);
                    break;
                case Cm_NewGuildTrain.Action.RANDOM_ACCELERATE_VALUE:
                    b.setAction(Sm_NewGuildTrain.Action.RESP_RANDOM_ACCELERATE);
                    randomAccelerate(exteControler);
                    break;
            }
        } catch (BusinessLogicMismatchConditionException e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_NewGuildTrain, b.getAction(), e.getErrorCodeEnum());
            exteControler.send(br.build());
            throw e;
        } catch (Exception e) {
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_NewGuildTrain, b.getAction());
            exteControler.send(br.build());
            throw e;
        }
    }


    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg, NewGuildCtrl exteControler) {
        if (innerMsg instanceof In_NewGuildTrainBeAccelerate.Request) {
            In_NewGuildTrainBeAccelerate.Request request = (In_NewGuildTrainBeAccelerate.Request) innerMsg;
            beAccelerate(request.getHeroId(), request.getPlayerNames(), exteControler);
        }
    }

    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg, NewGuildCtrl exteControler) {
        if (privateMsg instanceof Pr_CheckRedPointMsg.Request) {
            onCheckRedPoint((Pr_CheckRedPointMsg.Request) privateMsg, exteControler);
        }
    }

    private void onCheckRedPoint(Pr_CheckRedPointMsg.Request privateMsg, NewGuildCtrl exteControler) {
        if (privateMsg.getRedPointEnum() == null || privateMsg.getRedPointEnum() == RedPointEnum.GUILD_TRAINER) {
            HashMap<RedPointEnum, Boolean> redPointToShow = _checkRedPoint(exteControler);
            Pr_CheckRedPointMsg.Response response = new Pr_CheckRedPointMsg.Response(privateMsg, redPointToShow);
            exteControler.sendPrivateMsg(response);
        }
    }

    private HashMap<RedPointEnum, Boolean> _checkRedPoint(NewGuildCtrl exteControler) {
        HashMap<RedPointEnum, Boolean> redPointToShow = new HashMap<>();
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            redPointToShow.put(RedPointEnum.GUILD_TRAINER, false);
            return redPointToShow;
        }
        // TODO 需要策划给 具体什么时候出红点的逻辑，目前逻辑是主动加速次数是满的时候就出现红点
        int outRealmId = exteControler.getPlayerCtrl().getOuterRealmId();
        SimplePlayer simplePlayer = SimplePojoUtils.querySimplePlayerById(exteControler.getPlayerCtrl().getPlayerId(), outRealmId);
        In_NewGuildTrainGetTrainerInfo.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildTrainGetTrainerInfo.Request(simplePlayer));
        redPointToShow.put(RedPointEnum.GUILD_TRAINER, response.getGuildCenterPlayer().getAccelerateTimes() == MagicNumbers.DEFAULT_ZERO);
        return redPointToShow;
    }


    private void getTrainerInfo(String playerId, NewGuildCtrl exteControler) {
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        int outRealmId = exteControler.getPlayerCtrl().getOuterRealmId();
        SimplePlayer simplePlayer = SimplePojoUtils.querySimplePlayerById(playerId, outRealmId);
        In_NewGuildTrainGetTrainerInfo.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildTrainGetTrainerInfo.Request(simplePlayer));
        List<NewGuildTrainer> trainerList = response.getBeforeSettleTrainerList();
        //需要获取这个玩家的Heros由于只是用来展示，就不发checkOnlineMsg获取最新数据，直接取Redis缓存
        Heros heros = DBUtils.getHashPojo(playerId, exteControler.getPlayerCtrl().getOuterRealmId(), Heros.class);
        SenderFunc.sendInner(exteControler, Sm_NewGuildTrain.class, Sm_NewGuildTrain.Builder.class, Sm_NewGuildTrain.Action.RESP_GET_TRAINER_INFO, (b, br) -> {
            b.setPlayerId(playerId);
            b.addAllTrainers(NewGuildCtrlProtos.createSm_NewGUildTrain_Trainer_List(trainerList, heros));
        });
    }


    private void sync(NewGuildCtrl exteControler) {
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = exteControler.getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildTrainGetTrainerInfo.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildTrainGetTrainerInfo.Request(simplePlayer));
        NewGuildCenterPlayer guildCenterPlayer = response.getGuildCenterPlayer();
        List<NewGuildTrainer> trainerList = response.getBeforeSettleTrainerList();
        _tryInitTrainerCount(trainerList.size(), guildPlayer);
        _settleHeroExpByTrainerList(trainerList, exteControler.getPlayerCtrl().getCurLevel());
        List<Sm_NewGuildTrain_Trainer> smNewGuildTrainTrainerList = NewGuildCtrlProtos.createSm_NewGUildTrain_Trainer_List(guildCenterPlayer.getIndexAndTrainer(), herosCtrl);
        SenderFunc.sendInner(exteControler, Sm_NewGuildTrain.class, Sm_NewGuildTrain.Builder.class, Sm_NewGuildTrain.Action.RESP_SYNC, (b, br) -> {
            b.addAllTrainers(smNewGuildTrainTrainerList);
        });
        SenderFunc.sendInner(exteControler, Sm_Heros.class, Sm_Heros.Builder.class, Sm_Heros.Action.RESP_SYNC_PART, (b, br) -> {
            b.addAllHeros(ProtoUtils.create_Sm_Common_Hero_Lis(herosCtrl.getTarget().getIdToHero()));
        });
    }


    private void replace(int heroId, int index, NewGuildCtrl exteControler) {
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        if (!itemIoCtrl.canRemove(new IdAndCount(heroId, MagicNumbers.DEFAULT_ONE))) {
            String msg = String.format("没有这个英雄,无法放置或替换 heroId=%s", heroId);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        SimplePlayer simplePlayer = exteControler.getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildTrainReplace.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildTrainReplace.Request(simplePlayer, guildPlayer.getGuildId(), index, heroId));
        if (!response.getResult().isBeforeTrainerNull()) {
            _settleHeroExp(response.getResult().getBeforeTrainer(), exteControler.getPlayerCtrl().getCurLevel());
        }
        SenderFunc.sendInner(exteControler, Sm_NewGuildTrain.class, Sm_NewGuildTrain.Builder.class, Sm_NewGuildTrain.Action.RESP_REPLACE, (b, br) -> {
            b.setTrainer(NewGuildCtrlProtos.createSm_NewGuildTrain_Trainer_One_Trainer(response.getResult().getNowtrainer(), herosCtrl));
        });
    }

    private void unlock(int index, NewGuildCtrl exteControler) {
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = exteControler.getPlayerCtrl().getSimplePlayerAfterUpdate();
        _checkCanUnlock(index, exteControler.getPlayerCtrl(), guildPlayer.getTrainerCount());
        In_NewGuildTrainUnlock.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildTrainUnlock.Request(simplePlayer, guildPlayer.getGuildId(), index));
        guildPlayer.setTrainerCount(guildPlayer.getTrainerCount() + MagicNumbers.DEFAULT_ONE);
        IdMaptoCount consume = NewGuildCtrlUtils.getTrainerUnlockConsume(index);
        IdMaptoCount refresh_1 = itemIoExtp.getControlerForUpdate(Sm_NewGuildTrain.Action.RESP_UNLOCK).removeItem(consume);
        SenderFunc.sendInner(exteControler, Sm_NewGuildTrain.class, Sm_NewGuildTrain.Builder.class, Sm_NewGuildTrain.Action.RESP_UNLOCK, (b, br) -> {
            b.setTrainer(NewGuildCtrlProtos.createSm_NewGuildTrain_Trainer_One_Trainer(response.getTrainer(), herosCtrl));
            itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh_1), br);
        });
    }


    private void stamp(String playerId, NewGuildCtrl exteControler) {
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = exteControler.getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildTrainStamp.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildTrainStamp.Request(simplePlayer, playerId));
        SimplePlayer stampSimplePlayer = SimplePojoUtils.querySimplePlayerById(playerId, exteControler.getPlayerCtrl().getOuterRealmId());
        SenderFunc.sendInner(exteControler, Sm_NewGuildTrain.class, Sm_NewGuildTrain.Builder.class, Sm_NewGuildTrain.Action.RESP_STAMP, (b, br) -> {
            b.setStampCount(response.getGuildCenterPlayer().getStampPlayerIds().size());
            b.setMemberTrainer(NewGuildCtrlProtos.createSm_NewGuildTrain_Member_Trainer_One_Trainer(response.getStampGuildCenterPlayer(), stampSimplePlayer, response.getGuildCenterPlayer()));
        });
    }

    private void randomAccelerate(NewGuildCtrl exteControler) {
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = exteControler.getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildTrainGetMember.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildTrainGetMember.Request(simplePlayer));
        List<NewGuildCenterPlayer> roundList = NewGuildCtrlUtils.getMemberByRoundWithoutSelf(0, response.getGuildCenterPlayerList().size() - MagicNumbers.DEFAULT_ONE, response.getGuildCenterPlayerList(), exteControler.getPlayerCtrl().getPlayerId());
        List<NewGuildCenterPlayer> canBeAcceleratePlayerList = _screenCanBeAcceleratePlayer(roundList);
        if (canBeAcceleratePlayerList.size() == MagicNumbers.DEFAULT_ZERO) {
            String msg = "没有可以加速的玩家";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_NOT_PLAYER_ACCELERATE);
        }
        int playerIdIndex = RandomUtils.dropBetweenTowNum(MagicNumbers.DEFAULT_ZERO, canBeAcceleratePlayerList.size() - MagicNumbers.DEFAULT_ONE);
        NewGuildCenterPlayer beAcceleratePlayer = canBeAcceleratePlayerList.get(playerIdIndex);
        _accelerate(beAcceleratePlayer.getPlayerId(), MagicNumbers.DEFAULT_ONE, simplePlayer, exteControler, Sm_NewGuildTrain.Action.RESP_RANDOM_ACCELERATE);
    }


    private void accelerate(String playerId, int index, NewGuildCtrl exteControler) {
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = exteControler.getPlayerCtrl().getSimplePlayerAfterUpdate();
        _accelerate(playerId, index, simplePlayer, exteControler, Sm_NewGuildTrain.Action.RESP_ACCELERATE);
    }

    private void _accelerate(String beAcceleratePlayerId, int index, SimplePlayer simplePlayer, NewGuildCtrl exteControler, Sm_NewGuildTrain.Action action) {
        In_NewGuildTrainAccelerate.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildTrainAccelerate.Request(simplePlayer, beAcceleratePlayerId, index, action));
        NewGuildTrainer beAccelerateTrainer = response.getBeforeTrainer();
        List<String> playerNames = new ArrayList<>();
        playerNames.add(simplePlayer.getPlayerName());
        In_NewGuildTrainBeAccelerate.Response response1 = _sendBeAccelerateMsg(beAccelerateTrainer.getHeroId(), playerNames, simplePlayer.getOutRealmId(), beAcceleratePlayerId, exteControler);
        beAccelerateTrainer.setLastSettle(System.currentTimeMillis());
        Heros heros = response1.getHeros();
        TupleCell<Integer> tupleCell = AllServerConfig.Guild_Tainer_Accelerate_Reward.getConfig();
        IdAndCount idAndCount = new IdAndCount(tupleCell.get(TupleCell.FIRST), tupleCell.get(TupleCell.SECOND));
        IdMaptoCount refresh = itemIoExtp.getControlerForUpdate(action).addItem(idAndCount);
        SimplePlayer beSimplePlayer = SimplePojoUtils.querySimplePlayerById(beAcceleratePlayerId, simplePlayer.getOutRealmId());
        if (action == Sm_NewGuildTrain.Action.RESP_ACCELERATE) {
            SenderFunc.sendInner(exteControler, Sm_NewGuildTrain.class, Sm_NewGuildTrain.Builder.class, action, (b, br) -> {
                b.setAccelerateTimes(response.getGuildCenterPlayer().getAccelerateTimes());
                b.setPlayerId(beAcceleratePlayerId);
                b.setTrainer(NewGuildCtrlProtos.createSm_NewGuildTrain_Trainer(beAccelerateTrainer, heros));
                b.setMemberTrainer(NewGuildCtrlProtos.createSm_NewGuildTrain_Member_Trainer_One_Trainer(response.getBeAccelerateGuildCenterPlayer(), beSimplePlayer, response.getGuildCenterPlayer()));
                itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
            });
        } else if (action == Sm_NewGuildTrain.Action.RESP_RANDOM_ACCELERATE) {
            SenderFunc.sendInner(exteControler, Sm_NewGuildTrain.class, Sm_NewGuildTrain.Builder.class, action, (b, br) -> {
                b.setAccelerateTimes(response.getGuildCenterPlayer().getAccelerateTimes());
                b.addPlayerNames(beSimplePlayer.getPlayerName());
                itemIoCtrl.refreshItemAddToResponse(new IdMaptoCount().addAll(refresh), br);
            });
        }
    }


    private void beAccelerate(int heroId, List<String> playerNames, NewGuildCtrl exteControler) {
        _settleHeroAccelerateExp(heroId);
        SenderFunc.sendInner(exteControler, Sm_NewGuildTrain.class, Sm_NewGuildTrain.Builder.class, Sm_NewGuildTrain.Action.RESP_BE_ACCELERATE, (b, br) -> {
            b.addAllPlayerNames(playerNames);
        });
        Heros heros = herosCtrl.getTarget();
        In_NewGuildTrainBeAccelerate.Response response = new In_NewGuildTrainBeAccelerate.Response(heros);
        exteControler.getPlayerCtrl().getCurSendActorRef().tell(response, ActorRef.noSender());
    }

    private void getMember(Sm_Common_Round round, NewGuildCtrl exteControler) {
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = exteControler.getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildTrainGetMember.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildTrainGetMember.Request(simplePlayer));
        NewGuildCenterPlayer guildCenterPlayer = _getPlayerGuildCenterPlayer(guildPlayer.getPlayerId(), response.getGuildCenterPlayerList());
        List<NewGuildCenterPlayer> roundList = NewGuildCtrlUtils.getMemberByRoundWithoutSelf(round.getMin(), round.getMax(), response.getGuildCenterPlayerList(), exteControler.getPlayerCtrl().getPlayerId());
        Map<String, SimplePlayer> idToSimplePlayers = NewGuildCtrlUtils.getStringSimplePlayerMap(roundList, simplePlayer.getOutRealmId());
        SenderFunc.sendInner(exteControler, Sm_NewGuildTrain.class, Sm_NewGuildTrain.Builder.class, Sm_NewGuildTrain.Action.RESP_GET_MEMBER, (b, br) -> {
            b.setBeAccelerateTimes(guildCenterPlayer.getAcceleratePlayerName().size());
            b.setAccelerateTimes(guildCenterPlayer.getAccelerateTimes());
            b.setStampCount(guildCenterPlayer.getStampPlayerIds().size());
            b.addAllPlayerNames(guildCenterPlayer.getAcceleratePlayerName());
            b.addAllMemberTrainers(NewGuildCtrlProtos.createSm_NewGuildTrain_Member_Trainer_List(idToSimplePlayers, roundList, guildCenterPlayer));
        });

    }


    private void settle(int index, NewGuildCtrl exteControler) {
        NewGuildPlayer guildPlayer = exteControler.getTarget();
        if (!NewGuildCtrlUtils.hasGuild(guildPlayer)) {
            String msg = "玩家没有社团";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        SimplePlayer simplePlayer = exteControler.getPlayerCtrl().getSimplePlayerAfterUpdate();
        In_NewGuildTrainGetTrainerInfo.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildTrainGetTrainerInfo.Request(simplePlayer));
        NewGuildTrainer trainer = _getTrainerByIndex(index, response.getBeforeSettleTrainerList());
        _settleHeroExp(trainer, exteControler.getPlayerCtrl().getCurLevel());
        NewGuildTrainer afterSettleTrainer = response.getGuildCenterPlayer().getIndexAndTrainer().get(index);
        SenderFunc.sendInner(exteControler, Sm_NewGuildTrain.class, Sm_NewGuildTrain.Builder.class, Sm_NewGuildTrain.Action.RESP_SETTLE, (b, br) -> {
            b.setTrainer(NewGuildCtrlProtos.createSm_NewGuildTrain_Trainer_One_Trainer(afterSettleTrainer, herosCtrl));
        });
    }


/*
======================================================内部方法================================================================
*/

    /**
     * 过滤掉不能被动加速的玩家
     *
     * @param guildCenterPlayerList
     */
    private List<NewGuildCenterPlayer> _screenCanBeAcceleratePlayer(List<NewGuildCenterPlayer> guildCenterPlayerList) {
        List<NewGuildCenterPlayer> newGuildCenterPlayerList = new ArrayList<>();
        for (NewGuildCenterPlayer guildCenterPlayer : guildCenterPlayerList) {
            if (guildCenterPlayer.getAcceleratePlayerName().size() < MagicNumbers.BE_ACCELERATETIMES_MAX_TIMES) {
                for (NewGuildTrainer trainer : guildCenterPlayer.getIndexAndTrainer().values()) {
                    if (trainer.getHeroId() != MagicNumbers.DEFAULT_ZERO) {
                        newGuildCenterPlayerList.add(guildCenterPlayer);
                        break;
                    }
                }
            }
        }
        return newGuildCenterPlayerList;
    }


    private void _tryInitTrainerCount(int TrainerCount, NewGuildPlayer guildPlayer) {
        if (TrainerCount != guildPlayer.getTrainerCount()) {
            guildPlayer.setTrainerCount(TrainerCount);
        }

    }

    private void _checkCanUnlock(int index, PlayerCtrl playerCtrl, int hasOpenIndex) {
        IdMaptoCount consume = NewGuildCtrlUtils.getTrainerUnlockConsume(index);
        LogicCheckUtils.canRemove(itemIoCtrl, consume);
        int needLv = NewGuildCtrlUtils.getTrainerOpenLv(index);
        if (playerCtrl.getCurLevel() < needLv) {
            String msg = String.format("playerLv not enougth,nowLv=%s,needLv=%s", playerCtrl.getCurLevel(), needLv);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int needVipLv = NewGuildCtrlUtils.getTrainerOpenVipLv(index);
        if (playerCtrl.getCurVipLevel() < needVipLv) {
            String msg = String.format("playerVipLv not enougth, nowVipLv=%s,needvipLv=%s", playerCtrl.getCurVipLevel(), needVipLv);
            throw new BusinessLogicMismatchConditionException(msg);
        }
        int preIndex = NewGuildCtrlUtils.getTrainerPreOpenIndex(index);
        if (hasOpenIndex < preIndex) {
            String msg = String.format("preIndex not open, hasOpenIndex=%s,preIndex=%s", hasOpenIndex, preIndex);
            throw new BusinessLogicMismatchConditionException(msg);
        }
    }

    private In_NewGuildTrainBeAccelerate.Response _sendBeAccelerateMsg(int heroId, List<String> playerNames, int outRealmId, String playerId, NewGuildCtrl exteControler) {
        In_NewGuildTrainBeAccelerate.Request request = new In_NewGuildTrainBeAccelerate.Request(heroId, playerNames, outRealmId);
        CheckPlayerOnlineMsgRequest<In_NewGuildTrainBeAccelerate.Request> checkPlayerOnlineMsgRequest = new CheckPlayerOnlineMsgRequest<>(playerId, request);
        return ActorMsgSynchronizedExecutor.sendMsgToServer(exteControler.getPlayerCtrl().getContext().actorSelection(ActorSystemPath.WS_GameServer_Selection_World), checkPlayerOnlineMsgRequest);
    }


    private NewGuildTrainer _getTrainerByIndex(int index, List<NewGuildTrainer> guildTrainerList) {
        for (NewGuildTrainer trainer : guildTrainerList) {
            if (trainer.getIndex() == index) {
                return trainer;
            }
        }
        String msg = String.format("训练所没找到对应的Index, index=%s", index);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    private NewGuildCenterPlayer _getPlayerGuildCenterPlayer(String playerId, List<NewGuildCenterPlayer> guildCenterPlayerList) {
        for (NewGuildCenterPlayer guildCenterPlayer : guildCenterPlayerList) {
            if (guildCenterPlayer.getPlayerId().equals(playerId)) {
                return guildCenterPlayer;
            }
        }
        String msg = String.format("异常:guildCenterPlayerList中没有这个GuildcenterPlayer,playerId:%s", playerId);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    private void _settleHeroExpByTrainerList(List<NewGuildTrainer> trainerList, int playerLv) {
        for (NewGuildTrainer trainer : trainerList) {
            if (trainer.getHeroId() != MagicNumbers.DEFAULT_ZERO) {
                _settleHeroExp(trainer, playerLv);
            }
        }
    }

    /**
     * @param trainer
     * @param playerLv
     */
    private void _settleHeroExp(NewGuildTrainer trainer, int playerLv) {
        int exp = NewGuildCtrlUtils.mathHeroExp(trainer, playerLv);
        _addExp(trainer.getHeroId(), exp);
    }


    /**
     * 加速训练桩上的英雄增加经验
     *
     * @param heroId 真实Id
     */
    private void _settleHeroAccelerateExp(int heroId) {
        Hero hero = herosCtrl.getHero(heroId);
        int exp = NewGuildCtrlUtils.mathAccelerateHeroExp(hero);
        _addExp(heroId, exp);
    }


    private void _addExp(int heroId, long exp) {
        herosCtrl.addExpToHero(heroId, exp);
    }
}
