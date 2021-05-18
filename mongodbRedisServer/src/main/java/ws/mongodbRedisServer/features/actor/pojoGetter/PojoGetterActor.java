package ws.mongodbRedisServer.features.actor.pojoGetter;

import akka.actor.ActorRef;
import ws.common.mongoDB.interfaces.TopLevelPojo;
import ws.common.redis.RedisOpration;
import ws.common.utils.di.GlobalInjector;
import ws.relationship.base.PojoUtils;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.msg.db.getter.In_AllCommon_Hashes_MultiIdsOnePojoGetter;
import ws.relationship.base.msg.db.getter.In_AllCommon_Hashes_OneIdOnePojoGetter;
import ws.relationship.base.msg.db.getter.In_AllCommon_Strings_OneIdOnePojoGetter;
import ws.relationship.base.msg.db.getter.In_Game_Hashes_MultiIdsOnePojoGetter;
import ws.relationship.base.msg.db.getter.In_Game_Hashes_OneIdOnePojoGetter;
import ws.relationship.base.msg.db.getter.In_Game_Hashes_OneIdOnePojoGetter.Request;
import ws.relationship.base.msg.db.getter.In_Game_Hashes_OnePlayerAllPojosGetter;
import ws.relationship.base.msg.db.getter.In_Game_Strings_OneIdOnePojoGetter;
import ws.relationship.exception.PlayerPojosGetterException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PojoGetterActor extends WsActor {
    protected static final RedisOpration REDIS_OPRATION = GlobalInjector.getInstance(RedisOpration.class);

    @Override
    public void onRecv(Object msg) throws Exception {
        //---Game
        if (msg instanceof In_Game_Hashes_OnePlayerAllPojosGetter.Request) {
            _Game_Hashes_OnePlayerAllPojosGetterRequest((In_Game_Hashes_OnePlayerAllPojosGetter.Request) msg);

        } else if (msg instanceof In_Game_Hashes_MultiIdsOnePojoGetter.Request) {
            _Game_Hashes_MultiIdsOnePojoGetterRequest((In_Game_Hashes_MultiIdsOnePojoGetter.Request) msg);

        } else if (msg instanceof In_Game_Hashes_OneIdOnePojoGetter.Request) {
            _Game_Hashes_OneIdOnePojoGetterRequest((In_Game_Hashes_OneIdOnePojoGetter.Request) msg);

        } else if (msg instanceof In_Game_Strings_OneIdOnePojoGetter.Request) {
            _Game_Strings_OneIdOnePojoGetter((In_Game_Strings_OneIdOnePojoGetter.Request) msg);
        }
        //---AllCommon
        else if (msg instanceof In_AllCommon_Hashes_OneIdOnePojoGetter.Request) {
            _AllCommon_Hashes_OneIdOnePojoGetterRequest((In_AllCommon_Hashes_OneIdOnePojoGetter.Request) msg);

        } else if (msg instanceof In_AllCommon_Hashes_MultiIdsOnePojoGetter.Request) {
            _AllCommon_Hashes_MultiIdsOnePojoGetterRequest((In_AllCommon_Hashes_MultiIdsOnePojoGetter.Request) msg);

        } else if (msg instanceof In_AllCommon_Strings_OneIdOnePojoGetter.Request) {
            _AllCommon_Strings_OneIdOnePojoGetterRequest((In_AllCommon_Strings_OneIdOnePojoGetter.Request) msg);

        }
    }

    /**
     * 获取Game-Redis-Hashes-key的单个id的值
     *
     * @param request
     */
    private void _Game_Hashes_OneIdOnePojoGetterRequest(Request request) {
        try {
            int outerRealmId = request.getOuterRealmId();
            String id = request.getId();
            Class<? extends TopLevelPojo> topLevelPojoClass = request.getTopLevelPojoClass();
            TopLevelPojo pojo = PojoUtils.getGamePojoFromHashes(topLevelPojoClass, id, outerRealmId);
            sender().tell(new In_Game_Hashes_OneIdOnePojoGetter.Response(request, pojo), ActorRef.noSender());
        } catch (Exception e) {
            PlayerPojosGetterException exception = new PlayerPojosGetterException(request.toString(), e);
            sender().tell(new In_Game_Hashes_OneIdOnePojoGetter.Response(request, exception), ActorRef.noSender());
            throw exception;
        }
    }

    /**
     * 获取Game-Redis-Hashes-key的多个id的值
     *
     * @param request
     */
    private void _Game_Hashes_MultiIdsOnePojoGetterRequest(In_Game_Hashes_MultiIdsOnePojoGetter.Request request) {
        int outRealmId = request.getOutRealmId();
        try {
            Map<String, TopLevelPojo> idToTopLevelPojos = new HashMap<>();
            Class<? extends TopLevelPojo> topLevelPojoClass = request.getTopLevelPojoClass();
            List<String> ids = request.getIds();
            for (String id : ids) {
                TopLevelPojo pojo = PojoUtils.getGamePojoFromHashes(topLevelPojoClass, id, outRealmId);
                idToTopLevelPojos.put(id, pojo);
            }
            sender().tell(new In_Game_Hashes_MultiIdsOnePojoGetter.Response(request, idToTopLevelPojos), ActorRef.noSender());
        } catch (Exception e) {
            PlayerPojosGetterException exception = new PlayerPojosGetterException(request.toString(), e);
            sender().tell(new In_Game_Hashes_MultiIdsOnePojoGetter.Response(request, exception), ActorRef.noSender());
            throw exception;
        }
    }


    /**
     * 获取Game-Redis-Hashes- 玩家所有Pojos
     *
     * @param request
     */
    private void _Game_Hashes_OnePlayerAllPojosGetterRequest(In_Game_Hashes_OnePlayerAllPojosGetter.Request request) {
        try {
            Map<Class<? extends TopLevelPojo>, TopLevelPojo> topLevelPojoClassToTopLevelPojo = new HashMap<>();
            String playerId = request.getPlayerId();
            int outerRealmId = request.getOuterRealmId();
            topLevelPojoClassToTopLevelPojo.putAll(PojoUtils.getPlayerAllPojos(playerId, outerRealmId));
            sender().tell(new In_Game_Hashes_OnePlayerAllPojosGetter.Response(request, topLevelPojoClassToTopLevelPojo), ActorRef.noSender());
        } catch (Exception e) {
            PlayerPojosGetterException exception = new PlayerPojosGetterException(request.toString(), e);
            sender().tell(new In_Game_Hashes_OnePlayerAllPojosGetter.Response(request, exception), ActorRef.noSender());
            throw exception;
        }
    }

    /**
     * 获取Game-Redis-Strings- key的值
     *
     * @param request
     */
    private void _Game_Strings_OneIdOnePojoGetter(In_Game_Strings_OneIdOnePojoGetter.Request request) {
        try {
            int realmId = request.getOutRealmId();
            Class<? extends TopLevelPojo> topLevelPojoClass = request.getTopLevelPojoClass();
            TopLevelPojo pojo = PojoUtils.getGamePojoFromStrings(topLevelPojoClass, realmId);
            sender().tell(new In_Game_Strings_OneIdOnePojoGetter.Response(request, pojo), ActorRef.noSender());
        } catch (Exception e) {
            PlayerPojosGetterException exception = new PlayerPojosGetterException(request.toString(), e);
            sender().tell(new In_Game_Strings_OneIdOnePojoGetter.Response(request, exception), ActorRef.noSender());
            throw exception;
        }
    }


    /**
     * 获取AllCommon-Redis-Hashes-key的单个id的值
     *
     * @param request
     */
    private void _AllCommon_Hashes_OneIdOnePojoGetterRequest(In_AllCommon_Hashes_OneIdOnePojoGetter.Request request) {
        try {
            String id = request.getId();
            Class<? extends TopLevelPojo> topLevelPojoClass = request.getTopLevelPojoClass();
            TopLevelPojo pojo = PojoUtils.getAllCommonPojoFromHashes(topLevelPojoClass, id);
            sender().tell(new In_AllCommon_Hashes_OneIdOnePojoGetter.Response(request, pojo), ActorRef.noSender());
        } catch (Exception e) {
            PlayerPojosGetterException exception = new PlayerPojosGetterException(request.toString(), e);
            sender().tell(new In_AllCommon_Hashes_OneIdOnePojoGetter.Response(request, exception), ActorRef.noSender());
            throw exception;
        }
    }

    /**
     * 获取AllCommon-Redis-Hashes-key的多个id的值
     *
     * @param request
     */
    private void _AllCommon_Hashes_MultiIdsOnePojoGetterRequest(In_AllCommon_Hashes_MultiIdsOnePojoGetter.Request request) {
        try {
            Map<String, TopLevelPojo> idToTopLevelPojos = new HashMap<>();
            Class<? extends TopLevelPojo> topLevelPojoClass = request.getTopLevelPojoClass();
            for (String id : request.getIds()) {
                TopLevelPojo pojo = PojoUtils.getAllCommonPojoFromHashes(topLevelPojoClass, id);
                idToTopLevelPojos.put(id, pojo);
            }
            sender().tell(new In_AllCommon_Hashes_MultiIdsOnePojoGetter.Response(request, idToTopLevelPojos), ActorRef.noSender());
        } catch (Exception e) {
            PlayerPojosGetterException exception = new PlayerPojosGetterException(request.toString(), e);
            sender().tell(new In_AllCommon_Hashes_MultiIdsOnePojoGetter.Response(request, exception), ActorRef.noSender());
            throw exception;
        }
    }

    /**
     * 获取AllCommon-Redis-Strings-单个key的值
     *
     * @param request
     */
    private void _AllCommon_Strings_OneIdOnePojoGetterRequest(In_AllCommon_Strings_OneIdOnePojoGetter.Request request) {
        try {
            Class<? extends TopLevelPojo> topLevelPojoClass = request.getTopLevelPojoClass();
            TopLevelPojo pojo = PojoUtils.getAllCommonPojoFromStrings(topLevelPojoClass);
            sender().tell(new In_AllCommon_Strings_OneIdOnePojoGetter.Response(request, pojo), ActorRef.noSender());
        } catch (Exception e) {
            PlayerPojosGetterException exception = new PlayerPojosGetterException(request.toString(), e);
            sender().tell(new In_AllCommon_Strings_OneIdOnePojoGetter.Response(request, exception), ActorRef.noSender());
            throw exception;
        }
    }
}
