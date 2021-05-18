package ws.relationship.daos.newGuildPlayer;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.newGuild.NewGuildPlayer;

/**
 * Created by lee on 16-11-30.
 */
public class _NewGuildPlayerDao extends AbstractBaseDao<NewGuildPlayer> implements NewGuildPlayerDao {
    public _NewGuildPlayerDao() {
        super(NewGuildPlayer.class);
    }
}
