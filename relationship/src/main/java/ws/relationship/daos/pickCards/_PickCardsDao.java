package ws.relationship.daos.pickCards;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.pickCards.PickCards;

public class _PickCardsDao extends AbstractBaseDao<PickCards> implements PickCardsDao {

    public _PickCardsDao() {
        super(PickCards.class);
    }
}
