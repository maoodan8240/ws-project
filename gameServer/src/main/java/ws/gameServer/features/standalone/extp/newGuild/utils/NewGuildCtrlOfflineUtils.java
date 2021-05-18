package ws.gameServer.features.standalone.extp.newGuild.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildAgree;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildKick;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildOneKeyRefuse;
import ws.gameServer.features.standalone.extp.newGuild.msg.base.In_NewGuildRefuse;
import ws.relationship.topLevelPojos.newGuild.NewGuildPlayer;
import ws.relationship.topLevelPojos.simplePlayer.SimplePlayer;
import ws.relationship.utils.DBUtils;
import ws.relationship.utils.SimplePojoUtils;

/**
 * Created by lee on 5/10/17.
 */
public class NewGuildCtrlOfflineUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewGuildCtrlOfflineUtils.class);

    public static void offlineIn_NewGuildKick(In_NewGuildKick.Response msg) {
        SimplePlayer kickSimplePlayer = msg.getRequest().getKickSimplePlayer();
        NewGuildPlayer guildPlayer = _getNewGuildPlayer(kickSimplePlayer.getPlayerId(), kickSimplePlayer.getOutRealmId());
        guildPlayer.setGuildId(null);
        SimplePlayer simplePlayer = SimplePojoUtils.querySimplePlayerById(kickSimplePlayer.getPlayerId(), kickSimplePlayer.getOutRealmId());
        simplePlayer.setGuildId(null);
        DBUtils.saveHashPojo(simplePlayer.getOutRealmId(), simplePlayer);
        DBUtils.saveHashPojo(kickSimplePlayer.getOutRealmId(), guildPlayer);
    }

    public static void offlineIn_NewGuildRefuse(In_NewGuildRefuse.Response msg) {
        SimplePlayer refuseSimplePlayer = msg.getRequest().getRefuseSimplePlayer();
        NewGuildPlayer guildPlayer = _getNewGuildPlayer(msg.getRequest().getRefuseSimplePlayer().getPlayerId(), msg.getRequest().getRefuseSimplePlayer().getOutRealmId());
        guildPlayer.getApplyGuildIds().removeIf(guildId -> guildId.equals(msg.getRequest().getGuildId()));
        DBUtils.saveHashPojo(refuseSimplePlayer.getOutRealmId(), guildPlayer);
    }

    public static void offlineIn_NewGuildOneKeyRefuse(In_NewGuildOneKeyRefuse.Response msg) {
        int outRealmId = msg.getRequest().getSimplePlayer().getOutRealmId();
        NewGuildPlayer guildPlayer = _getNewGuildPlayer(msg.getBePlayerId(), msg.getRequest().getSimplePlayer().getOutRealmId());
        guildPlayer.getApplyGuildIds().removeIf(guildId -> guildId.equals(msg.getRequest().getSimplePlayer().getGuildId()));
        DBUtils.saveStringPojo(outRealmId, guildPlayer);
    }

    public static void offlineIn_NewGuildAgree(In_NewGuildAgree.Response msg) {
        SimplePlayer addSimplePlayer = msg.getRequest().getAddSimplePlayer();
        NewGuildPlayer guildPlayer = _getNewGuildPlayer(addSimplePlayer.getPlayerId(), addSimplePlayer.getOutRealmId());
        guildPlayer.getApplyGuildIds().removeIf(guildId -> guildId.equals(msg.getGuild().getGuildId()));
        guildPlayer.setGuildId(msg.getGuild().getGuildId());
        SimplePlayer simplePlayer = SimplePojoUtils.querySimplePlayerById(addSimplePlayer.getPlayerId(), addSimplePlayer.getOutRealmId());
        simplePlayer.setGuildId(msg.getGuild().getGuildId());
        DBUtils.saveHashPojo(simplePlayer.getOutRealmId(), simplePlayer);
        DBUtils.saveHashPojo(addSimplePlayer.getOutRealmId(), guildPlayer);
    }

    private static NewGuildPlayer _getNewGuildPlayer(String playerId, int outRealmId) {
        return DBUtils.getHashPojo(playerId, outRealmId, NewGuildPlayer.class);
    }


}
