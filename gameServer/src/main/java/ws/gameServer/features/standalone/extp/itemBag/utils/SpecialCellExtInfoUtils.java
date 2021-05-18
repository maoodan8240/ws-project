package ws.gameServer.features.standalone.extp.itemBag.utils;

import com.alibaba.fastjson.JSONObject;
import ws.protos.ItemBagProtos.Sm_ItemBag_SpecialItemExtInfo.KeyType;
import ws.relationship.topLevelPojos.itemBag.SpecialCell;

public class SpecialCellExtInfoUtils {

    public static String get(SpecialCell specialCell, KeyType key) {
        return specialCell.getExtInfo().get(key);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getJson(SpecialCell specialCell, KeyType key) {
        return (T) JSONObject.parse(get(specialCell, key));
    }

    public static <T> T getJson(SpecialCell specialCell, KeyType key, T defaultValue) {
        T obj = getJson(specialCell, key);
        return (obj == null) ? defaultValue : obj;
    }

    public static int getInt(SpecialCell specialCell, KeyType key) {
        return getInt(specialCell, key, 0);
    }

    public static long getLong(SpecialCell specialCell, KeyType key) {
        return getLong(specialCell, key, 0l);
    }

    public static int getInt(SpecialCell specialCell, KeyType key, int defaultValue) {
        try {
            return Integer.parseInt(get(specialCell, key));
        } catch (Throwable t) {
            return defaultValue;
        }
    }

    public static long getLong(SpecialCell specialCell, KeyType key, long defaultValue) {
        try {
            return Long.parseLong(get(specialCell, key));
        } catch (Throwable t) {
            return defaultValue;
        }
    }

    public static void put(SpecialCell specialCell, KeyType key, String value) {
        specialCell.getExtInfo().put(key, value);
    }

    public static void remove(SpecialCell specialCell, KeyType key) {
        specialCell.getExtInfo().remove(key);
    }

    public static void putJson(SpecialCell specialCell, KeyType key, Object value) {
        put(specialCell, key, JSONObject.toJSONString(value));
    }

    public static void putInt(SpecialCell specialCell, KeyType key, int value) {
        put(specialCell, key, String.valueOf(value));
    }

    public static void putLong(SpecialCell specialCell, KeyType key, long value) {
        put(specialCell, key, String.valueOf(value));
    }
}
