package ws.analogClient.features.flow;

import ws.analogClient.features.Action;
import ws.analogClient.features.ActionResult;
import ws.analogClient.features.utils.ClientUtils;
import ws.analogClient.system.config.AppConfig;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerLoginProtos.Cm_Login;
import ws.protos.PlayerLoginProtos.Sm_Login;
import ws.protos.errorCode.ErrorCodeProtos.ErrorCodeEnum;

public class AuthAction implements Action {
    @Override
    public ActionResult run(Object... objects) {
        Cm_Login.Builder b1 = Cm_Login.newBuilder();
        b1.setAction(Cm_Login.Action.AUTH);
        b1.setPlatformType(GlobalArgs.platformType);
        b1.setToken(objects[0].toString()); // token
        b1.setOuterRealmId(1);
        b1.setSpecifiedGameServerRole(AppConfig.getString(AppConfig.Key.WS_AnalogClient_game_server_role));

        Response response = ClientUtils.send(b1.build(), Sm_Login.Action.RESP_AUTH);
        if (response != null && response.getSmLogin() != null) {
            Sm_Login login = response.getSmLogin();
            if (login.getAction() == Sm_Login.Action.RESP_AUTH) {
                if (!response.getResult()) {
                    if (response.getErrorCode() == ErrorCodeEnum.UNREGISTERED) {
                        return new ActionResult(true, login.getSecurityCode(), "", "", objects[1].toString()); // account name
                    } else {
                        return new ActionResult(false);
                    }
                } else {
                    return new ActionResult(true, login.getSecurityCode(), login.getVpid(), login.getRpid(), "");
                }
            }
        }
        return new ActionResult(false, "", "", "", ""); // account name
    }

}
