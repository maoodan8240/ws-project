package ws.gameServer.features.standalone.actor.newGuildCenter.ctrl;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.mc.controler.AbstractControler;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.actor.cluster.ClusterListener;
import ws.gameServer.features.standalone.actor.newGuildCenter.utils.NewGuildCenterUtils;
import ws.gameServer.features.standalone.extp.mails.utils.MailsCtrlUtils;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildAppoint;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildGetApplys;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildGiveJob;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildJoin;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildSearch;
import ws.gameServer.features.standalone.extp.newGuild.utils.NewGuildCtrlUtils;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.protos.CommonProtos.Sm_Common_Round;
import ws.protos.EnumsProtos.CommonRankTypeEnum;
import ws.protos.EnumsProtos.GuildInstituteTypeEnum;
import ws.protos.EnumsProtos.GuildJobEnum;
import ws.protos.EnumsProtos.GuildJoinTypeEnum;
import ws.protos.EnumsProtos.GuildResearchProjectTypeEnum;
import ws.protos.NewGuildProtos.Cm_NewGuild;
import ws.protos.NewGuildResearchProtos.Cm_NewGuildResearch;
import ws.protos.errorCode.ErrorCodeProtos.ErrorCodeEnum;
import ws.relationship.base.MagicNumbers;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.base.MagicWords_Redis;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.CheckPlayerOnlineMsgRequest;
import ws.relationship.base.msg.mail.In_AddGmMail;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.guild.NewGuildDao;
import ws.relationship.daos.simpleId.SimpleIdDao;
import ws.relationship.enums.SimpleIdTypeEnum;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_GuildTrack_Row;
import ws.relationship.table.tableRows.Table_Guild_Row;
import ws.relationship.topLevelPojos.common.LevelUpObj;
import ws.relationship.topLevelPojos.common.TopLevelHolder;
import ws.relationship.topLevelPojos.mails.Mail;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildApplyInfo;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenter;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenterPlayer;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildInstitute;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildTrack;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildTrainer;
import ws.relationship.topLevelPojos.simpleGuild.SimpleGuild;
import ws.relationship.topLevelPojos.simpleId.SimpleId;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.ClusterMessageSender;
import ws.relationship.utils.DBUtils;
import ws.relationship.utils.InitRealmCreatedTargetsDB;
import ws.relationship.utils.RedisRankUtils;
import ws.relationship.utils.SimplePojoUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by lee on 16-11-30.
 */
