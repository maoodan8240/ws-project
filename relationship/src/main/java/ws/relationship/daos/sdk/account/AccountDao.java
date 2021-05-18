package ws.relationship.daos.sdk.account;

import ws.common.mongoDB.interfaces.BaseDao;
import ws.relationship.topLevelPojos.sdk.account.Account;

public interface AccountDao extends BaseDao<Account> {
    Account query(String accountName);
}
