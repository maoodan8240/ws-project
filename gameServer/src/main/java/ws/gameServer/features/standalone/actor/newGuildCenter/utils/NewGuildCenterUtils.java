package ws.gameServer.features.standalone.actor.newGuildCenter.utils;

import org.apache.commons.lang3.time.DateUtils;
import ws.gameServer.features.standalone.extp.newGuild.utils.NewGuildCtrlUtils;
import ws.protos.EnumsProtos.GuildBuildTypeEnum;
import ws.protos.EnumsProtos.GuildInstituteTypeEnum;
import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.protos.EnumsProtos.GuildResearchProjectTypeEnum;
import ws.relationship.base.MagicNumbers;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Guild_Row;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenterPlayer;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildInstitute;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildPlayerRedBag;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildSystemRedBag;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildTrainer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lee on 6/8/17.
 */
public class NewGuildCenterUtils {

    public static boolean isFunctionOpen(NewGuild guild, GuildBuildTypeEnum buildTypeEnum) {
        Table_Guild_Row guildRow = getGuildRow(NewGuildCtrlUtils.getGuildLevel(guild));
        return guildRow.getFunctionOpening().getAll().contains(buildTypeEnum.getNumber());
    }

    public static Table_Guild_Row getGuildRow(int lv) {
        return RootTc.get(Table_Guild_Row.class).get(lv);
    }

    /**
     * 训练桩上是否有格斗家
     *
     * @param guildCenterPlayer
     * @param index
     * @return
     */
    public static boolean hasTrainer(NewGuildCenterPlayer guildCenterPlayer, int index) {
        if (guildCenterPlayer.getIndexAndTrainer().containsKey(index)) {
            return guildCenterPlayer.getIndexAndTrainer().get(index).getHeroId() != MagicNumbers.DEFAULT_ZERO;
        }
        return false;
    }


    /**
     * 尝试替换
     *
     * @param guild
     * @param simplePlayer
     * @param heroId
     * @param index
     * @return
     */
    public static TrainerReplaceResult tryReplace(NewGuild guild, SimplePlayer simplePlayer, int heroId, int index) {
        NewGuildTrainer beforeTrainer = null;
        NewGuildCenterPlayer guildCenterPlayer = guild.getPlayerIdToGuildPlayerInfo().get(simplePlayer.getPlayerId());
        if (guildCenterPlayer.getIndexAndTrainer().get(index) != null) {
            NewGuildTrainer trainer = guildCenterPlayer.getIndexAndTrainer().get(index);
            beforeTrainer = trainer.clone();
        }
        NewGuildTrainer nowTrainer = new NewGuildTrainer(index, heroId, System.currentTimeMillis());
        guildCenterPlayer.getIndexAndTrainer().put(index, nowTrainer);
        if (beforeTrainer == null) {
            return new TrainerReplaceResult(nowTrainer);
        }
        return new TrainerReplaceResult(beforeTrainer, nowTrainer);
    }


    /**
     * 结算训练桩时间戳
     *
     * @param trainer
     */
    public static void settle(NewGuildTrainer trainer) {
        // 如果距离上次结算时间不足一分钟，忽略不做任何操作
        long now = System.currentTimeMillis();
        long lastSettleTime = trainer.getLastSettle();
        if (now - lastSettleTime > DateUtils.MILLIS_PER_MINUTE) {
            int remain = (int) ((System.currentTimeMillis() - trainer.getLastSettle()) % DateUtils.MILLIS_PER_MINUTE);
            trainer.setLastSettle(System.currentTimeMillis() - remain);
        }
    }

