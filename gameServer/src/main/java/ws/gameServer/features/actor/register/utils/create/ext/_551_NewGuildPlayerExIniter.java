package ws.gameServer.features.actor.register.utils.create.ext;

import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtensionIniter;
import ws.protos.EnumsProtos.GuildRedBagTypeEnum;
import ws.relationship.base.MagicNumbers;
import ws.relationship.topLevelPojos.newGuild.NewGuildPlayer;

/**
 * Created by lee on 16-12-15.
 */
public class _551_NewGuildPlayerExIniter implements ExtensionIniter {
    @Override
    public void init(ExtCommonData commonData) throws Exception {
        NewGuildPlayer guildPlayer = new NewGuildPlayer(commonData.getPlayer().getPlayerId());
        for (GuildRedBagTypeEnum typeEnum : GuildRedBagTypeEnum.values()) {
            guildPlayer.getRedBagTypeAndCount().put(typeEnum, MagicNumbers.DEFAULT_ZERO);
        }
        commonData.addPojo(guildPlayer);
    }
}
