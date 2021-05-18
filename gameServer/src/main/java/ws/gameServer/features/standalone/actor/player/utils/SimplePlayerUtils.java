package ws.gameServer.features.standalone.actor.player.utils;

import ws.common.mongoDB.interfaces.TopLevelPojo;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.extp.arena.ArenaExtp;
import ws.gameServer.features.standalone.extp.formations.FormationsExtp;
import ws.gameServer.features.standalone.extp.heros.HerosExtp;
import ws.gameServer.features.standalone.extp.newGuild.NewGuildExtp;
import ws.protos.EnumsProtos.FormationTypeEnum;
import ws.relationship.topLevelPojos.formations.Formation;
import ws.relationship.topLevelPojos.formations.Formations;
import ws.relationship.topLevelPojos.heros.Heros;
import ws.relationship.topLevelPojos.newGuild.NewGuildPlayer;
import ws.relationship.topLevelPojos.player.Player;
import ws.relationship.topLevelPojos.pvp.arena.Arena;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayerMfHero;
import ws.relationship.topLevelPojos.talent.Talent;
import ws.relationship.utils.RelationshipCommonUtils;
import ws.relationship.utils.attrs.HeroAttrsContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangweiwei on 17-4-14.
 */
public class SimplePlayerUtils {
    private static List<Class<? extends TopLevelPojo>> sensitiveChangedPojoClass = new ArrayList<Class<? extends TopLevelPojo>>() {
        private static final long serialVersionUID = -5661233787202491578L;

        // 以下topLevel发生变化可能导致 SimplePlayer 发生变化
        {
            add(Formations.class);
            add(Heros.class);
            add(Player.class);
            add(Talent.class); // 导致战斗力发生变化
            add(NewGuildPlayer.class);
            add(Arena.class);

        }
    };

    /**
     * SimplePlayer 是否对该topLevelPojo敏感
     *
     * @param topLevelPojo
     * @return
     */
    public static boolean sensitive(TopLevelPojo topLevelPojo) {
        if (topLevelPojo == null) {
            return false;
        }
        if (sensitiveChangedPojoClass.contains(topLevelPojo.getClass())) {
            return true;
        }
        return false;
    }


    public static SimplePlayer createSimplePlayer(PlayerCtrl playerCtrl, Map<Class<? extends TopLevelPojo>, TopLevelPojo> topLevelPojoClassToTopLevelPojo) {
        SimplePlayer simplePlayer = new SimplePlayer();
        setPlayerInfo(simplePlayer, playerCtrl, topLevelPojoClassToTopLevelPojo);
        setPvpInfo(simplePlayer, playerCtrl, topLevelPojoClassToTopLevelPojo);
        setGuildInfo(simplePlayer, playerCtrl, topLevelPojoClassToTopLevelPojo);
        return simplePlayer;
    }


    private static void setPlayerInfo(SimplePlayer simplePlayer, PlayerCtrl playerCtrl, Map<Class<? extends TopLevelPojo>, TopLevelPojo> topLevelPojoClassToTopLevelPojo) {
        HeroAttrsContainer attrsContainer = playerCtrl.getExtension(HerosExtp.class).getControlerForQuery().getAttrsContainer();
        Player player = playerCtrl.getTarget();
        simplePlayer.setSex(player.getBase().getSex());
        simplePlayer.setPlayerName(player.getBase().getName());
        simplePlayer.setPlayerId(player.getPlayerId());
        simplePlayer.setOutRealmId(player.getAccount().getOuterRealmId());
        simplePlayer.setInnerRealmId(player.getAccount().getInnerRealmId());
        simplePlayer.setIcon(player.getBase().getIconId());
        simplePlayer.setLv(player.getBase().getLevel());
        simplePlayer.setVipLv(player.getPayment().getVipLevel());
        simplePlayer.setSimplePlayerId(player.getBase().getSimpleId());
        simplePlayer.setBattleValue(attrsContainer.getFormationSumBattleValue_Formation_Main());
        simplePlayer.setSign(RelationshipCommonUtils.converNullToEmpty(player.getBase().getSign()));
        simplePlayer.setLastLoginTime(player.getOther().getLsinTime());
        simplePlayer.setLastLogoutTime(player.getOther().getLsoutTime());
        setSimplePlayerPosToHero(simplePlayer, playerCtrl); // 主阵容
    }


    private static void setPvpInfo(SimplePlayer simplePlayer, PlayerCtrl playerCtrl, Map<Class<? extends TopLevelPojo>, TopLevelPojo> topLevelPojoClassToTopLevelPojo) {
        if (!ArenaExtp.useExtension) {
            return;
        }
        if (topLevelPojoClassToTopLevelPojo.containsKey(Arena.class)) {
            Arena arena = (Arena) topLevelPojoClassToTopLevelPojo.get(Arena.class);
            int curRank = playerCtrl.getExtension(ArenaExtp.class).getControlerForQuery().getCurRank();
            simplePlayer.setPvpIcon(arena.getBase().getPvpIcon());
            simplePlayer.setPvpRank(curRank);
            simplePlayer.setPvpVictoryTimes(arena.getBase().getVictoryTimes());
            simplePlayer.setPvpDeclaration(arena.getBase().getDeclaration());
        }
    }

    private static void setGuildInfo(SimplePlayer simplePlayer, PlayerCtrl playerCtrl, Map<Class<? extends TopLevelPojo>, TopLevelPojo> topLevelPojoClassToTopLevelPojo) {
        if (!NewGuildExtp.useExtension) {
            return;
        }
        if (topLevelPojoClassToTopLevelPojo.containsKey(NewGuildPlayer.class)) {
            NewGuildPlayer guildPlayer = (NewGuildPlayer) topLevelPojoClassToTopLevelPojo.get(NewGuildPlayer.class);
            simplePlayer.setGuildId(RelationshipCommonUtils.converNullToEmpty(guildPlayer.getGuildId()));
        }
    }

    public static void setSimplePlayerPosToHero(SimplePlayer simplePlayer, PlayerCtrl playerCtrl) {
        Heros heros = playerCtrl.getExtension(HerosExtp.class).getControlerForQuery().getTarget();
        Formations formations = playerCtrl.getExtension(FormationsExtp.class).getControlerForQuery().getTarget();
        Formation formation_Main = formations.getTypeToFormation().get(FormationTypeEnum.F_MAIN);
        simplePlayer.getPosToHero().clear();
        formation_Main.getPosToFormationPos().forEach((pos, posObj) -> {
            int heroId = posObj.getHeroId();
            if (heroId > 0 && heros.getIdToHero().containsKey(heroId)) {
                SimplePlayerMfHero mfHero = new SimplePlayerMfHero(pos, heros.getIdToHero().get(heroId));
                simplePlayer.getPosToHero().put(pos, mfHero);
            }
        });
    }
}

