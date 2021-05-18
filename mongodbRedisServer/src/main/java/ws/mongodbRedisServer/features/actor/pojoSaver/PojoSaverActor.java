package ws.mongodbRedisServer.features.actor.pojoSaver;

import akka.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.TopLevelPojo;
import ws.common.mongoDB.utils.WsJsonUtils;
import ws.common.redis.RedisOpration;
import ws.common.redis.operation.In_RedisOperation;
import ws.common.redis.operation.RedisOprationEnum;
import ws.common.utils.di.GlobalInjector;
import ws.mongodbRedisServer.system.schedule.msg.In_RefreshUpdatedPlayerDataToMongodbRequest;
import ws.relationship.base.MagicWords_Redis;
import ws.relationship.base.PojoUtils;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.msg.db.saver.In_AllCommon_Hashes_OneIdOnePojoSaver;
import ws.relationship.base.msg.db.saver.In_AllCommon_Strings_OneIdOnePojoSaver;
import ws.relationship.base.msg.db.saver.In_Game_Hashes_OneId_MultiPojos_SameOutRealmId_Saver;
import ws.relationship.base.msg.db.saver.In_Game_Strings_OneIdOnePojoSaver;
import ws.relationship.exception.PlayerPojosGetterException;
import ws.relationship.topLevelPojos.TopLevelPojoContainer;


