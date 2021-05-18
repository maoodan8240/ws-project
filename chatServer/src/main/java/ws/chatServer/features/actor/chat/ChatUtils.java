package ws.chatServer.features.actor.chat;

import ws.relationship.base.MagicNumbers;
import ws.relationship.chatServer.ChatMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangweiwei on 17-5-4.
 */
public class ChatUtils {
    public static ChatMsg removeOverflowMsg(List<ChatMsg> msgList) {
        if (msgList.size() > MagicNumbers.EACH_CHAT_TYPE_MAX_STORE_MSG_COUNT) {
            return msgList.remove(0);  // 移除第一个
        }
        return null;
    }


    /**
     * for Map<K, List<T>>
     *
     * @param idToTLis
     * @param key
     * @param <T>
     * @param <K>
     * @return
     */
    public static <T, K> List<T> getListByKey(Map<K, List<T>> idToTLis, K key) {
        if (!idToTLis.containsKey(key)) {
            idToTLis.put(key, new ArrayList<>());
        }
        return idToTLis.get(key);
    }


    /**
     * Map<K1, Map<K2, V>>
     *
     * @param KToMap
     * @param key
     * @param <K1>
     * @param <K2>
     * @param <V>
     * @return
     */
    public static <K1, K2, V> Map<K2, V> getMapByKey(Map<K1, Map<K2, V>> KToMap, K1 key) {
        if (!KToMap.containsKey(key)) {
            KToMap.put(key, new HashMap<>());
        }
        return KToMap.get(key);
    }
}
