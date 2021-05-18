package ws.relationship.daos.guild;

import ws.common.mongoDB.interfaces.BaseDao;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;

/**
 * Created by lee on 6/2/17.
 */
public interface NewGuildDao extends BaseDao<NewGuild> {
    NewGuild queryNewGuild(String guildId);

    NewGuild queryNewGuildByName(String guildName);

    NewGuild queryNewGuildBySimpleId(int simpleId);
}
