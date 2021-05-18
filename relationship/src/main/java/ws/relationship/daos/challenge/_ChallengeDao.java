package ws.relationship.daos.challenge;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.challenge.Challenge;

/**
 * Created by lee on 17-3-27.
 */
public class _ChallengeDao extends AbstractBaseDao<Challenge> implements ChallengeDao {
    public _ChallengeDao() {
        super(Challenge.class);
    }
}
