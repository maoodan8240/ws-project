package ws.gameServer.features.standalone.actor.newGuildCenter.ctrl;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import ws.common.utils.mc.controler.Controler;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildAppoint;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildGetApplys.Request;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildGiveJob;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildJoin;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildSearch;
import ws.protos.CommonProtos.Sm_Common_Round;
import ws.protos.EnumsProtos.GuildJoinTypeEnum;
import ws.relationship.topLevelPojos.common.TopLevelHolder;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildApplyInfo;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenter;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenterPlayer;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildTrainer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

import java.util.List;
import java.util.Map;

/**
 * Created by lee on 16-11-30.
 */
public interface NewGuildCenterCtrl extends Controler<TopLevelHolder> {

    NewGuildCenter getNewGuildCenter(int centerOuterRealmId);

    NewGuild getGuildById(NewGuildCenter guildCenter, String guildId);

    void trySettleAllTrainer(NewGuildCenterPlayer guildCenterPlayer, List<NewGuildTrainer> trainerList);

    void saveGuildToMongoDB(int centerOuterRealmId, NewGuild guild);

    void addNewGuildTrack(Enum action, NewGuild guild, String... playerNames);

    Map<Integer, NewGuildCenter> getCenterOutRealmIdToGuildCenters();


    /**
     * 尝试结算
     *
     * @param guildCenterPlayer
     * @param trainer
     */
    void trySettle(NewGuildCenterPlayer guildCenterPlayer, NewGuildTrainer trainer);

    /**
     * 创建社团
     *
     * @param guildName
     * @param guildIcon
     * @param simplePlayer
     * @return NewGuild
     */
    NewGuild create(String guildName, int guildIcon, SimplePlayer simplePlayer);

    /**
     * 申请加入社团
     *
     * @param msg
     */
    In_NewGuildJoin.Response join(In_NewGuildJoin.Request msg);

    /**
     * 批准加入社团
     *
     * @param guildId
     * @param simplePlayer
     * @param addSimplePlayer
     */
    NewGuild agree(String guildId, SimplePlayer simplePlayer, SimplePlayer addSimplePlayer);

    /**
     * 获取社团信息
     *
     * @param guildId
     * @param simplePlayer
     * @return NewGuild
     */
    List<NewGuild> getGuildInfo(String guildId, SimplePlayer simplePlayer);

    /**
     * 拒绝
     *
     * @param guildId
     * @param simplePlayer
     * @param refuseSimplePlayer
     */
    void refuse(String guildId, SimplePlayer simplePlayer, SimplePlayer refuseSimplePlayer);

    /**
     * 踢出
     *
     * @param guildId
     * @param simplePlayer
     * @param kickSimplePlayer
     */
    void kick(String guildId, SimplePlayer simplePlayer, SimplePlayer kickSimplePlayer);

    /**
     * 退出社团
     *
     * @param guildId
     * @param simplePlayer
     */
    void out(String guildId, SimplePlayer simplePlayer);

    /**
     * 解散社团
     *
     * @param guildId
     * @param simplePlayer
     * @return
     */
    List<String> disband(String guildId, SimplePlayer simplePlayer);

    /**
     * 更新公告
     *
     * @param guildId
     * @param simplePlayer
     * @param joinTypeEnum
     * @param round
     * @return
     */
    NewGuild change(String guildId, SimplePlayer simplePlayer, GuildJoinTypeEnum joinTypeEnum, Sm_Common_Round round);

    /**
     * 搜索
     *
     * @param msg
     * @return
     */
    NewGuild search(In_NewGuildSearch.Request msg);

    /**
     * Actor上下文
     *
     * @param context
     */
    void setCurContext(ActorContext context);


    /**
     * 当前消息的发送者
     *
     * @param sender
     */
    void setCurSendActorRef(ActorRef sender);

    /**
     * 获取申请列表
     *
     * @param msg
     */
    List<NewGuildApplyInfo> getApplys(Request msg);

    /**
     * 取消申请
     *
     * @param guildId
     * @param simplePlayer
     * @return
     */
    NewGuild cancel(String guildId, SimplePlayer simplePlayer);

    /**
     * 修改该工会公告或头像
     *
     * @param simplePlayer
     * @param guildNotic
     * @param guildIcon
     * @param guildName
     * @return
     */
    NewGuild modify(SimplePlayer simplePlayer, String guildNotic, int guildIcon, String guildName);

    /**
     * 快速加入
     *
     * @param simplePlayer
     * @return
     */
    NewGuild oneKeyJoin(SimplePlayer simplePlayer);

    /**
     * 任命
     *
     * @return
     */
    In_NewGuildAppoint.Response appoint(In_NewGuildAppoint.Request msg);

    /**
     * 传职
     *
     * @return
     */
    In_NewGuildGiveJob.Response giveJob(In_NewGuildGiveJob.Request msg);

    /***
     * 邮件
     * @param simplePlayer
     * @param content
     * @param title
     * @return
     */
    NewGuild mails(SimplePlayer simplePlayer, String content, String title);


    /**
     * 一键拒绝
     *
     * @param simplePlayer
     */
    List<String> oneKeyRefuse(SimplePlayer simplePlayer);

    /**
     * 招募
     *
     * @param simplePlayer
     * @return
     */
    NewGuild recruit(SimplePlayer simplePlayer);


    void onDayChanged();
}
