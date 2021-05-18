package ws.gameServer.system.schedule;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.table.table.interfaces.Row;
import ws.common.utils.date.WsDateFormatEnum;
import ws.common.utils.date.WsDateUtils;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.monitor._Monitor;
import ws.gameServer.features.actor.world.msg.In_NoticeToKillOverTimeCachePlayerActorRequest;
import ws.gameServer.features.standalone.actor.arenaCenter.msg.In_SettleDaliyRankReward;
import ws.gameServer.features.standalone.actor.newGuildCenter.msg.In_NewGuildRedBagSendSysRedBag;
import ws.gameServer.system.actor.WsActorSystem;
import ws.gameServer.system.date.dayChanged.DayChanged;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.In_BroadcastEachHour;
import ws.relationship.base.msg.In_BroadcastEachMinute;
import ws.relationship.base.msg.In_DisplayActorSelfPath;
import ws.relationship.table.RootTc;

import java.util.Date;
import java.util.List;

public class Cron4jUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Cron4jUtils.class);

    /**
     * 打印所有可到达的Actor
     *
     * @param args
     */
    public static void displayActorSelfPath(String[] args) {
        WsActorSystem.get().actorSelection(ActorSystemPath.WS_Common_Selection_WSRoot + "/*").tell(new In_DisplayActorSelfPath(), ActorRef.noSender());
    }

    /**
     * 移除缓存超时的玩家
     *
     * @param args
     */
    public static void killOverTimeCachePlayerActor(String[] args) {
        int overTime = Integer.valueOf(args[0]);
        ActorSelection actorSelection = WsActorSystem.get().actorSelection(ActorSystemPath.WS_GameServer_Selection_World);
        actorSelection.tell(new In_NoticeToKillOverTimeCachePlayerActorRequest(overTime), ActorRef.noSender());
    }

    /**
     * 切日时间点 如果修改时间点，则该时间点必须小于当前系统时间
     *
     * @param args
     */
    public static void tellDayChanged(String[] args) {
        LOGGER.info("定时调度任务: tellDayChanged, 通知切日.");
        GlobalInjector.getInstance(DayChanged.class).setDayChangedStr();
        for (String path : ActorSystemPath.WS_GameServer_Selection_All) {
            ActorSelection actorSelection = WsActorSystem.get().actorSelection(path);
            actorSelection.tell(new In_DayChanged(), ActorRef.noSender());
        }
    }

    /**
     * 广播整点时间（服务器时间，非切日时间）
     *
     * @param args
     */
    public static void broadcastEachHour(String[] args) {
        int hour = Integer.valueOf(WsDateUtils.dateToFormatStr(new Date(), WsDateFormatEnum.HH));
        LOGGER.info("定时调度任务: broadcastEachHour, 当前整点为={} .", hour);
        for (String path : ActorSystemPath.WS_GameServer_Selection_All) {
            ActorSelection actorSelection = WsActorSystem.get().actorSelection(path);
            actorSelection.tell(new In_BroadcastEachHour.Request(hour), ActorRef.noSender());
        }
    }

    /**
     * 广播每个分钟（服务器时间，非切日时间）
     *
     * @param args
     */
    public static void broadcastEachMinute(String[] args) {
        int hour = Integer.valueOf(WsDateUtils.dateToFormatStr(new Date(), WsDateFormatEnum.HH));
        int minute = Integer.valueOf(WsDateUtils.dateToFormatStr(new Date(), WsDateFormatEnum.mm));
        LOGGER.info("定时调度任务: broadcastEachMinute, 当前整点为={} 分钟为={}.", hour, minute);
        for (String path : ActorSystemPath.WS_GameServer_Selection_All) {
            ActorSelection actorSelection = WsActorSystem.get().actorSelection(path);
            actorSelection.tell(new In_BroadcastEachMinute.Request(hour, minute), ActorRef.noSender());
        }
    }


    /**
     * 竞技场每日排名奖励
     *
     * @param args
     */
    public static void settleDaliyRankReward(String[] args) {
        LOGGER.info("定时调度任务: shouldSettleDaliyRankReward 竞技场 .");
        ActorSelection actorSelection = WsActorSystem.get().actorSelection(ActorSystemPath.WS_GameServer_Selection_ArenaCenter);
        actorSelection.tell(new In_SettleDaliyRankReward.Request(), ActorRef.noSender());
    }


    public static void guildCenterSendSysRedBag(String[] args) {
        LOGGER.info("定时调度任务:guildCenterSendSysRedBag 社团发系统红包");
        ActorSelection actorSelection = WsActorSystem.get().actorSelection(ActorSystemPath.WS_GameServer_Selection_GuildCenter);
        actorSelection.tell(new In_NewGuildRedBagSendSysRedBag.Request(), ActorRef.noSender());
    }

    /**
     * 打印jvm信息
     *
     * @param args
     */
    public static void playJvmInfo(String[] args) {
        try {
            new _Monitor().play();
        } catch (Exception e) {
        }
    }

    public static void refreshRootTc(String[] args) {
        LOGGER.info("定时调度任务: refreshRootTc 准备刷新变动的策划表");
        try {
            List<Class<? extends Row>> rs = RootTc.refresh();
            LOGGER.info("定时调度任务: refreshRootTc 变动的策划表={}", rs);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}
