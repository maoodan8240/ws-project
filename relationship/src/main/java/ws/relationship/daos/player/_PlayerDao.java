package ws.relationship.daos.player;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.player.Player;

public class _PlayerDao extends AbstractBaseDao<Player> implements PlayerDao {

    public _PlayerDao() {
        super(Player.class);
    }
}
