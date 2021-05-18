package ws.sdk.features.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.cooldown.implement.AutoClearCdList;
import ws.relationship.topLevelPojos.sdk.account.Account;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class _SessionManager implements SessionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(_SessionManager.class);
    private static final int DEFAULT_SESSION_MAX_TIME_TO_LIVE_IN_MINUTES = 1;
    private AutoClearCdList cdList = new AutoClearCdList();
    private Map<String, Account> map = new HashMap<String, Account>();
    private final int sessionMaxTimeToLiveInMinutes;

    public _SessionManager() {
        this(DEFAULT_SESSION_MAX_TIME_TO_LIVE_IN_MINUTES);
    }

    public _SessionManager(int sessionMaxTimeToLiveInMinutes) {
        this.sessionMaxTimeToLiveInMinutes = sessionMaxTimeToLiveInMinutes;
        _init();
    }

    private void _init() {
        cdList.setCallbackOnExpire(sessionId -> {
            LOGGER.debug("SessionId Expire, {}", sessionId);
            remove(sessionId);
        });
    }

    @Override
    public String newSessionId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public synchronized void put(String sessionId, Account account) {
        map.put(sessionId, account);
        cdList.add(sessionId, sessionMaxTimeToLiveInMinutes, Calendar.MINUTE);
    }

    @Override
    public synchronized void remove(String sessionId) {
        map.remove(sessionId);
        cdList.clear(sessionId);
    }

    @Override
    public synchronized int size() {
        return map.size();
    }

    @Override
    public synchronized Account queryBySessionId(String sessionId) {
        Account account = map.get(sessionId);
        LOGGER.info(map.toString());
        return account;
    }

}
