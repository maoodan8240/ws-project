package test;

import ws.common.redis.RedisOpration;
import ws.common.redis.operation.In_RedisOperation;
import ws.common.redis.operation.RedisOprationEnum.Hashes;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.schedule.GlobalScheduler;
import ws.mongodbRedisServer.Launcher;
import ws.mongodbRedisServer.system.config.AppConfig;
import ws.mongodbRedisServer.system.di.GlobalInjectorUtils;
import ws.relationship.topLevelPojos.TopLevelPojoContainer;
import ws.relationship.topLevelPojos.player.Player;

/**
 * Created by lee on 17-6-1.
 */
public class T {


    public static void main(String[] args) throws Exception {
        AppConfig.init();
        GlobalInjectorUtils.init();
        GlobalScheduler.init();
        Launcher.redisInit();
        Launcher.mongodbInit();

        RedisOpration REDIS_OPRATION = GlobalInjector.getInstance(RedisOpration.class);
        String rs = Hashes.hget.parseResult(REDIS_OPRATION.execute(new In_RedisOperation(Hashes.hget.newParmBuilder().build(TopLevelPojoContainer.getGameRedisKey(Player.class, 1), "592fd5c6784ba379bd0bb470"))));
        System.out.println(rs);
    }
}
