package ws.mongodbRedisServer.features.actor.pojoRemover;

import ws.common.mongoDB.interfaces.TopLevelPojo;
import ws.common.redis.RedisOpration;
import ws.common.redis.operation.In_RedisOperation;
import ws.common.redis.operation.RedisOprationEnum;
import ws.common.utils.di.GlobalInjector;
import ws.mongodbRedisServer.system.schedule.msg.In_RemoveOverTimePlayerDataFromRedisAndRefreshToMongodbRequest;
import ws.relationship.base.MagicNumbers;
import ws.relationship.base.MagicWords_Redis;
import ws.relationship.base.PojoUtils;
import ws.relationship.base.actor.WsActor;

import java.util.Map;
import java.util.Set;


public class PojoRemoverActor extends WsActor {
    protected static final RedisOpration REDIS_OPRATION = GlobalInjector.getInstance(RedisOpration.class);

    @Override
    public void onRecv(Object msg) throws Exception {
        if (msg instanceof In_RemoveOverTimePlayerDataFromRedisAndRefreshToMongodbRequest) {
            in_RemoveOverTimePlayerDataFromRedisAndRefreshToMongodbRequest();
        }
    }

    private void in_RemoveOverTimePlayerDataFromRedisAndRefreshToMongodbRequest() throws Exception {
        long divide = System.currentTimeMillis() - MagicNumbers.RemovePlayerDataFromRedisAfterLogoutNHour * 60 * 60 * 1000;
        Object rs = REDIS_OPRATION.execute(new In_RedisOperation(RedisOprationEnum.SortedSets.zrangeByScore.newParmBuilder().build(MagicWords_Redis.PlayerLogoutTime, -1, divide)));
        Set<String> playerIdAndRealmIds = RedisOprationEnum.SortedSets.zrangeByScore.parseResult(rs);
        for (String playerIdAndRealmId : playerIdAndRealmIds) {
            String[] tmp = playerIdAndRealmId.split("_");
            String playerId = tmp[0];
            int outerRealmId = Integer.parseInt(tmp[1]);
            Map<Class<? extends TopLevelPojo>, TopLevelPojo> topLevelPojoClassToTopLevelPojo = PojoUtils.getPlayerAllPojos(playerId, outerRealmId);
            for (TopLevelPojo pojo : topLevelPojoClassToTopLevelPojo.values()) {
                PojoUtils.saveGamePojoToHashesOrStringsMongodb(pojo, outerRealmId);
            }
            REDIS_OPRATION.execute(new In_RedisOperation(RedisOprationEnum.SortedSets.zrem.newParmBuilder().build(MagicWords_Redis.PlayerLogoutTime, playerIdAndRealmId)));
        }
    }
}
