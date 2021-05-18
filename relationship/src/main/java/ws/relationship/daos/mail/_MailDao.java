package ws.relationship.daos.mail;

import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.mails.Mails;

/**
 * Created by lee on 16-12-15.
 */
public class _MailDao extends AbstractBaseDao<Mails> implements MailDao {
    public _MailDao() {
        super(Mails.class);
    }
}
