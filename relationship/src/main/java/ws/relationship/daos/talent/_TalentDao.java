package ws.relationship.daos.talent;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.talent.Talent;

/**
 * Created by lee on 17-2-4.
 */
public class _TalentDao extends AbstractBaseDao<Talent> implements TalentDao {
    public _TalentDao() {
        super(Talent.class);
    }
}
