package ws.gameServer.features.standalone.actor.player;

import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.general.EnumUtils;
import ws.common.utils.mc.controler.Controler;
import ws.common.utils.message.interfaces.InnerMsg;
import ws.common.utils.message.interfaces.PrivateMsg;
import ws.gameServer.features.actor.world.msg.In_PrepareToKillPlayerActorRequest;
import ws.gameServer.features.actor.world.msg.In_PrepareToKillPlayerActorResponse;
import ws.gameServer.features.standalone.actor.player.ctrl.PlayerCtrl;
import ws.gameServer.features.standalone.actor.player.gm.PlayerGmSupport;
import ws.gameServer.features.standalone.actor.player.mc.extension.PlayerExtension;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_InitPlayerExtensions_Init_And_PostInit;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_InitPlayerExtensions_PostInit;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_PlayerLoginedRequest;
import ws.gameServer.features.standalone.actor.playerIO.msg.In_RelacePlayerConnectionRequest;
import ws.gameServer.features.standalone.actor.playerIO.utils.PlayerIOUtils;
import ws.gameServer.system.date.dayChanged.In_DayChanged;
import ws.gameServer.system.shutdownHook.msg.ServerBeginToShutDown;
import ws.protos.BattleProtos.Cm_TestBattle;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerProtos.Cm_GmCommand;
import ws.protos.PlayerProtos.Cm_Player;
import ws.protos.PlayerProtos.Cm_Player.Action;
import ws.relationship.base.actor.WsActor;
import ws.relationship.base.msg.In_MessagePassToOtherServer;
import ws.relationship.base.msg.In_PlayerDisconnectedRequest;
import ws.relationship.base.msg.In_PlayerOfflineRequest;
import ws.relationship.base.msg.player.In_ReGetCenterPlayer;
import ws.relationship.gm.GmCommandGroupNameConstants;
import ws.relationship.gm.GmCommandUtils;
import ws.relationship.gm.In_GmCommand;
import ws.relationship.topLevelPojos.player.Player;

import java.lang.reflect.Method;

public class PlayerActor extends WsActor {
    private static final Logger logger = LoggerFactory.getLogger(PlayerActor.class);

    private final PlayerCtrl playerCtrl;
    private final PlayerGmSupport playerGmSupport;

    public PlayerActor(PlayerCtrl playerCtrl) {
        this.playerCtrl = playerCtrl;
        this.playerCtrl.setContext(context());
        this.playerCtrl.setActorRef(getSelf());
        playerGmSupport = new PlayerGmSupport(playerCtrl);
    }

    private void onPlayerLogined(In_PlayerLoginedRequest request) {
        Player player = playerCtrl.getTarget();
        if (!request.getPlayerId().equals(player.getPlayerId())) {
            return;
        }
        playerCtrl.onPlayerLogined(request.getDeviceUid());
    }

    private void onRelacePlayerConnectionRequest(In_RelacePlayerConnectionRequest msg) {
        if (playerCtrl.getTarget().getPlayerId().equals(msg.getPlayerId())) {
            playerCtrl.setConnFlag(msg.getConnFlag());
            playerCtrl.setGatewaySender(msg.getGatewaySender());
        }
    }

    @Override
    public void onRecv(Object msg) throws Exception {
        logger.debug("\nplayer 接收到消息 > \n{} \n\n\n", msg.toString());
        playerCtrl.setCurSendActorRef(getSender());
        playerCtrl.clearNeedSavePojos();
        // 同步消息处理
        if (msg instanceof In_MessagePassToOtherServer) {
            onRecvNetworkMsg((In_MessagePassToOtherServer) msg);
        } else if (msg instanceof InnerMsg) {
            onRecvInnerMsg((InnerMsg) msg);
        } else if (msg instanceof PrivateMsg) {
            onRecvPrivateMsg((PrivateMsg) msg);
        }
        playerCtrl.saveAllNeedSavePojos();
    }

    /**
     * 当接收到网络消息时
     *
     * @param messagePassToOtherServer
     * @throws Exception
     */
    private void onRecvNetworkMsg(In_MessagePassToOtherServer messagePassToOtherServer) throws Exception {
        messagePassToOtherServer.getTimes().add(System.nanoTime());
        Method method = messagePassToOtherServer.getMessage().getClass().getMethod("getAction");
        Enum<?> action = (Enum<?>) method.invoke(messagePassToOtherServer.getMessage());
        playerCtrl.setNetworkMsgTimes(messagePassToOtherServer.getTimes(), EnumUtils.protoActionToString(action));
        Message myNetworkMsg = messagePassToOtherServer.getMessage();
        if (messagePassToOtherServer.getConnFlag().equals(playerCtrl.getConnFlag())) {
            onRecvMyNetworkMsg(myNetworkMsg);
        } else {
            onRecvOthersNetworkMsg(messagePassToOtherServer);
        }
    }


