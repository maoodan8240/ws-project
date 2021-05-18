package ws.analogClient.features.flow;

import ws.analogClient.features.Action;
import ws.analogClient.features.ActionResult;
import ws.analogClient.system.config.AppConfig;
import ws.protos.MessageHandlerProtos.Response;
import ws.protos.PlayerLoginProtos.Cm_Login;
import ws.protos.PlayerLoginProtos.Sm_Login;
import ws.analogClient.features.utils.ClientUtils;

public class LoginAction implements Action {

    @Override
    public ActionResult run(Object... objects) {
        Cm_Login.Builder b1 = Cm_Login.newBuilder();
        b1.setAction(Cm_Login.Action.LOGIN);
        b1.setSecurityCode(objects[0].toString());
        b1.setSpecifiedGameServerRole(AppConfig.getString(AppConfig.Key.WS_AnalogClient_game_server_role));

        Response response = ClientUtils.send(b1.build(), Sm_Login.Action.RESP_LOGIN);
        if (response != null && response.getSmLogin() != null) {
            Sm_Login login = response.getSmLogin();
            if (login.getAction() == Sm_Login.Action.RESP_LOGIN) {
                if (!response.getResult()) {
                    return new ActionResult(false);
                } else {
                    return new ActionResult(true);
                }
            }
        }
        return new ActionResult(false);
    }
}
