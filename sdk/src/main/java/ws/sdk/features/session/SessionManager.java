package ws.sdk.features.session;

import ws.relationship.topLevelPojos.sdk.account.Account;

public interface SessionManager {
    String newSessionId();

    void put(String sessionId, Account account);

    void remove(String sessionId);

    int size();

    Account queryBySessionId(String sessionId);
}