public class _NewGuildCenterCtrl extends AbstractControler<TopLevelHolder> implements NewGuildCenterCtrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(_NewGuildCenterCtrl.class);
    private static final SimpleIdDao SIMPLE_ID_DAO = DaoContainer.getDao(SimpleId.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static Map<Integer, NewGuildDao> centerOuterRealmIdToDao = new HashMap<>();
    private Map<Integer, NewGuildCenter> centerOutRealmIdToGuildCenters = new HashMap<>();
    private ActorRef curSender;
    private ActorContext context;


    static {
        SIMPLE_ID_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
    }

    @Override
    public void sendPrivateMsg(PrivateMsg privateMsg) {

    }

    @Override
    public Map<Integer, NewGuildCenter> getCenterOutRealmIdToGuildCenters() {
        return centerOutRealmIdToGuildCenters;
    }

    @Override
    public NewGuild create(String guildName, int guildIcon, SimplePlayer simplePlayer) {
        LogicCheckUtils.validateParam(String.class, guildName);
        LogicCheckUtils.validateParam(Integer.class, guildIcon);
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        if (_isExistName(guildName, guildCenter)) {
            String msg = "社团名已存在";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_EXISTNAME);
        }
        NewGuild guild = _createNewGuild(guildName, guildIcon, simplePlayer);
        guildCenter.getIdToGuild().put(guild.getGuildId(), guild);
        guildCenter.getGuildIdToApplyList().putIfAbsent(guild.getGuildId(), new ArrayList<>());
        _addPlayerInGuild(guild, simplePlayer, GuildJobEnum.GJ_MASTER);
        saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
        _saveSimpleGuild(guildCenter, guild);
        DBUtils.saveStringPojo(simplePlayer.getOutRealmId(), guildCenter);
        return guild;
    }


    @Override
    public In_NewGuildJoin.Response join(In_NewGuildJoin.Request request) {
        String guildId = request.getGuildId();
        SimplePlayer simplePlayer = request.getSimplePlayer();
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        LogicCheckUtils.validateParam(String.class, guildId);
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        if (!_isExistGuild(guildId, guildCenter)) {
            String msg = "社团不存在";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_NO_GUILD);
        }
        NewGuild guild = getGuildById(guildCenter, guildId);
        if (_isGuildFull(guild)) {
            String msg = "社团人数已满";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_GUILD_FULL);
        }
        if (NewGuildCenterUtils.isInGuild(guild, simplePlayer.getPlayerId())) {
            String msg = "批准的玩家已经在社团中";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_IN_GUILD);
        }
        _addToApplyList(guildCenter, guild, simplePlayer);
        if (!_isPlayerCanOneKeyJoin(guild, simplePlayer)) {
            DBUtils.saveStringPojo(simplePlayer.getOutRealmId(), guildCenter);
            return new In_NewGuildJoin.Response(request, guild, false);
        }
        _removeApply(guildCenter, guild, simplePlayer.getPlayerId());
        _addPlayerInGuild(guild, simplePlayer, GuildJobEnum.GJ_MEMBER);
        addNewGuildTrack(Cm_NewGuild.Action.AGREE, guild, simplePlayer.getPlayerName());
        saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
        DBUtils.saveStringPojo(simplePlayer.getOutRealmId(), guildCenter);
        return new In_NewGuildJoin.Response(request, guild, true);
    }

    @Override
    public NewGuild agree(String guildId, SimplePlayer simplePlayer, SimplePlayer addSimplePlayer) {
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        LogicCheckUtils.validateParam(Integer.class, addSimplePlayer.getOutRealmId());
        LogicCheckUtils.validateParam(String.class, guildId);
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        NewGuild guild = getGuildById(guildCenter, guildId);
        if (!_hasPermission(guild, simplePlayer)) {
            String msg = String.format("没有权限操作,职位:%s", guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId()).getJob());
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_NO_PERMISSIONS);
        }
        if (_isGuildFull(guild)) {
            String msg = "社团人数已满";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_GUILD_FULL);
        }
        if (!_isInApplyList(guildCenter, guild, addSimplePlayer.getPlayerId())) {
            String msg = "批准的玩家不在申请列表中";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_NOT_IN_APPLYLIST);
        }
        if (NewGuildCenterUtils.isInGuild(guild, addSimplePlayer.getPlayerId())) {
            String msg = "批准的玩家已经在社团中";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_IN_GUILD);
        }
        _removeApply(guildCenter, guild, addSimplePlayer.getPlayerId());
        _addPlayerInGuild(guild, addSimplePlayer, GuildJobEnum.GJ_MEMBER);
        addNewGuildTrack(Cm_NewGuild.Action.AGREE, guild, addSimplePlayer.getPlayerName());
        saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
        DBUtils.saveStringPojo(simplePlayer.getOutRealmId(), guildCenter);
        return guild;
    }

    @Override
    public List<NewGuild> getGuildInfo(String guildId, SimplePlayer simplePlayer) {
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        List<NewGuild> guildList = new ArrayList<>();
        if (StringUtils.isNotBlank(guildId)) {
            int outRealmId = simplePlayer.getOutRealmId();
            NewGuildCenter guildCenter = getNewGuildCenter(outRealmId);
            NewGuild guild = getGuildById(guildCenter, guildId);
            guildList.add(guild);
            _saveSimpleGuild(guildCenter, guild);
            return guildList;
        }
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        // TODO 启动服务器时加载所有guild到guildCenter
        if (guildCenter.getIdToGuild().size() == 0) {
            NewGuildDao dao = _getDao(guildCenter.getOutRealmId());
            List<NewGuild> dbGuildList = dao.findAll();
            for (NewGuild guild : dbGuildList) {
                guildCenter.getIdToGuild().put(guild.getGuildId(), guild);
            }
        }
        guildList.addAll(new ArrayList<>(guildCenter.getIdToGuild().values()));
        return guildList;
    }


    @Override
    public void refuse(String guildId, SimplePlayer simplePlayer, SimplePlayer refuseSimplePlayer) {
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        LogicCheckUtils.validateParam(Integer.class, refuseSimplePlayer.getOutRealmId());
        LogicCheckUtils.validateParam(String.class, guildId);
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        NewGuild guild = getGuildById(guildCenter, guildId);
        if (!_hasPermission(guild, simplePlayer)) {
            String msg = String.format("没有权限操作,职位:%s", guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId()).getJob());
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_NO_PERMISSIONS);
        }
        if (!_isInApplyList(guildCenter, guild, refuseSimplePlayer.getPlayerId())) {
            String msg = "批准的玩家不在申请列表中";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_NOT_IN_APPLYLIST);
        }
        if (NewGuildCenterUtils.isInGuild(guild, refuseSimplePlayer.getPlayerId())) {
            String msg = "批准的玩家已经在社团中";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_IN_GUILD);
        }
        _removeApply(guildCenter, guild, refuseSimplePlayer.getPlayerId());
        DBUtils.saveStringPojo(simplePlayer.getOutRealmId(), guildCenter);
    }

    @Override
    public void kick(String guildId, SimplePlayer simplePlayer, SimplePlayer kickSimplePlayer) {
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        LogicCheckUtils.validateParam(Integer.class, kickSimplePlayer.getOutRealmId());
        LogicCheckUtils.validateParam(String.class, guildId);
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        NewGuild guild = getGuildById(guildCenter, guildId);
        if (!_hasPermission(guild, simplePlayer)) {
            String msg = String.format("没有权限操作,职位:%s", guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId()).getJob());
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_NO_PERMISSIONS);
        }
        if (!NewGuildCenterUtils.isInGuild(guild, kickSimplePlayer.getPlayerId())) {
            String msg = "操作的玩家不在社团中";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        _kickPlayer(guild, kickSimplePlayer);
        saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
    }

    @Override
    public void out(String guildId, SimplePlayer simplePlayer) {
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        LogicCheckUtils.validateParam(String.class, guildId);
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        NewGuild guild = getGuildById(guildCenter, guildId);
        if (!NewGuildCenterUtils.isInGuild(guild, simplePlayer.getPlayerId())) {
            String msg = "操作的玩家不在社团中";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        _outGuild(guild, simplePlayer);
        addNewGuildTrack(Cm_NewGuild.Action.OUT, guild, simplePlayer.getPlayerName());
        saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
    }

    @Override
    public List<String> disband(String guildId, SimplePlayer simplePlayer) {
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        LogicCheckUtils.validateParam(String.class, guildId);
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        NewGuild guild = getGuildById(guildCenter, guildId);
        if (!_isMaster(guild, simplePlayer)) {
            String msg = "不是社团长，没有权限操作";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_NO_PERMISSIONS);
        }
        if (guild.getPlayerIdToGuildPlayerInfo().size() > MagicNumbers.DEFAULT_ONE) {
            String msg = "社团还有其他成员，无法解散";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_NO_PERMISSIONS);
        }
        if (!NewGuildCenterUtils.isInGuild(guild, simplePlayer.getPlayerId())) {
            String msg = "操作的玩家不在社团中";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        List<String> guildPlayerIds = new ArrayList<>(guild.getPlayerIdToGuildPlayerInfo().keySet());
        _removeGuild(guildCenter, guildId);
        guildPlayerIds.remove(simplePlayer.getPlayerId());
        _removeGuildFromMongoDB(simplePlayer.getOutRealmId(), guild);
        DBUtils.saveStringPojo(simplePlayer.getOutRealmId(), guildCenter);
        RedisRankUtils.removeFromRankByMember(simplePlayer.getOutRealmId(), CommonRankTypeEnum.RK_GUILDBATTLEVALUE, guildId);
        return guildPlayerIds;
    }


    @Override
    public NewGuild change(String guildId, SimplePlayer simplePlayer, GuildJoinTypeEnum joinTypeEnum, Sm_Common_Round round) {
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        LogicCheckUtils.validateParam(String.class, guildId);
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        NewGuild guild = getGuildById(guildCenter, guildId);
        if (!_hasPermission(guild, simplePlayer)) {
            String msg = String.format("没有权限操作,职位:%s", guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId()).getJob());
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_NO_PERMISSIONS);
        }
        if (!NewGuildCenterUtils.isInGuild(guild, simplePlayer.getPlayerId())) {
            String msg = "操作的玩家不在社团中";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        _change(guild, joinTypeEnum, round.getMin(), round.getMax());
        saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
        return guild;
    }

    @Override
    public NewGuild search(In_NewGuildSearch.Request request) {
        String searchArg = request.getSearchArg();
        int outRealmId = request.getSimplePlayer().getOutRealmId();
        LogicCheckUtils.validateParam(Integer.class, outRealmId);
        NewGuildCenter guildCenter = getNewGuildCenter(outRealmId);
        NewGuild guild;
        if (!StringUtils.isNumeric(searchArg)) {
            return _findGuildByName(guildCenter, searchArg);
        } else {
            try {
                int simpleId = Integer.parseInt(searchArg);
                guild = _findGuildBySimpleId(guildCenter, simpleId);
                if (guild == null) {
                    return _findGuildByName(guildCenter, searchArg);
                }
            } catch (NumberFormatException e) {
                LOGGER.error("" + e);
                String msg = String.format("搜索社团的参数是个数字字符串,但是数值很大无法转换,参数:%s" + searchArg);
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_ILLEGAL_ARGS);
            }

        }
        return guild;
    }

    @Override
    public void setCurContext(ActorContext context) {
        this.context = context;
    }

    @Override
    public void setCurSendActorRef(ActorRef sender) {
        this.curSender = sender;
    }

    @Override
    public List<NewGuildApplyInfo> getApplys(In_NewGuildGetApplys.Request request) {
        String guildId = request.getGuildId();
        SimplePlayer simplePlayer = request.getSimplePlayer();
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        LogicCheckUtils.validateParam(String.class, guildId);
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        NewGuild guild = getGuildById(guildCenter, guildId);
        if (!_hasPermission(guild, simplePlayer)) {
            String msg = String.format("没有权限操作,职位:%s", guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId()).getJob());
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_NO_PERMISSIONS);
        }
        return guildCenter.getGuildIdToApplyList().get(guildId);
    }

    @Override
    public NewGuild cancel(String guildId, SimplePlayer simplePlayer) {
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        LogicCheckUtils.validateParam(String.class, guildId);
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        NewGuild guild = getGuildById(guildCenter, guildId);
        if (guildCenter.getGuildIdToApplyList().containsKey(guildId)) {
            guildCenter.getGuildIdToApplyList().get(guildId).removeIf(playerId -> playerId.equals(simplePlayer.getPlayerId()));
        }
        if (_isInApply(simplePlayer.getPlayerId(), guildCenter.getGuildIdToApplyList().get(guildId))) {
            _removeApply(guildCenter, guild, simplePlayer.getPlayerId());
        }
        DBUtils.saveStringPojo(simplePlayer.getOutRealmId(), guildCenter);
        return guild;
    }

    @Override
    public NewGuild modify(SimplePlayer simplePlayer, String guildNotic, int guildIcon, String guildName) {
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        NewGuild guild = getGuildById(guildCenter, simplePlayer.getGuildId());
        if (!_hasPermission(guild, simplePlayer)) {
            String msg = String.format("没有权限操作,职位:%s", guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId()).getJob());
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_NO_PERMISSIONS);
        }
        if (StringUtils.isNotBlank(guildNotic)) {
            guild.setGuildNotice(guildNotic);
        }
        if (guildIcon != 0) {
            guild.setGuildIcon(guildIcon);
        }
        if (StringUtils.isNotBlank(guildName)) {
            guild.setGuildName(guildName);
        }
        saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
        return guild;
    }

    @Override
    public NewGuild oneKeyJoin(SimplePlayer simplePlayer) {
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        List<NewGuild> guildList = new ArrayList<>(guildCenter.getIdToGuild().values());
        NewGuildCtrlUtils.sortGuild(guildList);
        NewGuild guild = _chooseBestGuild(guildList, simplePlayer);
        _addPlayerInGuild(guild, simplePlayer, GuildJobEnum.GJ_MEMBER);
        _removeApply(guildCenter, guild, simplePlayer.getPlayerId());
        saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
        DBUtils.saveStringPojo(simplePlayer.getOutRealmId(), guildCenter);
        return guild;
    }


    @Override
    public In_NewGuildGiveJob.Response giveJob(In_NewGuildGiveJob.Request request) {
        SimplePlayer simplePlayer = request.getSimplePlayer();
        SimplePlayer giveSimplePlayer = request.getGiveSimplePlayer();
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        LogicCheckUtils.validateParam(Integer.class, giveSimplePlayer.getOutRealmId());
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        NewGuild guild = getGuildById(guildCenter, simplePlayer.getGuildId());
        if (!_isMaster(guild, simplePlayer)) {
            String msg = String.format("没有权限操作,职位:%s", guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId()).getJob());
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_NO_PERMISSIONS);
        }
        if (!NewGuildCenterUtils.isInGuild(guild, giveSimplePlayer.getPlayerId())) {
            String msg = "操作的玩家不在社团中";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        if (!_isTimeCanGiveJob(guild)) {
            String msg = "距离最后一次传职时间未到24小时";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_TIME_NOT_GIVE_JOB);
        }
        Map<String, NewGuildCenterPlayer> idAndGuildCenterPlayer = guild.getPlayerIdToGuildPlayerInfo();
        NewGuildCenterPlayer guildCenterPlayer = idAndGuildCenterPlayer.get(simplePlayer.getPlayerId());
        NewGuildCenterPlayer beGuildCenterPlayer = idAndGuildCenterPlayer.get(giveSimplePlayer.getPlayerId());
        _giveJob(guild, guildCenterPlayer, beGuildCenterPlayer, giveSimplePlayer);
        addNewGuildTrack(Cm_NewGuild.Action.GIVE_JOB, guild, giveSimplePlayer.getPlayerName(), GuildJobEnum.GJ_MASTER_VALUE + "");
        saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
        return new In_NewGuildGiveJob.Response(request, guildCenterPlayer, beGuildCenterPlayer, guild);
    }

    @Override
    public In_NewGuildAppoint.Response appoint(In_NewGuildAppoint.Request request) {
        SimplePlayer simplePlayer = request.getSimplePlayer();
        SimplePlayer appointSimplePlayer = request.getAppointSimplePlayer();
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        LogicCheckUtils.validateParam(Integer.class, appointSimplePlayer.getOutRealmId());
        GuildJobEnum job = request.getJob();
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        NewGuild guild = getGuildById(guildCenter, simplePlayer.getGuildId());
        if (!_hasPermission(guild, simplePlayer)) {
            String msg = String.format("没有权限操作,职位:%s", guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId()).getJob());
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_NO_PERMISSIONS);
        }
        if (!_isCanAppoint(guild, job)) {
            String msg = "目前该职位已满";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_JOB_MAX);
        }
        if (!NewGuildCenterUtils.isInGuild(guild, appointSimplePlayer.getPlayerId())) {
            String msg = "操作的玩家不在社团中";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_IS_NOT_IN_GUILD);
        }
        if (!_isJobCountCanAppoint(guild, job)) {
            String msg = String.format("当前社团职务数量已经达到最大值:%s", job);
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_JOB_MAX);
        }
        NewGuildCenterPlayer beAppointGuildCenterPlayer = _getGuildCenterPlayer(guild, appointSimplePlayer);
        beAppointGuildCenterPlayer.setJob(job);
        if (job == GuildJobEnum.GJ_MEMBER) {
            addNewGuildTrack(Cm_NewGuild.Action.APPOINT, guild, appointSimplePlayer.getPlayerName(), job.getNumber() + "");
        }
        saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
        return new In_NewGuildAppoint.Response(request, beAppointGuildCenterPlayer, guild);
    }

    @Override
    public NewGuild mails(SimplePlayer simplePlayer, String content, String title) {
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        LogicCheckUtils.validateParam(String.class, content);
        LogicCheckUtils.validateParam(String.class, title);
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        NewGuild guild = getGuildById(guildCenter, simplePlayer.getGuildId());
        if (!_hasPermission(guild, simplePlayer)) {
            String msg = String.format("没有权限操作,职位:%s", guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId()).getJob());
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_NO_PERMISSIONS);
        }
        if (!_isMailCountEnough(guild)) {
            String msg = "邮件数量不足";
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_MAILS_COUNT_ERROR);
        }
        List<String> playerIds = new ArrayList<>(guild.getPlayerIdToGuildPlayerInfo().keySet());
        Mail mail = MailsCtrlUtils.createGmMails(title, content, simplePlayer.getPlayerName());
        _sendMailToPlayers(playerIds, simplePlayer, mail);
        guild.setMailsCount(guild.getMailsCount() + MagicNumbers.DEFAULT_ONE);
        saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
        return guild;
    }


    @Override
    public List<String> oneKeyRefuse(SimplePlayer simplePlayer) {
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        NewGuild guild = getGuildById(guildCenter, simplePlayer.getGuildId());
        if (!_hasPermission(guild, simplePlayer)) {
            String msg = String.format("没有权限操作,职位:%s", guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId()).getJob());
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_NO_PERMISSIONS);
        }
        List<NewGuildApplyInfo> applyInfoList = guildCenter.getGuildIdToApplyList().get(guild.getGuildId());
        List<String> refuesePlayerIds = _removeAllApplyInfoAndGetPlayerIds(applyInfoList, guildCenter, guild);
        guildCenter.getGuildIdToApplyList().put(guild.getGuildId(), new ArrayList<>());
        DBUtils.saveStringPojo(simplePlayer.getOutRealmId(), guildCenter);
        return refuesePlayerIds;
    }


    @Override
    public NewGuild recruit(SimplePlayer simplePlayer) {
        LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
        NewGuildCenter guildCenter = getNewGuildCenter(simplePlayer.getOutRealmId());
        NewGuild guild = getGuildById(guildCenter, simplePlayer.getGuildId());
        if (!_hasPermission(guild, simplePlayer)) {
            String msg = String.format("没有权限操作,职位:%s", guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId()).getJob());
            throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_NO_PERMISSIONS);
        }
        return guild;
    }


    @Override
    public void onDayChanged() {
        for (NewGuildCenter guildCenter : centerOutRealmIdToGuildCenters.values()) {
            for (NewGuild guild : guildCenter.getIdToGuild().values()) {
                _resetGuild(guild);
                saveGuildToMongoDB(guild.getOuterRealmId(), guild);
            }
        }
    }


    /*
    ============================================================以下为内部方法==============================================================
     */


    private void _resetGuild(NewGuild guild) {
        guild.setMailsCount(MagicNumbers.DEFAULT_ZERO);
        guild.setTodayExp(MagicNumbers.DEFAULT_ZERO);
        _resetGuildPlayer(guild);
    }

    private void _resetGuildPlayer(NewGuild guild) {
        for (NewGuildCenterPlayer guildCenterPlayer : guild.getPlayerIdToGuildPlayerInfo().values()) {
            guildCenterPlayer.getAcceleratePlayerName().clear();
            guildCenterPlayer.setAccelerateTimes(MagicNumbers.DEFAULT_ZERO);
            guildCenterPlayer.setTodayContribution(MagicNumbers.DEFAULT_ZERO);
        }
    }


    private void _removeGuildFromMongoDB(int centerOuterRealmId, NewGuild guild) {
        _getDao(centerOuterRealmId).delete(guild);
    }

    @Override
    public void saveGuildToMongoDB(int centerOuterRealmId, NewGuild guild) {
        _getDao(centerOuterRealmId).insertIfExistThenReplace(guild);
    }


    private static NewGuildDao _getDao(int centerOuterRealmId) {
        if (centerOuterRealmIdToDao.containsKey(centerOuterRealmId)) {
            return centerOuterRealmIdToDao.get(centerOuterRealmId);
        }
        NewGuildDao dao = GlobalInjector.getInstance(NewGuildDao.class);
        dao.init(MONGO_DB_CLIENT, MagicWords_Redis.TopLevelPojo_Game_Prefix + centerOuterRealmId);
        centerOuterRealmIdToDao.put(centerOuterRealmId, dao);
        return dao;
    }


    @Override
    public void trySettleAllTrainer(NewGuildCenterPlayer guildCenterPlayer, List<NewGuildTrainer> trainerList) {
        for (NewGuildTrainer trainer : trainerList) {
            trySettle(guildCenterPlayer, trainer);
        }
    }

    /**
     * 当前社团职务数量是否能够任命
     *
     * @param guild
     * @param job
     * @return
     */
    private boolean _isJobCountCanAppoint(NewGuild guild, GuildJobEnum job) {
        if (job == GuildJobEnum.GJ_MEMBER) {
            return true;
        }
        int tabJobCount = _getGuildLvToJobCount(guild, job);
        int jobCount = NewGuildCtrlUtils.getJobCount(guild, job);
        return jobCount < tabJobCount;
    }

    private int _getGuildLvToJobCount(NewGuild guild, GuildJobEnum job) {
        Table_Guild_Row guildRow = NewGuildCenterUtils.getGuildRow(NewGuildCtrlUtils.getGuildLevel(guild));
        for (TupleCell<Integer> tupleCell : guildRow.getJobCount().getAll()) {
            if (tupleCell.get(TupleCell.FIRST) == job.getNumber()) {
                return tupleCell.get(TupleCell.SECOND);
            }
        }
        String msg = String.format("表中没有对应的职务数量:%s", job);
        throw new BusinessLogicMismatchConditionException(msg);
    }


    @Override
    public NewGuildCenter getNewGuildCenter(int centerOuterRealmId) {
        if (centerOutRealmIdToGuildCenters.containsKey(centerOuterRealmId)) {
            return centerOutRealmIdToGuildCenters.get(centerOuterRealmId);
        }
        NewGuildCenter guildCenter = DBUtils.getStringPojo(centerOuterRealmId, NewGuildCenter.class);
        if (guildCenter == null) {
            if (InitRealmCreatedTargetsDB.containsTopLevelPojo(centerOuterRealmId, NewGuildCenter.class.getSimpleName())) {
                throw new BusinessLogicMismatchConditionException("NewGuildCenter已经创建过，但是获取失败，请检查！centerOuterRealmId=" + centerOuterRealmId);
            }
            LOGGER.info("库里没找到这个GuildCenter,创建一个新的");
            guildCenter = new NewGuildCenter(ObjectId.get().toString());
            guildCenter.setOutRealmId(centerOuterRealmId);
            InitRealmCreatedTargetsDB.update(centerOuterRealmId, guildCenter);
            DBUtils.saveStringPojo(centerOuterRealmId, guildCenter);
        }
        centerOutRealmIdToGuildCenters.put(centerOuterRealmId, guildCenter);
        return guildCenter;
    }

    @Override
    public NewGuild getGuildById(NewGuildCenter guildCenter, String guildId) {
        if (guildCenter.getIdToGuild().containsKey(guildId)) {
            LOGGER.debug("通过guildId={} 查询[guildCenter对象]:在缓存中 ... ", guildId);
            return guildCenter.getIdToGuild().get(guildId);
        }
        LOGGER.debug("通过guildId={} 查询[guildCenter对象]:不在缓存中,去数据库中查询 ... ", guildId);
        NewGuild guild = _getDao(guildCenter.getOutRealmId()).findOne(guildId);
        guildCenter.getIdToGuild().put(guildId, guild);
        return guild;
    }


    private NewGuild _findGuildBySimpleId(NewGuildCenter guildCenter, int guildSimpleId) {
        for (Entry<String, NewGuild> entry : guildCenter.getIdToGuild().entrySet()) {
            if (entry.getValue().getSimpleId() == guildSimpleId) {
                LOGGER.debug("通过guildSimpleId={} 查询[guildCenter对象]:在缓存中 ... ", guildSimpleId);
                return entry.getValue();
            }
        }
        LOGGER.debug("通过guildSimpleId={} 查询[guildCenter对象]:不在缓存中,去数据库中查询 ... ", guildSimpleId);
        NewGuild guild = _getDao(guildCenter.getOutRealmId()).queryNewGuildBySimpleId(guildSimpleId);
        if (guild == null) {
            String errorMsg = "未搜索到符合的社团";
            throw new BusinessLogicMismatchConditionException(errorMsg, ErrorCodeEnum.GUILD_NO_GUILD);
        }
        guildCenter.getIdToGuild().put(guild.getGuildId(), guild);
        return guild;
    }

    private NewGuild _findGuildByName(NewGuildCenter guildCenter, String guildName) {
        for (Entry<String, NewGuild> entry : guildCenter.getIdToGuild().entrySet()) {
            if (guildName.equals(entry.getValue().getGuildName())) {
                LOGGER.debug("通过guildName={} 查询[guildCenter对象]:在缓存中 ... ", guildName);
                return entry.getValue();
            }
        }
        LOGGER.debug("通过guildName={} 查询[guildCenter对象]:不在缓存中,去数据库中查询 ... ", guildName);
        NewGuild guild = _getDao(guildCenter.getOutRealmId()).queryNewGuildByName(guildName);
        if (guild == null) {
            String errorMsg = "未搜索到符合的社团";
            throw new BusinessLogicMismatchConditionException(errorMsg, ErrorCodeEnum.GUILD_NO_GUILD);
        }
        guildCenter.getIdToGuild().put(guild.getGuildId(), guild);
        return guild;
    }

    /**
     * Redis保存SimpleGuild
     *
     * @param guildCenter
     * @param guild
     */
    private void _saveSimpleGuild(NewGuildCenter guildCenter, NewGuild guild) {
        List<String> playerIds = new ArrayList<>(guild.getPlayerIdToGuildPlayerInfo().keySet());
        Map<String, SimplePlayer> simplePlayerMap = SimplePojoUtils.querySimplePlayerLisByIds(playerIds, guildCenter.getOutRealmId());
        long battleValue = NewGuildCtrlUtils.getGuildBattleValue(simplePlayerMap);
        guild.setBattleValue(battleValue);
        SimpleGuild simpleGuild = NewGuildCtrlUtils.createSimpleGuild(guild);
        RedisRankUtils.insertRank(battleValue, guild.getGuildId(), guildCenter.getOutRealmId(), CommonRankTypeEnum.RK_GUILDBATTLEVALUE);
        DBUtils.saveHashPojo(guildCenter.getOutRealmId(), simpleGuild);
    }

    /**
     * 删除社团所有的申请
     *
     * @param applyInfoList
     * @param guildCenter
     * @param guild
     */
    private List<String> _removeAllApplyInfoAndGetPlayerIds(List<NewGuildApplyInfo> applyInfoList, NewGuildCenter guildCenter, NewGuild guild) {
        List<String> playerIds = new ArrayList<>();
        for (NewGuildApplyInfo applyInfo : applyInfoList) {
            Iterator<String> it = guildCenter.getPlayerIdToGuildIds().get(applyInfo.getPlayerId()).iterator();
            while (it.hasNext()) {
                String applyGuildId = it.next();
                if (applyGuildId.equals(guild.getGuildId())) {
                    it.remove();
                    playerIds.add(applyInfo.getPlayerId());
                }
            }
        }
        return playerIds;
    }


    @Override
    public void trySettle(NewGuildCenterPlayer guildCenterPlayer, NewGuildTrainer trainer) {
        NewGuildCenterUtils.settle(trainer);
        if (!DateUtils.isSameDay(new Date(), new Date(trainer.getLastSettle()))) {
            _clearAccelerateTimes(guildCenterPlayer);
        }
    }

    /**
     * 重置加速与被动加速次数
     *
     * @param guildCenterPlayer
     */
    private void _clearAccelerateTimes(NewGuildCenterPlayer guildCenterPlayer) {
        if (_isAccelerateTimesNeedReset(guildCenterPlayer)) {
            guildCenterPlayer.getAcceleratePlayerName().clear();
            guildCenterPlayer.setAccelerateTimes(MagicNumbers.DEFAULT_ZERO);
        }
    }

    private boolean _isAccelerateTimesNeedReset(NewGuildCenterPlayer guildCenterPlayer) {
        return guildCenterPlayer.getAcceleratePlayerName().size() != MagicNumbers.DEFAULT_ZERO || guildCenterPlayer.getAccelerateTimes() != MagicNumbers.DEFAULT_ZERO;
    }


    private void _sendMailToPlayers(List<String> playerIds, SimplePlayer simplePlayer, Mail mail) {
        for (String playerId : playerIds) {
            In_AddGmMail.Request addGmMailRequest = new In_AddGmMail.Request(playerId, simplePlayer.getOutRealmId(), mail);
            CheckPlayerOnlineMsgRequest<In_AddGmMail.Request> request = new CheckPlayerOnlineMsgRequest<>(playerId, addGmMailRequest);
            ClusterMessageSender.sendMsgToPath(ClusterListener.getActorContext(), request, ActorSystemPath.WS_GameServer_Selection_World);
        }
    }

    /**
     * 邮件数量是否足够
     *
     * @return
     */

    private boolean _isMailCountEnough(NewGuild guild) {
        int mailsCount = AllServerConfig.Guild_Mails_Count.getConfig();
        return guild.getMailsCount() < mailsCount;
    }

    /**
     * 当前时间和最后一次传职时间是否相隔24小时以上
     *
     * @param guild
     * @return
     */
    private boolean _isTimeCanGiveJob(NewGuild guild) {
        Date date1 = new Date(guild.getGiveJobTime());
        Date date2 = new Date(System.currentTimeMillis());
        return !DateUtils.isSameDay(date1, date2);
    }

    /**
     * 传职
     *
     * @param guild
     * @param giveSimplePlayer
     */
    private void _giveJob(NewGuild guild, NewGuildCenterPlayer guildCenterPlayer, NewGuildCenterPlayer beGuildCenterPlayer, SimplePlayer giveSimplePlayer) {
        guildCenterPlayer.setJob(GuildJobEnum.GJ_MEMBER);
        beGuildCenterPlayer.setJob(GuildJobEnum.GJ_MASTER);
        _changeMaster(guild, giveSimplePlayer);
        guild.setGiveJobTime(System.currentTimeMillis());
    }

    private void _changeMaster(NewGuild guild, SimplePlayer simplePlayer) {
        guild.setMasterPlayerId(simplePlayer.getPlayerId());
    }


    /**
     * 是否可以任命职位
     *
     * @param guild
     * @param job
     * @return
     */
    private boolean _isCanAppoint(NewGuild guild, GuildJobEnum job) {
        List<NewGuildCenterPlayer> guildPlayerList = new ArrayList<>(guild.getPlayerIdToGuildPlayerInfo().values());
        int count = 0;
        for (NewGuildCenterPlayer guildCenterPlayer : guildPlayerList) {
            if (guildCenterPlayer.getJob() == job) {
                count += 1;
            }
        }
        Table_Guild_Row guildRow = NewGuildCenterUtils.getGuildRow(NewGuildCtrlUtils.getGuildLevel(guild));
        int maxJobCount = guildRow.getGuildPeople();
        return maxJobCount > count;
    }


    /**
     * 选择可以快速加入的最好的社团
     *
     * @param guildList
     * @param simplePlayer
     * @return
     */
    private NewGuild _chooseBestGuild(List<NewGuild> guildList, SimplePlayer simplePlayer) {
        for (NewGuild guild : guildList) {
            if (_isLvCanJoin(guild, simplePlayer.getLv()) && _isJobCanJoin(guild) && !_isGuildFull(guild)) {
                return guild;
            }
        }
        String msg = "没有符合条件的社团可以快速加入 ";
        throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_ONE_KEY_JOIN_ERROR);
    }

    /**
     * 玩家是否能快速加入社团
     *
     * @param guild
     * @param simplePlayer
     * @return
     */
    private boolean _isPlayerCanOneKeyJoin(NewGuild guild, SimplePlayer simplePlayer) {
        return _isLvCanJoin(guild, simplePlayer.getLv()) && _isJobCanJoin(guild);
    }

    /**
     * 是否是自由加入
     *
     * @param guild
     * @return
     */
    private boolean _isJobCanJoin(NewGuild guild) {
        return guild.getJoinTypeEnum() == GuildJoinTypeEnum.GJ_FREE;
    }

    /**
     * 玩家等级是否可以加入
     *
     * @param guild
     * @param playerLv
     * @return
     */
    private boolean _isLvCanJoin(NewGuild guild, int playerLv) {
        return playerLv >= guild.getLimitLevel();
    }


    private void _change(NewGuild guild, GuildJoinTypeEnum joinTypeEnum, int min, int max) {
        guild.setJoinTypeEnum(joinTypeEnum);
        guild.setLimitLevel(min);
    }

    private TupleCell<Integer> _getGuildLimitLv() {
        return AllServerConfig.Guild_Join_Limit_Lv.getConfig();
    }

    private void _removeGuild(NewGuildCenter guildCenter, String guildId) {
        _removeGuildApplyList(guildCenter, guildId);
        guildCenter.getIdToGuild().remove(guildId);
    }


    private void _removeGuildApplyList(NewGuildCenter guildCenter, String guildId) {
        if (!guildCenter.getGuildIdToApplyList().containsKey(guildId)) {
            return;
        }
        List<NewGuildApplyInfo> applyInfos = guildCenter.getGuildIdToApplyList().remove(guildId);
        for (NewGuildApplyInfo applyInfo : applyInfos) {
            guildCenter.getPlayerIdToGuildIds().remove(applyInfo.getPlayerId());
        }
    }

    private void _outGuild(NewGuild guild, SimplePlayer simplePlayer) {
        guild.getPlayerIdToGuildPlayerInfo().remove(simplePlayer.getPlayerId());
    }


    private void _kickPlayer(NewGuild guild, SimplePlayer kickSimplePlayer) {
        guild.getPlayerIdToGuildPlayerInfo().remove(kickSimplePlayer.getPlayerId());
    }

    /**
     * 添加玩家到工会
     *
     * @param guild
     * @param simplePlayer
     * @param job
     */
    private void _addPlayerInGuild(NewGuild guild, SimplePlayer simplePlayer, GuildJobEnum job) {
        NewGuildCenterPlayer guildCenterPlayer = _createGuildCenterPlayer(simplePlayer, job);
        guild.getPlayerIdToGuildPlayerInfo().put(simplePlayer.getPlayerId(), guildCenterPlayer);
    }

    @Override
    public void addNewGuildTrack(Enum action, NewGuild guild, String... playerNames) {
        NewGuildTrack guildTrack = new NewGuildTrack();
        guildTrack.setTime(System.currentTimeMillis());
        guildTrack.setTpId(_getTrackIdByAction(action));
        guildTrack.getArgs().addAll(Arrays.asList(playerNames));
        guild.getGuildTracks().add(guildTrack);
    }

    private int _getTrackIdByAction(Enum action) {
        if (action instanceof Cm_NewGuild.Action) {
            Cm_NewGuild.Action action1 = (Cm_NewGuild.Action) action;
            switch (action1) {
                case AGREE:
                    return RootTc.get(Table_GuildTrack_Row.class).get(MagicNumbers.DEFAULT_ONE).getId();
                case OUT:
                    return RootTc.get(Table_GuildTrack_Row.class).get(MagicNumbers.DEFAULT_TWO).getId();
                case CREATE:
                    return RootTc.get(Table_GuildTrack_Row.class).get(MagicNumbers.DEFAULT_THREE).getId();
                case APPOINT:
                    return RootTc.get(Table_GuildTrack_Row.class).get(MagicNumbers.DEFAULT_FOUR).getId();
                case GIVE_JOB:
                    return RootTc.get(Table_GuildTrack_Row.class).get(MagicNumbers.DEFAULT_FOUR).getId();
            }
        } else if (action instanceof Cm_NewGuildResearch.Action) {
            Cm_NewGuildResearch.Action action1 = (Cm_NewGuildResearch.Action) action;
            switch (action1) {
                case RESEARCH:
                    return RootTc.get(Table_GuildTrack_Row.class).get(MagicNumbers.DEFAULT_FIVE).getId();
            }
        }
        String msg = String.format("这个Action没有对应的日志:%s", action);
        throw new BusinessLogicMismatchConditionException(msg);
    }


    /**
     * 创建一个GuildCenterPlayer
     *
     * @param simplePlayer
     * @param job
     * @return
     */
    private NewGuildCenterPlayer _createGuildCenterPlayer(SimplePlayer simplePlayer, GuildJobEnum job) {
        NewGuildCenterPlayer guildCenterPlayer = new NewGuildCenterPlayer();
        guildCenterPlayer.setJob(job);
        guildCenterPlayer.setJoinTime(System.currentTimeMillis());
        guildCenterPlayer.setPlayerId(simplePlayer.getPlayerId());
        guildCenterPlayer.setPlayerOuterReamlId(simplePlayer.getOutRealmId());
        return guildCenterPlayer;
    }

    /**
     * 删除申请
     *
     * @param guildCenter
     * @param guild
     * @param playerId
     */
    private void _removeApply(NewGuildCenter guildCenter, NewGuild guild, String playerId) {
        List<NewGuildApplyInfo> applyPlayerIds = guildCenter.getGuildIdToApplyList().get(guild.getGuildId());
        applyPlayerIds.removeIf(applyInfo -> applyInfo.getPlayerId().equals(playerId));
        guildCenter.getPlayerIdToGuildIds().remove(playerId);
    }


    /**
     * 是否在申请列表中
     *
     * @param guild
     * @param addPlayerId
     * @return
     */
    private boolean _isInApplyList(NewGuildCenter guildCenter, NewGuild guild, String addPlayerId) {
        if (!guildCenter.getPlayerIdToGuildIds().containsKey(addPlayerId)) {
            return false;
        }
        if (!_isInApply(addPlayerId, guildCenter.getGuildIdToApplyList().get(guild.getGuildId()))) {
            return false;
        }
        return true;
    }


    /**
     * 权限是否可以批准
     *
     * @param guild
     * @param simplePlayer
     * @return
     */
    private boolean _hasPermission(NewGuild guild, SimplePlayer simplePlayer) {
        NewGuildCenterPlayer guildCenterPLayer = guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId());
        return guildCenterPLayer.getJob() != GuildJobEnum.GJ_MEMBER;
    }

    /**
     * 是否是社团长
     *
     * @param guild
     * @param simplePlayer
     * @return
     */
    private boolean _isMaster(NewGuild guild, SimplePlayer simplePlayer) {
        NewGuildCenterPlayer guildCenterPLayer = guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId());
        return guild.getMasterPlayerId().equals(simplePlayer.getPlayerId()) && guildCenterPLayer.getJob() == GuildJobEnum.GJ_MASTER;
    }

    /**
     * 加入到申请列表中
     *
     * @param guildCenter
     * @param guild
     * @param simplePlayer
     */
    private void _addToApplyList(NewGuildCenter guildCenter, NewGuild guild, SimplePlayer simplePlayer) {
        if (!guildCenter.getGuildIdToApplyList().containsKey(guild.getGuildId())) {
            guildCenter.getGuildIdToApplyList().put(guild.getGuildId(), new ArrayList<>());
        }
        if (!_isInApply(simplePlayer.getPlayerId(), guildCenter.getGuildIdToApplyList().get(guild.getGuildId()))) {
            guildCenter.getGuildIdToApplyList().get(guild.getGuildId()).add(new NewGuildApplyInfo(simplePlayer.getPlayerId(), System.currentTimeMillis()));
        }
        if (!guildCenter.getPlayerIdToGuildIds().containsKey(simplePlayer.getPlayerId())) {
            guildCenter.getPlayerIdToGuildIds().put(simplePlayer.getPlayerId(), new ArrayList<>());
        }
        if (!guildCenter.getPlayerIdToGuildIds().get(simplePlayer.getPlayerId()).contains(guild.getGuildId())) {
            guildCenter.getPlayerIdToGuildIds().get(simplePlayer.getPlayerId()).add(guild.getGuildId());
        }
    }

    /**
     * 社团申请列表中是否有这个玩家
     *
     * @param playerId
     * @param applyInfoList
     * @return
     */
    private boolean _isInApply(String playerId, List<NewGuildApplyInfo> applyInfoList) {
        for (NewGuildApplyInfo applyInfo : applyInfoList) {
            if (applyInfo.getPlayerId().equals(playerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 社团人数是否已满
     *
     * @param guild
     * @return
     */
    private boolean _isGuildFull(NewGuild guild) {
        Table_Guild_Row guildRow = NewGuildCenterUtils.getGuildRow(NewGuildCtrlUtils.getGuildLevel(guild));
        int maxMemebers = guildRow.getGuildPeople();
        return guild.getPlayerIdToGuildPlayerInfo().size() >= maxMemebers;
    }

    /**
     * 创建新社团
     *
     * @param guildName
     * @param guildIcon
     * @param simplePlayer
     * @return
     */
    private NewGuild _createNewGuild(String guildName, int guildIcon, SimplePlayer simplePlayer) {
        NewGuild guild = new NewGuild();
        guild.setGuildId(ObjectId.get().toString());
        guild.setGuildIcon(guildIcon);
        guild.setOuterRealmId(simplePlayer.getOutRealmId());
        guild.setSimpleId(SIMPLE_ID_DAO.nextSimpleId(simplePlayer.getOutRealmId(), SimpleIdTypeEnum.GUILD));
        guild.setGuildName(guildName);
        guild.setMasterPlayerId(simplePlayer.getPlayerId());
        guild.setMasterOuterRealmId(simplePlayer.getOutRealmId());
        guild.setCreateTime(System.currentTimeMillis());
        guild.setJoinTypeEnum(GuildJoinTypeEnum.GJ_FREE);
        guild.setGuildNotice("");
        TupleCell<Integer> limitLv = _getGuildLimitLv();
        guild.setLimitLevel(limitLv.get(TupleCell.FIRST));
        _initInstitue(guild);
        NewGuildCenterUtils.sendSysRedBag(guild);
        return guild;
    }

    private void _initInstitue(NewGuild guild) {
        for (GuildInstituteTypeEnum instituteType : GuildInstituteTypeEnum.values()) {
            NewGuildInstitute institute = new NewGuildInstitute();
            for (GuildResearchProjectTypeEnum typeEnum : GuildResearchProjectTypeEnum.values()) {
                if (_isThisInsitituteProject(instituteType, typeEnum)) {
                    institute.getResearchTypeAndProject().put(typeEnum, new LevelUpObj(MagicNumbers.DEFAULT_ONE, MagicNumbers.DEFAULT_ZERO));
                }
            }
            guild.getTypeAndInstitute().put(instituteType, institute);
        }
    }

    private boolean _isThisInsitituteProject(GuildInstituteTypeEnum instituteType, GuildResearchProjectTypeEnum typeEnum) {
        switch (instituteType) {
            case GI_RB:
                switch (typeEnum) {
                    case GR_MONEY:
                    case GR_V_MONEY:
                        return true;
                }
                break;
            case GI_MC:
                switch (typeEnum) {
                    case GR_ELITE:
                    case GR_MEMBER:
                        return true;
                }
                break;
            case GI_LV:
                switch (typeEnum) {
                    case GR_GUILD_LV:
                        return true;
                }
                break;
        }
        return false;
    }


    /**
     * 是否已经存在这个社团
     *
     * @param guildId
     * @param guildCenter
     * @return
     */
    public boolean _isExistGuild(String guildId, NewGuildCenter guildCenter) {
        if (guildCenter.getIdToGuild().size() == 0) {
            NewGuildDao dao = _getDao(guildCenter.getOutRealmId());
            NewGuild dbGuild = dao.findOne(guildId);
            if (dbGuild == null) {
                String msg = String.format("can not find guild in mongodb,guildId:%s", guildId);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            guildCenter.getIdToGuild().put(dbGuild.getGuildId(), dbGuild);
        }
        return guildCenter.getIdToGuild().containsKey(guildId);
    }

    /**
     * 帮会名称是否存在
     *
     * @param guildName
     * @param guildCenter
     * @return
     */
    public boolean _isExistName(String guildName, NewGuildCenter guildCenter) {
        for (Entry<String, NewGuild> entry : guildCenter.getIdToGuild().entrySet()) {
            if (entry.getValue().getGuildName().equals(guildName)) {
                return true;
            }
        }
        return false;
    }

    private NewGuildCenterPlayer _getGuildCenterPlayer(NewGuild guild, SimplePlayer appointSimplePlayer) {
        return guild.getPlayerIdToGuildPlayerInfo().get(appointSimplePlayer.getPlayerId());
    }


}
