package ws.gameServer.features.standalone.actor.player.mc.extension;

import akka.actor.ActorRef;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.TopLevelPojo;
import ws.common.mongoDB.utils.WsJsonUtils;
import ws.common.redis.RedisOpration;
import ws.common.redis.operation.In_RedisOperation;
import ws.common.redis.operation.RedisOprationEnum;
import ws.common.utils.di.GlobalInjector;
import ws.common.utils.mc.controler.Controler;
import ws.common.utils.mc.extension.AbstractExtension;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.actor.register.utils.create.ext.base.ExtCommonData;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.mc.controler.PlayerExteControler;
import ws.gameServer.features.standalone.actor.player.mc.exception.TargetInsertIntoRedisFailedException;
import ws.relationship.base.ServerRoleEnum;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.In_MessagePassToOtherServer;
import ws.relationship.base.msg.db.saver.In_Game_Hashes_OneId_MultiPojos_SameOutRealmId_Saver;
import ws.relationship.topLevelPojos.PlayerTopLevelPojo;
import ws.relationship.topLevelPojos.TopLevelPojoContainer;
import ws.relationship.topLevelPojos.common.PlayerCreatedTargets;
import ws.relationship.topLevelPojos.common.TopLevelHolder;
import ws.relationship.utils.ActorMsgSynchronizedExecutor;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPlayerExtension<T extends Controler<?>> extends AbstractExtension<T> implements PlayerExtension<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPlayerExtension.class);
    protected static final RedisOpration REDIS_OPRATION = GlobalInjector.getInstance(RedisOpration.class);
    protected final PlayerCtrl ownerCtrl;
    protected ActorRef curSender;
    protected int outerRealmId;
    protected String playerId;

    public AbstractPlayerExtension(PlayerCtrl ownerCtrl) {
        this.ownerCtrl = ownerCtrl;
    }

    @Override
    public PlayerCtrl getOwnerCtrl() {
        return ownerCtrl;
    }

    @Override
    public void init() throws Exception {
        outerRealmId = ownerCtrl.getTarget().getAccount().getOuterRealmId();
        playerId = ownerCtrl.getTarget().getPlayerId();
        _init();
    }

    @Override
    public void initReference() throws Exception {
        _initReference();
    }

    @Override
    public void postInit() throws Exception {
        _postInit();
    }

    @SuppressWarnings("unchecked")
    public <TARGET extends PlayerTopLevelPojo, CONTROLER extends PlayerExteControler<TARGET>> void _init(Class<CONTROLER> controlerClass, Class<TARGET> targetClass) throws Exception {
        LOGGER.debug("初始化加载Target playerId={} controlerClass={} targetClass={} ...", playerId, controlerClass, targetClass);
        if (controlerClass == null) {
            return;
        }
        CONTROLER ctrl = GlobalInjector.getInstance(controlerClass);
        ctrl.setPlayerCtrl(ownerCtrl);
        setControler((T) ctrl);
        if (targetClass == null || TopLevelHolder.class.isAssignableFrom(targetClass)) {
            return;
        }
        TARGET target = ownerCtrl.getTopLevelPojo(targetClass);
        PlayerCreatedTargets createdTargets = ownerCtrl.getTopLevelPojo(PlayerCreatedTargets.class);
        if (target == null) {
            if (createdTargets.getTargetNames().contains(targetClass.getSimpleName())) {
                LOGGER.error("playerId={} targetClass={} target已经创建过一次，从数据库加载失败，尝试再次加载.", playerId, targetClass);
                // TODO: 17-3-27  重新从数据库加载
                return;
            }
            LOGGER.info("target为null,将自动生成! playerId={} targetClass={} .", playerId, targetClass);
            target = targetClass.getConstructor(String.class).newInstance(playerId);
            String targetJson = WsJsonUtils.javaObjectToJSONStr(target);
            ExtCommonData.addToPlayerCreatedTargets(createdTargets, target);
            List<TopLevelPojo> pojoLis = new ArrayList<>();
            pojoLis.add(target);
            pojoLis.add(createdTargets);
            In_Game_Hashes_OneId_MultiPojos_SameOutRealmId_Saver.Request request = new In_Game_Hashes_OneId_MultiPojos_SameOutRealmId_Saver.Request(outerRealmId, pojoLis);
            ActorMsgSynchronizedExecutor.sendMsgToSingleServerIgnoreEnv(ServerRoleEnum.WS_MongodbRedisServer, ownerCtrl.getContext(), ActorSystemPath.WS_MongodbRedisServer_Selection_PojoSaver, request);
            if (!RedisOprationEnum.Hashes.hexists.parseResult(REDIS_OPRATION.execute(new In_RedisOperation(RedisOprationEnum.Hashes.hexists.newParmBuilder().build(TopLevelPojoContainer.getGameRedisKey(target.getClass(), outerRealmId), playerId))))) {
                throw new TargetInsertIntoRedisFailedException(targetClass, targetJson);
            }
            ownerCtrl.addToTopLevelPojoClassToTopLevelPojo(target);
        }
        ctrl.setTarget(target);
    }


    public abstract void _init() throws Exception;

    public abstract void _initReference() throws Exception;

    public abstract void _postInit() throws Exception;

    @Override
    public void destroy() {
    }

    public void setCurSender(ActorRef curSender) {
        this.curSender = curSender;
    }

    /**
     * ==================================================================
     */
    @Override
    public void onRecvMyNetworkMsg(Message clientMsg) throws Exception {
    }

    @Override
    public void onRecvOthersNetworkMsg(In_MessagePassToOtherServer othersNetworkMsg) throws Exception {
    }

    @Override
    public void onRecvInnerMsg(InnerMsg innerMsg) throws Exception {
    }

    @Override
    public void onRecvPrivateMsg(PrivateMsg privateMsg) throws Exception {
    }
}
