package ws.gameServer.features.standalone.actor.playerIO;

import akka.actor.ActorRef;
import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.mongoDB.interfaces.TopLevelPojo;
import ws.common.utils.di.GlobalInjector;
import ws.gameServer.features.actor.cluster.ClusterListener;
import ws.gameServer.features.actor.world.msg.In_PrepareToKillPlayerActorRequest;
import ws.gameServer.features.actor.world.msg.In_PrepareToKillPlayerActorResponse;
import ws.gameServer.features.standalone.actor.player.PlayerActor;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.playerIO.ctrl.PlayerIOCtrl;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_InitPlayerExtensions_Init_And_PostInit;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_InitPlayerExtensions_PostInit;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_RelacePlayerConnectionRequest;
import ws.gameServer.features.standalone.actor.playerIO.utils.LoadAllPlayerExtensions;
import ws.gameServer.features.standalone.actor.playerIO.utils.PlayerIOUtils;
import ws.protos.CodesProtos.ProtoCodes.Code;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerLoginProtos.Sm_Login;
import ws.relationship.appServers.loginServer.player.msg.In_ConnectToPlayerRequest;
import ws.relationship.appServers.loginServer.player.msg.In_ConnectToPlayerResponse;
import ws.relationship.base.ServerRoleEnum;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.relationship.base.msg.CheckPlayerOnlineMsgRequest;
import ws.relationship.base.msg.In_MessagePassToGatewayServer;
import ws.relationship.base.msg.MultiCheckPlayerOnlineMsg;
import ws.relationship.base.msg.db.getter.In_Game_Hashes_OnePlayerAllPojosGetter;
import ws.relationship.topLevelPojos.player.Player;
import ws.relationship.utils.ActorMsgSynchronizedExecutor;
import ws.relationship.utils.ProtoUtils;
import ws.relationship.utils.RelationshipCommonUtils;

import java.util.Map;

public class PlayerIOActor extends WsActor {
    private static final Logger logger = LoggerFactory.getLogger(PlayerIOActor.class);
    private ActorRef playerActorRef;
    private String playerId;
    private int simpleId;
    private PlayerIOCtrl ctrl;

    public PlayerIOActor(String playerId) {
        this.playerId = playerId;
        ctrl = GlobalInjector.getInstance(PlayerIOCtrl.class);
    }

    @Override
    public void onRecv(Object msg) throws Exception {
        if (msg instanceof In_ConnectToPlayerRequest) {
            In_ConnectToPlayerRequest request = (In_ConnectToPlayerRequest) msg;
            if (request.isNewLogin()) { // 登陆请求
                onPlayerLoginRequest(request);
            } else {// 重连请求
                onPlayerReconnectRequest(request);
            }
        } else if (msg instanceof MultiCheckPlayerOnlineMsg.Request) {
            onMultiCheckPlayerOnlineMsgRequest((MultiCheckPlayerOnlineMsg.Request) msg);
        } else if (msg instanceof In_PrepareToKillPlayerActorRequest) {
            onPrepareToKillPlayerActorRequest((In_PrepareToKillPlayerActorRequest) msg);
        }
    }

    private void onMultiCheckPlayerOnlineMsgRequest(MultiCheckPlayerOnlineMsg.Request request) {
        boolean online = !(playerActorRef == null); // 是否在线 true在线 false不在线
        MultiCheckPlayerOnlineMsg.Response response = new MultiCheckPlayerOnlineMsg.Response(request);
        for (CheckPlayerOnlineMsgRequest<?> checkRequest : request.getCheckOnlineMsgs()) {
            if (online) {
                playerActorRef.tell(checkRequest.getAttachMsg(), checkRequest.getSender());
            } else {
                ctrl.handleOfflineMsg(playerId, checkRequest.getAttachMsg(), checkRequest.getSender());
                request.getWorldActorRef().tell(response, getSelf());
            }
        }
    }

