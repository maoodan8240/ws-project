package ws.loginServer.features.actor.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.MongoDBClient;
import ws.common.redis.RedisOpration;
import ws.common.utils.codec.ServerDHCoder;
import ws.common.utils.di.GlobalInjector;
import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.relationship.base.MagicWords_Mongodb;
import ws.relationship.daos.DaoContainer;
import ws.relationship.daos.centerPlayer.CenterPlayerDao;
import ws.relationship.daos.sdk.realm.OuterToInnerRealmListDao;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;
import ws.relationship.topLevelPojos.sdk.realm.OuterToInnerRealmList;

import java.util.ArrayList;
import java.util.List;

public class LoginUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginUtils.class);
    private static final RedisOpration REDIS_OPRATION = GlobalInjector.getInstance(RedisOpration.class);
    private static final MongoDBClient MONGO_DB_CLIENT = GlobalInjector.getInstance(MongoDBClient.class);
    private static final CenterPlayerDao CENTER_PLAYER_DAO = DaoContainer.getDao(CenterPlayer.class);
    private static final OuterToInnerRealmListDao OUTER_TO_INNER_REALMLIST_DAO = DaoContainer.getDao(OuterToInnerRealmList.class);

    static {
        CENTER_PLAYER_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
        OUTER_TO_INNER_REALMLIST_DAO.init(MONGO_DB_CLIENT, MagicWords_Mongodb.TopLevelPojo_All_Common);
    }


    public static boolean verifySecurity(String securityCode, String clientPublicKey, long time, String token) {
        String securityStr = ServerDHCoder.decrypt(securityCode, clientPublicKey);
        if (!(time + ">>" + token).equals(securityStr)) {
            LOGGER.info("验证securityCode失败！ clientPublicKey={} securityCode={} securityStr={} > 正确的securityStr={}", clientPublicKey, securityCode, securityStr, (time + ">>" + token));
            return false;
        }
        return true;
    }


    public static CenterPlayer getCenterPlayerListFirst(PlatformTypeEnum PlatformTypeEnum, int outerRealmId, String platformUid) {
        List<CenterPlayer> list = new ArrayList<>();
        List<Integer> outerRealmIds = OUTER_TO_INNER_REALMLIST_DAO.findAllCanUseOuterRealmIdsByOuterRealmId(outerRealmId);
        for (int tmpId : outerRealmIds) {
            CenterPlayer centerPlayer = getCenterPlayer(PlatformTypeEnum, tmpId, platformUid);
            if (centerPlayer != null) {
                list.add(centerPlayer);
            }
        }
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    private static CenterPlayer getCenterPlayer(PlatformTypeEnum PlatformTypeEnum, int outerRealmId, String platformUid) {
        CenterPlayer centerPlayer = CENTER_PLAYER_DAO.findCenterPlayer(PlatformTypeEnum, outerRealmId, platformUid);
        if (centerPlayer != null) {
            OuterToInnerRealmList realmList = OUTER_TO_INNER_REALMLIST_DAO.findByOuterRealmId(outerRealmId);
            centerPlayer.setInnerRealmId(realmList.getInnerRealmId()); // 更新innerRealmId
        }
        return centerPlayer;
    }

}
