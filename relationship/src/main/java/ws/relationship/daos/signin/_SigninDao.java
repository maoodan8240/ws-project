package ws.relationship.daos.signin;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.signin.Signin;

public class _SigninDao extends AbstractBaseDao<Signin> implements SigninDao {
    public _SigninDao() {
        super(Signin.class);
    }
}