public class PojoSaverActor extends WsActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PojoSaverActor.class);
    private static final RedisOpration REDIS_OPRATION = GlobalInjector.getInstance(RedisOpration.class);

    @Override
    public void onRecv(Object msg) throws Exception {
        if (msg instanceof In_RefreshUpdatedPlayerDataToMongodbRequest) {
            in_RefreshPlayerDataToMongodbRequest();
        }
        // AllCommon--
        else if (msg instanceof In_AllCommon_Strings_OneIdOnePojoSaver.Request) {
            _AllCommon_Strings_OneIdOnePojoSaver((In_AllCommon_Strings_OneIdOnePojoSaver.Request) msg);

        } else if (msg instanceof In_AllCommon_Hashes_OneIdOnePojoSaver.Request) {
            _AllCommon_Hashes_OneIdOnePojoSaver((In_AllCommon_Hashes_OneIdOnePojoSaver.Request) msg);

        }
        // Game--
        else if (msg instanceof In_Game_Strings_OneIdOnePojoSaver.Request) {
            _Game_Strings_OneIdOnePojoSaver((In_Game_Strings_OneIdOnePojoSaver.Request) msg);

        } else if (msg instanceof In_Game_Hashes_OneId_MultiPojos_SameOutRealmId_Saver.Request) {
            _Game_Hashes_OneId_MultiPojos_SameOutRealmId_Saver((In_Game_Hashes_OneId_MultiPojos_SameOutRealmId_Saver.Request) msg);
        }
    }

    private void _AllCommon_Strings_OneIdOnePojoSaver(In_AllCommon_Strings_OneIdOnePojoSaver.Request request) throws Exception {
        try {
            PojoUtils.saveAllCommonPojoToStringsRedis(request.getPojo());
            PojoUtils.saveAllCommonPojoToHashesOrStringsMongodb(request.getPojo());
            sender().tell(new In_AllCommon_Strings_OneIdOnePojoSaver.Response(request, null), ActorRef.noSender());
        } catch (Exception e) {
            PlayerPojosGetterException exception = new PlayerPojosGetterException(request.toString(), e);
            sender().tell(new In_AllCommon_Strings_OneIdOnePojoSaver.Response(request, exception), ActorRef.noSender());
            throw exception;
        }
    }

    private void _AllCommon_Hashes_OneIdOnePojoSaver(In_AllCommon_Hashes_OneIdOnePojoSaver.Request request) throws Exception {
        try {
            PojoUtils.saveAllCommonPojoToHashesRedis(request.getPojo());
            PojoUtils.saveAllCommonPojoToHashesOrStringsMongodb(request.getPojo());
            sender().tell(new In_AllCommon_Hashes_OneIdOnePojoSaver.Response(request, null), ActorRef.noSender());
        } catch (Exception e) {
            PlayerPojosGetterException exception = new PlayerPojosGetterException(request.toString(), e);
            sender().tell(new In_AllCommon_Hashes_OneIdOnePojoSaver.Response(request, exception), ActorRef.noSender());
            throw exception;
        }
    }

    private void _Game_Strings_OneIdOnePojoSaver(In_Game_Strings_OneIdOnePojoSaver.Request request) throws Exception {
        try {
            PojoUtils.saveGamePojoToStringsRedis(request.getPojo(), request.getOutRealmId());
            PojoUtils.saveGamePojoToHashesOrStringsMongodb(request.getPojo(), request.getOutRealmId());
            sender().tell(new In_Game_Strings_OneIdOnePojoSaver.Response(request, null), ActorRef.noSender());
        } catch (Exception e) {
            PlayerPojosGetterException exception = new PlayerPojosGetterException(request.toString(), e);
            sender().tell(new In_Game_Strings_OneIdOnePojoSaver.Response(request, exception), ActorRef.noSender());
            throw exception;
        }
    }

    private void _Game_Hashes_OneId_MultiPojos_SameOutRealmId_Saver(In_Game_Hashes_OneId_MultiPojos_SameOutRealmId_Saver.Request request) throws Exception {
        try {
            for (TopLevelPojo pojo : request.getPojoLis()) {
                PojoUtils.saveGamePojoToHashesRedis(pojo, request.getOutRealmId());
                PojoUtils.saveGamePojoToHashesOrStringsMongodb(pojo, request.getOutRealmId());
            }
            sender().tell(new In_Game_Hashes_OneId_MultiPojos_SameOutRealmId_Saver.Response(request, null), ActorRef.noSender());
        } catch (Exception e) {
            PlayerPojosGetterException exception = new PlayerPojosGetterException(request.toString(), e);
            sender().tell(new In_Game_Hashes_OneId_MultiPojos_SameOutRealmId_Saver.Response(request, exception), ActorRef.noSender());
            throw exception;
        }
    }

    /**
     * 数据发生变化了的玩家保存到mongodb
     *
     * @throws Exception
     */
    private void in_RefreshPlayerDataToMongodbRequest() throws Exception {
        long s = System.currentTimeMillis();
        Object rs = REDIS_OPRATION.execute(new In_RedisOperation(RedisOprationEnum.Lists.llen.newParmBuilder().build(MagicWords_Redis.PlayerIds_Updated)));
        long sumNeedHandler = RedisOprationEnum.Lists.llen.parseResult(rs);
        int sumHasHandler = 0;
        for (int i = 0; i < sumNeedHandler; i++) {
            Object rsRpop = null;
            String playerId = null;
            int realmId = 0;
            try {
                rsRpop = REDIS_OPRATION.execute(new In_RedisOperation(RedisOprationEnum.Lists.rpop.newParmBuilder().build(MagicWords_Redis.PlayerIds_Updated)));
                String playerIdAndRealmId = RedisOprationEnum.Lists.rpop.parseResult(rsRpop);
                String[] tmp = playerIdAndRealmId.split("_");
                playerId = tmp[0];
                realmId = Integer.parseInt(tmp[1]);
            } catch (Exception e) {
                LOGGER.error("获取玩家的变更字段playerIdAndRealmId异常！rsRpop={} playerId={} realmId={}", rsRpop, playerId, realmId);
                continue;
            }
            for (Class<? extends TopLevelPojo> topLevelPojoClass : TopLevelPojoContainer.getPlayerAllTopLevelClass()) {
                TopLevelPojo pojo = null;
                try {
                    pojo = PojoUtils.getGamePojoFromHashesRedis(topLevelPojoClass, playerId, realmId);
                    PojoUtils.saveGamePojoToHashesOrStringsMongodb(pojo, realmId);
                } catch (Exception e) {
                    LOGGER.error("获取玩家在Redis中的数据，并保存到Mongodb中，异常！playerId={} realmId={} pojo={} ", playerId, realmId, pojo == null ? null : WsJsonUtils.javaObjectToJSONStr(pojo));
                }
            }
            sumHasHandler++;
        }
        long e = System.currentTimeMillis();
        LOGGER.info("保存活跃玩家数据成功！ sumHasHandler={} sumNeedHandler={},总耗时={}(秒)", sumHasHandler, sumNeedHandler, (e - s) / 1000);
    }

}
