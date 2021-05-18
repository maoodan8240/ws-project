package ws.gameServer.features.standalone.extp.newGuild.utils;

import akka.actor.ActorContext;
import akka.actor.ActorSelection;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import ws.common.table.table.interfaces.cell.TupleCell;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.gameServer.features.actor.cluster.ClusterListener;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildGetInfo;
import ws.gameServer.features.standalone.extp.redPoint.msg.In_NotifyRedPointMsg;
import ws.gameServer.features.standalone.extp.utils.LogicCheckUtils;
import ws.protos.EnumsProtos.GuildInstituteTypeEnum;
import ws.protos.EnumsProtos.GuildJobEnum;
import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.protos.EnumsProtos.GuildResearchProjectTypeEnum;
import ws.protos.EnumsProtos.GuildResearchTypeEnum;
import ws.protos.EnumsProtos.GuildSendRedBagTypeEnum;
import ws.protos.EnumsProtos.RedPointEnum;
import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.MagicNumbers;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.CheckPlayerOnlineMsgRequest;
import ws.relationship.exception.BusinessLogicMismatchConditionException;
import ws.relationship.table.AllServerConfig;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Exp_Row;
import ws.relationship.table.tableRows.Table_GuildRedPacket_Row;
import ws.relationship.table.tableRows.Table_GuildResearch_Row;
import ws.relationship.table.tableRows.Table_GuildTrain_Row;
import ws.relationship.table.tableRows.Table_New_Card_Row;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.newGuild.NewGuildPlayer;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenterPlayer;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildInstitute;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildPlayerRedBag;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildRedBag;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildSystemRedBag;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildTrainer;
import ws.relationship.topLevelPojos.simpleGuild.SimpleGuild;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.ActorMsgSynchronizedExecutor;
import ws.relationship.utils.ClusterMessageSender;
import ws.relationship.utils.DBUtils;
import ws.relationship.utils.RandomUtils;
import ws.relationship.utils.RelationshipCommonUtils;
import ws.relationship.utils.RelationshipCommonUtils.SortConditionValues;
import ws.relationship.utils.RelationshipCommonUtils.SortRuleEnum;
import ws.relationship.utils.SimplePojoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NewGuildCtrlUtils {
    /**
     * 计算字符数量
     *
     * @param args
     * @return
     */
    public static int mathCharLength(String args) {
        int length = 0;
        for (int i = 0; i < args.length(); i++) {
            // 128代表中文字符，一个中文两个字符
            if (Character.codePointAt(args, i) > MagicNumbers.CHINA_COUNT) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length;
    }


    public static int getJobCount(NewGuild guild, GuildJobEnum jobEnum) {
        List<NewGuildCenterPlayer> guildCenterPlayerList = new ArrayList<>(guild.getPlayerIdToGuildPlayerInfo().values());
        int count = 0;
        for (NewGuildCenterPlayer guildCenterPlayer : guildCenterPlayerList) {
            if (guildCenterPlayer.getJob() == jobEnum) {
                count += 1;
            }
        }
        return count;

    }

    /**
     * 是否有社团
     *
     * @return
     */
    public static boolean hasGuild(NewGuildPlayer newGuildPlayer) {
        return !StringUtils.isEmpty(newGuildPlayer.getGuildId());
    }


    public static IdMaptoCount getTrainerUnlockConsume(int index) {
        Table_GuildTrain_Row guildTrainRow = RootTc.get(Table_GuildTrain_Row.class).get(index);
        int consume = guildTrainRow.getOpeningConsume();
        IdAndCount idAndCount = new IdAndCount(ResourceTypeEnum.RES_VIPMONEY_VALUE, consume);
        return new IdMaptoCount(idAndCount);
    }


    public static int getTrainerOpenLv(int index) {
        Table_GuildTrain_Row guildTrainRow = RootTc.get(Table_GuildTrain_Row.class).get(index);
        return guildTrainRow.getOpeningLevel();
    }

    public static int getTrainerPreOpenIndex(int index) {
        Table_GuildTrain_Row guildTrainRow = RootTc.get(Table_GuildTrain_Row.class).get(index);
        return guildTrainRow.getPreOpeningID();
    }

    public static int getTrainerOpenVipLv(int index) {
        Table_GuildTrain_Row guildTrainRow = RootTc.get(Table_GuildTrain_Row.class).get(index);
        return guildTrainRow.getOpeningVipLevel();
    }


    public static Map<String, SimplePlayer> getStringSimplePlayerMap(List<NewGuildCenterPlayer> roundList, int outerRealmId) {
        List<String> playerIds = getPlayerIdFromGuildCenterPlayerList(roundList);
        return SimplePojoUtils.querySimplePlayerLisByIds(playerIds, outerRealmId);
    }

    public static List<String> getPlayerIdFromGuildCenterPlayerList(List<NewGuildCenterPlayer> roundList) {
        List<String> playerIdList = new ArrayList<>();
        for (NewGuildCenterPlayer player : roundList) {
            playerIdList.add(player.getPlayerId());
        }
        return playerIdList;
    }


    /**
     * 社团列表排序
     *
     * @param guildList
     */
    public static void sortGuild(List<NewGuild> guildList) {
        RelationshipCommonUtils.mutliConditionSort(guildList, new SortConditionValues<NewGuild>() {
            @Override
            public long[] compareValues(NewGuild o1, NewGuild o2) {
                long[] v = new long[4];
                v[0] = getGuildLevel(o1) - getGuildLevel(o2);
                v[1] = o1.getBattleValue() - o2.getBattleValue();
                v[2] = o1.getPlayerIdToGuildPlayerInfo().size() - o2.getPlayerIdToGuildPlayerInfo().size();
                v[3] = o1.getCreateTime() - o2.getCreateTime();
                return v;
            }
        }, SortRuleEnum.ESC);
    }


    /**
     * 根据范围获取成员
     *
     * @param min
     * @param max
     * @param guildCenterPlayerList
     * @return
     */
    public static List<NewGuildCenterPlayer> getMemberByRound(int min, int max, List<NewGuildCenterPlayer> guildCenterPlayerList) {
        List<NewGuildCenterPlayer> roundList = new ArrayList<>();
        if (guildCenterPlayerList.size() == 0) {
            return roundList;
        }
        if (min < 0) {
            min = 0;
        }
        if (min > guildCenterPlayerList.size() - 1) {
            min = guildCenterPlayerList.size() - 1;
        }

        if (max >= guildCenterPlayerList.size()) {
            max = guildCenterPlayerList.size() - 1;
        }
        for (int i = min; i <= max; i++) {
            roundList.add(guildCenterPlayerList.get(i));
        }
        return roundList;
    }


    /**
     * 根据范围获取成员
     *
     * @param min
     * @param max
     * @param guildCenterPlayerList
     * @return
     */
    public static List<NewGuildCenterPlayer> getMemberByRoundWithoutSelf(int min, int max, List<NewGuildCenterPlayer> guildCenterPlayerList, String playerSelfId) {
        Iterator<NewGuildCenterPlayer> it = guildCenterPlayerList.iterator();
        while (it.hasNext()) {
            NewGuildCenterPlayer guildCenterPlayer = it.next();
            if (guildCenterPlayer.getPlayerId().equals(playerSelfId)) {
                it.remove();
            }
        }
        List<NewGuildCenterPlayer> roundList = new ArrayList<>();
        if (guildCenterPlayerList.size() == 0) {
            return roundList;
        }
        //如果min是1，代表客户端想从第一个开始获取，也就是ArrayList的第0个角标
        if (min < 0) {
            min = 0;
        }
        if (min > guildCenterPlayerList.size() - 1) {
            min = guildCenterPlayerList.size() - 1;
        }

        if (max >= guildCenterPlayerList.size()) {
            max = guildCenterPlayerList.size() - 1;
        }
        for (int i = min; i <= max; i++) {
            if (StringUtils.isBlank(playerSelfId)) {
                throw new IllegalArgumentException("playerSelf Name is null");
            }
            if (!guildCenterPlayerList.get(i).getPlayerId().equals(playerSelfId)) {
                roundList.add(guildCenterPlayerList.get(i));
            }
        }
        return roundList;
    }

    public static <I, O> O sendMsgToGuildCenter(I request) {
        ActorSelection actorSelection = ClusterListener.getActorContext().actorSelection((ActorSystemPath.WS_GameServer_Selection_NewGuildCenter));
        InnerMsg response = ActorMsgSynchronizedExecutor.sendMsgToServer(actorSelection, request);
        LogicCheckUtils.checkResponse(response);
        return (O) response;
    }

    public static boolean isPlayerRedBag(GuildRedBagTypeEnum redBagType) {
        return redBagType == GuildRedBagTypeEnum.PRT_MONEY || redBagType == GuildRedBagTypeEnum.PRT_V_MONEY;
    }

    public static NewGuild getGuildInfo(NewGuildPlayer guildPlayer, SimplePlayer simplePlayer) {
        In_NewGuildGetInfo.Response response = NewGuildCtrlUtils.sendMsgToGuildCenter(new In_NewGuildGetInfo.Request(guildPlayer.getGuildId(), simplePlayer));
        List<NewGuild> guildList = response.getGuildList();
        return guildList.get(MagicNumbers.DEFAULT_ZERO);
    }


    /**
     * 计算训练桩英雄经验
     *
     * @param trainer
     * @param playerLv
     * @return
     */
    public static int mathHeroExp(NewGuildTrainer trainer, int playerLv) {
        long now = System.currentTimeMillis();
        long lastSettleTime = trainer.getLastSettle();
        if (now - lastSettleTime > DateUtils.MILLIS_PER_MINUTE) {
            int minute = (int) ((now - lastSettleTime) / DateUtils.MILLIS_PER_MINUTE);
            int exp = Table_Exp_Row.getGuildTrainRoleExp(playerLv);
            return minute * exp;
        }
        return MagicNumbers.DEFAULT_ZERO;
    }

    /**
     * 0
     * 计算训练桩英雄加速经验
     *
     * @param hero
     * @return
     */
    public static int mathAccelerateHeroExp(Hero hero) {
        Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, hero.getTpId());
        int exp = Table_Exp_Row.getHeroUpgradeNeedExp(cardRow.getCardAptitude(), hero.getLv());
        return (int) Math.floor(exp * 0.05);

    }


    /**
     * 是否已经领取
     *
     * @param redBag
     * @param playerName
     * @return
     */
    public static boolean hasReceive(NewGuildRedBag redBag, String playerName) {
        return redBag.getPlayerNameAndShare().containsKey(playerName);
    }

    /**
     * 获取这个红包中领取资源最高的玩家名字
     *
     * @param redBag
     * @return
     */
    public static String hightTotalPlayerName(NewGuildRedBag redBag) {
        Map<String, Integer> playerNameAndShare = redBag.getPlayerNameAndShare();
        Map<String, Integer> newPlayerNameAndShare = RelationshipCommonUtils.sortMapByValue(playerNameAndShare, SortRuleEnum.ESC);
        List<String> list = new ArrayList<>(newPlayerNameAndShare.keySet());
        if (list.size() == MagicNumbers.DEFAULT_ZERO) {
            return "";
        }
        return list.get(MagicNumbers.DEFAULT_ZERO);
    }

    /**
     * 获取红包总金额
     *
     * @param redBag
     * @return
     */
    public static int getRedBagTotal(NewGuildRedBag redBag) {
        int count = 0;
        for (Integer i : redBag.getRedBagShare()) {
            count += i;
        }
        return count;
    }

    /**
     * 获取红包总份数
     *
     * @param redBag
     * @return
     */
    public static int getRedBagTotalCount(NewGuildRedBag redBag) {
        return redBag.getRedBagShare().size() + redBag.getPlayerNameAndShare().size();
    }

    /**
     * 根据红包类型生成红包
     *
     * @param guild
     * @param redBagTypeEnum
     * @return
     */
    public static NewGuildSystemRedBag createGuildRedBagByType(NewGuild guild, GuildRedBagTypeEnum redBagTypeEnum) {
        NewGuildInstitute institute = guild.getTypeAndInstitute().get(GuildInstituteTypeEnum.GI_RB);
        GuildResearchProjectTypeEnum researchProjectTypeEnum = getResearchProjectTypeByRedBagType(redBagTypeEnum);
        int instituteMoneyLv = institute.getResearchTypeAndProject().get(researchProjectTypeEnum).getLevel();
        Table_GuildResearch_Row guildResearchRow = RootTc.get(Table_GuildResearch_Row.class).get(instituteMoneyLv);
        int count = _getResourceCountByRedBagType(redBagTypeEnum, guildResearchRow);
        int sum = _getResourceSumByRedBagType(redBagTypeEnum, guildResearchRow);
        List<Integer> shares = randomShare(count, sum);
        return new NewGuildSystemRedBag(shares, new HashMap<>(), redBagTypeEnum);
    }

    public static NewGuildPlayerRedBag createPlayerRedBagByType(String playerName, GuildRedBagTypeEnum redBagTypeEnum, GuildSendRedBagTypeEnum sendRedBagTypeEnum) {
        int count = Table_GuildRedPacket_Row.getCountByRedBagType(sendRedBagTypeEnum, redBagTypeEnum);
        int sum = Table_GuildRedPacket_Row.getSumByRedBagType(sendRedBagTypeEnum, redBagTypeEnum);
        List<Integer> shares = randomShare(count, sum);
        return new NewGuildPlayerRedBag(shares, new HashMap<>(), redBagTypeEnum, playerName, System.currentTimeMillis());
    }


    private static int _getResourceCountByRedBagType(GuildRedBagTypeEnum redBagTypeEnum, Table_GuildResearch_Row guildResearchRow) {
        switch (redBagTypeEnum) {
            case GRT_MONEY:
                return guildResearchRow.getGoldRedBagNum();
            case GRT_V_MONEY:
                return guildResearchRow.getDiamondRedBagNum();
        }
        String msg = String.format("配置表里没有对应的类型数据:%s", redBagTypeEnum);
        throw new BusinessLogicMismatchConditionException(msg);
    }


    private static int _getResourceSumByRedBagType(GuildRedBagTypeEnum redBagTypeEnum, Table_GuildResearch_Row guildResearchRow) {
        switch (redBagTypeEnum) {
            case GRT_MONEY:
                return guildResearchRow.getGoldRedBagSum();
            case GRT_V_MONEY:
                return guildResearchRow.getDiamondRedBagSum();
        }
        String msg = String.format("配置表里没有对应的类型数据:%s", redBagTypeEnum);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    /**
     * 随机份额
     *
     * @param count
     * @param sum
     * @return
     */
    public static List<Integer> randomShare(int count, int sum) {
        List<Integer> list = new ArrayList<>();
        int total = 0;
        for (int i = 1; i < count; i++) {
            int r = RandomUtils.dropBetweenTowNum(1, 10);
            total = total + r;
            list.add(r);
        }
        List<Integer> shares = new ArrayList<>();
        int remain = sum;
        for (int i = 0; i < list.size(); i++) {
            int share = (sum * list.get(i)) / total;
            remain = remain - share;
            shares.add(share);
        }
        shares.add(remain);
        return shares;
    }

    /**
     * 根据红包类型获取对应的研究所项目
     *
     * @param redBagTypeEnum
     * @return
     */
    public static GuildResearchProjectTypeEnum getResearchProjectTypeByRedBagType(GuildRedBagTypeEnum redBagTypeEnum) {
        switch (redBagTypeEnum) {
            case GRT_MONEY:
                return GuildResearchProjectTypeEnum.GR_MONEY;
            case GRT_V_MONEY:
                return GuildResearchProjectTypeEnum.GR_V_MONEY;
        }
        String msg = "研究所项目中没有对应的红包类型";
        throw new BusinessLogicMismatchConditionException(msg);
    }


    public static IdAndCount getConsumeByResearchType(GuildResearchTypeEnum researchType) {
        switch (researchType) {
            case MONEY_RESEARCH:
                TupleCell<Integer> moneyTupleCell = AllServerConfig.Guild_Research_Money_Consume.getConfig();
                return new IdAndCount(ResourceTypeEnum.RES_MONEY_VALUE, moneyTupleCell.get(TupleCell.FIRST));
            case VIP_MONEY_RESEARCH:
                TupleCell<Integer> vipMoneyTupleCell = AllServerConfig.Guild_Research_Vip_Money_Consume.getConfig();
                return new IdAndCount(ResourceTypeEnum.RES_VIPMONEY_VALUE, vipMoneyTupleCell.get(TupleCell.FIRST));
        }
        String msg = String.format("研究院没有这个研究类型:%s", researchType);
        throw new BusinessLogicMismatchConditionException(msg);
    }


    public static IdAndCount getRewardByResearchType(GuildResearchTypeEnum researchType) {
        switch (researchType) {
            case MONEY_RESEARCH:
                TupleCell<Integer> moneyTupleCell = AllServerConfig.Guild_Research_Money_Consume.getConfig();
                return new IdAndCount(ResourceTypeEnum.RES_GUILD_MONEY_VALUE, moneyTupleCell.get(TupleCell.THIRD));
            case VIP_MONEY_RESEARCH:
                TupleCell<Integer> vipMoneyTupleCell = AllServerConfig.Guild_Research_Vip_Money_Consume.getConfig();
                return new IdAndCount(ResourceTypeEnum.RES_GUILD_MONEY_VALUE, vipMoneyTupleCell.get(TupleCell.THIRD));
        }
        String msg = String.format("研究院没有这个研究类型:%s", researchType);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    public static int getExpByResearchType(GuildResearchTypeEnum researchType) {
        switch (researchType) {
            case MONEY_RESEARCH:
                TupleCell<Integer> moneyTupleCell = AllServerConfig.Guild_Research_Money_Consume.getConfig();
                return moneyTupleCell.get(TupleCell.SECOND);
            case VIP_MONEY_RESEARCH:
                TupleCell<Integer> vipMoneyTupleCell = AllServerConfig.Guild_Research_Vip_Money_Consume.getConfig();
                return vipMoneyTupleCell.get(TupleCell.SECOND);
        }
        String msg = String.format("研究院没有这个研究类型:%s", researchType);
        throw new BusinessLogicMismatchConditionException(msg);
    }

    public static int getGuildLevel(NewGuild guild) {
        return guild.getTypeAndInstitute().get(GuildInstituteTypeEnum.GI_LV).getResearchTypeAndProject().get(GuildResearchProjectTypeEnum.GR_GUILD_LV).getLevel();
    }

    public static long getGuildExp(NewGuild guild) {
        return guild.getTypeAndInstitute().get(GuildInstituteTypeEnum.GI_LV).getResearchTypeAndProject().get(GuildResearchProjectTypeEnum.GR_GUILD_LV).getOvfExp();
    }

    public static SimpleGuild createSimpleGuild(NewGuild guild) {
        SimpleGuild simpleGuild = new SimpleGuild();
        simpleGuild.setGuildBattleValue(guild.getBattleValue());
        simpleGuild.setGuildId(guild.getGuildId());
        simpleGuild.setGuildLv(getGuildLevel(guild));
        simpleGuild.setGuildNotic(guild.getGuildNotice());
        simpleGuild.setMemberCount(guild.getPlayerIdToGuildPlayerInfo().size());
        SimplePlayer simplePlayer = SimplePojoUtils.querySimplePlayerById(guild.getMasterPlayerId(), guild.getMasterOuterRealmId());
        simpleGuild.setMasterName(simplePlayer.getPlayerName());
        simpleGuild.setGuildSimpleId(guild.getSimpleId());
        simpleGuild.setRank(guild.getRank());
        simpleGuild.setGuildName(guild.getGuildName());
        simpleGuild.setIcon(guild.getGuildIcon());
        simpleGuild.setOutRealmId(guild.getOuterRealmId());
        return simpleGuild;
    }


    public static SimpleGuild getSimpleGuild(String guildId, int outerRealmId) {
        return DBUtils.getHashPojo(guildId, outerRealmId, SimpleGuild.class);
    }

    public static Map<String, SimpleGuild> getSimpleGuildLis(List<String> guildIdLis, int outerRealmId) {
        return DBUtils.getHashPojoLis(guildIdLis, outerRealmId, SimpleGuild.class);
    }

    public static long getGuildBattleValue(Map<String, SimplePlayer> simplePlayerMap) {
        long battleValue = 0;
        for (Entry<String, SimplePlayer> entry : simplePlayerMap.entrySet()) {
            SimplePlayer simplePlayer = entry.getValue();
            battleValue += simplePlayer.getBattleValue();
        }
        return battleValue;
    }


    /***
     * 发送红点通知
     * @param actorContext
     * @param redPointType
     * @param guildCenterPlayerList
     */
    public static void sendNotifyRedPointMsg(ActorContext actorContext, RedPointEnum redPointType, List<NewGuildCenterPlayer> guildCenterPlayerList) {
        HashMap<RedPointEnum, Boolean> redPointToShow = new HashMap<>();
        redPointToShow.put(redPointType, true);
        for (NewGuildCenterPlayer guildCenterPlayer : guildCenterPlayerList) {
            CheckPlayerOnlineMsgRequest<In_NotifyRedPointMsg.Request> request = new CheckPlayerOnlineMsgRequest<>(guildCenterPlayer.getPlayerId(), new In_NotifyRedPointMsg.Request(redPointToShow));
            ClusterMessageSender.sendMsgToPath(actorContext, request, ActorSystemPath.WS_GameServer_Selection_World);
        }
    }


}