    /**
     * 接收到当前player客户端的网络消息
     *
     * @param myNetworkMsg
     * @throws Exception
     */
    private void onRecvMyNetworkMsg(Message myNetworkMsg) throws Exception {
        // 操作被禁止
        Response operationForbiddenResponse = PlayerIOUtils.tryOperationForbidden(playerCtrl.getCenterPlayer());
        if (operationForbiddenResponse != null) {
            logger.info("玩家={}被禁止操作了!", playerCtrl.getPlayerId());
            playerCtrl.sendResponse(operationForbiddenResponse);
            return;
        }
        if (myNetworkMsg instanceof Cm_Player) {
            Cm_Player cm_Player = (Cm_Player) myNetworkMsg;
            switch (cm_Player.getAction().getNumber()) {
                case Cm_Player.Action.SYNC_VALUE:
                    playerCtrl.sync();
                    break;
                case Cm_Player.Action.RENAME_VALUE:
                    onRename(cm_Player.getNewName());
                    break;
                case Cm_Player.Action.REICON_VALUE:
                    onIcon(cm_Player.getNewIcon());
                    break;
                case Action.SIGN_VALUE:
                    onSign(cm_Player.getNewSign());
                    break;
            }
        } else if (myNetworkMsg instanceof Cm_GmCommand) {
            Cm_GmCommand cm = (Cm_GmCommand) myNetworkMsg;
            In_GmCommand.Request gmCommand = GmCommandUtils.convertGmConentTo_In_GmCommand(cm.getContent());
            if (gmCommand != null) {
                onRecvInnerMsg(gmCommand);
            }
            return;
        } else if (myNetworkMsg instanceof Cm_TestBattle) {
            playerCtrl.testBattle();
        }

        for (PlayerExtension<? extends Controler<?>> playerExtension : playerCtrl.getAllExtensions().values()) {
            try {
                playerExtension.onRecvMyNetworkMsg(myNetworkMsg);
            } catch (Throwable t) {
                logger.error("PlayerExtension onRecvMyNetworkMsg Error, ext={}", playerExtension, t);
                continue;
            }
        }
    }

    private void onRecvOthersNetworkMsg(In_MessagePassToOtherServer othersNetworkMsg) throws Exception {
        for (PlayerExtension<? extends Controler<?>> playerExtension : playerCtrl.getAllExtensions().values()) {
            try {
                playerExtension.onRecvOthersNetworkMsg(othersNetworkMsg);
            } catch (Throwable t) {
                logger.error("PlayerExtension onRecvOthersNetworkMsg Error, ext={}", playerExtension, t);
                continue;
            }
        }
    }

    /**
     * 当接收到{@link InnerMsg}时
     *
     * @param innerMsg
     * @throws Exception
     */
    private void onRecvInnerMsg(InnerMsg innerMsg) throws Exception {
        // 处理初始化扩展
        if (innerMsg instanceof In_InitPlayerExtensions_Init_And_PostInit) {
            onIm_InitPlayerExtensions_Init_And_PostInit();
            return;
        } else if (innerMsg instanceof In_InitPlayerExtensions_PostInit) {
            onIm_InitPlayerExtensions_PostInit();
            return;
        } else if (innerMsg instanceof In_ReGetCenterPlayer.Request) {
            onReGetCenterPlayer();
            return;
        }

        // 处理断线重连
        else if (innerMsg instanceof In_RelacePlayerConnectionRequest) {
            onRelacePlayerConnectionRequest((In_RelacePlayerConnectionRequest) innerMsg);
        }

        // 处理正常登陆
        else if (innerMsg instanceof In_PlayerLoginedRequest) {
            onPlayerLogined((In_PlayerLoginedRequest) innerMsg);
        } else if (innerMsg instanceof In_GmCommand.Request) {
            onIm_GmCommand((In_GmCommand.Request) innerMsg);
        }
        //
        else if (innerMsg instanceof In_PrepareToKillPlayerActorRequest) {
            onPrepareToKillPlayerActorRequest();
            return;
        } else if (innerMsg instanceof In_DayChanged) {
            onDayChanged();
        } else if (innerMsg instanceof In_PlayerOfflineRequest) {
            onPlayerOfflineRequest();
        } else if (innerMsg instanceof In_PlayerDisconnectedRequest) {
            onPlayerDisconnectedRequest();
        }


        for (PlayerExtension<? extends Controler<?>> playerExtension : playerCtrl.getAllExtensions().values()) {
            try {
                playerExtension.setCurSender(getSender());
                playerExtension.onRecvInnerMsg(innerMsg);
            } catch (Throwable t) {
                logger.error("PlayerExtension onRecvInnerMsg Error, ext={}", playerExtension, t);
                continue;
            }
        }

        // 停机处理在最后
        if (innerMsg instanceof ServerBeginToShutDown) {
            onServerBeginToShutDown();
        }
    }

