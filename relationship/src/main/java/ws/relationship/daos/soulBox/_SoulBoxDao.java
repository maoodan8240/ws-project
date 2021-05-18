package ws.relationship.daos.soulBox;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.soulBox.SoulBox;

/**
 * Created by lee on 7/4/17.
 */
public class _SoulBoxDao extends AbstractBaseDao<SoulBox> implements SoulBoxDao {
    public _SoulBoxDao() {
        super(SoulBox.class);
    }
}
