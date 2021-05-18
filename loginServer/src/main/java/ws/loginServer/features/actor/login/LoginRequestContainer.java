package ws.loginServer.features.actor.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginRequestContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginActor.class);
    private static final int TIME_INTERVAL = 5 * 1000; // 5 秒钟检查一次 centerPlayerFlagToAddTime
    private static final int INVALID_TIME_INTERVAL = 30 * 1000; // 加入时间超过30秒的centerPlayerFlagToAddTime

    static {
        removeOverTimeCache();
    }

    private static final Map<String, Long> centerPlayerFlagToAddTime = new ConcurrentHashMap<>();

    public static void add(CenterPlayer centerPlayer) {
        centerPlayerFlagToAddTime.put(createFlag(centerPlayer), System.currentTimeMillis());
    }

    public static boolean contains(CenterPlayer centerPlayer) {
        return centerPlayerFlagToAddTime.containsKey(createFlag(centerPlayer));
    }

    public static void remove(CenterPlayer centerPlayer) {
        centerPlayerFlagToAddTime.remove(createFlag(centerPlayer));
    }

    /**
     * 平台-Uid-OuterRealmId
     *
     * @param centerPlayer
     * @return
     */
    public static String createFlag(CenterPlayer centerPlayer) {
        // 平台-平台uid-服Id
        return centerPlayer.getPlatformType().toString() + "_" + centerPlayer.getPlatformUid() + "_" + centerPlayer.getOuterRealmId();
    }

    public static void removeOverTimeCache() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(TIME_INTERVAL);
                    new ArrayList<>(centerPlayerFlagToAddTime.keySet()).forEach(flag -> {
                        long addTime = centerPlayerFlagToAddTime.get(flag);
                        if (System.currentTimeMillis() - addTime > INVALID_TIME_INTERVAL) {
                            centerPlayerFlagToAddTime.remove(flag);
                        }
                    });
                } catch (Exception e) {
                    LOGGER.error("移除过期的缓存出现异常！", e);
                }
            }
        }).start();
    }
}
