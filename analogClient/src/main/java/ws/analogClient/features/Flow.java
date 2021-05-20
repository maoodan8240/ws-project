package ws.analogClient.features;

import ws.analogClient.features.flow.*;

public enum Flow {
    /**
     * SDK 注册
     */
    SDK_REGISTER(new SdkRegisterAction()),
    /**
     * SDK 登录
     */
    SDK_LOGIN(new SdkLoginAction()),
    /**
     * 校验
     */
    AUTH(new AuthAction()), //
    /**
     * 注册
     */
    REGISTER(new RegisterAction()), //
    /**
     * 登录
     */
    LOGIN(new LoginAction()), //
    /**
     * 功能模块
     */
    FUNCTIONS(new FunctionsAction()),//
    ;
    private Action action;

    Flow(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }
}