    /**
     * 获取所有玩家发的红包
     *
     * @param guild
     * @return
     */
    public static List<NewGuildPlayerRedBag> getPlayerRedBag(NewGuild guild) {
        List<NewGuildPlayerRedBag> redBagList = new ArrayList<>();
        List<NewGuildPlayerRedBag> allRedBags = new ArrayList<>();
        for (List<NewGuildPlayerRedBag> list : guild.getTypeAndPlayerRedBag().values()) {
            for (NewGuildPlayerRedBag redBag : list) {
                allRedBags.add(redBag);
            }
        }
        Iterator<NewGuildPlayerRedBag> it = allRedBags.iterator();
        while (it.hasNext()) {
            NewGuildPlayerRedBag redBag = it.next();
            if (_isDeleteTime(redBag)) {
                guild.getTypeAndPlayerRedBag().remove(redBag.getRedBagTypeEnum());
                it.remove();
            } else {
                redBagList.add(redBag);
            }
        }
        return redBagList;
    }


    /**
     * 是否到了删除的时间
     *
     * @param redBag
     * @return
     */
    private static boolean _isDeleteTime(NewGuildPlayerRedBag redBag) {
        long shouldDeleteTime = redBag.getSendTime() + DateUtils.MILLIS_PER_DAY;
        long now = System.currentTimeMillis();
        return now >= shouldDeleteTime;
    }

    /**
     * 份额是否可以抢
     *
     * @param share
     * @return
     */
    public static boolean isShareCanGrab(List<Integer> share) {
        return share.size() > MagicNumbers.DEFAULT_ZERO;
    }


    /**
     * 发系统红包
     *
     * @param guild
     */
    public static void sendSysRedBag(NewGuild guild) {
        NewGuildSystemRedBag moneyRedBag = NewGuildCtrlUtils.createGuildRedBagByType(guild, GuildRedBagTypeEnum.GRT_MONEY);
        NewGuildSystemRedBag vipMoneyRedBag = NewGuildCtrlUtils.createGuildRedBagByType(guild, GuildRedBagTypeEnum.GRT_V_MONEY);
        guild.getTypeAndSysRedBag().clear();
        guild.getTypeAndSysRedBag().put(GuildRedBagTypeEnum.GRT_MONEY, moneyRedBag);
        guild.getTypeAndSysRedBag().put(GuildRedBagTypeEnum.GRT_V_MONEY, vipMoneyRedBag);
    }


    public static boolean hasResearchProject(GuildResearchProjectTypeEnum researchProjectType, NewGuildInstitute institute) {
        return institute.getResearchTypeAndProject().containsKey(researchProjectType);
    }

    public static boolean hasInstitute(GuildInstituteTypeEnum instituteType, NewGuild guild) {
        return guild.getTypeAndInstitute().containsKey(instituteType);
    }

    /**
     * 是否已经抢过了
     *
     * @return
     */
    public static boolean hasGrab(Map<String, Integer> playerNameAndShare, String playerName) {
        return playerNameAndShare.containsKey(playerName);
    }

    /**
     * 训练桩是否解锁
     *
     * @param guildCenterPlayer
     * @param index
     * @return
     */
    public static boolean isTrainUnlock(NewGuildCenterPlayer guildCenterPlayer, int index) {
        return guildCenterPlayer.getIndexAndTrainer().containsKey(index);
    }

    /**
     * 是否已经在社团
     *
     * @param guild
     * @param addPlayerId
     * @return
     */
    public static boolean isInGuild(NewGuild guild, String addPlayerId) {
        return guild.getPlayerIdToGuildPlayerInfo().containsKey(addPlayerId);
    }

    /***
     * 主动加速次数是否已经达到最大值
     * @param guildCenterPlayer
     * @return
     */
    public static boolean isAccelerateTimesMax(NewGuildCenterPlayer guildCenterPlayer) {
        return guildCenterPlayer.getAccelerateTimes() >= MagicNumbers.ACCELERATETIMES_MAX_TIMES;
    }

    /**
     * 被动加速次数是否达到最大值
     *
     * @param guildCenterPlayer
     * @return
     */
    public static boolean isBeAccelerateTimesMax(NewGuildCenterPlayer guildCenterPlayer) {
        return guildCenterPlayer.getAcceleratePlayerName().size() >= MagicNumbers.BE_ACCELERATETIMES_MAX_TIMES;
    }
}
