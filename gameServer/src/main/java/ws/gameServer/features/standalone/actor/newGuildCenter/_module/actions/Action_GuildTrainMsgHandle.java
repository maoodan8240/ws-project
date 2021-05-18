package ws.gameServer.features.standalone.actor.newGuildCenter._module.actions;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.ResultCode;
import ws.gameServer.features.standalone.actor.newGuildCenter._module.Action;
import ws.gameServer.features.standalone.actor.newGuildCenter.ctrl.NewGuildCenterCtrl;
import ws.gameServer.features.standalone.actor.newGuildCenter.utils.NewGuildCenterUtils;
import ws.gameServer.features.standalone.actor.newGuildCenter.utils.TrainerReplaceResult;
import ws.gameServer.features.standalone.extp.newGuild.msg.train.In_NewGuildTrainAccelerate;
import ws.gameServer.features.standalone.extp.newGuild.msg.train.In_NewGuildTrainAccelerate.Response;
import ws.gameServer.features.standalone.extp.newGuild.msg.train.In_NewGuildTrainGetMember;
import ws.gameServer.features.standalone.extp.newGuild.msg.train.In_NewGuildTrainGetTrainerInfo;
import ws.gameServer.features.standalone.extp.newGuild.msg.train.In_NewGuildTrainReplace;
import ws.gameServer.features.standalone.extp.newGuild.msg.train.In_NewGuildTrainStamp;
import ws.gameServer.features.standalone.extp.newGuild.msg.train.In_NewGuildTrainUnlock;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.protos.EnumsProtos.GuildBuildTypeEnum;
import ws.protos.NewGuildTrainProtos.Sm_NewGuildTrain;
import ws.protos.errorCode.ErrorCodeProtos.ErrorCodeEnum;
import ws.relationship.base.MagicNumbers;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.CheckPlayerOnlineMsgRequest;
import ws.relationship.base.resultCode.ResultMsg;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenter;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenterPlayer;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildTrainer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.CloneUtils;
import ws.relationship.utils.ClusterMessageSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Created by lee on 6/8/17.
 */
