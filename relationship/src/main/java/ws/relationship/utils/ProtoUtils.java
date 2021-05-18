package ws.relationship.utils;

import org.apache.commons.lang3.StringUtils;
import ws.common.utils.general.EnumUtils;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.CommonProtos.Sm_Common_Hero;
import ws.protos.CommonProtos.Sm_Common_Hero_Attr;
import ws.protos.CommonProtos.Sm_Common_Hero_Equipment;
import ws.protos.CommonProtos.Sm_Common_Hero_Skill;
import ws.protos.CommonProtos.Sm_Common_Hero_WarSoul;
import ws.protos.CommonProtos.Sm_Common_IdAndCount;
import ws.protos.CommonProtos.Sm_Common_IdAndCountList;
import ws.protos.CommonProtos.Sm_Common_IdMaptoCount;
import ws.protos.CommonProtos.Sm_Common_IdMaptoCountList;
import ws.protos.CommonProtos.Sm_Common_LvAndOvfExp;
import ws.protos.CommonProtos.Sm_Common_Rank;
import ws.protos.CommonProtos.Sm_Common_RankList;
import ws.protos.CommonProtos.Sm_Common_Round;
import ws.protos.CommonProtos.Sm_Common_SimpleGuild;
import ws.protos.CommonProtos.Sm_Common_SimpleMainFormation;
import ws.protos.CommonProtos.Sm_Common_SimpleMainFormation_Hero;
import ws.protos.CommonProtos.Sm_Common_SimplePlayer;
import ws.protos.CommonProtos.Sm_Common_SimplePlayer_Base;
import ws.protos.CommonProtos.Sm_Common_SimplePlayer_Pvp;
import ws.protos.EnumsProtos.CommonRankTypeEnum;
import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.protos.EnumsProtos.ErrorCodeEnum;
import ws.protos.EnumsProtos.HeroPositionEnum;
import ws.protos.EnumsProtos.SkillPositionEnum;
import ws.protos.EnumsProtos.WarSoulPositionEnum;
import ws.protos.MessageHandlerProtos.Response;
import ws.relationship.base.HeroAttrs;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.base.RedisRankAndScore;
import ws.relationship.topLevelPojos.common.Iac;
import ws.relationship.topLevelPojos.common.LevelUpObj;
import ws.relationship.topLevelPojos.heros.Equipment;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.simpleGuild.SimpleGuild;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayerMfHero;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ProtoUtils {

    public static Response.Builder create_Response(Code code, Enum<?> action) {
        return create_Response(code, action, ErrorCodeEnum.UNKNOWN);
    }

    public static Response.Builder create_Response(Code code, Enum<?> action, ErrorCodeEnum errorCode) {
        errorCode = errorCode == null ? ErrorCodeEnum.UNKNOWN : errorCode;
        Response.Builder br = Response.newBuilder();
        br.setMsgCode(code);
        br.setResult(false);
        br.setErrorCode(errorCode);
        br.setSmMsgAction(EnumUtils.protoActionToString(action));
        return br;
    }


    public static Sm_Common_IdMaptoCountList create_Sm_Common_IdMaptoCountList(List<IdMaptoCount> idMaptoCounts) {
        Sm_Common_IdMaptoCountList.Builder b = Sm_Common_IdMaptoCountList.newBuilder();
        for (IdMaptoCount idMaptoCount : idMaptoCounts) {
            b.addIdMaptoCounts(create_Sm_Common_IdMaptoCount(idMaptoCount));
        }
        return b.build();
    }

    public static Sm_Common_IdMaptoCount create_Sm_Common_IdMaptoCount(IdMaptoCount idMaptoCount) {
        Sm_Common_IdMaptoCount.Builder b = Sm_Common_IdMaptoCount.newBuilder();
        for (IdAndCount idAndCount : idMaptoCount.getAll()) {
            b.addIdAndCounts(create_Sm_Common_IdAndCount(idAndCount));
        }
        return b.build();
    }

    public static Sm_Common_IdAndCountList create_Sm_Common_IdAndCountList_UseIac(List<Iac> iacList) {
        Sm_Common_IdAndCountList.Builder bs = Sm_Common_IdAndCountList.newBuilder();
        for (Iac iac : iacList) {
            bs.addIdAndCounts(create_Sm_Common_IdAndCount(iac));
        }
        return bs.build();
    }


    public static Sm_Common_IdAndCountList create_Sm_Common_IdAndCountList(List<IdAndCount> idAndCountList) {
        Sm_Common_IdAndCountList.Builder bs = Sm_Common_IdAndCountList.newBuilder();
        for (IdAndCount idAndCount : idAndCountList) {
            bs.addIdAndCounts(create_Sm_Common_IdAndCount(idAndCount));
        }
        return bs.build();
    }

    public static Sm_Common_IdAndCount create_Sm_Common_IdAndCount(Iac iac) {
        return create_Sm_Common_IdAndCount(new IdAndCount(iac));
    }

    public static Sm_Common_IdAndCount create_Sm_Common_IdAndCount(IdAndCount idAndCount) {
        Sm_Common_IdAndCount.Builder b = Sm_Common_IdAndCount.newBuilder();
        b.setId(idAndCount.getId());
        b.setCount(idAndCount.getCount());
        return b.build();
    }

    public static List<Sm_Common_Hero> create_Sm_Common_Hero_Lis(Map<Integer, Hero> idToHero) {
        List<Sm_Common_Hero> list = new ArrayList<>();
        idToHero.values().forEach(hero -> {
            list.add(create_Sm_Common_Hero(hero).build());
        });
        return list;
    }

    public static Sm_Common_Hero.Builder create_Sm_Common_Hero(Hero hero) {
        Sm_Common_Hero.Builder b = Sm_Common_Hero.newBuilder();
        b.setId(hero.getId());
        b.setTpId(hero.getTpId());
        b.setLv(hero.getLv());
        b.setOvfExp(hero.getOvfExp());
        b.setQualityLv(hero.getQualityLv());
        b.setStarLv(hero.getStarLv());
        b.addAllSkills(create_Sm_Common_Hero_Skills(hero.getSkills()));
        b.addAllWarSouls(create_Sm_Common_Hero_WarSouls(hero.getSouls()));
        b.addAllEquips(_create_Sm_Common_Hero_Equipments(hero.getEquips()));
        return b;
    }

    public static Sm_Common_Hero create_Sm_Common_Hero_WithHeroAttrs(Hero hero, HeroAttrs heroAttrs) {
        Sm_Common_Hero.Builder b = create_Sm_Common_Hero(hero);
        b.addAllAttrs(_create_Sm_Common_Hero_Attrs(heroAttrs));
        return b.build();
    }


    private static List<Sm_Common_Hero_Skill> create_Sm_Common_Hero_Skills(Map<SkillPositionEnum, Integer> skillPosToLv) {
        List<Sm_Common_Hero_Skill> souls = new ArrayList<>();
        Sm_Common_Hero_Skill.Builder b = Sm_Common_Hero_Skill.newBuilder();
        for (Entry<SkillPositionEnum, Integer> entry : skillPosToLv.entrySet()) {
            b.setSkillPos(entry.getKey());
            b.setLv(entry.getValue());
            souls.add(b.build());
            b.clear();
        }
        return souls;
    }

    private static List<Sm_Common_Hero_WarSoul> create_Sm_Common_Hero_WarSouls(Map<WarSoulPositionEnum, LevelUpObj> soulPosToLv) {
        List<Sm_Common_Hero_WarSoul> souls = new ArrayList<>();
        Sm_Common_Hero_WarSoul.Builder b = Sm_Common_Hero_WarSoul.newBuilder();
        for (Entry<WarSoulPositionEnum, LevelUpObj> entry : soulPosToLv.entrySet()) {
            b.setSoulPos(entry.getKey());
            b.setLv(entry.getValue().getLevel());
            b.setOvfExp(entry.getValue().getOvfExp());
            souls.add(b.build());
            b.clear();
        }
        return souls;
    }

    public static Sm_Common_LvAndOvfExp create_Sm_Common_LvAndOvfExp(LevelUpObj levelUpObj) {
        Sm_Common_LvAndOvfExp.Builder b = Sm_Common_LvAndOvfExp.newBuilder();
        b.setLv(levelUpObj.getLevel());
        b.setOvfExp(levelUpObj.getOvfExp());
        return b.build();
    }


    private static List<Sm_Common_Hero_Attr> _create_Sm_Common_Hero_Attrs(HeroAttrs heroAttrs) {
        List<Sm_Common_Hero_Attr> list = new ArrayList<>();
        heroAttrs.getRaw().forEach((type, value) -> {

        });
        return list;
    }

    private static List<Sm_Common_Hero_Equipment> _create_Sm_Common_Hero_Equipments(LinkedHashMap<EquipmentPositionEnum, Equipment> equips) {
        List<Sm_Common_Hero_Equipment> equipments = new ArrayList<>();
        Sm_Common_Hero_Equipment.Builder b = Sm_Common_Hero_Equipment.newBuilder();
        equips.entrySet().forEach(kv -> {
            Equipment equipment = kv.getValue();
            b.setEquipPos(kv.getKey());
            b.setLv(equipment.getLv());
            b.setQualityLv(equipment.getQualityLv());
            b.setStarLv(equipment.getStarLv());
            b.setOvfExp(equipment.getOvfExp());
            equipments.add(b.build());
            b.clear();
        });
        return equipments;
    }

    /**
     * Sm_Common_IdMaptoCount 转化为 List<IdAndCount>
     *
     * @param map
     * @return
     */
    public static List<IdAndCount> parseSm_Common_IdMaptoCount(Sm_Common_IdMaptoCount map) {
        List<IdAndCount> idAndCountList = new ArrayList<>();
        for (Sm_Common_IdAndCount sidc : map.getIdAndCountsList()) {
            idAndCountList.add(new IdAndCount(sidc.getId(), sidc.getCount()));
        }
        return idAndCountList;
    }

    public static Sm_Common_SimplePlayer_Base create_Sm_Common_SimplePlayer_Base(SimplePlayer simplePlayer) {
        Sm_Common_SimplePlayer_Base.Builder b = Sm_Common_SimplePlayer_Base.newBuilder();
        b.setFirstHandValue(simplePlayer.getFirstHandValue());
        b.setSign(simplePlayer.getSign());
        b.setUltimateTestLevel(simplePlayer.getUltimateTestLevel());
        b.setSimplePlayer(create_Sm_Common_SimplePlayer(simplePlayer));
        b.setLsinTime(simplePlayer.getLastLoginTime());
        b.setLsoutTime(simplePlayer.getLastLogoutTime());
        b.setFormation(create_Sm_Common_SimpleMainFormation(create_Sm_Common_SimpleMainFormation_Hero_List(simplePlayer.getPosToHero())));
        return b.build();
    }


    public static List<Sm_Common_SimpleMainFormation_Hero> create_Sm_Common_SimpleMainFormation_Hero_List(Map<HeroPositionEnum, SimplePlayerMfHero> posToHero) {
        List<Sm_Common_SimpleMainFormation_Hero> sm_common_simpleMainFormation_heroList = new ArrayList<>();

        for (Entry<HeroPositionEnum, SimplePlayerMfHero> entry : posToHero.entrySet()) {
            sm_common_simpleMainFormation_heroList.add(create_Sm_Common_SimpleMainFormation_Hero(entry.getKey(), entry.getValue()));
        }
        return sm_common_simpleMainFormation_heroList;
    }

    public static Sm_Common_SimpleMainFormation create_Sm_Common_SimpleMainFormation(List<Sm_Common_SimpleMainFormation_Hero> smHeroList) {
        Sm_Common_SimpleMainFormation.Builder b = Sm_Common_SimpleMainFormation.newBuilder();
        b.addAllHeros(smHeroList);
        return b.build();
    }


    public static Sm_Common_SimpleMainFormation_Hero create_Sm_Common_SimpleMainFormation_Hero(HeroPositionEnum pos, SimplePlayerMfHero hero) {
        Sm_Common_SimpleMainFormation_Hero.Builder b = Sm_Common_SimpleMainFormation_Hero.newBuilder();
        b.setPos(pos);
        b.setHeroTpId(hero.getHeroTpId());
        b.setLv(hero.getLv());
        b.setQualityLv(hero.getQualityLv());
        b.setStarLv(hero.getStarLv());
        return b.build();
    }


    public static Sm_Common_SimplePlayer create_Sm_Common_SimplePlayer(SimplePlayer simplePlayer) {
        Sm_Common_SimplePlayer.Builder b = Sm_Common_SimplePlayer.newBuilder();
        b.setPlayerId(RelationshipCommonUtils.converNullToEmpty(simplePlayer.getPlayerId()));
        b.setSimplePlayerId(simplePlayer.getSimplePlayerId());
        b.setName(simplePlayer.getPlayerName());
        b.setIconId(simplePlayer.getIcon());
        b.setLevel(simplePlayer.getLv());
        b.setVipLevel(simplePlayer.getVipLv());
        b.setGuildId(RelationshipCommonUtils.converNullToEmpty(simplePlayer.getGuildId()));
        if (!StringUtils.isBlank(simplePlayer.getGuildId())) {
            SimpleGuild simpleGuild = SimplePojoUtils.querySimpleGuild(simplePlayer.getGuildId(), simplePlayer.getInnerRealmId());
            b.setGuildName(RelationshipCommonUtils.converNullToEmpty(simpleGuild.getGuildName()));
        }
        b.setBattleValue(simplePlayer.getBattleValue());
        return b.build();
    }

    /**
     * 通用排行榜，适用于展示SimplePlayer信息
     *
     * @param rankType
     * @param playerIdToSimplePlayer
     * @param redisRankAndScores
     * @return
     */
    public static Sm_Common_RankList create_Sm_Common_Rank_List(CommonRankTypeEnum rankType, Map<String, SimplePlayer> playerIdToSimplePlayer, List<RedisRankAndScore> redisRankAndScores) {
        Sm_Common_RankList.Builder b = Sm_Common_RankList.newBuilder();
        b.setRankType(rankType);
        for (RedisRankAndScore rankAndScore : redisRankAndScores) {
            if (!playerIdToSimplePlayer.containsKey(rankAndScore.getMember())) {
                continue;
            }
            b.addRanks(create_Sm_Common_Rank(playerIdToSimplePlayer.get(rankAndScore.getMember()), rankAndScore.getRank(), rankAndScore.getScore()));
        }
        return b.build();
    }

    /**
     * 通用排行榜，适用于展示SimpleGuild信息
     *
     * @param rankType
     * @param idToSimpleGuild
     * @param redisRankAndScores
     * @return
     */
    public static Sm_Common_RankList create_Sm_Common_Rank_List_ForGuild(CommonRankTypeEnum rankType, Map<String, SimpleGuild> idToSimpleGuild, List<RedisRankAndScore> redisRankAndScores) {
        Sm_Common_RankList.Builder b = Sm_Common_RankList.newBuilder();
        b.setRankType(rankType);
        for (RedisRankAndScore rankAndScore : redisRankAndScores) {
            if (!idToSimpleGuild.containsKey(rankAndScore.getMember())) {
                continue;
            }
            b.addRanks(create_Sm_Common_Rank(idToSimpleGuild.get(rankAndScore.getMember()), rankAndScore.getRank(), rankAndScore.getScore()));
        }
        return b.build();
    }


    public static Sm_Common_Rank create_Sm_Common_Rank(SimplePlayer simplePlayer, int rank, long score) {
        Sm_Common_Rank.Builder b = Sm_Common_Rank.newBuilder();
        b.setSimplePlayerBase(create_Sm_Common_SimplePlayer_Base(simplePlayer));
        b.setRank(rank);
        b.setScore(score);
        return b.build();
    }

    public static Sm_Common_Rank create_Sm_Common_Rank(SimpleGuild simpleGuild, int rank, long score) {
        Sm_Common_Rank.Builder b = Sm_Common_Rank.newBuilder();
        b.setGuild(create_Sm_Common_SimpleGuild(simpleGuild));
        b.setRank(rank);
        b.setScore(score);
        return b.build();
    }


    public static Sm_Common_Rank create_Sm_Common_Rank(SimplePlayer simplePlayer, Hero hero, HeroAttrs heroAttrs, int rank, long score) {
        Sm_Common_Rank.Builder b = Sm_Common_Rank.newBuilder();
        b.setHeroBelongPlayer(create_Sm_Common_SimplePlayer(simplePlayer));
        b.setHero(create_Sm_Common_Hero_WithHeroAttrs(hero, heroAttrs));
        b.setRank(rank);
        b.setScore(score);
        return b.build();
    }


    public static Sm_Common_SimplePlayer_Pvp create_Sm_Common_SimplePlayer_Pvp(SimplePlayer simplePlayer) {
        Sm_Common_SimplePlayer_Pvp.Builder b = Sm_Common_SimplePlayer_Pvp.newBuilder();
        Sm_Common_SimplePlayer_Base b1 = create_Sm_Common_SimplePlayer_Base(simplePlayer);
        b.setBase(b1);
        b.setRank(simplePlayer.getPvpRank());
        b.setPvpIcon(simplePlayer.getPvpIcon());
        b.setDeclaration(RelationshipCommonUtils.converNullToEmpty(simplePlayer.getPvpDeclaration()));
        b.setVictoryTimes(simplePlayer.getPvpVictoryTimes());
        return b.build();
    }

    public static Sm_Common_Round create_Sm_Common_Round(int min, int max) {
        Sm_Common_Round.Builder b = Sm_Common_Round.newBuilder();
        b.setMax(max);
        b.setMin(min);
        return b.build();
    }

    public static Sm_Common_SimpleGuild create_Sm_Common_SimpleGuild(SimpleGuild simpleGuild) {
        Sm_Common_SimpleGuild.Builder b = Sm_Common_SimpleGuild.newBuilder();
        b.setGuildId(simpleGuild.getGuildId());
        b.setGuildName(RelationshipCommonUtils.converNullToEmpty((simpleGuild.getGuildName())));
        b.setGuildSimpleId(simpleGuild.getGuildSimpleId());
        b.setGuildLv(simpleGuild.getGuildLv());
        b.setGuildBattleValue(simpleGuild.getGuildBattleValue());
        b.setMasterName(RelationshipCommonUtils.converNullToEmpty(simpleGuild.getMasterName()));
        b.setRank(simpleGuild.getRank());
        b.setMemberCount(simpleGuild.getMemberCount());
        b.setGuildNotic(RelationshipCommonUtils.converNullToEmpty(simpleGuild.getGuildNotic()));
        b.setIcon(simpleGuild.getIcon());
        return b.build();
    }


    public static Sm_Common_Round createSm_Common_Round(int min, int max) {
        Sm_Common_Round.Builder b = Sm_Common_Round.newBuilder();
        b.setMax(max);
        b.setMin(min);
        return b.build();
    }


}
