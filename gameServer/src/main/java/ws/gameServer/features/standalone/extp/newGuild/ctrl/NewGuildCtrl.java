package ws.gameServer.features.standalone.extp.newGuild.ctrl;


import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildAgree;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildAppoint;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildDisband;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildGiveJob;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildKick;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildOneKeyRefuse;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildRefuse;
import ws.gameServer.features.standalone.extp.redPoint.msg.Pr_CheckRedPointMsg;
import ws.protos.CommonProtos.Sm_Common_Round;
import ws.protos.EnumsProtos.GuildJobEnum;
import ws.protos.EnumsProtos.GuildJoinTypeEnum;
import ws.relationship.topLevelPojos.newGuild.NewGuildPlayer;

public interface NewGuildCtrl extends PlayerExteControler<NewGuildPlayer> {


    /**
     * 查询公会Id
     */
    String queryGuildId();

    /**
     * 创建社团
     *
     * @param guildName
     * @param guildIcon
     */
    void create(String guildName, int guildIcon);

    /**
     * 加入社团
     *
     * @param guildId
     */
    void join(String guildId);

    /**
     * 踢出社团
     *
     * @param playerId
     */
    void kick(String playerId);

    /**
     * 批准加入
     *
     * @param playerId
     */
    void agree(String playerId);


    /**
     * 拒绝
     *
     * @param playerId
     */
    void refuse(String playerId);


    /**
     * 退出社团
     */
    void out();

    /**
     * 解散社团
     */
    void disband();


    /**
     * 修改社团信息
     *
     * @param
     * @param joinTypeEnum
     * @param round
     */
    void change(GuildJoinTypeEnum joinTypeEnum, Sm_Common_Round round);


    /**
     * 搜索（社团ID，社团名字）
     *
     * @param searchArgs
     */
    void search(String searchArgs);


    /**
     * 按角标搜索
     *
     * @param round
     */
    void search(Sm_Common_Round round);

    /**
     * 按角标获取申请列表
     *
     * @param round
     */
    void getApplys(Sm_Common_Round round);

    /**
     * 获取社团信息
     *
     * @param round
     */
    void getMember(Sm_Common_Round round);

    /**
     * 社团同意加入 被操作者
     *
     * @param response
     */
    void agreeResponse(In_NewGuildAgree.Response response);

    /**
     * 任命
     *
     * @param playerId
     * @param job
     */
    void appoint(String playerId, GuildJobEnum job);

    /**
     * 取消申请
     *
     * @param guildId
     */
    void cancel(String guildId);

    /**
     * 更新公告
     *
     * @param guildNotice
     */
    void modify(String guildNotice);

    /**
     * 一键加入
     *
     * @param guildId
     */
    void oneKeyJoin(String guildId);

    /**
     * 更新图标
     *
     * @param guildIcon
     */
    void modify(int guildIcon);

    /***
     * 传职
     * @param playerId
     */
    void giveJob(String playerId);

    /***
     *
     * @param content
     * @param title
     */
    void mails(String content, String title);

    /**
     * 更新社团名字
     *
     * @param guildName
     */
    void modifyName(String guildName);

    /**
     * 招募
     */
    void recruit();

    /**
     * 获取日志
     */
    void getTrack();

    void kickResponse(In_NewGuildKick.Response response);

    void giveJobResponse(In_NewGuildGiveJob.Response response);

    void appointResponse(In_NewGuildAppoint.Response response);

    void oneKeyRefuse();

    void refuseResponse(In_NewGuildRefuse.Response response);

    void disbandResponse(In_NewGuildDisband.Response response);

    void oneKeyRefuseResponse(In_NewGuildOneKeyRefuse.Response response);

    void checkRedPointMsg(Pr_CheckRedPointMsg.Request privateMsg);
}