public class Action_GuildTrainMsgHandle implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(Action_GuildTrainMsgHandle.class);
    private ActorRef sender;
    private ActorContext context;
    private NewGuildCenterCtrl actorCtrl;

    @Override
    public void onRecv(NewGuildCenterCtrl actorCtrl, Object msg, ActorContext context, ActorRef sender) {
        _init(actorCtrl, context, sender);
        if (msg instanceof In_NewGuildTrainGetTrainerInfo.Request) {
            onIn_NewGuildTrainGetTrainerInfo((In_NewGuildTrainGetTrainerInfo.Request) msg);
        } else if (msg instanceof In_NewGuildTrainReplace.Request) {
            onIn_NewGuildTrainReplace((In_NewGuildTrainReplace.Request) msg);
        } else if (msg instanceof In_NewGuildTrainUnlock.Request) {
            onIn_NewGuildTrainUnlock((In_NewGuildTrainUnlock.Request) msg);
        } else if (msg instanceof In_NewGuildTrainStamp.Request) {
            onIn_NewGuildTrainStamp((In_NewGuildTrainStamp.Request) msg);
        } else if (msg instanceof In_NewGuildTrainAccelerate.Request) {
            onIn_NewGuildTrainAccelerate((In_NewGuildTrainAccelerate.Request) msg);
        } else if (msg instanceof In_NewGuildTrainGetMember.Request) {
            onIn_NewGuildTrainGetMember((In_NewGuildTrainGetMember.Request) msg);
        }
    }

    private void _init(NewGuildCenterCtrl actorCtrl, ActorContext context, ActorRef sender) {
        this.sender = sender;
        this.context = context;
        this.actorCtrl = actorCtrl;
    }

    private void onIn_NewGuildTrainGetMember(In_NewGuildTrainGetMember.Request request) {
        In_NewGuildTrainGetMember.Response response = null;
        try {
            SimplePlayer simplePlayer = request.getSimplePlayer();
            LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
            LogicCheckUtils.validateParam(String.class, simplePlayer.getPlayerId());
            NewGuildCenter guildCenter = actorCtrl.getNewGuildCenter(simplePlayer.getOutRealmId());
            NewGuild guild = actorCtrl.getGuildById(guildCenter, simplePlayer.getGuildId());
            if (!NewGuildCenterUtils.isFunctionOpen(guild, GuildBuildTypeEnum.GB_TRAIN)) {
                String msg = "社团训练所没有开启";
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_NO_OPEN_ERROR);
            }
            List<NewGuildCenterPlayer> guildCenterPlayerList = new ArrayList<>(guild.getPlayerIdToGuildPlayerInfo().values());
            response = new In_NewGuildTrainGetMember.Response(request, guildCenterPlayerList);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildTrainGetMember.Response(resultCode, request);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildTrainGetMember.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), request);
        } finally {
            sender.tell(response, context.self());
        }
    }

    private int _getAccelerateIndex(In_NewGuildTrainAccelerate.Request request) {
        switch (request.getAction()) {
            case RESP_ACCELERATE:
                return request.getIndex();
            case RESP_RANDOM_ACCELERATE:
                return MagicNumbers.DEFAULT_ONE;
            default:
                return MagicNumbers.DEFAULT_ONE;
        }
    }

    private boolean _hasTrainerByAction(Sm_NewGuildTrain.Action action, int index, NewGuildCenterPlayer beGuildCenterPlayer) {
        switch (action) {
            case RESP_ACCELERATE:
                NewGuildCenterUtils.hasTrainer(beGuildCenterPlayer, index);
            case RESP_RANDOM_ACCELERATE:
                if (!NewGuildCenterUtils.hasTrainer(beGuildCenterPlayer, index)) {
                    if (beGuildCenterPlayer.getIndexAndTrainer().size() < index + 1) {
                        _hasTrainerByAction(action, index + 1, beGuildCenterPlayer);
                    } else {
                        return false;
                    }
                }
            default:
                return false;
        }
    }


    private void onIn_NewGuildTrainAccelerate(In_NewGuildTrainAccelerate.Request request) {
        In_NewGuildTrainAccelerate.Response response = null;
        try {
            SimplePlayer simplePlayer = request.getSimplePlayer();
            String playerId = request.getPlayerId();
            int index = _getAccelerateIndex(request);
            LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
            LogicCheckUtils.validateParam(String.class, playerId);
            NewGuildCenter guildCenter = actorCtrl.getNewGuildCenter(simplePlayer.getOutRealmId());
            NewGuild guild = actorCtrl.getGuildById(guildCenter, simplePlayer.getGuildId());
            if (!NewGuildCenterUtils.isFunctionOpen(guild, GuildBuildTypeEnum.GB_TRAIN)) {
                String msg = "社团训练所没有开启";
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_NO_OPEN_ERROR);
            }
            if (!NewGuildCenterUtils.isInGuild(guild, playerId)) {
                String msg = "社团中找不到被加速的玩家";
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
            }
            NewGuildCenterPlayer beGuildCenterPlayer = guild.getPlayerIdToGuildPlayerInfo().get(playerId);
            NewGuildCenterPlayer guildCenterPlayer = guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId());
            if (NewGuildCenterUtils.isBeAccelerateTimesMax(beGuildCenterPlayer)) {
                String msg = String.format("对方被动加速次数已经用完,playerId:%s", beGuildCenterPlayer.getPlayerId());
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_BEACCELERATE_MAX_ERROR);
            }
            NewGuildTrainer trainer = _getBeAccelerateTrainer(beGuildCenterPlayer, index, request.getAction());
            if (_hasTrainerByAction(request.getAction(), index, beGuildCenterPlayer)) {
                String msg = String.format("被加速玩家训练桩上没有格斗家:%s", beGuildCenterPlayer.getPlayerId());
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_NO_HERO_ERROR);
            }
            if (NewGuildCenterUtils.isAccelerateTimesMax(guildCenterPlayer)) {
                String msg = "主动加速次数已经用完";
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_ACCELERATE_MAX_ERROR);
            }
            // 需要将结算前的状态clone下来传给extp，到extp那边需要再结算一下算出来要加的经验
            NewGuildTrainer trainerClone = trainer.clone();
            //检查对方被动加速前,先给他这个桩位结算一下,有可能长时间没计算已经切日了
            actorCtrl.trySettle(beGuildCenterPlayer, trainer);
            _saveAccelerateTimes(simplePlayer, guildCenterPlayer, beGuildCenterPlayer);
            actorCtrl.saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
            NewGuildCenterPlayer cloneBeGuildCenterPlayer = beGuildCenterPlayer.clone();
            response = new In_NewGuildTrainAccelerate.Response(request, cloneBeGuildCenterPlayer, trainerClone, guildCenterPlayer.clone());
            CheckPlayerOnlineMsgRequest<Response> checkOnlineRequest = new CheckPlayerOnlineMsgRequest<>(request.getPlayerId(), response);
            _sendCheckPlayerOnlineMsg(checkOnlineRequest, context);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildTrainAccelerate.Response(resultCode, request);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildTrainAccelerate.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), request);
        } finally {
            sender.tell(response, context.self());
        }
    }

    /**
     * 获取被加速的训练桩信息(根据action获取)
     * 如果是普通加速，直接获取
     * 如果是随机加速，看index是否存在，如果存在看对应桩的格斗家是否存在，如果存在直接获取
     * 如果随机加速的index对应桩上没有格斗家(heroId是0)，将index设置为0，每次+1，递归调用这个方法，看是否存在，
     * 当index当与所有训练桩的大小时，抛异常没有找到可以加速的格斗家
     *
     * @param beGuildCenterPlayer
     * @param index
     * @param action
     * @return
     */
    private NewGuildTrainer _getBeAccelerateTrainer(NewGuildCenterPlayer beGuildCenterPlayer, int index, Sm_NewGuildTrain.Action action) {
        switch (action) {
            case RESP_ACCELERATE:
                return beGuildCenterPlayer.getIndexAndTrainer().get(index);
            case RESP_RANDOM_ACCELERATE:
                if (beGuildCenterPlayer.getIndexAndTrainer().containsKey(index)) {
                    if (beGuildCenterPlayer.getIndexAndTrainer().get(index).getHeroId() != MagicNumbers.DEFAULT_ZERO) {
                        return beGuildCenterPlayer.getIndexAndTrainer().get(index);
                    } else {
                        return _getCanUseTrainer(beGuildCenterPlayer);
                    }
                }
                String msg = String.format("被加速玩家训练桩上没有格斗家:%s", beGuildCenterPlayer.getPlayerId());
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_NO_HERO_ERROR);
            default:
                msg = String.format("被加速玩家训练桩上没有格斗家:%s", beGuildCenterPlayer.getPlayerId());
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_NO_HERO_ERROR);
        }
    }

    /**
     * 获取一个可以使用的trainer
     *
     * @param guildCenterPlayer
     * @return
     */
    private NewGuildTrainer _getCanUseTrainer(NewGuildCenterPlayer guildCenterPlayer) {
        for (Entry<Integer, NewGuildTrainer> entry : guildCenterPlayer.getIndexAndTrainer().entrySet()) {
            if (entry.getValue().getHeroId() != MagicNumbers.DEFAULT_ZERO) {
                return entry.getValue();
            }
        }
        String msg = String.format("被加速玩家训练桩上没有格斗家:%s", guildCenterPlayer.getPlayerId());
        throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_NO_HERO_ERROR);
    }

    private void _sendCheckPlayerOnlineMsg(InnerMsg request, ActorContext actorContext) {
        ClusterMessageSender.sendMsgToPath(actorContext, request, ActorSystemPath.WS_GameServer_Selection_World);
    }

    private void _saveAccelerateTimes(SimplePlayer simplePlayer, NewGuildCenterPlayer guildCenterPlayer, NewGuildCenterPlayer beGuildCenterPlayer) {
        guildCenterPlayer.setAccelerateTimes(guildCenterPlayer.getAccelerateTimes() + MagicNumbers.DEFAULT_ONE);
        beGuildCenterPlayer.getAcceleratePlayerName().add(simplePlayer.getPlayerName());
    }

    private void onIn_NewGuildTrainStamp(In_NewGuildTrainStamp.Request request) {
        In_NewGuildTrainStamp.Response response = null;
        try {
            SimplePlayer simplePlayer = request.getSimplePlayer();
            String playerId = request.getPlayerId();
            LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
            LogicCheckUtils.validateParam(String.class, playerId);
            NewGuildCenter guildCenter = actorCtrl.getNewGuildCenter(simplePlayer.getOutRealmId());
            NewGuild guild = actorCtrl.getGuildById(guildCenter, simplePlayer.getGuildId());
            if (!NewGuildCenterUtils.isFunctionOpen(guild, GuildBuildTypeEnum.GB_TRAIN)) {
                String msg = "社团训练所没有开启";
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_NO_OPEN_ERROR);
            }
            if (!NewGuildCenterUtils.isInGuild(guild, playerId)) {
                String msg = "操作的玩家不在社团中";
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
            }
            NewGuildCenterPlayer guildCenterPlayer = guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId());
            if (!guildCenterPlayer.getStampPlayerIds().contains(playerId) && guildCenterPlayer.getStampPlayerIds().size() >= MagicNumbers.MAX_STAMP_COUNT) {
                String msg = "标记人数以达到最大值";
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_STAMP_MAX_ERROR);
            }
            if (simplePlayer.getPlayerId().equals(playerId)) {
                String msg = "不能标记自己";
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_NOT_STAMP_SELF);
            }
            if (!guildCenterPlayer.getStampPlayerIds().contains(playerId)) {
                guildCenterPlayer.getStampPlayerIds().add(playerId);
            } else {
                guildCenterPlayer.getStampPlayerIds().remove(playerId);
            }
            actorCtrl.saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
            NewGuildCenterPlayer stampGuildCenterPlayer = guild.getPlayerIdToGuildPlayerInfo().get(playerId);
            response = new In_NewGuildTrainStamp.Response(request, stampGuildCenterPlayer, guildCenterPlayer);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildTrainStamp.Response(resultCode, request);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildTrainStamp.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), request);
        } finally {
            sender.tell(response, context.self());
        }
    }

    private void onIn_NewGuildTrainUnlock(In_NewGuildTrainUnlock.Request request) {
        In_NewGuildTrainUnlock.Response response = null;
        try {
            SimplePlayer simplePlayer = request.getSimplePlayer();
            String guildId = request.getGuildId();
            int index = request.getIndex();
            LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
            LogicCheckUtils.validateParam(String.class, guildId);
            NewGuildCenter guildCenter = actorCtrl.getNewGuildCenter(simplePlayer.getOutRealmId());
            NewGuild guild = actorCtrl.getGuildById(guildCenter, guildId);
            if (!NewGuildCenterUtils.isFunctionOpen(guild, GuildBuildTypeEnum.GB_TRAIN)) {
                String msg = "社团训练所没有开启";
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_NO_OPEN_ERROR);
            }
            NewGuildCenterPlayer guildCenterPlayer = guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId());
            if (NewGuildCenterUtils.isTrainUnlock(guildCenterPlayer, index)) {
                String msg = String.format("这个训练桩已经解锁:%s" + index);
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_ALREADY_UNLOCK_ERROR);
            }
            guildCenterPlayer.getIndexAndTrainer().putIfAbsent(index, new NewGuildTrainer(index));
            actorCtrl.saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
            response = new In_NewGuildTrainUnlock.Response(request, guildCenterPlayer.getIndexAndTrainer().get(index).clone());
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildTrainUnlock.Response(resultCode, request);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildTrainUnlock.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), request);
        } finally {
            sender.tell(response, context.self());
        }

    }

    private void onIn_NewGuildTrainReplace(In_NewGuildTrainReplace.Request request) {
        In_NewGuildTrainReplace.Response response = null;
        try {
            SimplePlayer simplePlayer = request.getSimplePlayer();
            String guildId = request.getGuildId();
            int index = request.getIndex();
            int heroId = request.getHeroId();
            LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
            LogicCheckUtils.validateParam(String.class, guildId);
            NewGuildCenter guildCenter = actorCtrl.getNewGuildCenter(simplePlayer.getOutRealmId());
            NewGuild guild = actorCtrl.getGuildById(guildCenter, guildId);
            if (!NewGuildCenterUtils.isFunctionOpen(guild, GuildBuildTypeEnum.GB_TRAIN)) {
                String msg = "社团训练所没有开启";
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_NO_OPEN_ERROR);
            }
            NewGuildCenterPlayer guildCenterPlayer = guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId());
            if (!NewGuildCenterUtils.isTrainUnlock(guildCenterPlayer, index)) {
                String msg = String.format("这个训练桩未解锁:%s" + index);
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_IS_LOCK_ERROR);
            }
            TrainerReplaceResult result = NewGuildCenterUtils.tryReplace(guild, simplePlayer, heroId, index);
            actorCtrl.saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
            response = new In_NewGuildTrainReplace.Response(request, result);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildTrainReplace.Response(resultCode, request);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildTrainReplace.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), request);
        } finally {
            sender.tell(response, context.self());
        }
    }

    private void onIn_NewGuildTrainGetTrainerInfo(In_NewGuildTrainGetTrainerInfo.Request request) {
        In_NewGuildTrainGetTrainerInfo.Response response = null;
        try {
            SimplePlayer simplePlayer = request.getSimplePlayer();
            LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
            NewGuildCenter guildCenter = actorCtrl.getNewGuildCenter(simplePlayer.getOutRealmId());
            NewGuild guild = actorCtrl.getGuildById(guildCenter, simplePlayer.getGuildId());
            if (!NewGuildCenterUtils.isFunctionOpen(guild, GuildBuildTypeEnum.GB_TRAIN)) {
                String msg = "社团训练所没有开启";
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TRAIN_NO_OPEN_ERROR);
            }
            NewGuildCenterPlayer guildCenterPlayer = guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId());
            List<NewGuildTrainer> trainerList = new ArrayList<>(guildCenterPlayer.getIndexAndTrainer().values());
            List<NewGuildTrainer> beforeTrainerList = CloneUtils.cloneWsCloneableList(trainerList);
            _tryInitTrainer(guildCenterPlayer);
            actorCtrl.trySettleAllTrainer(guildCenterPlayer, trainerList);
            actorCtrl.saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
            response = new In_NewGuildTrainGetTrainerInfo.Response(request, beforeTrainerList, guildCenterPlayer);
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildTrainGetTrainerInfo.Response(resultCode, request);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildTrainGetTrainerInfo.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), request);
        } finally {
            sender.tell(response, context.self());

        }
    }

    private void _tryInitTrainer(NewGuildCenterPlayer guildCenterPlayer) {
        if (guildCenterPlayer.getIndexAndTrainer().size() == 0) {
            guildCenterPlayer.getIndexAndTrainer().putIfAbsent(MagicNumbers.DEFAULT_ONE, new NewGuildTrainer(MagicNumbers.DEFAULT_ONE));
            guildCenterPlayer.getIndexAndTrainer().putIfAbsent(MagicNumbers.DEFAULT_TWO, new NewGuildTrainer(MagicNumbers.DEFAULT_TWO));
        }
    }
}