    private void onPlayerLoginRequest(In_ConnectToPlayerRequest msg) {
        if (playerId.equals(msg.getPlayerId())) {
            String connFlag = msg.getConnFlag();
            // 操作被禁止
            Response operationForbiddenResponse = PlayerIOUtils.tryOperationForbidden(msg.getCenterPlayer());
            if (operationForbiddenResponse != null) {
                logger.info("玩家={}被禁止操作了!", playerId);
                getSender().tell(new In_ConnectToPlayerResponse(msg, false), self());
                sendResponse(operationForbiddenResponse, msg.getGatewaySender(), connFlag);
                return;
            }
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_Login, msg.getAction());
            Sm_Login.Builder b = Sm_Login.newBuilder();
            b.setAction(msg.getAction());
            try {
                PlayerCtrl playerCtrl = GlobalInjector.getInstance(PlayerCtrl.class);
                In_Game_Hashes_OnePlayerAllPojosGetter.Request request = new In_Game_Hashes_OnePlayerAllPojosGetter.Request(msg.getPlayerId(), msg.getOuterRealmId());
                In_Game_Hashes_OnePlayerAllPojosGetter.Response response = ActorMsgSynchronizedExecutor.sendMsgToSingleServerIgnoreEnv(ServerRoleEnum.WS_MongodbRedisServer, getContext(), ActorSystemPath.WS_MongodbRedisServer_Selection_PojoGetterManager, request);
                RelationshipCommonUtils.checkDbResponse(response);
                Map<Class<? extends TopLevelPojo>, TopLevelPojo> topLevelPojoClassToTopLevelPojo = response.getTopLevelPojoClassToTopLevelPojo();
                playerCtrl.setTopLevelPojoClassToTopLevelPojo(topLevelPojoClassToTopLevelPojo);
                Player player = playerCtrl.getTopLevelPojo(Player.class);
                this.simpleId = player.getBase().getSimpleId();
                playerCtrl.setGatewaySender(msg.getGatewaySender());
                playerCtrl.setConnFlag(msg.getConnFlag());
                playerCtrl.setTarget(player);
                playerCtrl.setCenterPlayer(msg.getCenterPlayer());
                LoadAllPlayerExtensions.loadAll(playerCtrl);
                String playerActorName = ActorSystemPath.WS_GameServer_Player + playerId;
                ActorRef ref = getContext().actorOf(Props.create(PlayerActor.class, playerCtrl), playerActorName);
                getContext().watch(ref);
                ref.tell(new In_InitPlayerExtensions_Init_And_PostInit(), getSelf());
                ref.tell(new In_PlayerLoginedRequest(playerId, msg.getDeviceUid()), getSelf());
                b.setRpid(player.getPlayerId());
                b.setVpid(player.getBase().getSimpleId());
                br.setSmLogin(b);
                br.setResult(true);
                getSender().tell(new In_ConnectToPlayerResponse(msg, true), self());
                sendResponse(br.build(), msg.getGatewaySender(), connFlag);
                playerActorRef = ref;
                logger.debug("玩家playerId={} 从数据库加载了数据,登陆系统了！", playerId);
            } catch (Exception e) {
                br.setSmLogin(b);
                br.setResult(false);
                logger.error("Login Error! ", e);
                getSender().tell(new In_ConnectToPlayerResponse(msg, false), self());
                sendResponse(br.build(), msg.getGatewaySender(), connFlag);
            }
        }
    }

    private void onPlayerReconnectRequest(In_ConnectToPlayerRequest msg) throws Exception {
        if (playerId.equals(msg.getPlayerId())) {
            String connFlag = msg.getConnFlag();
            // 操作被禁止
            Response operationForbiddenResponse = PlayerIOUtils.tryOperationForbidden(msg.getCenterPlayer());
            if (operationForbiddenResponse != null) {
                logger.info("玩家={}被禁止操作了!", playerId);
                getSender().tell(new In_ConnectToPlayerResponse(msg, false), self());
                sendResponse(operationForbiddenResponse, msg.getGatewaySender(), connFlag);
                return;
            }
            // 重连
            Response.Builder br = ProtoUtils.create_Response(Code.Sm_Login, msg.getAction());
            Sm_Login.Builder b = Sm_Login.newBuilder();
            b.setAction(msg.getAction());
            b.setRpid(playerId);
            b.setVpid(simpleId);
            br.setSmLogin(b);
            if (playerActorRef == null) {
                br.setResult(false);
                getSender().tell(new In_ConnectToPlayerResponse(msg, false), self());
                sendResponse(br.build(), msg.getGatewaySender(), connFlag);
                logger.error("Reconnect Err! ! strange ! 玩家在线，但是playerActorRef却不存在，这是怎么了？ ");
                return;
            }
            logger.debug("玩家playerId={} 断线重连成功！", playerId);
            playerActorRef.tell(new In_RelacePlayerConnectionRequest(playerId, connFlag, msg.getGatewaySender()), getSelf());
            Thread.sleep(5);
            if (msg.getAction() == Sm_Login.Action.RESP_LOGIN) {
                playerActorRef.tell(new In_InitPlayerExtensions_PostInit(), getSelf());
                playerActorRef.tell(new In_PlayerLoginedRequest(playerId, msg.getDeviceUid()), getSelf());
            }
            getSender().tell(new In_ConnectToPlayerResponse(msg, true), self());
            br.setResult(true);
            sendResponse(br.build(), msg.getGatewaySender(), connFlag);
        }
    }

    private void onPrepareToKillPlayerActorRequest(In_PrepareToKillPlayerActorRequest msg) {
        if (!msg.getTargetPlayerId().equals(playerId)) {
            return;
        }
        if (playerActorRef == null) {
            logger.info("玩家PlayerId={} 即将被移除,玩家不在线直接通知去kill！", playerId);
            getSender().tell(new In_PrepareToKillPlayerActorResponse(playerId), getSelf());
        } else {
            playerActorRef.tell(msg, getSender());
        }
    }

    private void sendResponse(Response response, ActorRef gatewaySender, String connFlag) {
        gatewaySender.tell(new In_MessagePassToGatewayServer(ClusterListener.getAddress(), connFlag, response, response.getSmMsgAction()), self());
    }
}
