package ws.gameServer.features.standalone.extp.newGuild.utils;

import ws.gameServer.features.standalone.extp.heros.ctrl.HerosCtrl;
import ws.protos.EnumsProtos.GuildInstituteTypeEnum;
import ws.protos.EnumsProtos.GuildJobEnum;
import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.protos.EnumsProtos.GuildResearchProjectTypeEnum;
import ws.protos.NewGuildProtos.Sm_NewGuild_ApplyInfo;
import ws.protos.NewGuildProtos.Sm_NewGuild_GuildInfo;
import ws.protos.NewGuildProtos.Sm_NewGuild_SimpleGuildInfo;
import ws.protos.NewGuildProtos.Sm_NewGuild_SimplePlayer;
import ws.protos.NewGuildProtos.Sm_NewGuild_Track;
import ws.protos.NewGuildRedBagProtos.Sm_NewGuildRedBag_Info;
import ws.protos.NewGuildRedBagProtos.Sm_NewGuildRedBag_Rank;
import ws.protos.NewGuildResearchProtos.Sm_NewGuildResearch_Institute;
import ws.protos.NewGuildResearchProtos.Sm_NewGuildResearch_Institute_Project;
import ws.protos.NewGuildResearchProtos.Sm_NewGuildResearch_Institute_Project.Builder;
import ws.protos.NewGuildTrainProtos.Sm_NewGuildTrain_Member_Trainer;
import ws.protos.NewGuildTrainProtos.Sm_NewGuildTrain_Trainer;
import ws.relationship.topLevelPojos.common.LevelUpObj;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.heros.Heros;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildApplyInfo;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildCenterPlayer;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildInstitute;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildPlayerRedBag;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildRedBag;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildSystemRedBag;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildTrack;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuildTrainer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.ProtoUtils;
import ws.relationship.utils.RelationshipCommonUtils;
import ws.relationship.utils.SimplePojoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NewGuildCtrlProtos {

    public static Sm_NewGuild_GuildInfo createSm_NewGuild_GuildInfo_Whitout_Track(NewGuild guild) {
        Sm_NewGuild_GuildInfo.Builder b = Sm_NewGuild_GuildInfo.newBuilder();
        Sm_NewGuild_SimpleGuildInfo smGuildSimpleGuildInfo = createSm_NewGuild_SimpleGuildInfo(guild);
        b.setSimpleGuildInfo(smGuildSimpleGuildInfo);
        return b.build();
    }

    public static Sm_NewGuild_GuildInfo createSm_NewGuild_GuildInfo(NewGuild guild) {
        Sm_NewGuild_GuildInfo.Builder b = Sm_NewGuild_GuildInfo.newBuilder();
        Sm_NewGuild_SimpleGuildInfo smGuildSimpleGuildInfo = createSm_NewGuild_SimpleGuildInfo(guild);
        b.addAllGuildTrack(create_Sm_Guild_Track_List(guild.getGuildTracks()));
        b.setSimpleGuildInfo(smGuildSimpleGuildInfo);
        return b.build();
    }

    public static Sm_NewGuild_SimpleGuildInfo createSm_NewGuild_SimpleGuildInfo(NewGuild guild) {
        Sm_NewGuild_SimpleGuildInfo.Builder smGuildSimpleGuildInfo = Sm_NewGuild_SimpleGuildInfo.newBuilder();
        smGuildSimpleGuildInfo.setGuildName(guild.getGuildName());
        SimplePlayer simplePlayer = SimplePojoUtils.querySimplePlayerById(guild.getMasterPlayerId(), guild.getMasterOuterRealmId());
        smGuildSimpleGuildInfo.setMasterName(simplePlayer.getPlayerName());
        smGuildSimpleGuildInfo.setGuildIcon(guild.getGuildIcon());
        smGuildSimpleGuildInfo.setGuildId(guild.getGuildId());
        smGuildSimpleGuildInfo.setGuildLevel(NewGuildCtrlUtils.getGuildLevel(guild));
        smGuildSimpleGuildInfo.setMembers(guild.getPlayerIdToGuildPlayerInfo().size());
        smGuildSimpleGuildInfo.setDeputyCount(NewGuildCtrlUtils.getJobCount(guild, GuildJobEnum.GJ_DEPUTY));
        smGuildSimpleGuildInfo.setEliteCount(NewGuildCtrlUtils.getJobCount(guild, GuildJobEnum.GJ_ELITE));
        smGuildSimpleGuildInfo.setMasterCount(NewGuildCtrlUtils.getJobCount(guild, GuildJobEnum.GJ_MASTER));
        smGuildSimpleGuildInfo.setGuildSimpleId(guild.getSimpleId());
        smGuildSimpleGuildInfo.setJoinType(guild.getJoinTypeEnum());
        smGuildSimpleGuildInfo.setLimitLevel(guild.getLimitLevel());
        smGuildSimpleGuildInfo.setMailsCount(guild.getMailsCount());
        smGuildSimpleGuildInfo.setGuildNotice(RelationshipCommonUtils.converNullToEmpty(guild.getGuildNotice()));
        return smGuildSimpleGuildInfo.build();
    }

    public static Sm_NewGuild_SimpleGuildInfo createSm_NewGuild_SimpleGuildInfo(NewGuild guild, List<String> applyGuildIdList) {
        Sm_NewGuild_SimpleGuildInfo.Builder smGuildSimpleGuildInfo = Sm_NewGuild_SimpleGuildInfo.newBuilder();
        smGuildSimpleGuildInfo.setGuildName(guild.getGuildName());
        SimplePlayer simplePlayer = SimplePojoUtils.querySimplePlayerById(guild.getMasterPlayerId(), guild.getMasterOuterRealmId());
        smGuildSimpleGuildInfo.setMasterName(simplePlayer.getPlayerName());
        smGuildSimpleGuildInfo.setGuildIcon(guild.getGuildIcon());
        smGuildSimpleGuildInfo.setGuildId(guild.getGuildId());
        smGuildSimpleGuildInfo.setGuildLevel(NewGuildCtrlUtils.getGuildLevel(guild));
        smGuildSimpleGuildInfo.setMembers(guild.getPlayerIdToGuildPlayerInfo().size());
        smGuildSimpleGuildInfo.setDeputyCount(NewGuildCtrlUtils.getJobCount(guild, GuildJobEnum.GJ_DEPUTY));
        smGuildSimpleGuildInfo.setEliteCount(NewGuildCtrlUtils.getJobCount(guild, GuildJobEnum.GJ_ELITE));
        smGuildSimpleGuildInfo.setMasterCount(NewGuildCtrlUtils.getJobCount(guild, GuildJobEnum.GJ_MASTER));
        smGuildSimpleGuildInfo.setJoinType(guild.getJoinTypeEnum());
        smGuildSimpleGuildInfo.setLimitLevel(guild.getLimitLevel());
        smGuildSimpleGuildInfo.setGuildSimpleId(guild.getSimpleId());
        smGuildSimpleGuildInfo.setGuildNotice(RelationshipCommonUtils.converNullToEmpty(guild.getGuildNotice()));
        smGuildSimpleGuildInfo.setMailsCount(guild.getMailsCount());
        if (applyGuildIdList.contains(guild.getGuildId())) {
            smGuildSimpleGuildInfo.setIsApply(true);
        }
        return smGuildSimpleGuildInfo.build();
    }

    public static List<Sm_NewGuild_SimpleGuildInfo> createSm_NewGuild_SimpleGuildInfo_List(List<NewGuild> guildList, List<String> applyGuildIdsList) {
        List<Sm_NewGuild_SimpleGuildInfo> sm_newGuild_simpleGuildInfoList = new ArrayList<>();
        for (NewGuild guild : guildList) {
            sm_newGuild_simpleGuildInfoList.add(createSm_NewGuild_SimpleGuildInfo(guild, applyGuildIdsList));
        }
        return sm_newGuild_simpleGuildInfoList;
    }


    public static List<Sm_NewGuild_SimpleGuildInfo> createSm_NewGuild_SimpleGuildInfo_List(List<NewGuild> guildList) {
        List<Sm_NewGuild_SimpleGuildInfo> sm_newGuild_simpleGuildInfoList = new ArrayList<>();
        for (NewGuild guild : guildList) {
            sm_newGuild_simpleGuildInfoList.add(createSm_NewGuild_SimpleGuildInfo(guild));
        }
        return sm_newGuild_simpleGuildInfoList;
    }


    private static List<Sm_NewGuild_Track> create_Sm_Guild_Track_List(List<NewGuildTrack> guildTracks) {
        List<Sm_NewGuild_Track> sm_newGuild_trackList = new ArrayList<>();
        for (NewGuildTrack guildTrack : guildTracks) {
            sm_newGuild_trackList.add(createSm_NewGuild_Track(guildTrack));
        }
        return sm_newGuild_trackList;
    }

    private static Sm_NewGuild_Track createSm_NewGuild_Track(NewGuildTrack guildTrack) {
        Sm_NewGuild_Track.Builder b = Sm_NewGuild_Track.newBuilder();
        b.setTime(guildTrack.getTime());
        b.setTpId(guildTrack.getTpId());
        List<String> list = guildTrack.getArgs();
        RelationshipCommonUtils.removeNullElements(list);
        b.addAllContent(list);
        return b.build();
    }

    public static List<Sm_NewGuild_SimplePlayer> createSm_NewGuild_SimplePlayer_List(Map<String, SimplePlayer> idToSimplePlayer, List<NewGuildCenterPlayer> guildCenterPlayerList) {
        List<Sm_NewGuild_SimplePlayer> sm_newGuild_simplePlayers = new ArrayList<>();
        for (NewGuildCenterPlayer guildCenterPlayer : guildCenterPlayerList) {
            sm_newGuild_simplePlayers.add(createSm_NewGuild_SimplePlayerOne(idToSimplePlayer.get(guildCenterPlayer.getPlayerId()), guildCenterPlayer));
        }
        return sm_newGuild_simplePlayers;
    }


    public static Sm_NewGuild_SimplePlayer createSm_NewGuild_SimplePlayerOne(SimplePlayer simplePlayer, NewGuildCenterPlayer guildCenterPlayer) {
        Sm_NewGuild_SimplePlayer.Builder b = Sm_NewGuild_SimplePlayer.newBuilder();
        b.setSimplePlayerBase(ProtoUtils.create_Sm_Common_SimplePlayer_Base(simplePlayer));
        b.setJob(guildCenterPlayer.getJob());
        b.setContributionSechdule(guildCenterPlayer.getSumContribution());
        b.setContribution(guildCenterPlayer.getTodayContribution());
        return b.build();
    }


    public static List<Sm_NewGuild_ApplyInfo> createSm_NewGuild_ApplyInfo_List(List<NewGuildApplyInfo> applyInfoList, Map<String, SimplePlayer> idTosimplePlayer) {
        List<Sm_NewGuild_ApplyInfo> sm_newGuild_applyInfoList = new ArrayList<>();
        for (NewGuildApplyInfo applyInfo : applyInfoList) {
            sm_newGuild_applyInfoList.add(createSm_NewGuild_ApplyInfo(applyInfo, idTosimplePlayer.get(applyInfo.getPlayerId())));
        }
        return sm_newGuild_applyInfoList;
    }


    public static Sm_NewGuild_ApplyInfo createSm_NewGuild_ApplyInfo(NewGuildApplyInfo applyInfo, SimplePlayer simplePlayer) {
        Sm_NewGuild_ApplyInfo.Builder b = Sm_NewGuild_ApplyInfo.newBuilder();
        b.setApplyTime(applyInfo.getApplyTime());
        b.setSimplePlayerBase(ProtoUtils.create_Sm_Common_SimplePlayer_Base(simplePlayer));
        return b.build();
    }

    public static List<Sm_NewGuildResearch_Institute> createSm_NewGuildResearch_Institute_List(Map<GuildInstituteTypeEnum, NewGuildInstitute> typeAndInstitute) {
        List<Sm_NewGuildResearch_Institute> smNewGuildResearchInstituteList = new ArrayList<>();
        for (Entry<GuildInstituteTypeEnum, NewGuildInstitute> entry : typeAndInstitute.entrySet()) {
            Sm_NewGuildResearch_Institute.Builder b = Sm_NewGuildResearch_Institute.newBuilder();
            b.setInstituteType(entry.getKey());
            b.addAllProject(createSm_NewGuildResearch_Institute_Project_List(entry.getValue()));
            smNewGuildResearchInstituteList.add(b.build());
        }
        return smNewGuildResearchInstituteList;
    }

    public static List<Sm_NewGuildResearch_Institute_Project> createSm_NewGuildResearch_Institute_Project_List(NewGuildInstitute guildInstitute) {
        List<Sm_NewGuildResearch_Institute_Project> smNewGuildResearchInstituteProjectList = new ArrayList<>();
        Map<GuildResearchProjectTypeEnum, LevelUpObj> researchTypeAndProjects = guildInstitute.getResearchTypeAndProject();
        for (Entry<GuildResearchProjectTypeEnum, LevelUpObj> entry : researchTypeAndProjects.entrySet()) {
            smNewGuildResearchInstituteProjectList.add(createSm_NewGuildResearch_Institute_Project(entry));
        }
        return smNewGuildResearchInstituteProjectList;
    }

    private static Sm_NewGuildResearch_Institute_Project createSm_NewGuildResearch_Institute_Project(Entry<GuildResearchProjectTypeEnum, LevelUpObj> entry) {
        Sm_NewGuildResearch_Institute_Project.Builder b = Sm_NewGuildResearch_Institute_Project.newBuilder();
        b.setResearchProjectType(entry.getKey());
        b.setLvUpObj(ProtoUtils.create_Sm_Common_LvAndOvfExp(entry.getValue()));
        return b.build();
    }

    public static Sm_NewGuildResearch_Institute_Project createSm_NewGuildResearch_Institute_ProjectByResearchType(Map<GuildResearchProjectTypeEnum, LevelUpObj> researchTypeAndProjects, GuildResearchProjectTypeEnum researchProjectType) {
        LevelUpObj levelUpObj = researchTypeAndProjects.get(researchProjectType);
        Builder b = Sm_NewGuildResearch_Institute_Project.newBuilder();
        b.setResearchProjectType(researchProjectType);
        b.setLvUpObj(ProtoUtils.create_Sm_Common_LvAndOvfExp(levelUpObj));
        return b.build();
    }


    public static List<Sm_NewGuildRedBag_Info> createSm_NewGuildRedBag_Info_List(Map<GuildRedBagTypeEnum, NewGuildSystemRedBag> typeAndNewGuildRedBag, SimplePlayer simplePlayer) {
        List<Sm_NewGuildRedBag_Info> smNewGuildRedBagInfoList = new ArrayList<>();
        for (Entry<GuildRedBagTypeEnum, NewGuildSystemRedBag> entry : typeAndNewGuildRedBag.entrySet()) {
            smNewGuildRedBagInfoList.add(createSm_NewGuildRedBag_Info(entry.getValue(), simplePlayer));
        }
        return smNewGuildRedBagInfoList;
    }


    public static List<Sm_NewGuildRedBag_Info> createSm_NewGuildRedBag_Info_List(List<NewGuildPlayerRedBag> redBagList, String playerId, int outerRealmId) {
        List<Sm_NewGuildRedBag_Info> smNewGuildRedBagInfoList = new ArrayList<>();
        for (NewGuildPlayerRedBag redBag : redBagList) {
            smNewGuildRedBagInfoList.add(createSm_NewGuildRedBag_Info(redBag, playerId, outerRealmId));
        }
        return smNewGuildRedBagInfoList;
    }

    /***
     * 玩家红包协议封装
     * @param redBag
     * @param simplePlayer
     * @return
     */
    public static Sm_NewGuildRedBag_Info createSm_NewGuildRedBag_Info(NewGuildPlayerRedBag redBag, SimplePlayer simplePlayer) {
        Sm_NewGuildRedBag_Info.Builder b = Sm_NewGuildRedBag_Info.newBuilder();
        int totoalCount = NewGuildCtrlUtils.getRedBagTotalCount(redBag);
        b.setCount(totoalCount - redBag.getPlayerNameAndShare().size());
        b.setRedBagType(redBag.getRedBagTypeEnum());
        boolean hasReceive = NewGuildCtrlUtils.hasReceive(redBag, simplePlayer.getPlayerName());
        b.setHasReceive(hasReceive);
        if (hasReceive) {
            //抢到的资源数量
            int grabCount = redBag.getPlayerNameAndShare().get(simplePlayer.getPlayerName());
            b.setGrabCount(grabCount);
        }
        b.setPlayerName(NewGuildCtrlUtils.hightTotalPlayerName(redBag));
        b.setRedBagId(redBag.getRedBagId());
        String ownerName = simplePlayer.getPlayerName();
        b.setOwnerName(ownerName);
        b.setTotalCount(totoalCount);
        b.setRedBagTotal(NewGuildCtrlUtils.getRedBagTotal(redBag));
        return b.build();
    }


    /**
     * 系统红包协议封装
     *
     * @param redBag
     * @param simplePlayer
     * @return
     */
    public static Sm_NewGuildRedBag_Info createSm_NewGuildRedBag_Info(NewGuildSystemRedBag redBag, SimplePlayer simplePlayer) {
        Sm_NewGuildRedBag_Info.Builder b = Sm_NewGuildRedBag_Info.newBuilder();
        int totoalCount = NewGuildCtrlUtils.getRedBagTotalCount(redBag);
        b.setCount(totoalCount - redBag.getPlayerNameAndShare().size());
        b.setRedBagType(redBag.getRedBagTypeEnum());
        boolean hasReceive = NewGuildCtrlUtils.hasReceive(redBag, simplePlayer.getPlayerName());
        b.setHasReceive(hasReceive);
        if (hasReceive) {
            //抢到的资源数量
            int grabCount = redBag.getPlayerNameAndShare().get(simplePlayer.getPlayerName());
            b.setGrabCount(grabCount);
        }
        b.setPlayerName(NewGuildCtrlUtils.hightTotalPlayerName(redBag));
        b.setTotalCount(totoalCount);
        b.setRedBagTotal(NewGuildCtrlUtils.getRedBagTotal(redBag));
        return b.build();
    }


    public static Sm_NewGuildRedBag_Info createSm_NewGuildRedBag_Info(NewGuildRedBag redBag, String playerId, int outerRealmId) {
        SimplePlayer simplePlayer = SimplePojoUtils.querySimplePlayerById(playerId, outerRealmId);
        if (NewGuildCtrlUtils.isPlayerRedBag(redBag.getRedBagTypeEnum())) {
            return createSm_NewGuildRedBag_Info((NewGuildPlayerRedBag) redBag, simplePlayer);
        }

        return createSm_NewGuildRedBag_Info((NewGuildSystemRedBag) redBag, simplePlayer);
    }

    public static List<Sm_NewGuildRedBag_Rank> createSm_NewGuildRedBag_Rank_List(Map<String, Integer> sortRank) {
        List<Sm_NewGuildRedBag_Rank> smNewGuildRedBagRankList = new ArrayList<>();
        for (Entry<String, Integer> entry : sortRank.entrySet()) {
            smNewGuildRedBagRankList.add(createSm_NewGuildRedBag_Rank(entry));
        }
        return smNewGuildRedBagRankList;
    }

    private static Sm_NewGuildRedBag_Rank createSm_NewGuildRedBag_Rank(Entry<String, Integer> entry) {
        Sm_NewGuildRedBag_Rank.Builder b = Sm_NewGuildRedBag_Rank.newBuilder();
        b.setPlayerName(entry.getKey());
        b.setReceiveCount(entry.getValue());
        return b.build();
    }


    public static Sm_NewGuildTrain_Trainer createSm_NewGuildTrain_Trainer_One_Trainer(NewGuildTrainer newGuildTrainer, HerosCtrl herosCtrl) {
        Sm_NewGuildTrain_Trainer.Builder b = Sm_NewGuildTrain_Trainer.newBuilder();
        b.setIndex(newGuildTrainer.getIndex());
        if (newGuildTrainer.getHeroId() != 0) {
            Hero hero = herosCtrl.getHero(newGuildTrainer.getHeroId());
            b.setHeroTpId(hero.getTpId());
            b.setLastSettle(newGuildTrainer.getLastSettle());
            b.setHeroLv(hero.getLv());
            b.setOvfExp(hero.getOvfExp());
            b.setQualityLv(hero.getQualityLv());
        }
        return b.build();
    }

    public static Sm_NewGuildTrain_Member_Trainer createSm_NewGuildTrain_Member_Trainer_One_Trainer(NewGuildCenterPlayer beGuildCenterPlayer, SimplePlayer besimplePlayer, NewGuildCenterPlayer guildCenterPlayer) {
        Sm_NewGuildTrain_Member_Trainer.Builder b = Sm_NewGuildTrain_Member_Trainer.newBuilder();
        b.setBeAccelerate(beGuildCenterPlayer.getAcceleratePlayerName().size());
        b.setPlayerId(besimplePlayer.getPlayerId());
        b.setIconId(besimplePlayer.getIcon());
        b.setIsStamp(_isStamp(besimplePlayer.getPlayerId(), guildCenterPlayer));
        b.setPlayerLv(besimplePlayer.getLv());
        b.setPlayerName(besimplePlayer.getPlayerName());
        return b.build();
    }

    private static boolean _isStamp(String playerId, NewGuildCenterPlayer guildCenterPlayer) {
        return guildCenterPlayer.getStampPlayerIds().contains(playerId);
    }

    public static List<Sm_NewGuildTrain_Trainer> createSm_NewGUildTrain_Trainer_List(List<NewGuildTrainer> trainerList, Heros heros) {
        List<Sm_NewGuildTrain_Trainer> smNewGuildTrainTrainerArrayList = new ArrayList<>();
        for (NewGuildTrainer trainer : trainerList) {
            smNewGuildTrainTrainerArrayList.add(createSm_NewGuildTrain_Trainer(trainer, heros));
        }
        return smNewGuildTrainTrainerArrayList;
    }


    public static List<Sm_NewGuildTrain_Trainer> createSm_NewGUildTrain_Trainer_List(Map<Integer, NewGuildTrainer> indexAndTrainer, HerosCtrl herosCtrl) {
        List<Sm_NewGuildTrain_Trainer> smNewGuildTrainTrainerArrayList = new ArrayList<>();
        for (Entry<Integer, NewGuildTrainer> entry : indexAndTrainer.entrySet()) {
            smNewGuildTrainTrainerArrayList.add(createSm_NewGuildTrain_Trainer(entry, herosCtrl));
        }
        return smNewGuildTrainTrainerArrayList;
    }


    public static Sm_NewGuildTrain_Trainer createSm_NewGuildTrain_Trainer(Entry<Integer, NewGuildTrainer> entry, HerosCtrl herosCtrl) {
        Sm_NewGuildTrain_Trainer.Builder b = Sm_NewGuildTrain_Trainer.newBuilder();
        b.setIndex(entry.getKey());
        if (entry.getValue().getHeroId() != 0) {
            Hero hero = herosCtrl.getHero(entry.getValue().getHeroId());
            b.setLastSettle(entry.getValue().getLastSettle());
            b.setHeroTpId(hero.getTpId());
            b.setHeroLv(hero.getLv());
            b.setOvfExp(hero.getOvfExp());
            b.setQualityLv(hero.getQualityLv());
        }
        return b.build();
    }

    public static Sm_NewGuildTrain_Trainer createSm_NewGuildTrain_Trainer(NewGuildTrainer trainer, Heros heros) {
        Sm_NewGuildTrain_Trainer.Builder b = Sm_NewGuildTrain_Trainer.newBuilder();
        b.setIndex(trainer.getIndex());
        if (trainer.getHeroId() != 0) {
            Hero hero = heros.getIdToHero().get(trainer.getHeroId());
            b.setLastSettle(trainer.getLastSettle());
            b.setHeroTpId(hero.getTpId());
            b.setHeroLv(hero.getLv());
            b.setOvfExp(hero.getOvfExp());
            b.setQualityLv(hero.getQualityLv());
        }
        return b.build();
    }

    public static List<Sm_NewGuildTrain_Member_Trainer> createSm_NewGuildTrain_Member_Trainer_List(Map<String, SimplePlayer> idToSimplePlayer, List<NewGuildCenterPlayer> guildCenterPlayerList, NewGuildCenterPlayer guildCenterPlayer) {
        List<Sm_NewGuildTrain_Member_Trainer> memberTrainerList = new ArrayList<>();
        for (NewGuildCenterPlayer member : guildCenterPlayerList) {
            SimplePlayer simplePlayer = idToSimplePlayer.get(member.getPlayerId());
            memberTrainerList.add(createSm_NewGuildTrain_Member_Trainer_One_Trainer(member, simplePlayer, guildCenterPlayer));
        }
        return memberTrainerList;
    }

}
