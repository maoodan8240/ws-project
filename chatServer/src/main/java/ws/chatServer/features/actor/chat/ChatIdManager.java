package ws.chatServer.features.actor.chat;

/**
 * Created by lee on 17-5-4.
 */
public class ChatIdManager {
    private static long CHAT_ID = System.currentTimeMillis();
    private static long CHAT_RELATION_ID = System.currentTimeMillis();
    private static final Object lock = new Object();
    private static final Object lockRelation = new Object();


    public static long getChatId() {
        synchronized (lock) {
            return CHAT_ID++;
        }
    }

    public static long getChatRelationId() {
        synchronized (lockRelation) {
            return CHAT_RELATION_ID++;
        }
    }
}
