package ws.gameServer.features.actor.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.actor.register.utils.create.RegisterUtils;
import ws.relationship.appServers.loginServer.player.msg.In_Register;
import ws.relationship.base.actor.WsActor;
import ws.relationship.topLevelPojos.centerPlayer.CenterPlayer;

public class RegisterActor extends WsActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterActor.class);

    public RegisterActor() {
        try {
            RegisterUtils.init();
        } catch (Exception e) {
            LOGGER.error("ExtensionIniter 加载异常！", e);
        }
    }

    @Override
    public void onRecv(Object msg) {
        if (msg instanceof In_Register.Request) {
            onRegisterRequest((In_Register.Request) msg);
        }
    }

    private void onRegisterRequest(In_Register.Request request) {
        CenterPlayer centerPlayer = request.getCenterPlayer(); // 如果成功，则gameId将被修改
        LOGGER.debug("Recv In_RegisterRequest, playerId={}, playerName={}", centerPlayer.getCenterId(), centerPlayer.getPlayerName());
        boolean rs = RegisterUtils.register(centerPlayer);
        getSender().tell(new In_Register.Response(request, rs), getSelf());
    }
}
