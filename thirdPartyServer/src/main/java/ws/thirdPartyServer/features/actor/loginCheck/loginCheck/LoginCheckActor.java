package ws.thirdPartyServer.features.actor.loginCheck.loginCheck;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.relationship.appServers.thirdPartyServer.In_LoginToPlatformRequest;
import ws.relationship.base.actor.WsActor;
import ws.thirdPartyServer.features.actor.loginCheck.loginCheck.platform.LoginCheckPlatform;
import ws.thirdPartyServer.features.actor.loginCheck.loginCheck.platform.LoginCheckPlatformHandlerManager;

public class LoginCheckActor extends WsActor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginCheckActor.class);
    private PlatformTypeEnum PlatformTypeEnum;

    public LoginCheckActor(PlatformTypeEnum PlatformTypeEnum) {
        this.PlatformTypeEnum = PlatformTypeEnum;
    }

    @Override
    public void onRecv(Object msg) throws Exception {
        if (msg instanceof In_LoginToPlatformRequest) {
            onLoginToPlatformRequest((In_LoginToPlatformRequest) msg);
        }
    }

    private void onLoginToPlatformRequest(In_LoginToPlatformRequest request) {
        LOGGER.info("[登录] > 接受到验证请求request={}", request.toString());

        PlatformTypeEnum PlatformTypeEnum = request.getPlatformType();
        if (PlatformTypeEnum != this.PlatformTypeEnum) {
            LOGGER.error("奇怪！PlatformTypeEnum ={} 的登录验证请求怎么会发到PlatformTypeEnum ={}的Actor上呢！", PlatformTypeEnum, this.PlatformTypeEnum);
            return;
        }
        LoginCheckPlatform loginCheckPlatform = LoginCheckPlatformHandlerManager.getPlatformtologincheckplatform().get(PlatformTypeEnum);
        if (loginCheckPlatform == null) {
            LOGGER.error("糟糕！在LoginCheckPlatformHandlerManager的loginCheckPlatform缓存中没有找到PlatformTypeEnum ={}的处理handler", PlatformTypeEnum);
            return;
        }
        try {
            loginCheckPlatform.loginAuthorizeRequest(getContext(), getSender(), request);
        } catch (Exception e) {
            LOGGER.error("第三方登录验证失败！", e);
        }
    }
}
