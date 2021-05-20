package ws.gameServer.features.standalone.actor.player.utils;

import ws.common.mongoDB.interfaces.TopLevelPojo;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.relationship.topLevelPojos.formations.Formations;
import ws.relationship.topLevelPojos.heros.Heros;
import ws.relationship.topLevelPojos.newGuild.NewGuildPlayer;
import ws.relationship.topLevelPojos.player.Player;
import ws.relationship.topLevelPojos.pvp.arena.Arena;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.topLevelPojos.talent.Talent;
import ws.relationship.utils.RelationshipCommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lee on 17-4-14.
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
        return simplePlayer;
    }


    private static void setPlayerInfo(SimplePlayer simplePlayer, PlayerCtrl playerCtrl, Map<Class<? extends TopLevelPojo>, TopLevelPojo> topLevelPojoClassToTopLevelPojo) {
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
        simplePlayer.setSign(RelationshipCommonUtils.converNullToEmpty(player.getBase().getSign()));
        simplePlayer.setLastLoginTime(player.getOther().getLsinTime());
        simplePlayer.setLastLogoutTime(player.getOther().getLsoutTime());
    }


}

