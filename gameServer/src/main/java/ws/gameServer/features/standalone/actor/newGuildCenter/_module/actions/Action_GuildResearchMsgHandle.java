package ws.gameServer.features.standalone.actor.newGuildCenter._module.actions;


import akka.actor.ActorContext;
import akka.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.message.interfaces.ResultCode;
import ws.gameServer.features.standalone.actor.newGuildCenter._module.Action;
import ws.gameServer.features.standalone.actor.newGuildCenter.ctrl.NewGuildCenterCtrl;
import ws.gameServer.features.standalone.actor.newGuildCenter.utils.NewGuildCenterUtils;
import ws.gameServer.features.standalone.extp.newGuild.msg.institute.In_NewGuildResearch;
import ws.gameServer.features.standalone.extp.newGuild.utils.NewGuildCtrlUtils;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.gameServer.features.standalone.extp.utils.UpgradeLevel;
import ws.protos.EnumsProtos.GuildBuildTypeEnum;
import ws.protos.EnumsProtos.GuildInstituteTypeEnum;
import ws.protos.EnumsProtos.GuildResearchProjectTypeEnum;
import ws.protos.EnumsProtos.GuildResearchTypeEnum;
import ws.protos.NewGuildResearchProtos.Cm_NewGuildResearch;
import ws.protos.errorCode.ErrorCodeProtos.ErrorCodeEnum;
import ws.relationship.base.MagicNumbers;
import ws.relationship.base.resultCode.ResultMsg;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.tableRows.Table_GuildResearch_Row;
import ws.relationship.table.tableRows.Table_Guild_Row;
import ws.relationship.topLevelPojos.common.LevelUpObj;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenter;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildInstitute;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

/**
 * Created by lee on 6/8/17.
 */
public class Action_GuildResearchMsgHandle implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(Action_GuildResearchMsgHandle.class);

    @Override
    public void onRecv(NewGuildCenterCtrl actorCtrl, Object msg, ActorContext context, ActorRef sender) {
        if (msg instanceof In_NewGuildResearch.Request) {
            onIn_NewGuildResearch((In_NewGuildResearch.Request) msg, actorCtrl, context, sender);
        }
    }

    private void onIn_NewGuildResearch(In_NewGuildResearch.Request request, NewGuildCenterCtrl actorCtrl, ActorContext context, ActorRef sender) {
        In_NewGuildResearch.Response response = null;
        try {
            SimplePlayer simplePlayer = request.getSimplePlayer();
            GuildInstituteTypeEnum instituteType = request.getInstituteType();
            GuildResearchProjectTypeEnum researchProjectType = request.getResearchProjectType();
            GuildResearchTypeEnum researchType = request.getResearchType();
            LogicCheckUtils.validateParam(Integer.class, simplePlayer.getOutRealmId());
            NewGuildCenter guildCenter = actorCtrl.getNewGuildCenter(simplePlayer.getOutRealmId());
            NewGuild guild = actorCtrl.getGuildById(guildCenter, simplePlayer.getGuildId());
            if (!NewGuildCenterUtils.isFunctionOpen(guild, GuildBuildTypeEnum.GB_RESEARCH)) {
                String msg = String.format("社团等级没有开启这个功能:%s", GuildBuildTypeEnum.GB_RESEARCH);
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_INSTITUTE_NO_OPEN_ERROR);
            }
            if (!NewGuildCenterUtils.hasInstitute(instituteType, guild)) {
                String msg = String.format("社团没有这个研究院:%s", instituteType);
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_INSTITUTE_NOT_EXIST);
            }
            NewGuildInstitute institute = guild.getTypeAndInstitute().get(instituteType);
            if (!NewGuildCenterUtils.hasResearchProject(researchProjectType, institute)) {
                String msg = String.format("研究院没有这个研究项目:%s", researchProjectType);
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_RESEARCH_PROJECT_NOT_EXIST);
            }
            if (_isMaxLv(instituteType, researchProjectType, guild)) {
                String msg = "这个研究项目已经达到了MaxLv";
                throw new BusinessLogicMismatchConditionException(msg, ErrorCodeEnum.GUILD_RESEARCH_PROJECT_NOT_EXIST);
            }
            int addExp = NewGuildCtrlUtils.getExpByResearchType(researchType);
            _addGuildInstituteExp(guild, addExp, instituteType, researchProjectType);
            actorCtrl.addNewGuildTrack(Cm_NewGuildResearch.Action.RESEARCH, guild, simplePlayer.getPlayerName(), researchType.getNumber() + "", researchProjectType.getNumber() + "", addExp + "");
            actorCtrl.saveGuildToMongoDB(simplePlayer.getOutRealmId(), guild);
            response = new In_NewGuildResearch.Response(request, guild.clone());
        } catch (BusinessLogicMismatchConditionException e) {
            LOGGER.error("", e);
            ResultCode resultCode = LogicCheckUtils.createResultCode(e);
            response = new In_NewGuildResearch.Response(resultCode, request);
        } catch (Exception e) {
            LOGGER.error("", e);
            response = new In_NewGuildResearch.Response(ResultMsg.DEFAULT_UNKNOW.setMessage(e.toString()), request);
        } finally {
            sender.tell(response, context.self());
        }
    }


    private boolean _isMaxLv(GuildInstituteTypeEnum instituteType, GuildResearchProjectTypeEnum researchProjectType, NewGuild guild) {
        int maxLv = 0;
        if (researchProjectType == GuildResearchProjectTypeEnum.GR_GUILD_LV) {
            maxLv = Table_Guild_Row.getGuildMaxLv();
        } else {
            maxLv = Table_GuildResearch_Row.getMaxLvByResearchProjectType(researchProjectType);
        }
        int lv = guild.getTypeAndInstitute().get(instituteType).getResearchTypeAndProject().get(researchProjectType).getLevel();
        return maxLv == 0 || lv >= maxLv;
    }

    /**
     * 增加社团经验
     *
     * @param guild
     * @param expOffered
     * @param instituteType
     * @param researchProjectType
     */
    private void _addGuildInstituteExp(NewGuild guild, int expOffered, GuildInstituteTypeEnum instituteType, GuildResearchProjectTypeEnum researchProjectType) {
        NewGuildInstitute institute = guild.getTypeAndInstitute().get(instituteType);
        LevelUpObj levelUpObj = institute.getResearchTypeAndProject().get(researchProjectType);
        int todayExp = guild.getTodayExp();
        // 此经验不能超过社团人数*250(不包含额外增加的人数，对应社团等级表中社团等级对应的对大社团人数
        int maxExp = _mathMaxExp(levelUpObj.getLevel());
        if ((todayExp + expOffered) > maxExp) {
            expOffered = maxExp - todayExp;
        }
        UpgradeLevel.levelUp(levelUpObj, expOffered, levelUpObj.getLevel(), (oldLevel) -> {
            Table_Guild_Row guildRow = NewGuildCenterUtils.getGuildRow(oldLevel);
            return guildRow.getGuildExp();
        });
        if (researchProjectType == GuildResearchProjectTypeEnum.GR_GUILD_LV) {
            guild.setTodayExp(guild.getTodayExp() + expOffered);
        }

    }


    private int _mathMaxExp(int guildLv) {
        int maxNum = NewGuildCenterUtils.getGuildRow(guildLv).getGuildPeople();
        return maxNum * MagicNumbers.GUILD_MEMBER_BASE_EXP;
    }

}
