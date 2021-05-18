package ws.gameServer.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.utils.di.GlobalInjector;
import ws.gameServer.features.actor.cluster.ClusterListener;
import ws.gameServer.system.config.AppConfig;
import ws.gameServer.system.config.AppConfig.Key;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.base.cluster.AkkaAddressContext;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.sdk.realm.OuterToInnerRealmListDao;
import ws.relationship.topLevelPojos.sdk.realm.OuterToInnerRealmList;
import ws.relationship.utils.ClusterUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by zhangweiwei on 17-6-7.
 */
public class ServerGlobalData {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerGlobalData.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final OuterToInnerRealmListDao OUTER_TO_INNER_REALMLIST_DAO = DaoContainer.getDao(OuterToInnerRealmList.class);

    static {
        OUTER_TO_INNER_REALMLIST_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
    }


    private static Locale locale = null;
    private static Map<Integer, OuterToInnerRealmList> outerRealmIdToOuterToInnerRealm = new HashMap<>();

    public static void init() {
        initLocale();
        initRealmData();
    }


    private static void initLocale() {
        String lang = AppConfig.getString(Key.WS_Common_Config_lang);
        String country = AppConfig.getString(Key.WS_Common_Config_country);
        locale = new Locale(lang, country);
        LOGGER.debug("当前语言环境:lang={} country={}", locale.getLanguage(), locale.getCountry());
    }

    private static void initRealmData() {
        outerRealmIdToOuterToInnerRealm.clear();
        List<OuterToInnerRealmList> outerToInnerRealmLists = OUTER_TO_INNER_REALMLIST_DAO.findByGameServerRole(AppConfig.getServerRole());
        outerToInnerRealmLists.forEach(outerToInnerRealmList -> {
            outerRealmIdToOuterToInnerRealm.put(outerToInnerRealmList.getOuterRealmId(), outerToInnerRealmList);
        });
    }


    public static Locale getLocale() {
        return locale;
    }


    public static OuterToInnerRealmList getOuterToInnerRealmList(int outerRealmId) {
        return outerRealmIdToOuterToInnerRealm.get(outerRealmId).clone();
    }


    public static String getGateServerRole(int outerRealmId) {
        return getOuterToInnerRealmList(outerRealmId).getGateURL();
    }

    public static AkkaAddressContext getGateServerAddressContext(int outerRealmId) {
        return ClusterUtils.getAddressContext(ClusterListener.getActorContext(), getGateServerRole(outerRealmId));
    }

    public static String getGameServerRole(int outerRealmId) {
        return getOuterToInnerRealmList(outerRealmId).getGameServerRole();
    }

    public static AkkaAddressContext getGameServerAddressContext(int outerRealmId) {
        return ClusterUtils.getAddressContext(ClusterListener.getActorContext(), getGameServerRole(outerRealmId));
    }

    public static String getChatServerRole(int outerRealmId) {
        return getOuterToInnerRealmList(outerRealmId).getChatServerRole();
    }

    public static AkkaAddressContext getChatServerAddressContext(int outerRealmId) {
        return ClusterUtils.getAddressContext(ClusterListener.getActorContext(), getChatServerRole(outerRealmId));
    }

    public static String getDbServerRole(int outerRealmId) {
        return getOuterToInnerRealmList(outerRealmId).getDbServerRole();
    }

    public static AkkaAddressContext getDbServerAddressContext(int outerRealmId) {
        return ClusterUtils.getAddressContext(ClusterListener.getActorContext(), getDbServerRole(outerRealmId));
    }
}