    private void onReGetCenterPlayer() {
        playerCtrl.onReGetCenterPlayer();
        getSender().tell(new In_ReGetCenterPlayer.Response(), getSelf());
    }

    /**
     * 处理该玩家的GM命令
     *
     * @param im_GmCommand
     */
    private void onIm_GmCommand(In_GmCommand.Request im_GmCommand) {
        if (im_GmCommand.getGroupName().equals(GmCommandGroupNameConstants.Player)) {
            playerGmSupport.exec(im_GmCommand.getFromType(), im_GmCommand.getCommandName(), im_GmCommand.getArgs());
        }
    }

    /**
     * extp的初始化
     */
    private void onIm_InitPlayerExtensions_Init_And_PostInit() {
        _PlayerExtension_Init();
        _PlayerExtension_InitReference();
        _playerExtension_PostInit();
    }


    private void onIm_InitPlayerExtensions_PostInit() {
        _playerExtension_PostInit();
    }

    private void _PlayerExtension_Init() {
        for (PlayerExtension<? extends Controler<?>> playerExtension : playerCtrl.getAllExtensions().values()) {
            try {
                playerExtension.init();
            } catch (Throwable t) {
                logger.error("PlayerExtension init Error, ext={}", playerExtension, t);
                continue;
            }
        }
    }

    private void _PlayerExtension_InitReference() {
        for (PlayerExtension<? extends Controler<?>> playerExtension : playerCtrl.getAllExtensions().values()) {
            try {
                playerExtension.initReference();
            } catch (Throwable t) {
                logger.error("PlayerExtension initReference Error, ext={}", playerExtension, t);
                continue;
            }
        }
    }

    private void _playerExtension_PostInit() {
        try {
            playerCtrl.postInit();
        } catch (Exception e) {
            logger.error("playerCtrl postInit Error, ext={}", e);
        }
        for (PlayerExtension<? extends Controler<?>> playerExtension : playerCtrl.getAllExtensions().values()) {
            try {
                playerExtension.postInit();
            } catch (Throwable t) {
                logger.error("PlayerExtension _initAll Error, ext={}", playerExtension, t);
                continue;
            }
        }
    }

    /**
     * 当接收到{@link PrivateMsg}时
     *
     * @param privateMsg
     * @throws Exception
     */
    private void onRecvPrivateMsg(PrivateMsg privateMsg) throws Exception {
        for (PlayerExtension<? extends Controler<?>> playerExtension : playerCtrl.getAllExtensions().values()) {
            try {
                playerExtension.onRecvPrivateMsg(privateMsg);
            } catch (Throwable t) {
                logger.error("PlayerExtension onRecvPrivateMsg Error, ext={}", playerExtension, t);
                continue;
            }
        }
    }


    private void onRename(String newName) {
        playerCtrl.onRename(newName);
    }

    private void onIcon(int newIcon) {
        playerCtrl.onIcon(newIcon);
    }

    private void onSign(String newSign) {
        playerCtrl.onSign(newSign);
    }


    /**
     * 玩家退出时保存玩家数据
     */
    private void onPrepareToKillPlayerActorRequest() {
        logger.info("玩家PlayerId={} 即将被移除,玩家在线 现在开始保存玩家数据！", playerCtrl.getTarget().getPlayerId());
        logger.info("玩家PlayerId={} 即将被移除,玩家在线 保存数据完毕直接通知去kill！！", playerCtrl.getTarget().getPlayerId());

        playerCtrl.setLsoutTime();
        getSender().tell(new In_PrepareToKillPlayerActorResponse(playerCtrl.getTarget().getPlayerId()), getSelf());
    }

    private void onServerBeginToShutDown() {
        logger.warn("收到停止服务器请求，开启保存玩家所有数据！");
    }


    private void onPlayerOfflineRequest() {
        playerCtrl.setLsoutTime();
    }


    private void onPlayerDisconnectedRequest() {
        playerCtrl.onPlayerDisconnected();
    }

    private void onDayChanged() {
        playerCtrl.resetDataAtDayChanged();
    }
}
